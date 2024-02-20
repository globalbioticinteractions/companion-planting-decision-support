package nl.vu.kai.companion;

public class Configuration {
    private Configuration() {
    } // utility class

    public static final String ONTOLOGY_PATH="../owl/companion_planting-with-tablev3.owl";

    public static final String IRI_PREFIX="http://www.semanticweb.org/kai/ontologies/2024/";

    public static final String NEIGHBOUR_IRI =IRI_PREFIX+"neighbour";
    public static final String GARDEN_IRI=IRI_PREFIX+"Garden";
    public static final String GARDEN_RELATION_IRI =IRI_PREFIX+"containsFlora";

    public enum GardenConfigurationProperty {
        BAD_GARDEN("BadGarden",-1),
        GARDEN("Garden",0),
        GOOD_GARDEN_1("CompanionGarden",1),
        GOOD_GARDEN_2("3CompanionGarden",2),
        GOOD_GARDEN_3("3TripleCompanionGarden",3)
        ;
        public final String iri, name;
        public final int rating;
        private GardenConfigurationProperty(String name, int rating){
            this.name=name;
            this.iri=IRI_PREFIX+name;
            this.rating =rating;
        }
        @Override
        public String toString() { return name; }
    }
}
