package TSP;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphVisualizer extends JPanel {
	private final List<Vertex> vertices; // List of vertices in the graph
	private final List<Edge> edges; // List of edges in the graph
	private final List<String> routeList; // Route order for visualization

	public GraphVisualizer(List<Vertex> vertices, List<Edge> edges, List<String> routeList) {
		this.vertices = vertices; // Initialize vertices
		this.edges = edges; // Initialize edges
		this.routeList = routeList; // Initialize route list
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int radius = 200; // Radius for circular node layout
		int centerX = getWidth() / 2; // Center X-coordinate of the panel
		int centerY = getHeight() / 2; // Center Y-coordinate of the panel
		int numNodes = routeList.size(); // Number of nodes to visualize
		Point[] points = new Point[numNodes]; // Array to hold positions of nodes

		setBackground(Color.WHITE); // Set the background color of the panel

		// Draw the legend text
		String legendText = "Blue: Start Point, Red: End Point";
		g.setFont(new Font("Arial", Font.BOLD, 16));
		FontMetrics fm = g.getFontMetrics();
		int textWidth = fm.stringWidth(legendText);
		int textX = (getWidth() - textWidth) / 2; // Center the text horizontally
		int textY = 30; // Position the text at the top of the frame
		g.setColor(Color.BLACK);
		g.drawString(legendText, textX, textY);

		// Draw nodes on the graph
		g.setFont(new Font("Arial", Font.BOLD, 12));
		for (int i = 0; i < numNodes; i++) {
			double angle = 2 * Math.PI * i / numNodes; // Angle for circular layout
			int x = (int) (centerX + radius * Math.cos(angle)); // Calculate X-coordinate
			int y = (int) (centerY + radius * Math.sin(angle)); // Calculate Y-coordinate
			points[i] = new Point(x, y); // Save the position of the node

			// Determine node color based on position in the routeList
			if (i == 0) {
				g.setColor(new Color(135, 206, 250)); // Lighter blue for start node
			} else if (i == numNodes - 1) {
				g.setColor(Color.RED); // Red for end node
			} else {
				g.setColor(new Color(102, 205, 170)); // Default node color
			}

			// Draw the node
			g.fillOval(x - 15, y - 15, 30, 30); // Draw filled circle for the node
			g.setColor(Color.BLACK); // Set color for border
			g.drawOval(x - 15, y - 15, 30, 30); // Draw border around the node
			g.drawString(routeList.get(i), x - 5, y + 5); // Draw node label
		}

		// Draw edges connecting nodes
		g.setColor(new Color(65, 105, 225)); // Set color for edges
		for (Edge edge : edges) {
			if (edge == null || edge.Owner == null || edge.Child == null) {
				continue; // Skip null or invalid edges
			}

			int index1 = routeList.indexOf(edge.Owner.getName()); // Find index of start node
			int index2 = routeList.indexOf(edge.Child.getName()); // Find index of end node

			if (index1 != -1 && index2 != -1) {
				Point p1 = points[index1]; // Start node position
				Point p2 = points[index2]; // End node position
				g.drawLine(p1.x, p1.y, p2.x, p2.y); // Draw line representing the edge
			}
		}
	}

	public static void displayGraph(List<Vertex> vertices, List<Edge> edges, List<String> routeList, String title) {
		JFrame frame = new JFrame(title); // Create a new window with the specified title
		GraphVisualizer panel = new GraphVisualizer(vertices, edges, routeList); // Create the panel for visualization
		frame.add(panel); // Add panel to the frame
		frame.setSize(600, 600); // Set the size of the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Specify close operation
		frame.setVisible(true); // Make the window visible
	}
}
