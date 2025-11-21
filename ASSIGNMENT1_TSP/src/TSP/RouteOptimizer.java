package TSP;

import java.util.*;

public class RouteOptimizer {
	// Optimize the given route list using Dijkstra's algorithm
	public static List<String> optimizeRoute(List<String> routeList, Matrix matrix) {
		List<String> optimizedRoute = new ArrayList<>(); // List to store the optimized route

		for (int i = 0; i < routeList.size() - 1; i++) {
			String start = routeList.get(i); // Starting node
			String end = routeList.get(i + 1); // Ending node
			List<String> segment = findShortestPath(start, end, matrix); // Find the shortest path for the segment

			if (!optimizedRoute.isEmpty()) {
				segment.remove(0); // Avoid duplicate starting node
			}
			optimizedRoute.addAll(segment); // Append the segment to the optimized route
		}

		int totalLength = 0;
		for (int i = 0; i < optimizedRoute.size() - 1; i++) {
			totalLength += matrix.getDistance(optimizedRoute.get(i), optimizedRoute.get(i + 1));
		}
		System.out.println("Total Route Length: " + totalLength + " units");

		return optimizedRoute; // Return the full optimized route
	}

	// Find the shortest path between two nodes using Dijkstra's algorithm
	public static List<String> findShortestPath(String start, String end, Matrix matrix) {
		Map<String, Integer> distances = new HashMap<>(); // Map to store distances from start node
		Map<String, String> previous = new HashMap<>(); // Map to store the previous node in the path
		Set<String> visited = new HashSet<>(); // Set of visited nodes
		PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance)); // Priority
																										// queue for
																										// Dijkstra's
																										// algorithm
		List<String> allNodes = new ArrayList<>(matrix.getNodes()); // Get all nodes from the matrix

		// Initialize distances and previous nodes
		for (String node : allNodes) {
			distances.put(node, Integer.MAX_VALUE); // Set initial distance to infinity
			previous.put(node, null); // Set no previous node
		}
		distances.put(start, 0); // Distance to the start node is zero
		pq.add(new NodeDistance(start, 0)); // Add the start node to the priority queue

		// Dijkstra's algorithm main loop
		while (!pq.isEmpty()) {
			NodeDistance current = pq.poll(); // Get the node with the smallest distance

			if (visited.contains(current.node))
				continue; // Skip if the node is already visited
			visited.add(current.node); // Mark the node as visited

			// Update distances for neighbors
			for (String neighbor : allNodes) {
				if (!visited.contains(neighbor)) {
					int distance = matrix.getDistance(current.node, neighbor); // Get the distance to the neighbor
					if (distance < Integer.MAX_VALUE) { // If the neighbor is reachable
						int newDist = distances.get(current.node) + distance; // Calculate the new distance
						if (newDist < distances.get(neighbor)) {
							distances.put(neighbor, newDist); // Update the distance
							previous.put(neighbor, current.node); // Update the previous node
							pq.add(new NodeDistance(neighbor, newDist)); // Add the neighbor to the queue
						}
					}
				}
			}
		}

		// Reconstruct the shortest path from end to start
		List<String> path = new LinkedList<>();
		for (String at = end; at != null; at = previous.get(at)) {
			path.add(0, at); // Add nodes to the path in reverse order
		}
		return path; // Return the reconstructed path
	}

	// Helper class to store nodes with their distances for the priority queue
	static class NodeDistance {
		String node; // Node name
		int distance; // Distance from the start node

		public NodeDistance(String node, int distance) {
			this.node = node;
			this.distance = distance;
		}
	}
}
