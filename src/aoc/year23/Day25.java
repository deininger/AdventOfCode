package aoc.year23;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aoc.util.Karger;
import aoc.util.PuzzleApp;
import aoc.util.Karger.Graph;

public class Day25 extends PuzzleApp {

	public static final void main(String[] args) {
		System.out.println("December 25: Snowverload");
		PuzzleApp app = new Day25();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day25-part1";
	}

	private Map<String,Node> nodes = new HashMap<>();
	private Map<Integer,Edge> edges = new HashMap<>();

	public void parseLine(String line) {
		String[] nodeNames = line.replace(":", "").split(" ");
		Node base = nodes.computeIfAbsent(nodeNames[0], s -> new Node(s));

		for (int i = 1; i < nodeNames.length; i++) {
			Node node = nodes.computeIfAbsent(nodeNames[i], s -> new Node(s));
			Edge edge = new Edge(nodeNames[0], nodeNames[i]);
			edges.put(edges.size(), edge);
			base.connect(node);
		}
	}


	public void process() {
		System.out.println("Starting graph has " + nodes.size() + " nodes");
		System.out.println("Starting graph has " + edges.size() + " edges");

		// Trying Karger's algorithm...

		Graph kargerGraph = new Karger.Graph(nodes.size(), edges.size());

		// Assign numbers to all the Nodes:

		Map<Node,Integer> numberedNodes = new HashMap<>();

		int nodeNumber = 0;
		for (Node n : nodes.values()) {
			numberedNodes.put(n, nodeNumber++);
		}

		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			int leftNumber = numberedNodes.get(nodes.get(edge.sourceName()));
			int rightNumber = numberedNodes.get(nodes.get(edge.destinationName()));
			kargerGraph.edge[i] = new Karger.Edge(leftNumber, rightNumber);
		}

		System.out.println("Cut found by Karger's randomized algo is "+ Karger.kargerMinCut(kargerGraph));

	}

	public void results() {
	}


	class Edge {
		private String sourceName;
		private String destinationName;
		private boolean active = true;

		public Edge(String sourceName, String destinationName) {
			this.sourceName = sourceName;
			this.destinationName = destinationName;
		}

		public String sourceName() {
			return sourceName;
		}

		public String destinationName() {
			return destinationName;
		}

		private boolean active() {
			return this.active;
		}

		private void activate() {
			this.active = true;
		}

		private void deactivate() {
			this.active = false;
		}

		public String toString() {
			return sourceName + "-" + destinationName;
		}
	}

	class Node {
		private String name;
		private Node parent;

		public Node(String name) {
			this.name = name;
			this.parent = this;
		}

		public String name() {
			return name;
		}

		public Node parent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public Node root() {
			Node root = this.parent;
			if (root != this) root = root.root();
			return root;
		}

		public void connect(Node other) {
			Node a = this.root();
			Node b = other.root();

			if (a != b) {
				// System.out.println("Connecting " + this.getValue() + " to " + other.getValue() + " by giving " + b.getValue() + " root " + a.getValue());
				b.parent = a;
			}
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.name);
			if (!this.equals(parent())) sb.append("->").append(parent.toString());
			return sb.toString();
		}
	}



	private Map<Node,List<Node>> connectedComponents(Collection<Node> nodes) {
		Map<Node,List<Node>> roots = new HashMap<>();
		 
		for (Node n : nodes) {
			List<Node> childList = roots.computeIfAbsent(n.root(), k -> new ArrayList<Node>());
			childList.add(n);
		}

		// System.out.println("Set of roots is: " + roots.keySet());

		// for (NodeWithParent p : roots.keySet()) {
		//	System.out.println("Node " + p.getValue() + " has children " + roots.get(p));
		// }

		return roots;
	}

	private void resetAndRecalculateParents() {
		// Clear all the parents:
		nodes.values().forEach(n -> n.setParent(n));

		// Traverse all the connections to build a new set of parents:

		edges.values().stream().filter(e -> e.active()).forEach(e -> { 
			Node source = nodes.get(e.sourceName()); 
			Node dest = nodes.get(e.destinationName());
			source.connect(dest);
		});
	}

	private long connectionCount(Node node) {
		return edges.values().stream().filter(e -> e.sourceName().equals(node.name()) || e.destinationName().equals(node.name())).count();
	}

	private long connectionCount(Edge edge) {
		Node source = nodes.get(edge.sourceName());
		Node dest = nodes.get(edge.destinationName());
		return Math.min(connectionCount(source), connectionCount(dest));
	}

	private void abandoned() {
		int result = connectedComponents(nodes.values()).keySet().size();
		System.out.println("Starting graph has " + result + " subgroups");
		
		// Reorder our list of nodes by the number of connections each has, lowest to highest:

		Comparator<Node> sortNodesByConnectionCount = (Node first, Node second) -> 
				Long.compare(connectionCount(first), connectionCount(second));

		List<Node> sortedNodes = new ArrayList<>(nodes.values());
		Collections.sort(sortedNodes, sortNodesByConnectionCount);

		Node firstNode = sortedNodes.get(0);
		System.out.println("Lowest node " + firstNode.name() + " has " + connectionCount(firstNode) + " connections");

		Comparator<Edge> sortEdgesByConnectionCount = (Edge first, Edge second) -> 
				Long.compare(connectionCount(first), connectionCount(second));

		List<Edge> sortedEdges = new ArrayList<>(edges.values());
		Collections.sort(sortedEdges, sortEdgesByConnectionCount);
		System.out.println("Lowest edge " + sortedEdges.get(0) + " has " + connectionCount(sortedEdges.get(0)) + " connections");

		List<Edge> filteredSortedEdges = sortedEdges.stream().filter(e -> connectionCount(e) < 5).toList();
		System.out.println("Filtered down to " + filteredSortedEdges.size() + " filtered sorted edges");

		for (int i = 1; i <= filteredSortedEdges.size(); i++) {
			System.out.println("Iteration I " + i + " of " + edges.size() + "...");
			edges.get(i).deactivate();
			for (int j = i + 1; j <= filteredSortedEdges.size(); j++) {
				System.out.println("Iteration J " + j + " of " + edges.size() + "...");
				edges.get(j).deactivate();
				for (int k = j + 1; k <= filteredSortedEdges.size(); k++) {
					// System.out.println("Iteration K " + k + " of " + edges.size() + "...");
					edges.get(k).deactivate();
					resetAndRecalculateParents();
					Map<Node,List<Node>> roots = connectedComponents(nodes.values());
					result = roots.keySet().size();
					if (result > 1) { 
						System.out.println("Deactivated Edges " + edges.get(i) + "," + edges.get(j) + "," + edges.get(k) + " has " + result + " subgroups");
						roots.entrySet().forEach(entry -> { 
							System.out.println("Root " + entry.getKey() + " has " + entry.getValue().size() + " nodes");
						});
					}
					edges.get(k).activate();
				}
				edges.get(j).activate();
			}
			edges.get(i).activate();
		}
	}

}