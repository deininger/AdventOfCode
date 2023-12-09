package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class RopeBridgeGrid {
	static class Spot {
		private Grid grid;
		
		private int x;
		private int y;
		
		private Spot upSpot;
		private Spot downSpot;
		private Spot leftSpot;
		private Spot rightSpot;
		
		// private List<String> actions = new ArrayList<>();
		private boolean visitedByHead;
		private boolean visitedByTail;
		
		public Spot(Grid grid, int x, int y) {
			this.grid = grid;
			// actions.add(".");
			this.x = x;
			this.y = y;
		}

		public void setLoc(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public void addAction(String action) {
			// actions.add(action);
			if (action.equals("H")) visitedByHead = true;
			if (action.equals("T")) visitedByTail = true;
		}
		
		public boolean hasUp() {
			return upSpot != null;
		}

		public boolean hasDown() {
			return downSpot != null;
		}

		public boolean hasLeft() {
			return leftSpot != null;
		}

		public boolean hasRight() {
			return rightSpot != null;
		}

		public Spot up() {
			if (!hasUp()) { grid.growUp(); }			
			return upSpot;
		}
		
		public Spot down() {
			if (!hasDown()) { grid.growDown(); }			
			return downSpot;
		}
		
		public Spot left() {
			if (!hasLeft()) { grid.growLeft(); }			
			return leftSpot;
		}
		
		public Spot right() {
			if (!hasRight()) { grid.growRight(); }			
			return rightSpot;
		}
		
		public String toString() {
			return visitedByTail ? "#" : ".";
			// return actions.get(actions.size()-1);
		}
	}
		
	static class Grid {
		private Spot centerSpot;
		private Spot topLeftSpot;
		private Spot bottomRightSpot;
		
		private Spot headSpot;
		private Spot tailSpot;
		
		public Grid() {
			// Start with enough Lists to support a "1x1" grid
			centerSpot = new Spot(this, 0, 0);
			topLeftSpot = centerSpot;
			bottomRightSpot = centerSpot;
			
			headSpot = centerSpot;
			tailSpot = centerSpot;
			tailSpot.addAction("T");
			headSpot.addAction("H");
		}

		public void growLeft() {
			// Add a new row of Spots on the left of the Grid
			// System.out.println("Growing Left");
			
			Spot s = topLeftSpot;
			
			s.leftSpot = new Spot(grid, s.x-1, s.y);
			s.leftSpot.rightSpot = s;
			
			while (s.hasDown()) {
				s = s.down();
				s.leftSpot = new Spot(grid, s.x-1, s.y);
				s.leftSpot.rightSpot = s;
				s.leftSpot.upSpot = s.upSpot.leftSpot;
				s.upSpot.leftSpot.downSpot = s.leftSpot;
			}
			
			topLeftSpot = topLeftSpot.leftSpot;
		}

		public void growRight() {
			// Add a new row of Spots on the right of the Grid
			// System.out.println("Growing Right");

			Spot s = bottomRightSpot;
			
			s.rightSpot = new Spot(grid, s.x+1, s.y);
			s.rightSpot.leftSpot = s;
			
			while (s.hasUp()) {
				s = s.up();
				s.rightSpot = new Spot(grid, s.x+1, s.y);
				s.rightSpot.leftSpot = s;
				s.rightSpot.downSpot = s.downSpot.rightSpot;
				s.downSpot.rightSpot.upSpot = s.rightSpot;
			}
			
			bottomRightSpot = bottomRightSpot.rightSpot;
		}

		public void growUp() {
			// Add a new row of Spots on the left of the Grid
			// System.out.println("Growing Up");

			Spot s = topLeftSpot;
			
			s.upSpot = new Spot(grid, s.x, s.y-1);
			s.upSpot.downSpot = s;
			
			while (s.hasRight()) {
				s = s.right();
				s.upSpot = new Spot(grid, s.x, s.y-1);
				s.upSpot.downSpot = s;
				s.upSpot.leftSpot = s.leftSpot.upSpot;
				s.leftSpot.upSpot.rightSpot = s.upSpot;
			}
			
			topLeftSpot = topLeftSpot.upSpot;
		}

		public void growDown() {
			// System.out.println("Growing Down");

			// Add a new row of Spots on the right of the Grid
			
			Spot s = bottomRightSpot;
			
			s.downSpot = new Spot(grid, s.x, s.y+1);
			s.downSpot.upSpot = s;
			
			while (s.hasLeft()) {
				s = s.left();
				s.downSpot = new Spot(grid, s.x, s.y+1);
				s.downSpot.upSpot = s;
				s.downSpot.rightSpot = s.rightSpot.downSpot;
				s.rightSpot.downSpot.leftSpot = s.downSpot;
			}
			
			bottomRightSpot = bottomRightSpot.downSpot;
		}

		public void moveHeadDown(int distance) {
			// headSpot.addAction("#");
			headSpot = headSpot.down();
			// headSpot.addAction("H");
			grid.moveTailTowardHead();

			if (distance > 1 ) { moveHeadDown(distance - 1); }
		}
		
		public void moveHeadUp(int distance) {
			// headSpot.addAction("#");
			headSpot = headSpot.up();
			// headSpot.addAction("H");
			grid.moveTailTowardHead();

			if (distance > 1 ) { moveHeadUp(distance - 1); }
		}
		
		public void moveHeadLeft(int distance) {
			// headSpot.addAction("#");
			headSpot = headSpot.left();
			// headSpot.addAction("H");
			grid.moveTailTowardHead();

			if (distance > 1 ) { moveHeadLeft(distance - 1); }
		}
		
		public void moveHeadRight(int distance) {
			// headSpot.addAction("#");
			headSpot = headSpot.right();
			// headSpot.addAction("H");
			grid.moveTailTowardHead();

			if (distance > 1 ) { moveHeadRight(distance - 1); }
		}

		public void moveTailDown(int distance) {
			tailSpot.addAction(".");
			tailSpot = tailSpot.down();
			tailSpot.addAction("T");
			if (distance > 1 ) { moveTailDown(distance - 1); }
		}
		
		public void moveTailUp(int distance) {
			tailSpot.addAction(".");
			tailSpot = tailSpot.up();
			tailSpot.addAction("T");
			if (distance > 1 ) { moveTailUp(distance - 1); }
		}
		
		public void moveTailLeft(int distance) {
			tailSpot.addAction(".");
			tailSpot = tailSpot.left();
			tailSpot.addAction("T");
			if (distance > 1 ) { moveTailLeft(distance - 1); }
		}
		
		public void moveTailRight(int distance) {
			tailSpot.addAction(".");
			tailSpot = tailSpot.right();
			tailSpot.addAction("T");
			if (distance > 1 ) { moveTailRight(distance - 1); }
		}

		public String spotToString(Spot s) {
			if ( s.equals(headSpot) ) {
				return "H";
			} else if (s.equals(tailSpot)) {
				return "T";
			} else {
				return s.toString();
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			Spot s = topLeftSpot;
			
			Spot r = s;
			sb.append(spotToString(r));
			
			while (r.hasRight()) {
				r = r.right();
				sb.append(spotToString(r));
			}
			sb.append("\n");
			
			while (s.hasDown()) {
				s = s.down();
				r = s;
				sb.append(spotToString(r));
				
				while (r.hasRight()) {
					r = r.right();
					sb.append(spotToString(r));
				}
				sb.append("\n");
			}
			
			return sb.toString();
		}

		public int countVisitedByTail() {
			int count = 0;
			Spot s = topLeftSpot;
			Spot r = s;
			if (r.visitedByTail) count++;
			
			while (r.hasRight()) {
				r = r.right();
				if (r.visitedByTail) count++;
			}

			while (s.hasDown()) {
				s = s.down();
				r = s;
				if (r.visitedByTail) count++;

				while (r.hasRight()) {
					r = r.right();
					if (r.visitedByTail) count++;
				}
			}

			return count;
		}
		
		public void moveTailTowardHead() {
			int xDist = headSpot.x - tailSpot.x;
			int yDist = headSpot.y - tailSpot.y;
			int xMovement = Integer.signum(xDist);
			int yMovement = Integer.signum(yDist);

			if (Math.abs(xDist) > 1 || Math.abs(yDist) > 1) {
				System.out.println( "Head is at (" + headSpot.x + "," + headSpot.y 
						+ "), tail is at (" + tailSpot.x + "," + tailSpot.y 
						+ "), moving (" + xMovement + "," + yMovement + ")");

				if (xMovement == 1 && yMovement == 1) {
					// Move Right & Down
					tailSpot = tailSpot.right();
					tailSpot = tailSpot.down();
				} else if (xMovement == 1 && yMovement == 0) {
					// Move Right
					tailSpot = tailSpot.right();
				} else if (xMovement == 1 && yMovement == -1) {
					// Move Right & Up
					tailSpot = tailSpot.right();
					tailSpot = tailSpot.up();
				} else if (xMovement == 0 && yMovement == -1) {
					// Move Up
					tailSpot = tailSpot.up();
				} else if (xMovement == -1 && yMovement == -1) {
					// Move Left & Up
					tailSpot = tailSpot.left();
					tailSpot = tailSpot.up();
				} else if (xMovement == -1 && yMovement == 0) {
					// Move Left
					tailSpot = tailSpot.left();
				} else if (xMovement == -1 && yMovement == 1) {
					// Move Left & Down
					tailSpot = tailSpot.left();
					tailSpot = tailSpot.down();
				} else if (xMovement == 0 && yMovement == 1) {
					// Move Down
					tailSpot = tailSpot.down();
				} else {
					// Don't move
				}
				
				tailSpot.addAction("T");

				System.out.println("New Tail is at (" + tailSpot.x + "," + tailSpot.y + ")");
			} else {
				System.out.println("Not moving Tail");
			}
		}
	}
	
	private static Grid grid = new Grid();
	
	public static final void main(String[] args) throws IOException {
		System.out.println("December  9: Rope Bridge");

		BufferedReader reader = new BufferedReader(new FileReader("data/Data9"));
		String line = reader.readLine();
		
		while (line != null) {
			process(line);
			line = reader.readLine();
		}
		
		reader.close();
		
		System.out.println("Size of grid: " + grid.bottomRightSpot.x + " by " + grid.bottomRightSpot.y);

		System.out.println("Number of spots visited by Tail: " + grid.countVisitedByTail());
	}
	
	public static void process(String line) {
		String[] parts = line.split(" ");
		String direction = parts[0];
		int distance = Integer.parseInt(parts[1]);
		
		System.out.println("Processing " + direction + " " + distance);

		switch (parts[0]) {
		case "D": grid.moveHeadDown(distance); break;
		case "U": grid.moveHeadUp(distance); break;
		case "R": grid.moveHeadRight(distance); break;
		case "L": grid.moveHeadLeft(distance); break;
		default: throw new IllegalArgumentException("Unknown Direction '" + direction + "'");	
		}
		
		System.out.println(grid);
	}
}
