package nl.vu.kai.companion.util;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

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

}
