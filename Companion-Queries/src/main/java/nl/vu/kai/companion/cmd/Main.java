package nl.vu.kai.companion.cmd;

import nl.vu.kai.companion.Configuration;
import nl.vu.kai.companion.Configuration.*;
import nl.vu.kai.companion.GardenConfigurationChecker;
import nl.vu.kai.companion.repairs.RepairException;
import nl.vu.kai.companion.util.OWLFormatter;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args){
        if(args.length!=2){
            printHelpAndExit();
        }

        try {
            Set<OWLClass> plants = getPlants(new File(args[1]));

            GardenConfigurationChecker checker = new GardenConfigurationChecker();

            OWLOntology ontology = checker.getPlantOntology();
            OWLFormatter formatter = new OWLFormatter(ontology);

            switch(args[0]) {
                case "check": check(checker,plants); break;
                case "explain": explain(checker,plants,formatter); break;
                case "suggest": suggest(checker,plants,formatter); break;
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
        } catch (RepairException e) {
            System.out.println("There was an exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void check(GardenConfigurationChecker checker, Set<OWLClass> plants) throws OWLOntologyCreationException {
        for(GardenConfigurationProperty property
                : GardenConfigurationProperty.values()) {

            if(property.equals(GardenConfigurationProperty.GARDEN))
                continue; // this is not relevant
            boolean result = checker.checkProperty(plants, property);
            if (result) {
                System.out.println("Plants satisfy "+property);
            } else
                System.out.println("Plants do not satisfy "+property);
        }
    }

    private static void explain(GardenConfigurationChecker checker, Set<OWLClass> plants, OWLFormatter formatter)
            throws OWLOntologyCreationException {
        for(GardenConfigurationProperty property: GardenConfigurationProperty.values()) {
            if(property.equals(GardenConfigurationProperty.GARDEN) || !checker.checkProperty(plants,property))
                continue; // this is not relevant
            try {
                System.out.println("Explanation for "+property+":");
                System.out.println(("Explanation for "+property+":").replaceAll(".","="));
                System.out.println();

                Set<OWLAxiom> explanation = checker.explainProperty(plants, property);

                explanation
                        .stream()
                        .map(formatter::format)
                        .forEach(System.out::println);
                System.out.println();
                System.out.println();
            } catch(IllegalArgumentException ie) {
                ;
            }
        }
    }

    private static void suggest(GardenConfigurationChecker checker, Set<OWLClass> plants, OWLFormatter formatter)
            throws OWLOntologyCreationException, RepairException {

        System.out.println("This would be an optimal way to place the plants without having anti companions next to each other:");
        System.out.println("===============================");
        System.out.println();
        for(OWLIndividualAxiom axiom:checker.organizePlants(plants)){
            System.out.println(formatter.format(axiom));
        }
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
        System.out.println("'check' checks which configurations those plants would satisfy if all put together.");
        System.out.println("'explain' additionally explains why.");
        System.out.println("'suggest' suggests an arrangement of the given plants such that no incompatible " +
                "plants are next to each other, while the best positive configuration is reached.");
        System.exit(1);
    }
}
