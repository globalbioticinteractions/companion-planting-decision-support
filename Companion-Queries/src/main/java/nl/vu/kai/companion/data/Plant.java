package nl.vu.kai.companion.data;

import java.util.Objects;
import java.util.Optional;

public class Plant {
    
    private final String iri;
    private Optional<String> name, scientificName;

    public Plant(String iri, Optional<String> name, Optional<String> scientificName) {
        this.iri = iri;
        this.name = name;
        this.scientificName = scientificName;
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
}
