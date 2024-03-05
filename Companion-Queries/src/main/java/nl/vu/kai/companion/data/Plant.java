package nl.vu.kai.companion.data;

import java.util.Objects;
import java.util.Optional;

public class Plant {
    
    private final String iri;
    private Optional<String> name, scientificName, wikilink;

    public Plant(String iri, Optional<String> name, Optional<String> scientificName, Optional<String> wikilink) {
        this.iri = iri;
        this.name = name;
        this.scientificName = scientificName;
        this.wikilink = wikilink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return Objects.equals(iri, plant.iri);
    }

    @Override
    public int hashCode() {
        return iri.hashCode();
    }

    public String getIri() {
        return iri;
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<String> getScientificName() {
        return scientificName;
    }

    public Optional<String> getWikilink() {
        return wikilink;
    }
}
