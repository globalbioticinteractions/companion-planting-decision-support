package nl.vu.kai.companion;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;

import java.util.Collections;
import java.util.Set;

public class CompatibilityChecker {

    /**
     * Check whether plants represented by given classes are compatible.
     */
    public boolean compatible(Set<OWLClass> plants) {
        return false;
    }

    /**
     * Return an organization of given plants that respects their compatibility
     */
    public Set<OWLIndividualAxiom> organizePlants(Set<OWLClass> plants) {
        return Collections.emptySet();
    }

    /**
     * Return an instantiation of the given plants that respects compability.
     */
    public Set<OWLIndividualAxiom> configurePlants(Set<OWLClass> plants) {
        return Collections.emptySet();
    }


}
