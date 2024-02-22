package nl.vu.kai.companion.restservice;

import java.util.concurrent.atomic.AtomicLong;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import nl.vu.kai.companion.restservice.Greeting;

import nl.vu.kai.companion.GardenConfigurationChecker;
import nl.vu.kai.companion.util.OWLFormatter;
import nl.vu.kai.companion.Configuration;
import nl.vu.kai.companion.Configuration.*;
import nl.vu.kai.companion.repairs.RepairException;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class Controller {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

    
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

    // @GetMapping("/check")
    // public void callcheck(@RequestParam(value = "filename", defaultValue = "test.list") String filename) throws FileNotFoundException, OWLOntologyCreationException{
    //     Set<OWLClass> plants = getPlants(new File(filename));
    //     GardenConfigurationChecker checker = new GardenConfigurationChecker();

    //     OWLOntology ontology = checker.getPlantOntology();
    //     // OWLFormatter formatter = new OWLFormatter(ontology);

    //     check(checker,plants);

    // }
            

    // private static void check(GardenConfigurationChecker checker, Set<OWLClass> plants) throws OWLOntologyCreationException {
    //     for(GardenConfigurationProperty property
    //             : GardenConfigurationProperty.values()) {

    //         if(property.equals(GardenConfigurationProperty.GARDEN))
    //             continue; // this is not relevant
    //         boolean result = checker.checkProperty(plants, property);
    //         if (result) {
    //             System.out.println("Plants satisfy "+property);
    //         } else
    //             System.out.println("Plants do not satisfy "+property);
    //     }
    // }

    // private static void explain(GardenConfigurationChecker checker, Set<OWLClass> plants, OWLFormatter formatter)
    //         throws OWLOntologyCreationException {
    //     for(GardenConfigurationProperty property: GardenConfigurationProperty.values()) {
    //         if(property.equals(GardenConfigurationProperty.GARDEN))
    //             continue; // this is not relevant
    //         try {
    //             Set<OWLAxiom> explanation = checker.explainProperty(plants, property);

    //             System.out.println("Explanation for "+explanation+":");
    //             System.out.println("Explanation for "+explanation+":".replaceAll(".","="));
    //             System.out.println();
    //             explanation
    //                     .stream()
    //                     .map(formatter::format)
    //                     .forEach(System.out::println);
    //             System.out.println();
    //             System.out.println();
    //         } catch(IllegalArgumentException ie) {
    //             ;
    //         }
    //     }
    // }

    // private static void suggest(GardenConfigurationChecker checker, Set<OWLClass> plants, OWLFormatter formatter)
    //         throws OWLOntologyCreationException, RepairException {

    //     System.out.println("This would be an optimal way to place the plants without having anti companions next to each other:");
    //     System.out.println("===============================");
    //     System.out.println();
    //     for(OWLIndividualAxiom axiom:checker.organizePlants(plants)){
    //         System.out.println(formatter.format(axiom));
    //     }
    // }

    // private static Set<OWLClass> getPlants(File file) throws FileNotFoundException {
    //     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    //     OWLDataFactory factory = manager.getOWLDataFactory();

    //     BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

    //     return reader.lines()
    //             .map(IRI::create)
    //             .map(factory::getOWLClass)
    //             .collect(Collectors.toSet());
    // }

    // private static void printHelpAndExit() {
    //     System.out.println("Usage: ");
    //     System.out.println("java -jar ... "+Main.class.getCanonicalName()+" [check|explain|suggest] FILE_NAME");
    //     System.out.println();
    //     System.out.println("FILE_NAME should contain a list of IRIs of plants.");
    //     System.out.println("'check' checks which configurations those plants would satisfy if all put together.");
    //     System.out.println("'explain' additionally explains why.");
    //     System.out.println("'suggest' suggests an arrangement of the given plants such that no incompatible " +
    //             "plants are next to each other, while the best positive configuration is reached.");
    //     System.exit(1);
    // }
}