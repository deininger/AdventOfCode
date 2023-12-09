package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RopeBridge {
	private class Loc {
		private int x;
		private int y;

		public Loc(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * The distance between two Locs is the larger of the absolute differences of
		 * their Cartesian coordinates.
		 */
		public int distance(Loc l) {
			return Integer.max(Math.abs(l.x - x), Math.abs(l.y - y));
		}

		public Loc adjacentLoc(String direction) {
			switch (direction) {
			case "U":
				return new Loc(x, y - 1);
			case "D":
				return new Loc(x, y + 1);
			case "L":
				return new Loc(x - 1, y);
			case "R":
				return new Loc(x + 1, y);
			default:
				throw new IllegalArgumentException("Unsupported direction '" + direction + "'");
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(x, y);
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
			Loc other = (Loc) obj;
			return x == other.x && y == other.y;
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}

	private class Segment {
		private String name;
		private Loc loc;

		public Segment(String name) {
			this.name = name;
			this.loc = new Loc(0, 0);
		}

		public void setName(String name) {
			this.name = name;
		}

		public Loc loc() {
			return loc;
		}

		public void setLoc(Loc loc) {
			this.loc = loc;
		}

		public String toString() {
			return name + loc;
		}

		/**
		 * The Segment moves from its current position (x,y) toward the given segment by
		 * taking at most 1 step i the X axis and/or 1 step in the Y axis. If it is
		 * currently less than or equal to 1 unit away in either the X or Y axis, it
		 * does not move.
		 */
		public void moveToward(Segment s) {
			int newX = loc.x;
			int newY = loc.y;

			if (loc.distance(s.loc()) > 1) {
				newX += Integer.signum(s.loc.x - loc.x);
				newY += Integer.signum(s.loc.y - loc.y);
				Loc newLoc = new Loc(newX, newY);
				// System.out.println("Moving " + name + " from " + loc + " to " + newLoc);
				setLoc(newLoc);
			} else {
				// System.out.println("Not moving " + name);
			}
		}
	}

	private class Snake {
		private List<Segment> segments;

		public Snake(int size) {
			segments = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				segments.add(new Segment(Integer.toString(i)));
			}
			segments.get(0).setName("H");
			segments.get(size - 1).setName("T");
		}

		public Segment head() {
			return segments.get(0);
		}
		
		public Segment tail() {
			return segments.get(segments.size() - 1);
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();

			sb.append("Snake ");
			for (Segment s : segments) {
				sb.append(" ").append(s.toString());
			}

			return sb.toString();
		}

		/**
		 * Move the entire snake by moving each segment. The head moves from its current
		 * position by taking 1 step in the given direction. Each segment following
		 * moves "toward" the previous segment, following the Segment.moveToward()
		 * logic.
		 */
		public void move(String direction) {
			Segment head = head();

			head.setLoc(head.loc().adjacentLoc(direction));
			
			for (int i = 1; i < segments.size(); i++) {
				segments.get(i).moveToward(segments.get(i - 1));
			}
		}
	}

	private static RopeBridge app = new RopeBridge();

	public static final void main(String[] args) throws IOException {
		System.out.println("December  9: Rope Bridge");
		BufferedReader reader = new BufferedReader(new FileReader("data/Data9"));
		app.process(reader);
		reader.close();
	}

	Snake snake = new Snake(10);
	Set<Loc> tailVisitedLocations = new HashSet<>();

	public void process(BufferedReader reader) {
		String line;
		try {
			line = reader.readLine();

			while (line != null) {
				app.process(line);
				line = reader.readLine();
			}

			// System.out.println("Spots visited by Tail: " + tailVisitedLocations);
			System.out.println("Tail visited " + tailVisitedLocations.size() + " locations");

		} catch (IOException e) {
			System.err.println("Exception while reading data file: " + e.getLocalizedMessage());
		}
	}

	public void process(String line) {
		String[] parts = line.split(" ");
		String direction = parts[0];
		int distance = Integer.parseInt(parts[1]);

		// System.out.println("Processing " + direction + " " + distance);

		for (int i = 0; i < distance; i++) {
			snake.move(direction);
			// System.out.println(snake);
			tailVisitedLocations.add(snake.tail().loc());
		}
	}
}
