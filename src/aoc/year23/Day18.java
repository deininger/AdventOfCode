package aoc.year23;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day18 extends PuzzleApp {
	private static final int PART = 2;

	public static final void main(String[] args) {
		System.out.println("December 18: Lavaduct Lagoon");
		PuzzleApp app = new Day18();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day18-part1";
	}

	private List<DigInstruction> instructions = new ArrayList<>();

	public void parseLine(String line) {
		String[] parts = line.split(" ");
		if (PART == 1) {
			Direction direction = Direction.withCode(parts[0]);
			int distance = Integer.parseInt(parts[1]);
			String color = parts[2];
			instructions.add(new DigInstruction(direction, distance, color));
		} else {
			String hex = parts[2].substring(2, 7);
			String dir = parts[2].substring(7, 8);
			Direction direction = Direction.withIntValue(Integer.parseInt(dir));
			int distance = 0;
			for (int i = 0; i < 5; i++) {
				char c = hex.charAt(i);
				int value = 0;
				if (c >= '0' && c <= '9') { value = c - '0'; }
				if (c >= 'a' && c <= 'f') { value = c - 'a' + 10; }
				if (c >= 'A' && c <= 'F') { value = c - 'A' + 10; }
				distance = distance * 16 + value;
			}
			String color = parts[0];
			System.out.println(line + " becomes " + direction + " " + distance);
			instructions.add(new DigInstruction(direction, distance, color));
		}
	}

	List<Loc> points = new ArrayList<>();
	// SparseGrid<String> grid = new SparseGrid<>();

	public void process() {
		System.out.println("Instructions: " + instructions);

		/* 
		Loc pos = Loc.ORIGIN;
		grid.add(pos, "Origin");

		for (DigInstruction instruction : instructions) {
			for (int i = 0; i < instruction.distance; i++) {
				pos = pos.step(instruction.direction);
					grid.add(pos, instruction.color);
			}
		}
		*/

		Loc point = Loc.ORIGIN;
		points.add(point);

		for (DigInstruction instruction : instructions) {
			// int extra = 0;
			// if (instruction.direction == Direction.DOWN || instruction.direction == Direction.RIGHT) extra = 1;
			point = point.move(instruction.direction, instruction.distance);
			points.add(point);
		}

		System.out.println("Points: " + points);

		// System.out.println(grid);
	}

	private static long pathLength(List<Loc> v) {
		int n = v.size();
		long length = 0;
		for (int i = 0; i < n - 1; i++) {
			length += v.get(i).distance(v.get(i+1));
		}
		return length;
	}

	/*
	 * This is an implementation of the Shoelace Formula (https://en.wikipedia.org/wiki/Shoelace_formula)
	 * to calculate the area of a polygon defined by a set of vertex coordinates.
	 */
	private static long shoelaceArea(List<Loc> v) {
        int n = v.size();
        long a = 0;
        for (int i = 0; i < n - 1; i++) {
            a += ((long)v.get(i).x()) * ((long)v.get(i + 1).y()) - ((long)v.get(i + 1).x()) * ((long)v.get(i).y());
        }
        // return Math.abs(a + v.get(n - 1).x() * v.get(0).y() - v.get(0).x() * v.get(n - 1).y()) / 2.0;
    	return Math.abs(a) / 2;
  }

	public void results() {
		// System.out.println("Part 1: enclosed area = " + grid.calculateEnclosedArea());
		// System.out.println("Enclosed area = " + (shoelaceArea(points) + pathLength(points)/2 + 1));
		System.out.println("Path length = " + pathLength(points));
		System.out.println("Shoelace area = " + shoelaceArea(points));

		// We use Pick's Theorem (https://en.wikipedia.org/wiki/Pick%27s_theorem) here to add back in the
		// missing area of the path itself (which was only half-counted by the Shoelace Algorithm)

		System.out.println("Total area: " + (shoelaceArea(points) + pathLength(points) / 2 + 1));
	}

	class SparseGrid<T> {
		private Loc topLeft = Loc.ORIGIN;
		private Loc bottomRight = Loc.ORIGIN;
		private Map<Loc,T> cells = new HashMap<>();

		public SparseGrid() {
		}

		public void add(Loc loc, T t) {
			if (loc.x() < topLeft.x()) topLeft = topLeft.setX(loc.x());
			if (loc.x() > bottomRight.x()) bottomRight = bottomRight.setX(loc.x());
			if (loc.y() < topLeft.y()) topLeft = topLeft.setY(loc.y());
			if (loc.y() > bottomRight.y()) bottomRight = bottomRight.setY(loc.y());

			cells.put(loc, t);
		}

		public T at(Loc loc) {
			return cells.get(loc);
		}

		public boolean outside(Loc loc) {
			return loc.x() < topLeft.x() || loc.x() > bottomRight.x() || loc.y() < topLeft.y() || loc.y() > bottomRight.y();
		}

		public long calculateEnclosedArea() {
			long cellArea = cells.size(); // The area occupied by the cells

			// Find an interior point:
			Loc fillStart = null;

			for (int i = topLeft.x(); i < bottomRight.x(); i++) {
				if (this.at(new Loc(i, topLeft.y())) != null) {
					fillStart = new Loc(i + 1, topLeft.y() + 1);
					break;
				}
			}

			System.out.println("Grid dimensions are " + topLeft + " to " + bottomRight);
			System.out.println("Fill starting at " + fillStart);

			Deque<Loc> fillQueue = new ArrayDeque<>();
			Set<Loc> filled = new HashSet<>();

			fillQueue.add(fillStart);

			while (!fillQueue.isEmpty()) {
				Loc l = fillQueue.remove();
				
				if (!outside(l) && !filled.contains(l)) {
					filled.add(l);

					for (Direction d : Direction.values()) {
						Loc l2 = l.move(d);
						if (at(l2) == null) fillQueue.add(l2);
					}
				}
			}

			return cellArea + filled.size();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			for (int y = topLeft.y(); y <= bottomRight.y(); y++) {
				for (int x = topLeft.x(); x <= bottomRight.x(); x++) {
					Loc loc = new Loc(x, y);
					if (cells.containsKey(loc)) {
						sb.append("#");
					} else {
						sb.append(".");
					}
				}
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	class DigInstruction {
		public Direction direction;
		public int distance;
		public String color;

		public DigInstruction(Direction direction, int distance, String color) {
			this.direction = direction;
			this.distance = distance;
			this.color = color;
		}

		public String toString() {
			return direction + " " + distance;
		}
	}
}