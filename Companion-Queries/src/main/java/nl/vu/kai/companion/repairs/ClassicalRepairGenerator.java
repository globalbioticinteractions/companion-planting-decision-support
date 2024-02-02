package nl.vu.kai.companion.repairs;

import nl.vu.kai.companion.util.OWLFormatter;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
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

        OWLAxiom incAxiom = owlFactory.getOWLSubClassOfAxiom(
                owlFactory.getOWLThing(),
                owlFactory.getOWLNothing()
        );

        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        ExplanationGeneratorFactory egFactory =
                new InconsistentOntologyExplanationGeneratorFactory(
                        reasonerFactory,
                        owlFactory,
                        () -> owlManager,
                        1000);

        OWLFormatter formatter = new OWLFormatter(ontology);

        while(!reasoner.isConsistent()) {
            ExplanationGenerator<OWLAxiom> explanationGenerator =
                    egFactory.createExplanationGenerator(ontology);

            Optional<Explanation<OWLAxiom>> optExplanation =
                    explanationGenerator.getExplanations(incAxiom,1)
                            .stream()
                            .findFirst();

            if(!optExplanation.isPresent())
                throw new RepairException("Inconsistency couldn't be explained!");

             Set<OWLAxiom> explanation = optExplanation.get().getAxioms();


            Optional<OWLAxiom> optRemove = explanation.stream()
                    .filter(flexibleAxioms::contains)
                    .findAny();

            if(!optRemove.isPresent())
                throw new RepairException("Ontology cannot be repaired by using only the flexible axioms!");
            else {
                OWLAxiom remove = optRemove.get();
                flexibleAxioms.remove(remove);
                ontology.remove(remove);
                reasoner.flush();
            }
        }

        return flexibleAxioms;
    }


}
