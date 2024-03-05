package nl.vu.kai.companion.data;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Edge {
    private String from, to;
    private Plant fromPlant, toPlant;
    private String property;
    private String id;
    

    public Edge(Plant to, Plant from, String prop){
        toPlant = to;
        fromPlant = from;
        property = prop;

        this.to = toPlant.getName().get();
        this.from = fromPlant.getName().get();
        id = this.from.concat(this.to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Plant getFromPlant() {
        return fromPlant;
    }

    public Plant getToPlant() {
        return toPlant;
    }

    public String getProperty() {
        return property;
    }

    public String getId() {
        return id;
    }

    public Set<Node> getNodes(){
        Set<Node> nodes = new HashSet<>();
        nodes.add(new Node(fromPlant));
        nodes.add(new Node(toPlant));
        return nodes;
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
        Edge other = (Edge) obj;
        if (id == null) {
          if (other.id != null)
            return false;
        } else if (!id.equals(other.id))
          return false;
        return true;
      }
}
