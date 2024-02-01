package nl.vu.kai.companion;

import com.clarkparsia.owlapi.explanation.ExplanationGenerator;
import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import nl.vu.kai.companion.repairs.ClassicalRepairGenerator;
import nl.vu.kai.companion.repairs.RepairException;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class CompatibilityChecker {

    private final OWLOntology plantOntology;
    private final OWLDataFactory owlFactory;
    private final OWLOntologyManager owlManager;

    public CompatibilityChecker() throws OWLOntologyCreationException {
        owlManager = OWLManager.createOWLOntologyManager();
        owlFactory = owlManager.getOWLDataFactory();
        plantOntology = owlManager.loadOntologyFromOntologyDocument(new File(Configuration.ONTOLOGY_PATH));
    }

    /**
     * Check whether plants represented by given classes are compatible.
     */
    public boolean compatible(Set<OWLClass> plants) throws OWLOntologyCreationException {
        OWLOntology maximalABox = createMaximalABox(plants);

        maximalABox.addAxioms(plantOntology.axioms());

        OWLReasoner reasoner = new ReasonerFactory().createReasoner(maximalABox);

        return reasoner.isConsistent();
    }

    /**
     * Provided that the given plants are not compatible, return an explanation for it.
     *
     * @throws IllegalArgumentException if the set of plants is actually compatible
     */
    public Set<OWLAxiom> explainIncompatibility(Set<OWLClass> plants) throws OWLOntologyCreationException {
        OWLOntology maximalABox = createMaximalABox(plants);

        maximalABox.addAxioms(plantOntology.axioms());

        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(maximalABox);

        if(reasoner.isConsistent())
            throw new IllegalArgumentException("Plants are compatible!");

        ExplanationGenerator explanationGenerator =
                new DefaultExplanationGenerator(
                        owlManager,
                        reasonerFactory,
                        maximalABox,
                        new SilentExplanationProgressMonitor()
                );

        return explanationGenerator.getExplanation(owlFactory.getOWLThing());
    }

    /**
     * Return an organization of given plants that respects their compatibility
     */
    public Set<OWLIndividualAxiom> organizePlants(Set<OWLClass> plants) throws OWLOntologyCreationException, RepairException {
        OWLOntology maximalABox = createMaximalABox(plants);

        Set<OWLIndividualAxiom> flexible = maximalABox.aboxAxioms(Imports.EXCLUDED)
                .map(x -> (OWLIndividualAxiom)x)
                .collect(Collectors.toSet());

        maximalABox.addAxioms(plantOntology.axioms());

        ClassicalRepairGenerator repairGenerator = new ClassicalRepairGenerator();

        return repairGenerator.computeRepair(maximalABox, flexible);
    }

    /**
     * Return an instantiation of the given plants that respects compability.
     */
    public Set<OWLIndividualAxiom> configurePlants(Set<OWLClass> plants) {
        throw new AssertionError("Not implemented!");
    }


    private OWLOntology createMaximalABox(Set<OWLClass> plants) throws OWLOntologyCreationException {
        OWLOntology ontology = owlManager.createOntology();

        OWLObjectProperty neighbour = owlFactory.getOWLObjectProperty(IRI.create(Configuration.nextToIRI));

        Set<OWLIndividual> instances = new HashSet<>();
        int count = 0;
        for(OWLClass plant:plants) {
            OWLIndividual ind = owlFactory.getOWLNamedIndividual(IRI.create("p"+count));
            count++;
            instances.add(ind);
            ontology.add(owlFactory.getOWLClassAssertionAxiom(plant,ind));
        }

        for(OWLIndividual i1: instances)
            for(OWLIndividual i2: instances)
                ontology.add(owlFactory.getOWLObjectPropertyAssertionAxiom(
                        neighbour,i1,i2
                ));

        return ontology;
    }

}
