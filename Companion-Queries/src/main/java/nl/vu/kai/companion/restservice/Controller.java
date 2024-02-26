package nl.vu.kai.companion.restservice;

import java.util.concurrent.atomic.AtomicLong;

import java.io.*;
import java.util.*;
import java.util.stream.Collector;
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
import nl.vu.kai.companion.util.OntologyTools;
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
    
	// @GetMapping("/greeting")
	// public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    //     return new Greeting(counter.incrementAndGet(), String.format(template, name));
	// }

    @GetMapping("/getPlants")
    public List<Plant> getPlantNames() throws OWLOntologyCreationException{
        checker = new GardenConfigurationChecker();
        Stream<Plant> plantsStream = checker.plants(); //This is currenlty still empty so I'm using the following as an example to test the rest of functionality
        List<Plant> result = plantsStream.collect(Collectors.toList());
        return result;
    }

    // passing # in the param doesn't work, so the last part of the IRI gets trancuated in the process
    // change it to a post?
    @GetMapping("/getCompanions")
    public List<Plant> getCompanions(@RequestParam(value = "plant", defaultValue = "http://www.semanticweb.org/kai/ontologies/2024/companion-planting#Carrot") String plantString) throws OWLOntologyCreationException{
        
        System.out.println("Plant IRI:" + plantString);

        OWLClass plantClass = checker.asOWLClass(checker.getPlant(plantString));
        OWLOntology ontology = checker.getPlantOntology();
        // ontology.property
        
        OWLObjectProperty property = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty(Configuration.COMPANION_PROPERTY_IRI);
        Collection<OWLClass> answercol = OntologyTools.simpleQuery(checker.getPlantOntology(), plantClass, property);
        
        List<Plant> result = answercol.stream()
                .map(OWLClass::getIRI)
                .map(x -> checker.toPlant(x))
                .collect(Collectors.toList());
            
        return result;
    }

    @PostMapping(value = "/check", consumes = {"*/*"})
    public List<PropertyResponse> check(@RequestBody List<String> selectedPlants) throws OWLOntologyCreationException{
        Set<OWLClass> plantClasses = getPlants(selectedPlants);

        // if (selectedPlants.isEmpty()) {
        //     selectedPlants = testingplants;
        // }
        
        // OWLOntology ontology = checker.getPlantOntology();
        // OWLFormatter formatter = new OWLFormatter(ontology);

        // String output = check(checker,plants);

        List<PropertyResponse> resultList = new ArrayList();
        for(GardenConfigurationProperty property
                : GardenConfigurationProperty.values()) {

            if(property.equals(GardenConfigurationProperty.GARDEN))
                continue; // this is not relevant
            boolean result = checker.checkProperty(plantClasses, property);
            
            resultList.add(new PropertyResponse(property, result));
            
            // if (result) {
            //     System.out.println("Plants satisfy "+property);
            // } else
            //     System.out.println("Plants do not satisfy "+property);
        }

        return resultList;

    }

    @PostMapping(value = "/explain", consumes = {"*/*"})
    public List<String> explain(@RequestBody ExplanationRequest data)
            throws OWLOntologyCreationException {
        
        Set<OWLClass> plantClasses = getPlants(data.getPlantlist());
        OWLFormatter formatter = new OWLFormatter(checker.getPlantOntology());
        List<String> explanationString = new ArrayList<String>();
        
        try {
            System.out.println("I AM HERE! AND THIS IS THE DATA I RECEIVED: "+data.getProperty().toString() +", "+data.getPlantlist().toString());
            
            Set<OWLAxiom> explanation = checker.explainProperty(plantClasses, data.getProperty());
            System.out.println("I RAN THE EXPLANATION AND I GOT "+explanation.size()+" NUMBER OF AXIOMS.");
            // System.out.println("===============================");
            // System.out.println("Explanation for " + explanation + ":");
            // System.out.println("Explanation for " + explanation + ":".replaceAll(".", "="));
            // System.out.println();
            // explanation
            //         .stream()
            //         .map(formatter::format)
            //         .forEach(System.out::println);
            
            // System.out.println("===============================");
            // System.out.println();
            // System.out.println();

            for(OWLAxiom exp:checker.explainProperty(plantClasses, data.getProperty())){
                explanationString.add(formatter.format(exp));
            }
        } catch (IllegalArgumentException ie) {
            System.out.println("====================");
            System.out.println("There was an error getting the explanation.");
            ;
        }

        return explanationString;
    }

    @PostMapping(value = "/suggest", consumes = {"*/*"})
    public List<PlacementSuggestion> suggest(@RequestBody List<String> selectedPlants)
            throws OWLOntologyCreationException, RepairException {

        Set<OWLClass> plantClasses = getPlants(selectedPlants);
        OWLFormatter formatter = new OWLFormatter(checker.getPlantOntology());
        
        // System.out.println("This would be an optimal way to place the plants without having anti companions next to each other:");
        // System.out.println("===============================");
        // System.out.println();
        List<PlacementSuggestion> plantconfig = new ArrayList<PlacementSuggestion>();

        for(OWLIndividualAxiom axiom:checker.organizePlants(plantClasses)){
            String ax = formatter.format(axiom);
            plantconfig.add(new PlacementSuggestion(ax));
            // System.out.println(formatter.format(axiom));
        }
        
        // System.out.println("===============================");
        // System.out.println();
        // System.out.println();

        return plantconfig;
    }

    private Set<OWLClass> getPlants(List<String> selectedPlants) {
        Set<OWLClass> plantClasses = new HashSet<OWLClass>();

        for(String iri : selectedPlants) {
            plantClasses.add(checker.asOWLClass(checker.getPlant(iri)));
        }
        return plantClasses;
    }

}