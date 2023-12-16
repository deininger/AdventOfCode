package aoc.year23;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;
import aoc.util.Direction;

public class Day16 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 16: The Floor Will Be Lava");
		PuzzleApp app = new Day16();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day16-part1";
	}

	private CharacterGrid grid = new CharacterGrid();

	public void parseLine(String line) {
		grid.addRow(line);
	}

	private Set<LocationAndDirection> visited = new HashSet<>();

	private Deque<LocationAndDirection> queue = new LinkedList<>();

	private int traverse(LocationAndDirection start) {
		queue.push(start);

		while (!queue.isEmpty()) {
			LocationAndDirection ld = queue.pop();

			if (grid.contains(ld.location()) && !visited.contains(ld)) {

				// System.out.println("Visiting " + ld + " " + grid.at(ld.location()));
				visited.add(ld);

				switch (grid.at(ld.location())) {
					case '.':
						queue.push(ld.step());
						break;
					case '-':
						switch (ld.direction()) {
							case UP, DOWN:
								queue.push(ld.turnLeft().step());
								queue.push(ld.turnRight().step());
								break;
							case LEFT, RIGHT:
								queue.push(ld.step());
								break;
						}
						break;
					case '|':
						switch (ld.direction()) {
							case LEFT, RIGHT:
								queue.push(ld.turnLeft().step());
								queue.push(ld.turnRight().step());
								break;
							case UP, DOWN:
								queue.push(ld.step());
								break;
						}
						break;
					case '\\':
						switch (ld.direction()) {
							case LEFT, RIGHT:
								queue.push(ld.turnRight().step());
								break;
							case UP, DOWN:
								queue.push(ld.turnLeft().step());
								break;
						}
						break;
					case '/':
						switch (ld.direction()) {
							case LEFT, RIGHT:
								queue.push(ld.turnLeft().step());
								break;
							case UP, DOWN:
								queue.push(ld.turnRight().step());
								break;
						}
						break;
				}
			}
		}

		return visited.stream().map(ld -> ld.location()).collect(Collectors.toSet()).size();
	}

	public void process() {
		// System.out.println(grid);
		LocationAndDirection start = new LocationAndDirection(Loc.ORIGIN, Direction.RIGHT);
		System.out.println("Traversing grid...");
		int traversalCount = traverse(start);
		System.out.println("LD Cache contains " + visited.size() + " LDs");
		System.out.println("Part 1: Visited " + traversalCount + " locations");

		// Part 2: Try from all possible entry points:
		int maxEnergizedTiles = traversalCount; // Might as well use part 1 as our minimum
		LocationAndDirection maxLD = start;

		for (int r = 0; r < grid.height(); r++) { // Along the left edge, moving RIGHT:
			visited.clear();
			queue.clear();
			start = new LocationAndDirection(new Loc(0, r), Direction.RIGHT);
			traversalCount = traverse(start);
			// System.out.println("... " + start + " --> " + traversalCount);

			if (traversalCount > maxEnergizedTiles) {
				maxEnergizedTiles = traversalCount;
				maxLD = start;
			}
		}

		for (int r = 0; r < grid.height(); r++) { // Along the right edige, moving LEFT:
			visited.clear();
			queue.clear();
			start = new LocationAndDirection(new Loc(grid.width()-1, r), Direction.LEFT);
			traversalCount = traverse(start);
			// System.out.println("... " + start + " --> " + traversalCount);

			if (traversalCount > maxEnergizedTiles) {
				maxEnergizedTiles = traversalCount;
				maxLD = start;
			}
		}

		for (int c = 0; c < grid.width(); c++) { // Along the top edge, moving DOWN:
			visited.clear();
			queue.clear();
			start = new LocationAndDirection(new Loc(c, 0), Direction.DOWN);
			traversalCount = traverse(start);
			// System.out.println("... " + start + " --> " + traversalCount);

			if (traversalCount > maxEnergizedTiles) {
				maxEnergizedTiles = traversalCount;
				maxLD = start;
			}
		}

		for (int c = 0; c < grid.height(); c++) { // Along the bottom edige, moving UP:
			visited.clear();
			queue.clear();
			start = new LocationAndDirection(new Loc(c, grid.height()-1), Direction.UP);
			traversalCount = traverse(start);
			// System.out.println("... " + start + " --> " + traversalCount);

			if (traversalCount > maxEnergizedTiles) {
				maxEnergizedTiles = traversalCount;
				maxLD = start;
			}
		}

		System.out.println("Part 2: Maximum Visited " + maxEnergizedTiles + " locations (" + (100.0 * maxEnergizedTiles) / (grid.width() * grid.height()) + "%), starting at " + maxLD);
	}

	public void results() {

	}

	class LocationAndDirection {
		private Loc location;
		private Direction direction;

		public LocationAndDirection(Loc location, Direction direction) {
			this.location = location;
			this.direction = direction;
		}

		public Loc location() {
			return location;
		}

		public Direction direction() {
			return direction;
		}

		public LocationAndDirection step() {
			return new LocationAndDirection(location.step(direction), direction);
		}

		public LocationAndDirection turnLeft() {
			return new LocationAndDirection(location, direction.turnLeft());
		}

		public LocationAndDirection turnRight() {
			return new LocationAndDirection(location, direction.turnRight());
		}

		public String toString() {
			return location + " " + direction;
		}

		public int hashCode() {
			return location.hashCode() ^ direction.hashCode();
		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;
			if (o.getClass() != this.getClass())
				return false;
			LocationAndDirection other = (LocationAndDirection) o;
			return this.location.equals(other.location) && this.direction == other.direction;
		}
	}
}
