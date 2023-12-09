package aoc.year22;

import java.util.ArrayList;
import java.util.List;

import aoc.util.CubeSurface;
import aoc.util.Grid;
import aoc.util.PuzzleApp;

public class MonkeyMapWithCube extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 22: Monkey Map");
		PuzzleApp app = new MonkeyMapWithCube();
		app.run();
	}
	
	public String filename() {
		return "data/data22small";
	}
	
	private List<String> mapLines = new ArrayList<>();
	private int widestLine = 0;
	private String pathLine = new String();
	
	public void parseLine(String line) {
		if (!line.isEmpty()) {
			if (line.startsWith(" ") || line.startsWith("#") || line.startsWith(".")) {
				mapLines.add(line);
				if (line.length() > widestLine) widestLine = line.length();
			} else {
				pathLine = line;
			}
		}
	}
	
	Grid<Tile> grid;
	CubeSurface<Tile> cube;
	
	public void process() {
		// Populate the Grid:
		grid = createGrid(mapLines, widestLine);

		
		
		
		
		
		
		
		for (String step : path(pathLine)) {
//			System.out.println("Before '" + step + "': " + me);

//			switch (step) {
//			case "R", "L": me.turn(step); break;
//			default: me = me.move(Integer.parseInt(step));

//			System.out.println("After '" + step + "': " + me);
		}
		
//		System.out.println("Final position: " + me);
//		
//		System.out.println("Part 1 result: " + ((me.row()+1) * 1000 + (me.col()+1) * 4 + me.facing().intValue()));

	}
	
	List<String> path(String pathLine) {		
		String[] steps = pathLine.split("[RL]");
		String[] turns = pathLine.split("\\d+");
		
		List<String> path = new ArrayList<>();
		path.add(steps[0]);
		
		for(int i = 1; i < steps.length; i++) {
			path.add(turns[i]);
			path.add(steps[i]);
		}
		
		// System.out.println(path);
		return path;
	}
	
	private Grid<Tile> createGrid(List<String> mapLines, int widestLine) {
		// Populate the grid:
		grid = new Grid<>(mapLines.size(), widestLine);
				
		for (int row = 0; row < grid.height(); row++) {
			for (int col = 0; col < grid.width(); col++) {
				if (col < mapLines.get(row).length()) {
					grid.set(row, col, new Tile(row, col, mapLines.get(row).charAt(col)));
				} else {
					grid.set(row, col, new Tile(row, col, Tile.OUTSIDE));
				}
			}
		}

		return grid;
	}
	
	private CubeSurface<Tile> createCubeSurface(Grid<Tile> grid) {
		int cubeSize = (int) Math.sqrt(grid.height() * grid.width() / 12);
		cube = new CubeSurface<>(cubeSize);

		// Traverse the grid, assuming it's either 3x4 or 4x3, and assign blocks into the cube:
		// First, find the first nonempty square at the top of the grid:
		
		int startRow = 0;
		int startCol = 0;
		
		while (grid.get(0, cubeSize * startCol).value().isEmpty()) { startCol++; }

		cube.stitchTopFace(grid.subgrid(startRow, startCol, cubeSize));
		
		
		return cube;
	}
	
	public void results() {
		
	}
	
//	class Location {
//		private Grid map;
//		private int row;
//		private int col;
//		private Direction facing;
//		
//		public Location(Grid map) {
//			this.map = map;
//			this.row = 0;
//			this.col = 0;
//			while (map.isOutside(row,col)) col++;
//			this.facing = Direction.RIGHT;
//		}
//		
//		public Location(Grid map, int row, int col, Direction facing) {
//			this.map = map;
//			this.row = row;
//			this.col = col;
//			this.facing = facing;
//		}
//		
//		public int row() {
//			return row;
//		}
//		
//		public int col() {
//			return col;
//		}
//		
//		public void setRow(int row) {
//			this.row = row;
//		}
//		
//		public void setCol(int col) {
//			this.col = col;
//		}
//		
//		public Direction facing() {
//			return facing;
//		}
//		
//		public void setFacing(Direction facing) {
//			this.facing = facing;
//		}
//		
//		public Location move(int steps) {
//			Location loc = this;
//			for (int i = 0; i < steps; i++) {
//				loc = map.step(loc);
//				// System.out.println(loc);
//			}
//			
//			return loc;
//		}
//		
//		public Location turn(String direction) {
//			switch (direction) {
//			case "R":
//				facing = facing.turnRight();
//				break;
//			case "L":
//				facing = facing.turnLeft();
//				break;
//			default:
//				throw new IllegalArgumentException("Unknown direction '" + direction + "'");
//			}
//			return this;
//		}
//		
//		public String toString() {
//			return "[" + (row+1) + "," + (col+1) + "," + facing + "]";
//		}
//	}
	
	class Tile {
		private static final char OUTSIDE = ' ';
		private static final char EMPTY = '.';
		private static final char WALL = '#';

		int x; // original x location in input file
		int y; // original y location in input file
		char c;

		public Tile(int x, int y, char c) {
			this.x = x;
			this.y = y;
			this.c = c;
		}
		
		public int x() {
			return x;
		}
		
		public int y() {
			return y;
		}
		
		public char c() {
			return c;
		}
		
		public boolean isOutside() {
			return c == OUTSIDE;
		}

		public boolean isEmpty() {
			return c == EMPTY;
		}
		
		public boolean isWall() {
			return c == WALL;
		}
		
		public String toString() {
			return String.valueOf(c);
		}
	}
}