package TSP;

import java.util.*;

public class PrimsAlgorithm {

	private PrimsAlgorithm() {
		// Prevent instantiation as this is a utility class
	}

	/**
	 * Executes Prim's Algorithm to find the Minimum Spanning Tree (MST).
	 * 
	 * @param unsortedList List of vertices to process.
	 * @param matrix       The adjacency matrix for distances between vertices.
	 * @param indexMap     A mapping of vertex names to their indices.
	 * @return A list of vertices representing the MST.
	 */
	public static List<Vertex> run(List<Vertex> unsortedList, Matrix matrix, Map<String, Integer> indexMap) {
		Comparator<Vertex> compareMethod = new VertexComparator(); // Comparator for sorting vertices
		final Vertex baseVertex = unsortedList.get(0); // Start with the first vertex as the base

		// Initialize edges for all vertices based on their distance to the base vertex
		unsortedList.forEach(vertex -> {
			int distance = matrix.getDistance(baseVertex.getName(), vertex.getName()); // Calculate distance to base
																						// vertex
			vertex.edge = new Edge(vertex, baseVertex, distance); // Assign edge with the calculated distance
		});
		Collections.sort(unsortedList, compareMethod); // Sort vertices by edge weight

		List<Vertex> sortedList = new ArrayList<>(); // List to hold vertices in MST order
		Set<String> addedEdges = new HashSet<>(); // Track edges to prevent duplicates

		while (!unsortedList.isEmpty()) {
			Vertex pointConnected = unsortedList.get(0); // Get the vertex with the smallest edge weight
			Edge parentEdge = pointConnected.edge; // Store the parent edge
			String edgeKey = getEdgeKey(parentEdge); // Generate a unique key for the edge

			if (!addedEdges.contains(edgeKey)) {
				pointConnected.connectedVertices.add(parentEdge); // Add the edge to its connections
				addedEdges.add(edgeKey); // Mark the edge as added
			}

			if (!addedEdges.contains(getReverseEdgeKey(parentEdge))) {
				Edge reverseEdge = new Edge(parentEdge.Child, parentEdge.Owner, parentEdge.weight); // Create reciprocal
																									// edge
				parentEdge.Child.connectedVertices.add(reverseEdge); // Add reciprocal edge to the other vertex
				addedEdges.add(getReverseEdgeKey(parentEdge)); // Mark reciprocal edge as added
			}

			sortedList.add(pointConnected); // Add the current vertex to the sorted list
			unsortedList.remove(pointConnected); // Remove it from the unsorted list
			Collections.sort(unsortedList, compareMethod); // Re-sort the remaining vertices

			// Update edges for the remaining unsorted vertices
			if (!unsortedList.isEmpty()) {
				Vertex basedVertex = unsortedList.get(0); // Select the next base vertex
				unsortedList.forEach(vertex -> {
					if (vertex != basedVertex) {
						int distance = matrix.getDistance(vertex.getName(), basedVertex.getName()); // Calculate
																									// distance to base
																									// vertex
						if (distance < vertex.edge.weight) {
							vertex.edge = new Edge(vertex, basedVertex, distance); // Update edge if the new distance is
																					// shorter
						}
					}
				});
			}
		}
		return sortedList; // Return the sorted list representing the MST
	}

	// Helper method to generate a unique key for an edge
	private static String getEdgeKey(Edge edge) {
		return edge.Owner.getName() + "-" + edge.Child.getName(); // Forward key
	}

	// Helper method to generate a unique key for the reverse of an edge
	private static String getReverseEdgeKey(Edge edge) {
		return edge.Child.getName() + "-" + edge.Owner.getName(); // Reverse key
	}
}
