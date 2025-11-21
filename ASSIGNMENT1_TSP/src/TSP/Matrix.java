package TSP;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Matrix {
	private Map<String, Map<String, Integer>> adjacencyMatrix; // Stores adjacency matrix

	public Matrix(String filePath) throws IOException {
		// Initialize Gson for reading JSON
		Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Enable pretty printing for better output
		Type matrixType = new TypeToken<Map<String, Map<String, Integer>>>() {
		}.getType();

		try (FileReader reader = new FileReader(filePath)) {
			adjacencyMatrix = gson.fromJson(reader, matrixType); // Load adjacency matrix from the JSON file
		}
	}

	// Retrieve distance between two nodes
	public int getDistance(String from, String to) {
		Map<String, Integer> distancesFrom = adjacencyMatrix.get(from); // Get distances for the starting node
		if (distancesFrom == null || distancesFrom.get(to) == null) {
			return Integer.MAX_VALUE; // Return max value if no connection exists
		}
		return distancesFrom.get(to); // Return the distance to the target node
	}

	// Get all nodes in the matrix
	public Set<String> getNodes() {
		return adjacencyMatrix.keySet(); // Return all keys (nodes) in the adjacency matrix
	}

	// Print the adjacency matrix
	public void printMatrix() {
		for (String from : adjacencyMatrix.keySet()) { // Iterate over all source nodes
			System.out.println("From " + from + ":");
			for (String to : adjacencyMatrix.get(from).keySet()) { // Iterate over target nodes for each source
				System.out.println("  To " + to + " = " + adjacencyMatrix.get(from).get(to) + " units");
			}
		}
	}
}
