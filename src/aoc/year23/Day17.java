package aoc.year23;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Grid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day17 extends PuzzleApp {
	private static final int MAX_LINEAR_PATH_LENGTH = 3;
	private static final Integer MAX_INT_VALUE = 999999; // Integer.MAX_VALUE;

	public static final void main(String[] args) {
		System.out.println("December 17: Clumsy Crucible");
		PuzzleApp app = new Day17();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day17-sample";
	}

	private CharacterGrid grid = new CharacterGrid();

	public void parseLine(String line) {
		grid.addRow(line);
	}

	private Map<Position, Integer> visited = new HashMap<>(); // The path we took to find a solution, and the solution
	private Queue<Position> visiting = new LinkedList<>(); // The locations we've already visited during recursion

	private int traverse(Position p, Loc end) {
		if (!grid.contains(p.location()))
			return MAX_INT_VALUE;

		if (visiting.contains(p))
			return MAX_INT_VALUE;

		if (p.location().equals(end)) {
			// visiting.add(p);
			// heatLoss += grid.at(p.location()) - '0';
			// System.out.println("Reached end with heat loss " + heatLoss + " by visiting:
			// " + visiting.size());
			// visiting.remove(p);
			int result = grid.at(p.location()) - '0';
			System.out.println("Recording visit " + p + " with result " + result);
			visited.put(p, result);
			return result;
		}

		if (visited.containsKey(p))
			return visited.get(p);

		// System.out.println("Visiting " + p + " " + (grid.at(p.location()) - '0'));

		visiting.add(p);

		int leftTurnResult = traverse(p.turnLeft().step(), end);
		int rightTurnResult = traverse(p.turnRight().step(), end);
		int straightResult = p.linearPathLength < MAX_LINEAR_PATH_LENGTH ? traverse(p.step(), end) : MAX_INT_VALUE;
		int minimumResult = Math.min(leftTurnResult, Math.min(rightTurnResult, straightResult));

		visiting.remove(p);

		if (minimumResult != MAX_INT_VALUE) {
			minimumResult += (grid.at(p.location()) - '0');
		}

		if (minimumResult != MAX_INT_VALUE) {
			System.out.println("Recording visit " + p + " with result " + minimumResult);
			visited.put(p, minimumResult);
		}

		return minimumResult;
	}

	public void process() {
		System.out.println(grid);
		Position start = new Position(Loc.ORIGIN, Direction.RIGHT, 0);
		Loc end = new Loc(grid.width() - 1, grid.height() - 1);
		System.out.println("Traversing grid...");
		int minimumHeatLoss = traverse(start, end);
		System.out.println("Part 1: Minimum Heat Loss is " + (minimumHeatLoss - (grid.at(Loc.ORIGIN) - '0')));
		System.out.println("        Number of locations visited is " + visited.size());

		// Part 2:

	}

	public void results() {
		Grid<Integer> heatGrid = new Grid<>(grid.height(), grid.width());
		for (Map.Entry<Position, Integer> e : visited.entrySet()) {
			Loc l = e.getKey().location();
			if (heatGrid.get(l.y(), l.x()).value() == null || heatGrid.get(l.y(), l.x()).value() > e.getValue()) {
				heatGrid.set(l.y(), l.x(), e.getValue());
			}
		}
		System.out.println(heatGrid);
	}

	class Position {
		private Loc location;
		private Direction direction;
		private int linearPathLength;

		public Position(Loc location, Direction direction, int linearPathLength) {
			this.location = location;
			this.direction = direction;
			this.linearPathLength = linearPathLength;
		}

		public Position(Loc location, Direction direction) {
			this(location, direction, 0);
		}

		public Loc location() {
			return location;
		}

		public Direction direction() {
			return direction;
		}

		public int linearPathLength() {
			return linearPathLength;
		}

		public Position step() {
			return new Position(location.step(direction), direction, linearPathLength + 1);
		}

		public Position turnLeft() {
			return new Position(location, direction.turnLeft(), 0);
		}

		public Position turnRight() {
			return new Position(location, direction.turnRight(), 0);
		}

		public String toString() {
			return location + " " + direction + " (" + linearPathLength + ")";
		}

		public int hashCode() {
			return location.hashCode() ^ direction.hashCode() + linearPathLength;
		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;
			if (o.getClass() != this.getClass())
				return false;
			Position other = (Position) o;
			return this.location.equals(other.location) && this.direction == other.direction
					&& this.linearPathLength == other.linearPathLength;
		}
	}
}
