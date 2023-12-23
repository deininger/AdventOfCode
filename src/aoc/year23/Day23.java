package aoc.year23;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Graph;
import aoc.util.Loc;
import aoc.util.Node;
import aoc.util.PuzzleApp;

public class Day23 extends PuzzleApp {
	private static final boolean STEEP_SLOPES = false; // Part 1 true, Part 2 false

	public static final void main(String[] args) {
		System.out.println("December 23: A Long Walk");
		PuzzleApp app = new Day23();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day23-part1";
	}

	private CharacterGrid grid = new CharacterGrid();

	public void parseLine(String line) {
		// if(!STEEP_SLOPES) line = line.replace('<','.').replace('>','.').replace('^','.').replace('v','.');
		grid.addRow(line);
	}

	private List<Loc> validSteps(Loc current, List<Loc> visited) {
		List<Loc> valid = new ArrayList<>();

		if (STEEP_SLOPES) {
		switch (grid.at(current)) {
			case '.': // Allowed to walk in any direction (that isn't a wall)
				valid.addAll(current.adjacent().filter(l -> grid.contains(l) && grid.at(l) != '#').toList());
				break;
			case '>':
				valid.add(current.move(Direction.RIGHT));
				break;
			case '<':
				valid.add(current.move(Direction.LEFT));
				break;
			case '^':
				valid.add(current.move(Direction.UP));
				break;
			case 'v':
				valid.add(current.move(Direction.DOWN));
				break;
			default: 
				throw new IllegalArgumentException("Found '" + grid.at(current) + "' in grid at " + current);
		}
		} else {
			valid.addAll(current.adjacent().filter(l -> grid.contains(l) && grid.at(l) != '#').toList());
		}

		if (visited != null) valid.removeAll(visited);

		// System.out.println("Valid steps from " + current + " are: " + valid);
		return valid;
	}

	
	private List<Loc> walk(Loc current, Loc end, List<Loc> visited) {
		if (visited.contains(current)) return new ArrayList<>();
		// System.out.println("Walking at " + current + ", path length " + visited.size());
		visited.add(current);
		if (current.equals(end)) {
			System.out.println("** Reached end with path length " + (visited.size()-1));
			// System.out.println(grid.overlayPath(visited, '+'));
			return visited;
		}
		List<Loc> nextLocs = validSteps(current, visited);

		while (nextLocs.size() == 1) {
			current = nextLocs.get(0);
			visited.add(current);
			if (current.equals(end)) {
				System.out.println("** Reached end with path length " + (visited.size()-1));
				// System.out.println(grid.overlayPath(visited, '+'));
				return visited;
			}
			nextLocs = validSteps(current, visited);
		}

		if (nextLocs.size() == 0) {
			// System.out.println("Reached " + current + " and can't move!");
			return new ArrayList<>();
		} else {
			List<Loc> result = new ArrayList<>();
			for (Loc l : nextLocs) {
				// System.out.println("  Branch at " + current + " has paths " + nextLocs + ", trying " + l);
				List<Loc> nextVisited = walk(l, end, new ArrayList<>(visited));
				if (nextVisited.size() > result.size()) {
					result = nextVisited;
				}
				// System.out.println("  Result of trying " + l + " was " + visited.size());
			}
			// System.out.println("    Chose path from " + current + ", new path length is " + visited.size());
			return result;		
		}
	}

	public void processBruteForce() {
		Loc start = new Loc(1, 0);
		Loc end = new Loc(grid.width()-2, grid.width()-1);

		System.out.println("Starting at " + start + ", ending at " + end);

		List<Loc> path = walk(start, end, new ArrayList<>());

		System.out.println("Part 1: Step count = " + (path.size() - 1)); // Subtract 1 to not count the Start

		// System.out.println(grid.overlayPath(path, 'O'));
	}

	private Loc findEndOfPath(Loc current, List<Loc>visited) {
		List<Loc> nextLocs = validSteps(current, visited);
		
		while (nextLocs.size() == 1) {
			visited.add(current);
			current = nextLocs.get(0);
			nextLocs = validSteps(current, visited);
		}

		visited.add(current);
		return current;
	}

	private Graph<Loc> buildGraph(Node<Loc> start, Node<Loc> finish) {
		Graph<Loc> graph = new Graph<>(start);
		Deque<Node<Loc>> q = new ArrayDeque<>();
		q.add(start);

		while (!q.isEmpty()) {
			Node<Loc> n = q.pop();
			if (n.equals(finish)) {
				continue;
			}
			List<Loc> neighbors = validSteps(n.getValue(), null);
			neighbors.forEach( l -> {
				List<Loc> visited = new ArrayList<>();
				visited.add(n.getValue()); // Prevent going backwards
				Loc end = findEndOfPath(l, visited);
				Node<Loc> endNode = graph.get(end);
				if (endNode == null) {
					endNode = new Node<>(end);
					graph.add(endNode);
					q.add(endNode);
					// System.out.println("Added " + end + " to queue and graph");
				} else if (endNode.equals(finish)) {
					// System.out.println("Found path from " + n + " to FINISH");
				}
				// System.out.println("Connecting " + n + " to " + endNode + " with weight " + visited.size());
				n.connect(endNode, visited.size()-1);
			});
		}

		return graph;
	}

	public int traverse(Node<Loc> start, Node<Loc> finish, Set<Node<Loc>> visited) {
		visited.add(start);

		if (start.equals(finish)) {
			// System.out.println("Reached end, visited nodes: " + visited);
			return 0;
		}

		return start.edges().filter(e -> !visited.contains(e.getKey())).mapToInt(e -> {
				return e.getValue() + traverse(e.getKey(), finish, new HashSet<>(visited)); 
			}).max().orElse(-999999); // If there are no edge to traverse, and we're not at the finish, then this path is BAD!
	}

	public void process() {
		Node<Loc> start = new Node<>(new Loc(1, 0));
		Node<Loc> finish = new Node<>(new Loc(grid.width()-2, grid.width()-1));
		Graph<Loc> graph = buildGraph(start, finish);
	
		System.out.println("Start Node: " + start);
		System.out.println("Finish Node: " + finish);
		System.out.println("Graph contains " + graph.size() + " nodes");

		Set<Node<Loc>> visitedNodes = new HashSet<>();
		int result = traverse(start, finish, visitedNodes);

		System.out.println("Part 2: max traversal = " + result);
			// sample answer is 154
			// 6450 is correct part 2 answer
	}

	public void results() {
	}

}