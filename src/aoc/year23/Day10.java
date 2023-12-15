package aoc.year23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aoc.util.ParseException;
import aoc.util.PuzzleApp;

public class Day10 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 10: Pipe Maze");
		PuzzleApp app = new Day10();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day10-part1";
	}
	
	private Maze maze = new Maze();
	
	public void parseLine(String line) {
		maze.addRow(line);
	}
	
	public boolean same(int[] posA, int[] posB) {
		return posA[0] == posB[0] && posA[1] == posB[1];
	}
	
	private int stepsA = 1;
	private int stepsB = 1;

	private Maze cleanMaze;
	
	public void process() {
		cleanMaze = new Maze(maze);

		// Find starting position:
		int[] start = maze.findCell(Maze.START);
		
		System.out.println("Found START at " + start[0] + "," + start[1]);
		
		Direction[] dirs = maze.getDirections(start);
		Set<Direction> dirSet = Set.of(dirs);
		System.out.println("Valid directions are " + dirSet);

		// Need to put the appropriate symbol on the START location in cleanMaze:

		if (dirSet.contains(Direction.NORTH) && dirSet.contains(Direction.SOUTH)) {
			cleanMaze.setCell(start, Maze.NORTH_SOUTH);
		} else if (dirSet.contains(Direction.NORTH) && dirSet.contains(Direction.WEST)) {
			cleanMaze.setCell(start, Maze.NORTH_WEST);
		} else if (dirSet.contains(Direction.NORTH) && dirSet.contains(Direction.EAST)) {
			cleanMaze.setCell(start, Maze.NORTH_EAST);
		} else if (dirSet.contains(Direction.SOUTH) && dirSet.contains(Direction.EAST)) {
			cleanMaze.setCell(start, Maze.SOUTH_EAST);
		} else if (dirSet.contains(Direction.SOUTH) && dirSet.contains(Direction.WEST)) {
			cleanMaze.setCell(start, Maze.SOUTH_WEST);
		} else if (dirSet.contains(Direction.EAST) && dirSet.contains(Direction.WEST)) {
			cleanMaze.setCell(start, Maze.EAST_WEST);
		}

		
		int[] prevPosA = start;
		int[] prevPosB = start;
		int[] posA = maze.move(start, dirs[0]);
		int[] posB = maze.move(start, dirs[1]);
		
		cleanMaze.setCell(posA, maze.getCell(posA));
		cleanMaze.setCell(posB, maze.getCell(posB));
		
		while (!same(posA, posB) && !same(posA, prevPosB) && !same(posB, prevPosA)) {
			int[] newPosA = maze.step(posA, prevPosA);
			prevPosA = posA;
			posA = newPosA;
			stepsA++;
			
			int[] newPosB = maze.step(posB, prevPosB);
			prevPosB = posB;
			posB = newPosB;
			stepsB++;

			// System.out.println("current position: " + posA[0] + "," + posA[1] + " " + posB[0] + "," + posB[1]);

			cleanMaze.setCell(posA, maze.getCell(posA));
			cleanMaze.setCell(posB, maze.getCell(posB));
		}

		System.out.println("Final position: " + posA[0] + "," + posA[1] + " " + posB[0] + "," + posB[1]);
	}
	
	public void results() {
		System.out.println("Part 1: Steps taken = " + stepsA + " (" + stepsB + ")");
	

		// Now traverse the "clean" maze, to figure out which cells are Inside vs Outside:
		int insideCount = 0;
		
		for (int r = 0; r < cleanMaze.rows.size(); r++) {
			for (int c = 0; c < cleanMaze.rows.get(0).length(); c++) {
				if (cleanMaze.getCell(r, c) != Maze.GROUND) continue;

				// Count the maze traversals to the NORTH:
				int northTraversals = 0;
				boolean entryWest = false;
				boolean entryEast = false;
				
				for (int x = r; x >= 0; x--) {
					char cell = cleanMaze.getCell(x, c);
					switch (cell) {
					case Maze.EAST_WEST: 
						northTraversals++; 
						break;
					case Maze.NORTH_EAST: 
						entryEast = true; 
						break;
					case Maze.NORTH_WEST: 
						entryWest = true; 
						break;
					case Maze.SOUTH_EAST: 
						if (entryWest) northTraversals++; 
						entryEast = false; 
						entryWest = false; 
						break;
					case Maze.SOUTH_WEST: 
						if (entryEast) northTraversals++; 
						entryEast = false; 
						entryWest = false; 
						break;
					case Maze.NORTH_SOUTH:
						// Nothing to count
						break;
					case Maze.GROUND:
						// Nothing to count
						break;
					default:
						throw new ParseException("Encountered " + cell + " while travelling NORTH");
					}
				}
				
				// Count the maze traversals to the SOUTH:
				int southTraversals = 0;
				entryWest = false;
				entryEast = false;
				
				for (int x = r; x < cleanMaze.rows.size(); x++) {
					char cell = cleanMaze.getCell(x, c);
					switch (cell) {
					case Maze.EAST_WEST: 
						southTraversals++; 
						break;
					case Maze.SOUTH_EAST: 
						entryEast = true; 
						break;
					case Maze.SOUTH_WEST: 
						entryWest = true; 
						break;
					case Maze.NORTH_EAST: 
						if (entryWest) southTraversals++; 
						entryEast = false; 
						entryWest = false; 
						break;
					case Maze.NORTH_WEST: 
						if (entryEast) southTraversals++; 
						entryEast = false; 
						entryWest = false; 
						break;
					case Maze.NORTH_SOUTH:
						// Nothing to count
						break;
					case Maze.GROUND:
						// Nothing to count
						break;
					default:
						throw new ParseException("Encountered " + cell + " while travelling SOUTH");
					}
				}

				// Count the maze traversals to the WEST:
				int westTraversals = 0;
				boolean entrySouth = false;
				boolean entryNorth = false;
				
				for (int y = c; y >= 0; y--) {
					char cell = cleanMaze.getCell(r, y);
					switch (cell) {
					case Maze.NORTH_SOUTH: 
						westTraversals++; 
						break;
					case Maze.NORTH_WEST: 
						entryNorth = true; 
						break;
					case Maze.SOUTH_WEST: 
						entrySouth = true; 
						break;
					case Maze.NORTH_EAST: 
						if (entrySouth) westTraversals++; 
						entryNorth = false; 
						entrySouth = false; 
						break;
					case Maze.SOUTH_EAST: 
						if (entryNorth) westTraversals++; 
						entryNorth = false; 
						entrySouth = false; 
						break;
					case Maze.EAST_WEST:
						// Nothing to count
						break;
					case Maze.GROUND:
						// Nothing to count
						break;
					default:
						throw new ParseException("Encountered " + cell + " while travelling WEST");
					}
				}
				
				// Count the maze traversals to the EAST:
				int eastTraversals = 0;
				entrySouth = false;
				entryNorth = false;
				
				for (int y = c; y < cleanMaze.rows.get(0).length(); y++) {
					char cell = cleanMaze.getCell(r, y);
					switch (cell) {
					case Maze.NORTH_SOUTH: 
						eastTraversals++; 
						break;
					case Maze.NORTH_EAST: 
						entryNorth = true; 
						break;
					case Maze.SOUTH_EAST: 
						entrySouth = true; 
						break;
					case Maze.NORTH_WEST: 
						if (entrySouth) eastTraversals++; 
						entryNorth = false; 
						entrySouth = false; 
						break;
					case Maze.SOUTH_WEST: 
						if (entryNorth) eastTraversals++; 
						entryNorth = false; 
						entrySouth = false; 
						break;
					case Maze.EAST_WEST:
						// Nothing to count
						break;
					case Maze.GROUND:
						// Nothing to count
						break;
					default:
						throw new ParseException("Encountered " + cell + " while travelling WEST");
					}
				}

				if (northTraversals % 2 == 1
						|| southTraversals % 2 == 1
						|| westTraversals % 2 == 1
						|| eastTraversals % 2 == 1 ) {
					System.out.println("Cell at " + r + "," + c + " is INSIDE (" + northTraversals + "," + southTraversals + "," + westTraversals + "," + eastTraversals + ")");
					insideCount++;
				}
			}
		}
		
		System.out.println("Cells inside the map = " + insideCount);
	}
	
	class Maze {
		private static final char START = 'S';
		private static final char NORTH_SOUTH = '|';
		private static final char EAST_WEST = '-';
		private static final char NORTH_EAST = 'L';
		private static final char NORTH_WEST = 'J';
		private static final char SOUTH_WEST = '7';
		private static final char SOUTH_EAST = 'F';
		private static final char GROUND = '.';

		private List<String> rows = new ArrayList<>();
		
		public Maze() {
			
		}
		
		/*
		 * Create a new empty maze with the same size as the given maze.
		 */
		public Maze(Maze m) {
			String s = "";
			for (int c = 0; c < m.rows.get(0).length(); c++) {
				s = s + ".";
			}
			for (int i = 0; i < m.rows.size(); i++) {
				this.addRow(s); // This is ok because Strings are immutable
			}
		}
		public void addRow(String row) {
			rows.add(row);
		}
		
		public char getCell(int r, int c) {
			if (r < 0 || r >= rows.size() || c < 0 || c >= rows.get(0).length()) return GROUND;
			return rows.get(r).charAt(c);
		}
		
		public char getCell(int[] pos) {
			return getCell(pos[0], pos[1]);
		}
		
		public void setCell(int[] pos, char newValue) {
			String s = rows.get(pos[0]);
			s = s.substring(0, pos[1]) + newValue + s.substring(pos[1] + 1);
			rows.set(pos[0], s);
		}
		
		public Direction[] getDirections(int[] pos) {
			Set<Direction> d = new HashSet<>();
			
			switch(getCell(pos)) {
			case START:
				// Direction has to be determined by examining neighbors
				char northCell = getCell(pos[0]-1, pos[1]);
				if (NORTH_SOUTH == northCell || SOUTH_EAST == northCell || SOUTH_WEST == northCell) {
					d.add(Direction.NORTH);
				}
				char westCell = getCell(pos[0], pos[1]-1);
				if (EAST_WEST == westCell || NORTH_EAST == westCell || SOUTH_EAST == westCell) {
					d.add(Direction.WEST);
				}
				char southCell = getCell(pos[0]+1, pos[1]);
				if (NORTH_SOUTH == southCell || NORTH_EAST == southCell || NORTH_WEST == southCell) {
					d.add(Direction.SOUTH);
				}
				char eastCell = getCell(pos[0], pos[1]+1);
				if (EAST_WEST == eastCell || NORTH_WEST == eastCell || SOUTH_WEST == eastCell) {
					d.add(Direction.EAST);
				}
				break;
			case NORTH_SOUTH:
				d.add(Direction.NORTH);
				d.add(Direction.SOUTH);
				break;
			case EAST_WEST:
				d.add(Direction.EAST);
				d.add(Direction.WEST);
				break;
			case NORTH_EAST:
				d.add(Direction.NORTH);
				d.add(Direction.EAST);
				break;
			case NORTH_WEST:
				d.add(Direction.NORTH);
				d.add(Direction.WEST);
				break;
			case SOUTH_EAST:
				d.add(Direction.SOUTH);
				d.add(Direction.EAST);
				break;
			case SOUTH_WEST:
				d.add(Direction.SOUTH);
				d.add(Direction.WEST);
				break;
			case GROUND:
				break;
			default:
				throw new ParseException("Unsupported map character '" + getCell(pos) + "'");
			}
			
			return d.toArray(new Direction[0]);
		}
		
		public Direction getDirection(int[] fromPos, int[] toPos) {
			if (toPos[0] - fromPos[0] == 1) return Direction.SOUTH;
			if (toPos[0] - fromPos[0] == -1) return Direction.NORTH;
			if (toPos[1] - fromPos[1] == 1) return Direction.EAST;
			if (toPos[1] - fromPos[1] == -1) return Direction.WEST;
			throw new IllegalArgumentException("Cannot determine direction from " + fromPos[0] + "," + fromPos[1] + " to " + toPos[0] + "," + toPos[1]);
		}
		
		public int[] move(int[] pos, Direction d) {
			int[] newPos;
			switch (d) {
				case NORTH: newPos = new int[] { pos[0]-1, pos[1] }; break;
				case SOUTH: newPos = new int[] { pos[0]+1, pos[1] }; break;
				case EAST: newPos = new int[] { pos[0], pos[1]+1 }; break;
				case WEST: newPos = new int[] { pos[0], pos[1]-1 }; break;
				default: newPos = new int[0];
			}
			return newPos;
		}
		
		/*
		 * Take a step from the current position that's NOT back to the previous position.
		 */
		public int[] step(int[] pos, int[] prevPos) {
			Set<Direction> dirs = new HashSet<>(Arrays.asList(getDirections(pos)));
			Direction directionBack = getDirection(pos, prevPos);
			dirs.remove(directionBack);
			
			if (dirs.size() != 1) {
				throw new ParseException("Can't step from " + pos[0] + "," + pos[1] + "!!");
			}
			
			Direction newDir = dirs.toArray(new Direction[0])[0];
			int[] newPos = new int[0];
			
			switch (newDir) {
				case NORTH: newPos = new int[] { pos[0]-1, pos[1] }; break;
				case SOUTH: newPos = new int[] { pos[0]+1, pos[1] }; break;
				case EAST: newPos = new int[] { pos[0], pos[1]+1 }; break;
				case WEST: newPos = new int[] { pos[0], pos[1]-1 }; break;
				default: newPos = new int[0];
			}
			
			return newPos;
		}
		
		public int[] findCell(char value) {
			for (int r = 0; r < rows.size(); r++) {
				int c = rows.get(r).indexOf(value);
				if (c >= 0) return new int[] {r, c};
			}
			return new int[0]; // zero-length array
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < rows.size(); i++) {
				sb.append(rows.get(i)).append("\n");
			}
			return sb.toString();
		}
	}
	
	enum Direction {
		NORTH, SOUTH, EAST, WEST;
	}
}
