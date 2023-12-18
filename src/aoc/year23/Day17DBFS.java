package aoc.year23;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day17DBFS extends PuzzleApp {
	private static final int MAX_LINEAR_PATH_LENGTH = 10;	// 3 for part 1, 10 for part 2
	private static final int MIN_TURN_PATH_LENGTH = 4;		// 0 for part 1, 4 for part 2

	public static final void main(String[] args) {
		System.out.println("December 17: Clumsy Crucible");
		PuzzleApp app = new Day17DBFS();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day17-part1";
	}

	private CharacterGrid grid = new CharacterGrid();

	public void parseLine(String line) {
		grid.addRow(line);
	}

	PriorityQueue<Node> pqueue = new PriorityQueue<>();
	Set<Node> distances = new HashSet<>();

	private String printNode(Node finalNode) {
		StringBuilder sb = new StringBuilder();
		Node n = finalNode;
		if (n.prevNode() != null) sb.append(printNode(n.prevNode())).append(" ");
		sb.append(n);
		return sb.toString();
	}

	public void process() {
		// System.out.println(grid);

		// MUST try both directions from origin!
		pqueue.add(new Node(Loc.ORIGIN, Direction.RIGHT, 0, 0, 0, null));
		pqueue.add(new Node(Loc.ORIGIN, Direction.DOWN, 0, 0, 0, null));

		Loc endLoc = new Loc(grid.width() - 1, grid.height() - 1);

		while (!pqueue.isEmpty()) {
			Node n = pqueue.remove();
			Loc l = n.location();
			Direction d = n.direction();

			// System.out.println(l.toString() + " " + d + " " + n.pathLength() + " " + n.cost() + " " + n.distance());

			if (!distances.contains(n)) {
				if (!l.equals(endLoc)) {
					distances.add(n);
				} else {
					if (n.pathLength() >= MIN_TURN_PATH_LENGTH) {
						// System.out.println("Reached end with path length " + n.pathLength() + " >= " + MIN_TURN_PATH_LENGTH + " in ULTRA mode! (" + n.distance() + ")");
						// System.out.println(printNode(n));
						distances.add(n);
					} else {
						// System.out.println("Can't reach end with path length " + n.pathLength() + " < " + MIN_TURN_PATH_LENGTH + " in ULTRA mode!");
					}
				}

				for (Direction newD : Direction.values()) {
					Loc newLoc = l.step(newD);
					if (!grid.contains(newLoc)) continue;
					int newPathLength = n.pathLength() + 1;
					int cost = grid.at(newLoc) - '0';

					if (newD == d) {
						// Moving in the same direction, increase pathLength by 1
						if (newPathLength <= MAX_LINEAR_PATH_LENGTH) {  // NEW path length can't be more than MAX_LINEAR_PATH_LENGTH
							Node newNode = new Node(newLoc, d, newPathLength, cost, n.distance() + cost, n);
							pqueue.add(newNode);
						} else {
							// Skip (can't go more than MAX_LINEAR_PATH_LENGTH steps in the same direction)
						}
					} else if (newD.turnAround() == d) {
						// Skip (can't turn around)
					} else {
						// Turning right or left, reset pathLength to 1 (because we also take a step in the new direction)
						if (n.pathLength() >= MIN_TURN_PATH_LENGTH) { // CURRENT path length must be >= MIN_TURN_PATH_LENGTH
							Node newNode = new Node(newLoc, newD, 1, cost, n.distance() + cost, n);
							pqueue.add(newNode);
						} else {
							// Skip (can't turn if we haven't moved at least 4 steps in the same direction)
							// System.out.println("Can't turn yet, " + n.pathLength() + " < " + MIN_TURN_PATH_LENGTH);
						}
					}
				}
			}
		}
	}

	public void results() {
		System.out.println("Results Set size = " + distances.size());

		Integer minDistance = Integer.MAX_VALUE;
		Loc endLoc = new Loc(grid.width() - 1, grid.height() - 1);
		Node minDistanceNode = null;

		for (Node n : distances) {
			if (n.location().equals(endLoc)) {
				// System.out.println(n.location().toString() + " " + n.direction() + " " + n.pathLength() + " " + n.cost() + " " + n.distance());
				if (n.distance() < minDistance) {
					minDistance = n.distance();
					minDistanceNode = n;
				}
			}
		}

		System.out.println(printNode(minDistanceNode));
		System.out.println("Min Distance = " + minDistance);  // Part 2 is NOT 810
	}

	class Node implements Comparable<Node>{
		private Loc location;
		private Direction direction;
		private Integer pathLength;
		private Integer cost;
		private Integer distance;
		private Node prevNode;

		public Node(Loc location, Direction direction, Integer pathLength, Integer cost, Integer distance, Node prevNode) {
			this.location = new Loc(location);
			this.direction = direction;
			this.pathLength = pathLength;
			this.cost = cost;
			this.distance = distance;
			this.prevNode = prevNode;
		}

		public int compareTo(Node other) {
			return this.distance.compareTo(other.distance());
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

		public Integer distance() {
			return distance;
		}

		public Node prevNode() {
			return prevNode;
		}

		public String toString() {
			return location.toString() + " " + direction() + "[" + pathLength + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((location == null) ? 0 : location.hashCode());
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + ((pathLength == null) ? 0 : pathLength.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (direction != other.direction)
				return false;
			if (pathLength == null) {
				if (other.pathLength != null)
					return false;
			} else if (!pathLength.equals(other.pathLength))
				return false;
			return true;
		}
	}
}
