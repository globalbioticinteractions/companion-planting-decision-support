package nl.vu.kai.companion.cmd;

import nl.vu.kai.companion.CompatibilityChecker;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args){
        if(args.length!=2){
            printHelpAndExit();
        }

        try {
            Set<OWLClass> plants = getPlants(new File(args[1]));

            CompatibilityChecker checker = new CompatibilityChecker();

            switch(args[0]) {
                case "check": check(checker,plants); break;
                case "explain": explain(checker,plants); break;
                case "suggest": suggest(checker,plants); break;
                default:
                    System.out.println("Unknown parameter: "+args[0]);
                    System.out.println();
                    printHelpAndExit();
            }

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find file "+args[1]);
            System.out.println();
            printHelpAndExit();
        } catch (OWLOntologyCreationException e) {
            System.out.println("There was an exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void suggest(CompatibilityChecker checker, Set<OWLClass> plants) {
        System.out.println("Here is a configuration that should work:");
        checker.configurePlants(plants).forEach(System.out::println);
    }

    private static void explain(CompatibilityChecker checker, Set<OWLClass> plants) throws OWLOntologyCreationException {
        boolean compatible = checker.compatible(plants);
        if(compatible){
            System.out.println("Plants are compatible.");
        } else {
            System.out.println("Plants are not compatible.");
            System.out.println();
            System.out.println("Explanation:");
            checker.explainIncompatibility(plants).forEach(System.out::println);
        }
    }

    private static void check(CompatibilityChecker checker, Set<OWLClass> plants) throws OWLOntologyCreationException {
        boolean result = checker.compatible(plants);
        if(result){
            System.out.println("Plants are not compatible.");
        } else
            System.out.println("Plants are compatible.");
    }

    private static Set<OWLClass> getPlants(File file) throws FileNotFoundException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        return reader.lines()
                .map(IRI::create)
                .map(factory::getOWLClass)
                .collect(Collectors.toSet());
    }

    private static void printHelpAndExit() {
        System.out.println("Usage: ");
        System.out.println("java -jar ... "+Main.class.getCanonicalName()+" [check|explain|suggest] FILE_NAME");
        System.out.println();
        System.out.println("FILE_NAME should contain a list of IRIs of plants.");
        System.out.println("'check' checks whether those plants are compatible with each other.");
        System.out.println("'explain' additionally explains why they are not.");
        System.out.println("'suggest' suggests an arrangement of the given plants that is compatible.");
        System.exit(1);
    }
}
