package TSP;

public class Edge {
	Vertex Owner, Child; // Start and end vertices of the edge
	int parent, child, weight; // Parent and child indices, and weight of the edge

	public Edge(Vertex Owner, Vertex Child, int weight) {
		this.Owner = Owner; // Initialize the start vertex
		this.Child = Child; // Initialize the end vertex
		this.weight = weight; // Set the weight of the edge
	}
}
