package nl.vu.kai.companion.data;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Node {
    private String id;
    private Plant plant;
    private String group;
    private Optional<String> scientificname, wikilink;
    // private String wikilink;
    

    public Node(Plant plant){
        this.plant = plant;
        id = plant.getName().get();
        group = "default";
        this.scientificname = plant.getScientificName();
        this.wikilink = plant.getWikilink();
    }

    public void setGroup(String g){
        this.group = g;
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public Plant getPlant() {
        return plant;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        Node other = (Node) obj;
        if (id == null) {
          if (other.id != null)
            return false;
        } else if (!id.equals(other.id))
          return false;
        return true;
      }
}
