package aoc.year22;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class HillClimbingAlgorithm extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 12: Hill Climbing Algorithm");
		PuzzleApp app = new HillClimbingAlgorithm();
		app.run();
	}
	
	public String filename() {
		return "data/data12modified";
	}
	
	public void setup() {
	}

	private List<String> inputLines = new ArrayList<>();
	
	void processLine(String line) {
		inputLines.add(line);
	}
	
	private char[][] grid;
	private Loc gridSize;
	private Loc start;
	private Loc end;
	
	void printGrid() {
		for (int i = 0; i < gridSize.x(); i++) {
			for (int j = 0; j < gridSize.y(); j++) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}
	
	public void results() {
		gridSize = new Loc(inputLines.size(),inputLines.get(0).length());
		grid = new char[gridSize.x()][gridSize.y()];
		
		System.out.println("Initializing grid of size " + gridSize);
		
		for (int i = 0; i < gridSize.x(); i++) {
			String line = inputLines.get(i);
			for (int j = 0; j < gridSize.y(); j++) {
				grid[i][j] = line.charAt(j);
				
				if (grid[i][j] == 'S') {
					start = new Loc(i,j);
					grid[i][j] = 'a';
					// System.out.println("Starting point is " + start);
				} else if (grid[i][j] == 'E') {
					end = new Loc(i,j);
					grid[i][j] = 'z';
					// System.out.println("End point is " + end);
				}
				
				// printGrid();
			}
		}
		
		Set<Loc> alreadyTraversed = new HashSet<>();

		int pathLength = findShortestPath(0, start, alreadyTraversed);
		
		System.out.println("Shortest path length is " + pathLength);
	}
	
	private int height(Loc loc) {
		return grid[loc.x()][loc.y()] - 'a';
	}

	private boolean heightMatches(Loc from, Loc to) {
		return (height(from) - height(to)) <= 1;
	}
	
	private char at(Loc loc) {
		return grid[loc.x()][loc.y()];
	}

	private String locDesc(Loc loc) {
		return "'" + at(loc) + "'," + loc + "," + height(loc);
	}
	
	private int bestHeight = 0;
	private int bestDistance = 500;
	private int bestResult = 500;
	
	private int count = 0;
	
	private int findShortestPath(int level, Loc loc, Set<Loc> alreadyTraversed) {	
		if (loc.equals(end)) {
			bestResult = alreadyTraversed.size();
			System.out.println("Reached End! (level " + level + "): " + bestResult);
			System.out.println(level + " Path: " + alreadyTraversed.stream().map(l -> locDesc(l)).collect(Collectors.toList()) + "\n");
			return 0;
		}
		
		if (height(loc) > bestHeight) {
			bestHeight = height(loc);
			System.out.println("  New best height: " + bestHeight);
		}

		if (loc.distance(end) < bestDistance) {
			bestDistance = loc.distance(end);
			System.out.println("  New best distance: " + bestDistance);
		}
		
		if (alreadyTraversed.size() > bestResult) { return Integer.MAX_VALUE; } // too big, give up
		
		if (alreadyTraversed.contains(loc)) { return Integer.MAX_VALUE; } // loop, give up
		
		if (!loc.within(gridSize)) throw new IllegalArgumentException("Location out of bounds: " + loc);

		alreadyTraversed.add(loc);
		
		if ( count++ % 1000000 == 0) {
			System.out.println( level + " Traversing " + locDesc(loc));
		}
		
		int shortestPath = Integer.MAX_VALUE;

		List<Loc> adjacent = loc.adjacent()
				.filter(l -> l.within(gridSize))
				.filter(l -> heightMatches(loc, l))
				.filter(l -> !alreadyTraversed.contains(l))
				.sorted((l1, l2) -> end.distance(l1) - end.distance(l2))
				.collect(Collectors.toList());
		
		for (Loc l : adjacent) {
			// System.out.println(level + " Examining " + locDesc(l) + " at distance " + l.distance(end));
			int x = findShortestPath(level+1, l, alreadyTraversed);
			shortestPath = Math.min(x, shortestPath);
		}
		
		alreadyTraversed.remove(loc);
		
		if (shortestPath == Integer.MAX_VALUE) {
			return shortestPath;
		} else {
			return 1 + shortestPath;
		}
	}
}
