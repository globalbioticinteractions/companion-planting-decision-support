package nl.vu.kai.companion.restservice;

import java.util.List;
import nl.vu.kai.companion.data.*;

public class CompanionResponse {
    
    public final List<Node> nodes;
    public final List<Edge> edges;

    public CompanionResponse(List<Node> nodes, List<Edge> edges){
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
