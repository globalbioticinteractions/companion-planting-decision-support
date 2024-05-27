package nl.vu.kai.companion.util;

import org.semanticweb.HermiT.ReasonerFactory;
// import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import nl.vu.kai.companion.Configuration;
import nl.vu.kai.companion.GardenConfigurationChecker;
import nl.vu.kai.companion.data.Edge;
import nl.vu.kai.companion.data.Plant;

import java.util.*;
import java.util.stream.Collectors;

public class OntologyTools {
    private OntologyTools(){
        // utility class
    }

    /**
     * Returns the asserted "leafs" in the subclass hierarchy below the given superClass
     */
    public static Collection<OWLClass> assertedLeafs(OWLOntology ontology, OWLClass superClass){
        Queue<OWLClass> toProcess = new LinkedList<>();
        toProcess.add(superClass);
        Set<OWLClass> processed = new HashSet<>(); // take care of cycles

        Set<OWLClass> result = new HashSet<>();

        while(!toProcess.isEmpty()){
            OWLClass next = toProcess.poll();
            if(!processed.contains(next)) {
                processed.add(next);
                Collection<OWLClass> subClasses = assertedSubClasses(ontology, next);
                if (subClasses.isEmpty())
                    result.add(next);
                else
                    subClasses.forEach(toProcess::add);
            }
        }
        return result;
    }

    /**
     * Returns all classes that have been explicitly declared subclasses.
     */
    private static Collection<OWLClass> assertedSubClasses(OWLOntology ontology, OWLClass superClass) {
        return ontology.subClassAxiomsForSuperClass(superClass)
                .map(x -> x.getSubClass())
                .filter(x -> x instanceof OWLClass)
                .map(x -> (OWLClass) x)
                .collect(Collectors.toList());
    }

    public static Collection<OWLClass> simpleQuery(OWLOntology ontology, OWLClass plant, OWLObjectProperty property){
        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClassExpression query = factory.getOWLObjectSomeValuesFrom(property, plant);

        NodeSet<OWLClass> answer = reasoner.getSubClasses(query);
        // answer.entities();

        return answer.entities()
                .collect(Collectors.toList());
    }

    public static Set<Edge> companionGraph(GardenConfigurationChecker checker, List<String> plants){
        OWLOntology ontology = checker.getPlantOntology();
        Set<Edge> edges = new HashSet<>();

        for (String plantstring : plants) {
            Plant plant1 = checker.getPlant(plantstring);
            OWLClass plant = checker.asOWLClass(plant1);
            // Plant plant1 = checker.getPlant(plant.getIRI().toString());
            
            Collection<OWLClass> companions = simpleQuery(ontology, plant, ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(Configuration.COMPANION_PROPERTY_IRI));
            Collection<OWLClass> anticompanions = simpleQuery(ontology, plant, ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(Configuration.ANTI_COMPANION_PROPERTY_IRI));

            for (OWLClass companion : companions) {
                Plant comPlant = checker.getPlant(companion.getIRI().toString());
                if (!companion.isOWLNothing()) {
                    edges.add(new Edge(plant1,comPlant,"companion"));
                }
                
            }

            for (OWLClass companion : anticompanions) {
                Plant comPlant = checker.getPlant(companion.getIRI().toString());
                if (!companion.isOWLNothing()) {
                    edges.add(new Edge(plant1,comPlant,"anticompanion"));
                }
            }
            
        }

        return edges;
    }

}
