package nl.vu.kai.companion.repairs;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.ExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import java.util.Optional;
import java.util.Set;

public class ClassicalRepairGenerator {

    /**
     * Computes a classical repair for the given ontology (assuming it is inconsistent), where it keeps all
     * axioms except for the given flexibleAxioms as unchangeable. In addition to fixing the ontology, it also
     * returns the resulting set of flexibleAxioms that are in the repair;
     * @param ontology
     * @param flexibleAxioms
     * @return
     */
    public <T extends OWLAxiom> Set<T> computeRepair(OWLOntology ontology, Set<T> flexibleAxioms) throws RepairException {

        OWLOntologyManager owlManager = ontology.getOWLOntologyManager();
        OWLDataFactory owlFactory = owlManager.getOWLDataFactory();

        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        while(!reasoner.isConsistent()) {
            ExplanationGenerator explanationGenerator =
                    new DefaultExplanationGenerator(
                            owlManager,
                            reasonerFactory,
                            ontology,
                            new SilentExplanationProgressMonitor()
                    );

            Set<OWLAxiom> explanation = explanationGenerator.getExplanation(owlFactory.getOWLThing());
            Optional<OWLAxiom> optRemove = explanation.stream()
                    .filter(flexibleAxioms::contains)
                    .findAny();

            if(!optRemove.isPresent())
                throw new RepairException("Ontology cannot be repaired by using only the flexible axioms!");
            else {
                OWLAxiom remove = optRemove.get();
                flexibleAxioms.remove(remove);
                ontology.remove(remove);
            }
        }

        return flexibleAxioms;
    }


}
