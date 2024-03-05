package nl.vu.kai.companion.repairs;

import com.clarkparsia.owlapi.explanation.DefaultExplanationGenerator;
import com.clarkparsia.owlapi.explanation.util.SilentExplanationProgressMonitor;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassicalRepairGenerator {


    /**
     * Compute a classical repair with the following constraints:
     * - only the flexible axioms can be removed
     * - the given "keepEntailments" have to be kept as entailments
     */
    public <T extends OWLAxiom> Optional<Set<T>> computeRepair(
            OWLOntology ontology,
            T undesiredAxiom,
            Set<T> flexibleAxioms,
            Set<OWLAxiom> keepEntailments) {

        System.out.println("Trying to repair "+undesiredAxiom);

        OWLOntologyManager owlManager = ontology.getOWLOntologyManager();
        OWLDataFactory owlFactory = owlManager.getOWLDataFactory();


        OWLReasonerFactory reasonerFactory = new ReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
        /*ExplanationGeneratorFactory egFactory =
                new InconsistentOntologyExplanationGeneratorFactory(
                        reasonerFactory,
                        owlFactory,
                        () -> owlManager,
                        1000);
        */
        OWLFormatter formatter = new OWLFormatter(ontology);

        return innerRepair(ontology,flexibleAxioms,keepEntailments,reasoner, reasonerFactory, formatter, undesiredAxiom);
    }

    private <T extends OWLAxiom> Optional<Set<T>> innerRepair(
            OWLOntology ontology,
            Set<T> flexibleAxioms,
            Set<OWLAxiom> keepEntailments,
            OWLReasoner reasoner,
            OWLReasonerFactory reasonerFactory,
            OWLFormatter formatter,
            OWLAxiom toRemove)  {

        reasoner.flush();
        if(!reasoner.isEntailed(toRemove)){
            System.out.println("Done repairing!");
            // the repair is successfull - we can return
            return Optional.of(Collections.emptySet());
        }
        if(!keepEntailments.stream().allMatch(reasoner::isEntailed)){
            System.out.println("Failure repairing.");
            // this branch has failed - we need to backtrack
            return Optional.empty();
        }

        /*ExplanationGenerator<OWLAxiom> explanationGenerator =
                egFactory.createExplanationGenerator(ontology);

        Optional<Explanation<OWLAxiom>> optExplanation =
                explanationGenerator.getExplanations(toRemove,1).stream().findAny();

        assert optExplanation.isPresent() : "Inconsistency couldn't be explained!";
        Collection<OWLAxiom> candidates = optExplanation.get()
                .getAxioms();

        */
        DefaultExplanationGenerator explanationGenerator =
                new DefaultExplanationGenerator(ontology.getOWLOntologyManager(), reasonerFactory, ontology,
                        new SilentExplanationProgressMonitor());

        Collection<OWLAxiom> candidates = explanationGenerator.getExplanation(toRemove);

        Set<T> remove = new HashSet<>();

        boolean success=false;
        while(!success && !candidates.isEmpty()){
            OWLAxiom candidate = candidates.iterator().next();
            System.out.println("Can we remove "+candidate+"?");
            candidates.remove(candidate);
            if(flexibleAxioms.contains(candidate)){
                System.out.println("Yes we can and try!");
                ontology.remove(candidate);
                Optional<Set<T>> opt =
                        innerRepair(ontology,flexibleAxioms,keepEntailments,reasoner,reasonerFactory,formatter,toRemove);
                if(opt.isPresent()){
                    System.out.println("Removing "+candidate+" worked!");
                    success=true;
                    remove = new HashSet<>(opt.get());
                    remove.add((T)candidate);
                } else {
                    System.out.println("Removing "+candidate+" did not work.");
                    ontology.add(candidate);
                }
            }
        }

        if(!success){
            return Optional.empty();
        } else
            return Optional.of(remove);
    }

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

        //OWLFormatter formatter = new OWLFormatter(ontology);

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
