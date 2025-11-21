package TSP;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
	private String name; // Name of the vertex
	List<Edge> connectedVertices = new ArrayList<>(); // List of edges connected to the vertex
	Edge edge; // Current edge of the vertex

	public Vertex(String name) {
		this.name = name; // Initialize the vertex with its name
	}

	public String getName() {
		return name; // Return the name of the vertex
	}
}
