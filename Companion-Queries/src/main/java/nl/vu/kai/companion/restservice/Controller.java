package nl.vu.kai.companion.restservice;

import java.util.concurrent.atomic.AtomicLong;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

// import nl.vu.kai.companion.restservice.Greeting;

import nl.vu.kai.companion.GardenConfigurationChecker;
import nl.vu.kai.companion.util.OWLFormatter;
import rationals.properties.isEmpty;
import nl.vu.kai.companion.Configuration;
import nl.vu.kai.companion.Configuration.*;
import nl.vu.kai.companion.repairs.RepairException;
import nl.vu.kai.companion.data.*;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class Controller{

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
    private static GardenConfigurationChecker checker; // = new GardenConfigurationChecker();
    private static List<String> testingplants = Arrays.asList("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot","http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint","http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot");
    
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

    @GetMapping("/getPlants")
    public List<Plant> getPlantNames() throws OWLOntologyCreationException{
        checker = new GardenConfigurationChecker();
        Stream<Plant> plantsStream = checker.plants(); //This is currenlty still empty so I'm using the following as an example to test the rest of functionality
        List<Plant> result = plantsStream.collect(Collectors.toList());

        //remove once checker.plants() works!
        // result.add(new Plant("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot", Optional.of("Carrot"), Optional.of("Daucus Carota")));
        // result.add(new Plant("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Mint", Optional.of("Mint"), Optional.of("Mentha")));
        // result.add(new Plant("http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Shallot", Optional.of("Shallot"), Optional.of("Allium Ascalonicum")));

        // System.out.println(result); //This is currently still empty for some reason...
        return result;
    }

    @PostMapping(value = "/check", consumes = {"*/*"})
    public List<PropertyResult> check(@RequestBody List<String> selectedPlants) throws OWLOntologyCreationException{
        Set<OWLClass> plantClasses = new HashSet<OWLClass>();

        if (selectedPlants.isEmpty()) {
            selectedPlants = testingplants;
        }

        for(String iri : selectedPlants) {
            plantClasses.add(checker.asOWLClass(checker.getPlant(iri)));
        }


        // OWLOntology ontology = checker.getPlantOntology();
        // OWLFormatter formatter = new OWLFormatter(ontology);

        // String output = check(checker,plants);

        List<PropertyResult> resultList = new ArrayList();
        for(GardenConfigurationProperty property
                : GardenConfigurationProperty.values()) {

            if(property.equals(GardenConfigurationProperty.GARDEN))
                continue; // this is not relevant
            boolean result = checker.checkProperty(plantClasses, property);
            
            resultList.add(new PropertyResult(property, result));
            
            // if (result) {
            //     System.out.println("Plants satisfy "+property);
            // } else
            //     System.out.println("Plants do not satisfy "+property);
        }

        return resultList;

    }
            

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