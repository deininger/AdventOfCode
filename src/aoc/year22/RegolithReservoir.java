package aoc.year22;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class RegolithReservoir extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 14: Regolith Reservoir");
		PuzzleApp app = new RegolithReservoir();
		app.run();
	}

	public String filename() {
		return "data/data14";
	}
	
	public void setup() {
	}

	private static final boolean HAS_FLOOR = true; // Floor is 2 units below lowest point in map
	
	private Loc min = new Loc(500,0);
	private Loc max = new Loc(500,0);
	private Loc size;
	
	private List<List<Loc>> paths = new ArrayList<>();
	
	void processLine(String line) {
		String[] parts = line.split(" -> ");
		List<Loc> path = new ArrayList<>();
		
		for (String part: parts) {
			String[] coordinates = part.split(",");
			int x = Integer.parseInt(coordinates[0]);
			int y = Integer.parseInt(coordinates[1]);
			Loc l = new Loc(x,y);
			path.add(l);
			
			if (x < min.x()) { min.setX(x); }
			if (y < min.y()) { min.setY(y); }
			if (x > max.x()) { max.setX(x); }
			if (y > max.y()) { max.setY(y); }
		}
		
		paths.add(path);
	}
	
	char[][] grid;
	
	void initializeGrid() {
		// Increase max Location by 1 to include outer edge of map:
		max = new Loc(max.x() + 1, max.y() + 1);
				
		
		if (HAS_FLOOR) {
			// Make the grid 2 bigger in the Y dimension,
			// and make it 2*Y bigger in the X direction!
			// This should be more than enough width to handle
			// the sand pileup from the floor.
			
			min = new Loc(min.x() - max.y() - 2, min.y());
			max = new Loc(max.x() + max.y() + 2, max.y() + 2);
		}
		
		size = new Loc(max.x() - min.x(), max.y() - min.y());

		System.out.println("Creating grid with dimensions " + size);

		grid = new char[size.x()][size.y()];

		for (int j = 0; j < size.y(); j++) {
			for (int i = 0; i < size.x(); i++) {
				grid[i][j] = '.';
			}
		}
		
		if (HAS_FLOOR) {
			for (int i = 0; i < size.x(); i++) {
				grid[i][size.y()-1] = '#';
			}
		}
		
		for(List<Loc> path : paths) {
			Deque<Loc> queue = new ArrayDeque<>(path);
			Loc start = queue.pop();
			while (!queue.isEmpty()) {
				Loc end = queue.pop();
				start.pathTo(end).forEach(l -> set(l,'#'));
				start = end;
			}
		}
	}
	
	public void process() {
		initializeGrid();
		Loc sandRestingSpot;
		
		do {
			sandRestingSpot = dropSand(new Loc(500,0));
			// printGrid();
		} 
		while (sandRestingSpot != null);
	}
	
	Loc dropSand(Loc l) {
		if (!l.within(min,max)) {
			return null; // Sand has dropped off edge of map
		}
		
		if (at(l) != '.') {
			return null; // Full of sand
		}
		
		Loc down = l.adjacentLoc("D");
		if (!down.within(min,max)) return null;

		if (at(down) == '.') {
			return dropSand(down);
		} 
		
		Loc downLeft = down.adjacentLoc("L");
		if (!downLeft.within(min,max)) return null;

		if (at(downLeft) == '.') {
			return dropSand(downLeft);
		}
		
		Loc downRight = down.adjacentLoc("R");
		if (!downRight.within(min,max)) return null;

		if (at(downRight) == '.') {
			return dropSand(downRight);
		} 
		
		set(l, '*'); // We've found our resting place;
		return l; 	
	}
	
	void set(Loc l, char c) {
		if (l.within(min,max)) {
			grid[l.x() - min.x()][l.y() - min.y()] = c;
		}
	}
	
	char at(Loc l) {
		if (l.within(min,max)) {
			return grid[l.x() - min.x()][l.y() - min.y()];
		} else {
			return '?';
		}
	}
	
	void printGrid() {
		for (int j = min.y(); j < max.y(); j++) {
			for (int i = min.x(); i < max.x(); i++) {
				System.out.print(at(new Loc(i,j)));
			}
			System.out.println();
		}
		System.out.println();
	}
	
	int countSand() {
		int count = 0;
		for (int j = min.y(); j < max.y(); j++) {
			for (int i = min.x(); i < max.x(); i++) {
				if (at(new Loc(i,j)) == '*') {
					count++;
				}
			}
		}
		return count;
	}
	
	public void results() {
		// System.out.println(" " + min + " " + max + " " + size);
		
		printGrid();
		
		System.out.println(countSand());
	}
}
