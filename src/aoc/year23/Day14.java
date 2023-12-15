package aoc.year23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import aoc.util.PuzzleApp;

public class Day14 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 14: Parabolic Reflector Dish");
		PuzzleApp app = new Day14();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day14-part1";
	}
	
	private Grid grid = new Grid();
	
	public void parseLine(String line) {
		grid.addRow(line);
	}
	
	private static final char ROCK = 'O';
	private static final char SPACE = '.';

	private void tiltNorth(Grid g) {
		for (int r = 0; r < g.height(); r++) {
			for (int c = 0; c < g.width(); c++) {
				if (g.at(r, c) == ROCK) {
					for (int src = r, dst = r-1; dst >= 0 && g.at(dst, c) == SPACE; src--, dst--) {
						g.swap(src, c, dst, c);
					}
				}
			}
		}
	}

	private void tiltWest(Grid g) {
		for (int c = 0; c < g.width(); c++) {
			for (int r = 0; r < g.height(); r++) {
				if (g.at(r, c) == ROCK) {
					for (int src = c, dst = c-1; dst >= 0 && g.at(r, dst) == SPACE; src--, dst--) {
						g.swap(r, src, r, dst);
					}
				}
			}
		}
	}

	private void tiltSouth(Grid g) {
		for (int r = g.height()-1; r >= 0; r--) {
			for (int c = 0; c < g.width(); c++) {
				if (g.at(r, c) == ROCK) {
					for (int src = r, dst = r+1; dst < g.height() && g.at(dst, c) == SPACE; src++, dst++) {
						g.swap(src, c, dst, c);
					}
				}
			}
		}
	}

	private void tiltEast(Grid g) {
		for (int c = g.width()-1; c >= 0; c--) {
			for (int r = 0; r < g.height(); r++) {
				if (g.at(r, c) == ROCK) {
					for (int src = c, dst = c+1; dst < g.width() && g.at(r, dst) == SPACE; src++, dst++) {
						g.swap(r, src, r, dst);
					}
				}
			}
		}
	}

	private void cycle(Grid g) {
		tiltNorth(g);
		tiltWest(g);
		tiltSouth(g);
		tiltEast(g);
	}
	
	/* 
	 * Load is calculated from the "bottom" of the grid, so the load at row r is grid.height() - r
	 */
	private int totalLoad(Grid g) {
		int load = 0;
		
		for (int r = 0; r < g.height(); r++) {
			for (int c = 0; c < g.width(); c++) {
				if (g.at(r, c) == ROCK) {
					load += g.height() - r;
				}
			}
		}
		
		return load;
	}
	
	public void processPart1() {
		System.out.println("Part 1: Tilting Grid North");
		tiltNorth(grid);
		System.out.println(grid);
		System.out.println("Part 1: total = " + totalLoad(grid));
	}
	
	private static final int MAX_ITERATIONS = 1000000000;
	
	private Map<Integer, Grid> grids = new HashMap<>();
	private int startOfRepeat = 0;
	private int lengthOfRepeat = 0;

	public void process() {
		grids.put(0, new Grid(grid));
		int iteration = 1;
		
		// Find the first iteration:
		
		while (iteration < MAX_ITERATIONS && startOfRepeat == 0) {
			cycle(grid);
			
			if (grids.containsValue(grid)) {
				System.out.println("Grids start repeating at " + iteration);
				break;
			} else {
				grids.put(iteration, new Grid(grid));
			}
			iteration++;
		}
		
		Grid save = new Grid(grid);
		
		// Find the loop size:
		
		do {
			cycle(grid);
			lengthOfRepeat++;
		} while (!grid.equals(save));
			
		System.out.println("Grids loop size " + lengthOfRepeat);

		startOfRepeat = iteration - lengthOfRepeat;
	}
	
	public void results() {
		int whichGrid = startOfRepeat + (MAX_ITERATIONS - startOfRepeat) % lengthOfRepeat;
		System.out.println("Grid at " + MAX_ITERATIONS + " should be same as grid at " + whichGrid);
		Grid answer = grids.get(whichGrid);
		// System.out.println(answer);
		System.out.println("Total load for grid " + whichGrid + " is " + totalLoad(answer));
	}
	
	class Grid {
		private List<String> rows = new ArrayList<>();
		
		public Grid() {
		}
		
		public Grid(Grid other) {
			for (int i = 0; i < other.rows.size(); i++) {
				this.rows.add(other.rows.get(i));
			}
		}
		
		public void addRow(String row) {
			rows.add(row);
		}
		
		public int height() {
			return rows.size();
		}
		
		public int width() {
			return rows.get(0).length();
		}
		
		public char at(int r, int c) {
			return rows.get(r).charAt(c);
		}
		
		public void set(int r, int c, char x) {
			String line = rows.remove(r);
			line = line.substring(0,c) + x + line.substring(c+1);
			rows.add(r, line);
		}
		
		public void swap(int startR, int startC, int destR, int destC) {
			char x = at(destR, destC);
			set(destR, destC, at(startR, startC));
			set(startR, startC, x);
			
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			rows.forEach(l -> sb.append(l).append('\n'));
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(rows);
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
			Grid other = (Grid) obj;
			return Objects.equals(rows, other.rows);
		}
	}
}