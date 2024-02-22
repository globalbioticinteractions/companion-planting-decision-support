package nl.vu.kai.companion.util;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

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


}
