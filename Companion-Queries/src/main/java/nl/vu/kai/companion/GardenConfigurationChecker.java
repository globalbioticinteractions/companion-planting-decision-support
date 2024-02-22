package nl.vu.kai.companion;

import nl.vu.kai.companion.data.Plant;
import nl.vu.kai.companion.repairs.ClassicalRepairGenerator;
import nl.vu.kai.companion.repairs.RepairException;
import nl.vu.kai.companion.util.OntologyTools;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GardenConfigurationChecker {

    private final OWLOntology plantOntology;
    private final OWLDataFactory owlFactory;
    private final OWLOntologyManager owlManager;

    private final static String GARDEN_NAME = "garden";

    public GardenConfigurationChecker() throws OWLOntologyCreationException {
        owlManager = OWLManager.createOWLOntologyManager();
        owlFactory = owlManager.getOWLDataFactory();
        plantOntology = owlManager.loadOntologyFromOntologyDocument(new File(Configuration.ONTOLOGY_PATH));
    }

    public OWLOntology getPlantOntology() {
        return plantOntology;
    }

    public Stream<Plant> plants() {
        return OntologyTools
                .assertedLeafs(plantOntology,
                        owlFactory.getOWLClass(
                                IRI.create(
                                        Configuration.PLANT_IRI)))
                .stream()
                .map(OWLClass::getIRI)
                .map(this::toPlant);

    }

    public Plant getPlant(String iriString) {
        return toPlant(IRI.create(iriString));
    }

    public OWLClass asOWLClass(Plant plant) {
        return owlFactory.getOWLClass(IRI.create(plant.getIri()));
    }

    public Plant toPlant(IRI iri) {
        String iriString = iri.getIRIString();
        Optional<String> label = plantOntology.annotationAssertionAxioms(iri)
                .filter(x -> x.getProperty().isLabel())
                .flatMap(x->  x.literalValue().stream())
                .filter(x -> x.hasLang(Configuration.LANGUAGE))
                .map(x -> x.getLiteral())
                .findFirst();
        Optional<String> scientificName = plantOntology.annotationAssertionAxioms(iri)
                .filter(this::scientificNameAnnotation)
                .flatMap(x->  x.literalValue().stream())
                .map(x -> x.getLiteral())
                .findFirst();

        return new Plant(iriString,label,scientificName);
    }

    private boolean scientificNameAnnotation(OWLAnnotationAssertionAxiom ax) {
        return ax.getProperty().getIRI().getIRIString().equals(Configuration.SCIENTIFIC_NAME_IRI);
    }


    /**
     * Check whether plants represented by given classes are compatible.
     */
    public boolean checkProperty(
            Set<OWLClass> plants,
            Configuration.GardenConfigurationProperty property) throws OWLOntologyCreationException {
        OWLOntology maximalABox = createMaximalABox(plants);

        maximalABox.addAxioms(plantOntology.axioms());

        OWLReasoner reasoner = new ReasonerFactory().createReasoner(maximalABox);

        return reasoner.isEntailed(getAxiom(property));
    }

    /**
     * Provided that the given plants are not compatible, return an explanation for it.
     *
     * @throws IllegalArgumentException if the set of plants is actually compatible
     */
    public Set<OWLAxiom> explainProperty(
            Set<OWLClass> plants,
            Configuration.GardenConfigurationProperty property) throws OWLOntologyCreationException {
        OWLOntology maximalABox = createMaximalABox(plants);
        OWLAxiom axiom = getAxiom(property);

        maximalABox.addAxioms(plantOntology.axioms());

        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(maximalABox);

        if(!reasoner.isEntailed(axiom))
            throw new IllegalArgumentException("Property not satisfied!");

        ExplanationGenerator explanationGenerator =
                new InconsistentOntologyExplanationGeneratorFactory(
                        reasonerFactory,
                        owlFactory,
                        () -> owlManager,
                        1000)
                        .createExplanationGenerator(maximalABox);


        Set<Explanation<OWLAxiom>> explanations = explanationGenerator.getExplanations(
                axiom,
                1);

        return explanations
                .stream()
                .map((Explanation x) -> x.getAxioms())
                .findFirst()
                .get();
    }


    public Set<OWLIndividualAxiom> organizePlants(Set<OWLClass> plants) throws OWLOntologyCreationException {
        List<Configuration.GardenConfigurationProperty> properties =
                Arrays.stream(Configuration.GardenConfigurationProperty.values()).filter(
                        x -> {
                            try {
                                return checkProperty(plants,x);
                            } catch (OWLOntologyCreationException e) {
                                return false;
                            }
                        }
                ).collect(Collectors.toList());

        properties.remove(Configuration.GardenConfigurationProperty.BAD_GARDEN);

        Set<OWLIndividualAxiom> currentABox=null;
        while(currentABox==null){
            Configuration.GardenConfigurationProperty bestProperty = bestProperty(properties);
            try {
                currentABox = organizePlants(plants,bestProperty);
            } catch(RepairException re) {
                // repair not possible -> we continue
                System.out.println("Cannot preserve property "+bestProperty);
            }
        }
        return currentABox;
    }

    private static Configuration.GardenConfigurationProperty bestProperty(Collection<Configuration.GardenConfigurationProperty> properties)  {
        return properties.stream().max((x,y) -> x.rating-y.rating).get();
    }

    /**
     * Return an organization of given plants that satisfies the given property, yet still does not lead to a
     * bad garden.
     */
    public Set<OWLIndividualAxiom> organizePlants(
            Set<OWLClass> plants, Configuration.GardenConfigurationProperty desiredProperty)
            throws OWLOntologyCreationException, RepairException {
        OWLOntology maximalABox = createMaximalABox(plants);

        Set<OWLIndividualAxiom> aboxAxioms = maximalABox.aboxAxioms(Imports.EXCLUDED)
                .map(x -> (OWLIndividualAxiom)x)
                .collect(Collectors.toSet());

        maximalABox.addAxioms(plantOntology.axioms());

        ClassicalRepairGenerator repairGenerator = new ClassicalRepairGenerator();

        Set<OWLIndividualAxiom> flexible =
                aboxAxioms.stream()
                        .filter(x -> x instanceof OWLObjectPropertyAssertionAxiom)
                        .map(x -> (OWLIndividualAxiom) x)
                        .collect(Collectors.toSet());

        Set<OWLClassAssertionAxiom> rest =
                aboxAxioms.stream()
                        .filter(x -> x instanceof OWLClassAssertionAxiom)
                        .map(x -> (OWLClassAssertionAxiom) x)
                        .collect(Collectors.toSet());

        Set<OWLIndividualAxiom> repair = repairGenerator.computeRepair(maximalABox, flexible);

        repair.addAll(rest);

        return repair;
    }

    /**
     * Return an instantiation of the given plants that respects compability.
     */
    public Set<OWLIndividualAxiom> configurePlants(Set<OWLClass> plants) {
        throw new AssertionError("Not implemented!");
    }

    private OWLAxiom getAxiom(Configuration.GardenConfigurationProperty property)  {
        return owlFactory.getOWLClassAssertionAxiom(
                owlFactory.getOWLClass(IRI.create(property.iri)),
                owlFactory.getOWLNamedIndividual(IRI.create(GARDEN_NAME)));
    }

    private OWLOntology createMaximalABox(Set<OWLClass> plants) throws OWLOntologyCreationException {
        OWLOntology ontology = owlManager.createOntology();

        OWLObjectProperty neighbour = owlFactory.getOWLObjectProperty(IRI.create(Configuration.NEIGHBOUR_IRI));

        OWLIndividual garden = owlFactory.getOWLNamedIndividual(IRI.create(GARDEN_NAME));

        ontology.add(owlFactory.getOWLClassAssertionAxiom(
                owlFactory.getOWLClass(IRI.create(Configuration.GARDEN_IRI)),
                garden));


        Set<OWLIndividual> instances = new HashSet<>();
        int count = 0;
        for(OWLClass plant:plants) {
            OWLIndividual ind = owlFactory.getOWLNamedIndividual(IRI.create("plant"+count));
            count++;
            instances.add(ind);
            ontology.add(owlFactory.getOWLClassAssertionAxiom(plant,ind));
            ontology.add(owlFactory.getOWLObjectPropertyAssertionAxiom(
                    owlFactory.getOWLObjectProperty(IRI.create(Configuration.GARDEN_RELATION_IRI)),
                            garden, ind)
            );
        }

        for(OWLIndividual i1: instances)
            for(OWLIndividual i2: instances)
                ontology.add(owlFactory.getOWLObjectPropertyAssertionAxiom(
                        neighbour,i1,i2
                ));

        return ontology;
    }

}
