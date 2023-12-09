package aoc.year22;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aoc.util.PuzzleApp;

public class MonkeyMap extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 22: Monkey Map");
		PuzzleApp app = new MonkeyMap();
		app.run();
	}
	
	public String filename() {
		return "data/data22";
	}

	private static final boolean CUBE_WRAP = true;
	
	private List<String> mapLines = new ArrayList<>();
	private int widestLine = 0;
	private String pathLine;
	
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
	
	Grid map = null;
	List<String> path = new ArrayList<>();
	
	char[][] rotate(char[][] subgrid, int size) {
		char[][] newSubgrid = new char[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newSubgrid[i][j] = subgrid[size-j-1][i];
			}
		}
		return newSubgrid;
	}
	
	Grid convert(Grid oldGrid) {
		// Change the "big" map into the "small" map format! Crazy...
		
		int sideLength = widestLine / 3;
		
		char[][] empty =               oldGrid.subgrid(0 * sideLength, 0 * sideLength, sideLength);
		char[][] sideA =               oldGrid.subgrid(0 * sideLength, 1 * sideLength, sideLength);
		char[][] sideB =        rotate(oldGrid.subgrid(sideLength * 3, sideLength * 0, sideLength), sideLength);
		char[][] sideC =        rotate(oldGrid.subgrid(sideLength * 2, sideLength * 0, sideLength), sideLength);
		char[][] sideD =               oldGrid.subgrid(sideLength * 1, sideLength * 1, sideLength);
		char[][] sideE =               oldGrid.subgrid(sideLength * 2, sideLength * 1, sideLength);
		char[][] sideF = rotate(rotate(oldGrid.subgrid(sideLength * 0, sideLength * 2, sideLength), sideLength), sideLength);

		Grid newGrid = new Grid(sideLength * 3, sideLength * 4);
		
		newGrid.setTiles(empty, 0 * sideLength, 0 * sideLength, sideLength);
		newGrid.setTiles(empty, 0 * sideLength, 1 * sideLength, sideLength);
		newGrid.setTiles(sideA, 0 * sideLength, 2 * sideLength, sideLength);
		newGrid.setTiles(empty, 0 * sideLength, 3 * sideLength, sideLength);

		newGrid.setTiles(sideB, 1 * sideLength, 0 * sideLength, sideLength);
		newGrid.setTiles(sideC, 1 * sideLength, 1 * sideLength, sideLength);
		newGrid.setTiles(sideD, 1 * sideLength, 2 * sideLength, sideLength);
		newGrid.setTiles(empty, 1 * sideLength, 3 * sideLength, sideLength);

		newGrid.setTiles(empty, 2 * sideLength, 0 * sideLength, sideLength);
		newGrid.setTiles(empty, 2 * sideLength, 1 * sideLength, sideLength);
		newGrid.setTiles(sideE, 2 * sideLength, 2 * sideLength, sideLength);
		newGrid.setTiles(sideF, 2 * sideLength, 3 * sideLength, sideLength);

		System.out.println(newGrid);
		return newGrid;
	}
	
	
	public void process() {

		if (CUBE_WRAP) {
			map = new CubeGrid(mapLines, widestLine);
			if (widestLine > 50) map = convert(map);
		} else {
			map = new Grid(mapLines, widestLine);
		}
		
		// System.out.println(map);
		
		String[] steps = pathLine.split("[RL]");
		String[] turns = pathLine.split("\\d+");
		
		path.add(steps[0]);
		for(int i = 1; i < steps.length; i++) {
			path.add(turns[i]);
			path.add(steps[i]);
		}
		
		// System.out.println(path);
		
		Location me = new Location(map);
		
		// System.out.println(me);
		
		for (String step : path) {
			// System.out.println("Before '" + step + "': " + me);

			switch (step) {
				case "R", "L": me.turn(step); break;
				default: me = me.move(Integer.parseInt(step));
			}
			
			// System.out.println("After '" + step + "': " + me);
		}
		
		System.out.println("Final position: " + me);
		
		System.out.println("Part 1 result: " + ((me.row()+1) * 1000 + (me.col()+1) * 4 + me.facing().intValue()));

		// small map result: 6032 [6,8,RIGHT]
		// part 1 result: 126350 [126,87,LEFT]
		// part 2 result: 
			// 63406 is too low [63,101,LEFT]
	}
	
	public void results() {
		
	}
	
	class Location {
		private Grid map;
		private int row;
		private int col;
		private Direction facing;
		
		public Location(Grid map) {
			this.map = map;
			this.row = 0;
			this.col = 0;
			while (map.isOutside(row,col)) col++;
			this.facing = Direction.RIGHT;
		}
		
		public Location(Grid map, int row, int col, Direction facing) {
			this.map = map;
			this.row = row;
			this.col = col;
			this.facing = facing;
		}
		
		public int row() {
			return row;
		}
		
		public int col() {
			return col;
		}
		
		public void setRow(int row) {
			this.row = row;
		}
		
		public void setCol(int col) {
			this.col = col;
		}
		
		public Direction facing() {
			return facing;
		}
		
		public void setFacing(Direction facing) {
			this.facing = facing;
		}
		
		public Location move(int steps) {
			Location loc = this;
			for (int i = 0; i < steps; i++) {
				loc = map.step(loc);
				// System.out.println(loc);
			}
			
			return loc;
		}
		
		public Location turn(String direction) {
			switch (direction) {
			case "R":
				facing = facing.turnRight();
				break;
			case "L":
				facing = facing.turnLeft();
				break;
			default:
				throw new IllegalArgumentException("Unknown direction '" + direction + "'");
			}
			return this;
		}
		
		public String toString() {
			return "[" + (row+1) + "," + (col+1) + "," + facing + "]";
		}
	}
	
	enum Direction {
		RIGHT(0), DOWN(1), LEFT(2), UP(3);

		private static final Map<Integer, Direction> BY_INTVALUE = new HashMap<>();

		static {
			for (Direction d : values()) {
				BY_INTVALUE.put(d.intValue(), d);
			}
		}

		public static Direction withIntValue(int intValue) {
			return BY_INTVALUE.get(intValue);
		}

		private int intValue;

		private Direction(int intValue) {
			this.intValue = intValue;
		}

		public int intValue() {
			return intValue;
		}
		
		public Direction turnRight() {
			return Direction.withIntValue((this.intValue + 1) % 4);
		}
		
		public Direction turnLeft() {
			return Direction.withIntValue((this.intValue + 3) % 4);
		}		
	}
	
	class Grid {
		private static final char OUTSIDE = ' ';
		private static final char EMPTY = '.';
		private static final char WALL = '#';
		
		private int width;
		private int height;
		char[][] tiles;
		
		public Grid(int height, int width) {
			this.width = width;
			this.height = height;
			tiles = new char[height][width];
		}
		
		public Grid(List<String> lines, int widestLine) {
			this.height = lines.size();
			this.width = widestLine;
			tiles = new char[height][width];
			
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					if (col < lines.get(row).length()) {
						tiles[row][col] = lines.get(row).charAt(col);
					} else {
						tiles[row][col] = OUTSIDE;
					}
				}
			}
		}
		
		public int width() {
			return width;
		}
		
		public int height() {
			return height;
		}
		
		public char[][] subgrid(int r, int c, int size) {
			char[][] subgrid = new char[size][size];
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					subgrid[i][j] = tiles[r+i][c+j];
				}
			}
			return subgrid;
		}
		
		public void setTiles(char[][] tiles, int r, int c, int size) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					this.tiles[r+i][c+j] = tiles[i][j];
				}
			}
		}
		
		public char at(int row, int col) {
			return tiles[row][col];
		}
		
		public Location step(Location loc) {
			int r = 0;
			int c = 0;
			
			switch (loc.facing()) {
			case RIGHT: c = 1;
				break;
			case DOWN: r = 1;
				break;
			case LEFT: c = -1;
				break;
			case UP: r = -1;
				break;
			}

			int newR = (loc.row() + r + height()) % height();
			int newC = (loc.col() + c + width()) % width();

			while (isOutside(newR, newC)) {
				if (r < 0) { if (--newR < 0) newR = height() - 1; }
				if (r > 0) { if (++newR >= height()) newR = 0; }
				if (c < 0) { if (--newC < 0) newC = width() - 1; }
				if (c > 0) { if (++newC >= width()) newC = 0; }
			}
				
			if (isEmpty(newR, newC)) {
				loc.setRow(newR);
				loc.setCol(newC);
			}
				
			return loc;
		}
		
		public boolean isOutside(int row, int col) {
			return at(row,col) == OUTSIDE;
		}

		public boolean isEmpty(int row, int col) {
			return at(row,col) == EMPTY;
		}
		
		public boolean isWall(int row, int col) {
			return at(row,col) == WALL;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					sb.append(tiles[row][col]);
				}
				
				sb.append('\n');
			}
			
			return sb.toString();
		}
	}
	
	class CubeGrid extends Grid {
		private int sideLength;
		
		public CubeGrid(List<String> lines, int widestLine) {
			super(lines, widestLine);
			sideLength = widestLine / 4;
		}
		
		// Label the cube sides like this:
		//		        1111
		//		        1111
		//		        1111
		//		        1111
		//		222233334444
		//		222233334444
		//		222233334444
		//		222233334444
		//		        55556666
		//		        55556666
		//		        55556666
		//		        55556666				
		//
		// Side 1 connects to sides 4 (Down->Down), 2 (Up->Down), 3 (Left->Down), 6 (Right->Left)
		// Side 2 connects to sides 3 (Right->Right), 1 (Up->Down), 5 (Down->Up), 6 (Left->Up)
		// Side 3 connects to sides 2 (Left->Left), 4 (Right->Right), 1 (Up->Right), 5 (Down->Right)
		// Side 4 connects to sides 1 (Up->Up), 3 (Left->Left), 5 (Down->Down), 6 (Right->Down)
		// Side 5 connects to sides 4 (Up->Up), 6 (Right->Right), 3 (Left->Up), 2 (Down->Up)
		// Side 6 connects to sides 5 (Left->Left), 4 (Up->Left), 1 (Right->Left), 2 (Down->Right)

		public Location step(Location loc) {
			int r = loc.row();
			int c = loc.col();
			Direction facing = loc.facing();
			
			switch (facing) {
			case RIGHT: c++;
				if (c < width()) {
					if (isEmpty(r,c)) { loc.setCol(c); return loc; }
					if (isWall(r,c)) { return loc; }
				}
								
				if (r < sideLength ) {				// Moving from side 1 to side 6 (R->L)
					r = sideLength * 3 - r - 1;		// Top row of side 1 maps to bottom row of side 6
					c = sideLength * 4 - 1;			// Enter at right edge of side 6
					facing = Direction.LEFT;
				} else if (r < sideLength * 2) {	// Moving from side 4 to side 6 (R->D)
					c = 5 * sideLength - r - 1;		// Top row of side 4 maps to right column of side 6
					r = sideLength * 2;				// Enter at top edge of side 6
					facing = Direction.DOWN;					
				} else if (r < sideLength * 3) {	// Moving from side 6 to side 1 (R->L)
					r = sideLength * 3 - r - 1;		// Top row of side 6 maps to bottom row of side 1
					c = sideLength * 3 - 1;			// Enter at right edge of side 1
					facing = Direction.LEFT;
				}
				
				if (isEmpty(r,c)) {
					return new Location(this,r,c,facing);
				} else if (isWall(r,c)) {
					return loc; // can't move
				} else {
					throw new IllegalArgumentException("Something went wrong when moving RIGHT");
				}

			case LEFT: c--;
				if (c >= 0) {
					if (isEmpty(r,c)) { loc.setCol(c); return loc; }
					if (isWall(r,c)) { return loc; }
				}

				if (r < sideLength ) {				// Moving from side 1 to side 3 (L->D)
					r = sideLength + c;				// Top row of side 1 maps to left column of side 3
					c = sideLength;					// Enter at top edge of side 3
					facing = Direction.DOWN;
				} else if (r < sideLength * 2) {	// Moving from side 2 to side 6 (L->U)
					c = sideLength * 5 - r - 1;		// Top row of side 2 maps to right column of side 6
					r = sideLength * 3 - 1;			// Enter at bottom edge of side 6
					facing = Direction.UP;					
				} else if (r < sideLength * 3) {	// Moving from side 5 to side 3 (L->U)
					c = sideLength * 4 - r - 1;		// Top row of side 5 maps to right edge of side 3
					r = sideLength * 2 - 1;			// Enter at bottom edge of side 3
					facing = Direction.UP;
				}

				if (isEmpty(r,c)) {
					return new Location(this,r,c,facing);
				} else if (isWall(r,c)) {
					return loc; // can't move
				} else {
					throw new IllegalArgumentException("Something went wrong when moving LEFT: ");
				}

			case UP: r--;
				if (r > 0) {
					if (isEmpty(r,c)) { loc.setRow(r); return loc; }
					if (isWall(r,c)) { return loc; }
				}
							
				if (c < sideLength ) {				// Moving from side 2 to side 1 (U->D)
					c = sideLength * 3 - c - 1;		// Left column of side 2 is right column of side 1
					r = 0;							// Enter at top edge of side 1
					facing = Direction.DOWN;
				} else if (c < sideLength * 2) {	// Moving from side 3 to side 1 (U->R)
					r = c - sideLength;				// Left edge of side 3 is top edge of side 1
					c = sideLength * 2;				// Enter at left edge of side 1
					facing = Direction.RIGHT;					
				} else if (c < sideLength * 3) {	// Moving from side 1 to side 2 (U->D)
					c = sideLength * 3 - c - 1;		// Right edge of side 1 is left edge of side 2
					r = sideLength;					// Enter at top edge of side 2
					facing = Direction.DOWN;
				} else if (c < sideLength * 4) {	// Moving from side 6 to side 4 (U->L)
					r = sideLength * 5 - c - 1;		// Right edge of side 6 is top row of side 4
					c = sideLength * 3 - 1;			// Enter at right edge of side 6
					facing = Direction.LEFT;
				}
			
				if (isEmpty(r,c)) {
					return new Location(this,r,c,facing);
				} else if (isWall(r,c)) {
					return loc; // can't move
				} else {
					throw new IllegalArgumentException("Something went wrong when moving UP");
				}

			case DOWN: r++;
				if (r < height()) {
					if (isEmpty(r,c)) { loc.setRow(r); return loc; }
					if (isWall(r,c)) { return loc; }
				}
						
				if (c < sideLength ) {				// Moving from side 2 to side 5 (D->U)
					c = sideLength * 3 - c - 1;		// Left edge of side 2 is right edge of side 5
					r = sideLength * 3 - 1;			// Enter at bottom edge of side 5
					facing = Direction.UP;
				} else if (c < sideLength * 2) {	// Moving from side 3 to side 5 (D->R)
					r = sideLength * 5 - c - 1;		// Left edge of side 3 is bottom edge of side 5
					c = sideLength * 2 - 1;			// Enter at right edge of side 5
					facing = Direction.RIGHT;					
				} else if (c < sideLength * 3) {	// Moving from side 5 to side 2 (D->U)
					c = sideLength * 3 - c - 1;		// Right edge of side 5 is left edge of side 2
					r = sideLength * 2 - 1;			// Enter at bottom edge of side 2
					facing = Direction.UP;
				} else if (c < sideLength * 4) {	// Moving from side 6 to side 2 (D->R)
					r = sideLength * 5 - c - 1;		// Right edge of side 6 is top edge of side 2
					c = 0;							// Enter at left edge of side 2
					facing = Direction.RIGHT;
				}
		
				if (isEmpty(r,c)) {
					return new Location(this,r,c,facing);
				} else if (isWall(r,c)) {
					return loc; // can't move
				} else {
					throw new IllegalArgumentException("Something went wrong when moving UP");
				}
				
			default:
				throw new IllegalArgumentException("This can't happen with an enum!");	
			}
		}
	}
}
