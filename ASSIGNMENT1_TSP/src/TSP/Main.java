package TSP;

import java.io.IOException;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		String startpoint, endpoint; // Variables to hold the start and endpoint of the route
		int pickupAmount, dropoffAmount; // Number of pickup and drop-off zones
		List<String> routeList = new ArrayList<>(); // List to store the route

		Scanner kb = new Scanner(System.in); // Scanner for user input

		try {
			// Load the adjacency matrix from a JSON file
			Matrix matrix = new Matrix("src/TSP/AdjacantMatrix.json");

			System.out.println("\n------ Task 1+2: Create Adjacency Matrix and Display Adjacency Matrix ------\n");
			System.out.println("Adjacency Matrix Loaded:");
			matrix.printMatrix(); // Print the adjacency matrix

			// Task 3: Get the start and endpoint from the user
			System.out.println("\n------ Task 3: Set Start and End Points ------\n");
			startpoint = getValidNode("What is your Startpoint? A - Z", matrix, kb); // Prompt for a valid start node
			endpoint = getValidNode("What is your endpoint? A - Z", matrix, kb); // Prompt for a valid endpoint
			routeList.add(startpoint); // Add the start point to the route list

			// Task 3: Define pickup zones
			System.out.println("\n------ Task 3: Define Pick-Up and Drop-Off Zones ------\n");
			pickupAmount = getValidInteger("How many pick-up zones do you want?", kb); // Get the number of pickup zones

			if (pickupAmount != 0) {
				for (int i = 0; i < pickupAmount; i++) {
					String pickup = getValidNode("Which pick-up zone do you want?", matrix, kb); // Get each pickup zone
					routeList.add(pickup); // Add it to the route list
				}
			}

			// Define drop-off zones
			dropoffAmount = getValidInteger("How many drop-off zones do you want?", kb); // Get the number of drop-off
																							// zones

			if (dropoffAmount != 0) {
				for (int i = 0; i < dropoffAmount; i++) {
					String dropoff = getValidNode("Which drop-off zone do you want?", matrix, kb); // Get each drop-off
																									// zone
					routeList.add(dropoff); // Add it to the route list
				}
			}

			routeList.add(endpoint); // Add the endpoint to the route list

			// Create an index map for the route
			Map<String, Integer> indexMap = createIndexMap(routeList);

			// Task 4: Optimize the route using Dijkstra's algorithm
			System.out.println("\n------ Task 4: Optimizing Route using Dijkstra ------\n");
			List<String> optimizedRoute = RouteOptimizer.optimizeRoute(routeList, matrix); // Optimize the route
			if (optimizedRoute == null || optimizedRoute.isEmpty()) {
				System.err.println("Error: Optimized route is empty. Please check the adjacency matrix or input.");
				return; // Exit if optimization fails
			}
			System.out.println("Optimized Route: " + String.join(" -> ", optimizedRoute));

			// Task 5: Construct the Minimum Spanning Tree (MST)
			System.out.println("\n------ Task 5: Constructing Minimum Spanning Tree (MST) ------\n");
			System.out.println("Constructing Minimum Spanning Tree (MST)...");

			List<Vertex> vertices = initializeVertices(routeList); // Initialize vertices for the MST
			List<Vertex> mst = PrimsAlgorithm.run(vertices, matrix, indexMap); // Run Prim's Algorithm

			if (mst == null || mst.isEmpty()) {
				System.err.println("Error: Minimum Spanning Tree is empty.");
				return; // Exit if MST construction fails
			}
			System.out.println("MST completed. Here are the edges:");
			List<Edge> uniqueEdges = getUniqueEdges(mst); // Get unique edges
			printMST(mst); // Print the MST edges
			GraphVisualizer.displayGraph(mst, uniqueEdges, routeList, "Minimum Spanning Tree (MST)");

			// Task 6: Identify vertices with odd degrees
			System.out.println("\n------ Task 6: Identifying Odd Degree Vertices ------\n");
			List<Vertex> oddDegreeNodes = findOddDegreeNodes(mst); // Find vertices with odd degrees

			if (oddDegreeNodes == null || oddDegreeNodes.isEmpty()) {
				System.err.println("Error: No odd-degree vertices found.");
			} else {
				System.out.println("Vertices with an odd degree: " + getVertexNames(oddDegreeNodes));
			}

			// Task 7: Calculate the Minimum Weight Perfect Matching (MWPM)
			System.out.println("\n------ Task 7: Calculating Minimum Weight Perfect Matching (MWPM) ------\n");
			List<Edge> mwpm = findMWPM(oddDegreeNodes, matrix, indexMap); // Find the MWPM edges

			if (mwpm == null || mwpm.isEmpty()) {
				System.err.println("Error: Minimum Weight Perfect Matching is empty.");
			} else {
				System.out.println("MWPM completed. Here are the edges:");
				printEdges(mwpm); // Print the MWPM edges
			}

			// Task 8: Create a multigraph by combining MST and MWPM
			System.out.println("\n------ Task 8: Creating Multigraph ------\n");
			List<Edge> multigraph = createMultigraph(mst, mwpm); // Combine MST and MWPM to create the multigraph
			if (multigraph == null || multigraph.isEmpty()) {
				System.err.println("Error: Multigraph is empty.");
			} else {
				System.out.println("Multigraph completed. Here are the edges:");
				printEdges(multigraph); // Print the multigraph edges
				List<Vertex> multigraphVertices = initializeVertices(routeList); // Initialize vertices for the
																					// multigraph
				GraphVisualizer.displayGraph(multigraphVertices, multigraph, routeList, "Multigraph Visualization"); // Visualize
																														// the
																														// multigraph
			}

			// Task 9: Construct a Hamiltonian Circuit
			System.out.println("\n------ Task 9: Constructing Hamiltonian Circuit (Optimal Route) ------\n");
			List<Vertex> allVertices = initializeVertices(routeList);
			List<Vertex> optimalRoute = createHamiltonianCircuit(multigraph, allVertices);
			if (optimalRoute == null || optimalRoute.isEmpty()) {
				System.err.println("Error: Hamiltonian Circuit could not be created.");
			} else {
				optimalRoute.add(optimalRoute.get(0));

				int totalCircuitLength = 0; // Count total length here
				for (int i = 0; i < optimalRoute.size() - 1; i++) {
					Vertex current = optimalRoute.get(i);
					Vertex next = optimalRoute.get(i + 1);
					totalCircuitLength += matrix.getDistance(current.getName(), next.getName());
				}
				System.out.println("Total Circuit Length: " + totalCircuitLength + " units");

				printOptimalRoute(optimalRoute); // Print the optimal route
				System.out.println("Hamiltonian Circuit completed.");
			}

			// Task 10: Display the final route graphically
			System.out.println("\n------ Task 10: Displaying Route Graphically ------\n");
			List<Edge> optimalRouteEdges = new ArrayList<>();
			if (optimalRoute != null && !optimalRoute.isEmpty()) {
				for (int i = 0; i < optimalRoute.size() - 1; i++) {
					Vertex current = optimalRoute.get(i); // Get the current vertex
					Vertex next = optimalRoute.get(i + 1); // Get the next vertex
					int distance = matrix.getDistance(current.getName(), next.getName()); // Calculate the distance
					optimalRouteEdges.add(new Edge(current, next, distance)); // Add the edge to the route
				}
				GraphVisualizer.displayGraph(optimalRoute, optimalRouteEdges, routeList,
						"Optimal Route (Hamiltonian Circuit)"); // Visualize the optimal route
			}
		} catch (IOException e) {
			System.err.println("Error loading adjacency matrix file: " + e.getMessage()); // Handle file I/O errors
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage()); // Handle any other errors
		} finally {
			kb.close(); // Close the scanner to release resources
		}
	}

	// Helper method to get a valid node input
	private static String getValidNode(String prompt, Matrix matrix, Scanner kb) {
		while (true) {
			System.out.print(prompt + " ");
			String node = kb.next().toUpperCase(); // Convert input to uppercase

			if (matrix.getNodes().contains(node)) {
				return node; // Return if valid
			} else {
				System.err.println("Invalid node. Please enter a valid node from A - Z.");
			}
		}
	}

	// Helper method to get a valid integer input
	private static int getValidInteger(String prompt, Scanner kb) {
		while (true) {
			System.out.print(prompt + " ");
			try {
				int value = kb.nextInt(); // Read the integer
				if (value >= 0) {
					return value; // Return if non-negative
				} else {
					System.err.println("Value must be non-negative. Try again.");
				}
			} catch (InputMismatchException e) {
				System.err.println("Invalid input. Please enter a valid integer.");
				kb.next(); // Clear invalid input
			}
		}
	}

	// Initialize vertices based on the route list
	private static List<Vertex> initializeVertices(List<String> routeList) {
		List<Vertex> vertices = new ArrayList<>();
		for (String name : routeList) {
			vertices.add(new Vertex(name)); // Create a vertex for each name
		}
		return vertices;
	}

	// Print the edges of the MST
	private static void printMST(List<Vertex> mst) {
		Set<String> printedEdges = new HashSet<>(); // To track printed edges

		for (Vertex vertex : mst) {
			for (Edge edge : vertex.connectedVertices) {
				String edgeKey = edge.Owner.getName() + "-" + edge.Child.getName();
				String reverseKey = edge.Child.getName() + "-" + edge.Owner.getName();

				if (!printedEdges.contains(edgeKey) && !printedEdges.contains(reverseKey)) {
					// Print the edge if neither direction has been printed
					System.out.println("Edge from " + edge.Owner.getName() + " to " + edge.Child.getName()
							+ " with weight " + edge.weight);
					printedEdges.add(edgeKey); // Mark this edge as printed
				}
			}
		}
	}

	public static List<Vertex> findOddDegreeNodes(List<Vertex> mst) {
		List<Vertex> oddDegreeNodes = new ArrayList<>();

		// Calculate the degree of each vertex in the MST
		for (Vertex vertex : mst) {
			int degree = vertex.connectedVertices.size(); // Degree is the count of connected edges
			if (degree % 2 != 0) { // Check if the degree is odd
				oddDegreeNodes.add(vertex);
			}
		}
		return oddDegreeNodes;
	}

	// Calculate Minimum Weight Perfect Matching (MWPM)
	public static List<Edge> findMWPM(List<Vertex> oddDegreeNodes, Matrix matrix, Map<String, Integer> indexMap) {
		List<Edge> matching = new ArrayList<>();

		while (!oddDegreeNodes.isEmpty()) {
			Vertex v1 = oddDegreeNodes.remove(0); // Remove and get the first vertex
			Vertex bestMatch = null;
			int minDistance = Integer.MAX_VALUE;

			// Find the closest match for v1
			for (Vertex v2 : oddDegreeNodes) {
				int distance = matrix.getDistance(v1.getName(), v2.getName());
				if (distance < minDistance) {
					minDistance = distance;
					bestMatch = v2;
				}
			}

			if (bestMatch != null) {
				// Add the matched pair as an edge
				matching.add(new Edge(v1, bestMatch, minDistance));
				oddDegreeNodes.remove(bestMatch); // Remove the matched vertex
			}
		}
		return matching;
	}

	// Create a map of vertices with their indices
	private static Map<String, Integer> createIndexMap(List<String> routeList) {
		Map<String, Integer> indexMap = new HashMap<>();
		for (int i = 0; i < routeList.size(); i++) {
			indexMap.put(routeList.get(i), i); // Map each vertex name to its index
		}
		return indexMap;
	}

	// Create a multigraph by combining MST and MWPM
	public static List<Edge> createMultigraph(List<Vertex> mst, List<Edge> mwpm) {
		List<Edge> multigraph = new ArrayList<>();
		Map<String, Integer> degreeCount = new HashMap<>(); // Track degree of each node
		Set<String> seenEdges = new HashSet<>(); // Avoid duplicate edges

		// Initialize degree count for all nodes in MST
		for (Vertex vertex : mst) {
			degreeCount.put(vertex.getName(), 0);
		}
		// Add edges from MST to the multigraph
		for (Vertex vertex : mst) {
			for (Edge edge : vertex.connectedVertices) {
				String edgeKey = createEdgeKey(edge.Owner, edge.Child);

				// Add edge only if both nodes have less than 2 connections
				if (!seenEdges.contains(edgeKey)) {
					String ownerName = edge.Owner.getName();
					String childName = edge.Child.getName();

					if (degreeCount.get(ownerName) < 2 && degreeCount.get(childName) < 2) {
						multigraph.add(edge);
						seenEdges.add(edgeKey);
						degreeCount.put(ownerName, degreeCount.get(ownerName) + 1);
						degreeCount.put(childName, degreeCount.get(childName) + 1);
					}
				}
			}
		}

		// Add edges from MWPM to the multigraph
		for (Edge edge : mwpm) {
			String edgeKey = createEdgeKey(edge.Owner, edge.Child);

			// Add edge only if both nodes have less than 2 connections
			if (!seenEdges.contains(edgeKey)) {
				String ownerName = edge.Owner.getName();
				String childName = edge.Child.getName();

				if (degreeCount.get(ownerName) < 2 && degreeCount.get(childName) < 2) {
					multigraph.add(edge);
					seenEdges.add(edgeKey);
					degreeCount.put(ownerName, degreeCount.get(ownerName) + 1);
					degreeCount.put(childName, degreeCount.get(childName) + 1);
				}
			}
		}

		// Add missing edges for nodes with less than 2 connections
		List<String> nodesNeedingEdges = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : degreeCount.entrySet()) {
			if (entry.getValue() < 2) {
				nodesNeedingEdges.add(entry.getKey());
			}
		}

		for (int i = 0; i < nodesNeedingEdges.size(); i++) {
			String node1 = nodesNeedingEdges.get(i);
			for (int j = i + 1; j < nodesNeedingEdges.size(); j++) {
				String node2 = nodesNeedingEdges.get(j);
				String edgeKey = node1 + "-" + node2;

				// Ensure no duplicate or invalid connections
				if (!seenEdges.contains(edgeKey) && degreeCount.get(node1) < 2 && degreeCount.get(node2) < 2) {
					Vertex v1 = findVertex(mst, node1);
					Vertex v2 = findVertex(mst, node2);
					if (v1 != null && v2 != null) {
						int distance = v1.getName().equals(v2.getName()) ? 1 : 2; // Example distance handling
						Edge newEdge = new Edge(v1, v2, distance);
						multigraph.add(newEdge);
						seenEdges.add(edgeKey);
						degreeCount.put(node1, degreeCount.get(node1) + 1);
						degreeCount.put(node2, degreeCount.get(node2) + 1);
					}
				}
			}
		}

		// Validate that all nodes now have exactly 2 connections
		for (String node : degreeCount.keySet()) {
			if (degreeCount.get(node) != 2) {
				System.err.println("Error: Node " + node + " still does not have exactly 2 connections.");
			}
		}

		return multigraph;
	}

	// Helper function to find a vertex by name
	private static Vertex findVertex(List<Vertex> vertices, String name) {
		for (Vertex vertex : vertices) {
			if (vertex.getName().equals(name)) {
				return vertex;
			}
		}
		return null;
	}

	// Create a Hamiltonian Circuit from the multigraph
	public static List<Vertex> createHamiltonianCircuit(List<Edge> multigraph, List<Vertex> allVertices) {
		List<Vertex> circuit = new ArrayList<>();
		Set<Vertex> visited = new HashSet<>();

		Vertex start = allVertices.get(0); // Start from the first vertex
		circuit.add(start);
		visited.add(start);

		while (circuit.size() < allVertices.size()) {
			boolean added = false;
			for (Edge edge : multigraph) {
				Vertex next = null;
				if (edge.Owner.equals(circuit.get(circuit.size() - 1)) && !visited.contains(edge.Child)) {
					next = edge.Child;
				} else if (edge.Child.equals(circuit.get(circuit.size() - 1)) && !visited.contains(edge.Owner)) {
					next = edge.Owner;
				}

				if (next != null) {
					circuit.add(next);
					visited.add(next);
					added = true;
					break;
				}
			}

			// If no connected node can be added, find the next unvisited node
			if (!added) {
				for (Vertex v : allVertices) {
					if (!visited.contains(v)) {
						circuit.add(v);
						visited.add(v);
						break;
					}
				}
			}
		}

		return circuit;
	}

	// Print the optimal route (Hamiltonian Circuit)
	private static void printOptimalRoute(List<Vertex> optimalRoute) {
		System.out.print("Optimal Route (Hamiltonian Circuit): ");
		for (Vertex vertex : optimalRoute) {
			System.out.print(vertex.getName() + " -> "); // Print each vertex in the route
		}
		System.out.println("End");
	}

	// Get the names of the vertices in a list
	private static String getVertexNames(List<Vertex> vertices) {
		StringBuilder names = new StringBuilder();
		for (Vertex v : vertices) {
			names.append(v.getName()).append(" "); // Append each vertex name
		}
		return names.toString();
	}

	// Print edges with their details
	private static void printEdges(List<Edge> edges) {
		for (Edge edge : edges) {
			if (edge == null || edge.Child == null || edge.Owner == null) {
				System.out.println("Invalid edge detected. Skipping...");
				continue; // Skip invalid edges
			}
			System.out.println("Edge from " + edge.Owner.getName() + " to " + edge.Child.getName() + " with weight "
					+ edge.weight);
		}
	}

	private static List<Edge> getUniqueEdges(List<Vertex> vertices) {
		Set<String> seenEdges = new HashSet<>(); // To track processed edges
		List<Edge> uniqueEdges = new ArrayList<>(); // To store unique edges

		for (Vertex vertex : vertices) {
			for (Edge edge : vertex.connectedVertices) {
				String edgeKey = edge.Owner.getName() + "-" + edge.Child.getName();
				String reverseKey = edge.Child.getName() + "-" + edge.Owner.getName();

				if (!seenEdges.contains(edgeKey) && !seenEdges.contains(reverseKey)) {
					uniqueEdges.add(edge); // Add edge if it's not a duplicate
					seenEdges.add(edgeKey); // Mark the edge as processed
				}
			}
		}

		return uniqueEdges;
	}

	// Helper function to create a consistent edge key
	private static String createEdgeKey(Vertex v1, Vertex v2) {
		if (v1.getName().compareTo(v2.getName()) < 0) {
			return v1.getName() + "-" + v2.getName();
		} else {
			return v2.getName() + "-" + v1.getName();
		}
	}

}
