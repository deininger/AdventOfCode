package aoc.year23;

import aoc.util.CharacterGrid;
import aoc.util.DijkstraGraph;
import aoc.util.DijkstraGraph.Node;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day17Dijkstra extends PuzzleApp {

	public static final void main(String[] args) {
		System.out.println("December 17: Clumsy Crucible");
		PuzzleApp app = new Day17Dijkstra();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day17-test";
	}

	private CharacterGrid grid = new CharacterGrid();

	public void parseLine(String line) {
		grid.addRow(line);
	}

	private DijkstraGraph<Integer> graph = new DijkstraGraph<>();

	public void process() {
		System.out.println(grid);

		// Convert our Grid to a Dijkstra Graph:

		for (int y = 0; y < grid.height(); y++) {
			for (int x = 0; x < grid.width(); x++) {
				Loc loc = new Loc(x, y);
				String name = loc.toString();
				Integer value = grid.at(loc) - '0';
				Node<Integer> node = new Node<>(name, value);
				graph.addNode(node);
			}
		}

		for (int y = 0; y < grid.height(); y++) {
			for (int x = 0; x < grid.width(); x++) {
				Loc loc = new Loc(x,y);
				String name = loc.toString();
				Node<Integer> node = graph.getNode(name);
				for (Direction d : Direction.values()) {
					Loc neighbor = loc.step(d);
					if (grid.contains(neighbor)) {
						String neighborName = neighbor.toString();
						Node<Integer> neighborNode = graph.getNode(neighborName);
						node.addEdge(neighborNode, neighborNode.value());
					}
				}
			}
		}

		// Now we can find the shortest path from the origin to the end:

		Node<Integer> start = graph.getNode("(0,0)");
		graph = DijkstraGraph.calculateShortestPathFromSource(graph, start);

		Loc endLoc = new Loc(grid.width() - 1, grid.height() - 1);
		Node<Integer> end = graph.getNode(endLoc.toString());

		System.out.println("Shortest path from origin to end: " + end.distance());
	}

	public void results() {
	}

	class LocationDirectionAndPathLength {
		private Loc location;
		private Direction direction;
		private Integer pathLength;
		private Integer cost;

		public LocationDirectionAndPathLength(Loc location, Direction direction, Integer pathLength, Integer cost) {
			this.location = location;
			this.direction = direction;
			this.pathLength = pathLength;
			this.cost = cost;
		}

		public Loc location() {
			return location;
		}

		public Direction direction() {
			return direction;
		}

		public Integer pathLength() {
			return pathLength;
		}

		public Integer cost() {
			return cost;
		}
	}
}
