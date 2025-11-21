package TSP;

import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex> {
	@Override
	public int compare(Vertex v1, Vertex v2) {
		return Integer.compare(v1.edge.weight, v2.edge.weight); // Compare vertices by edge weight
	}
}
