package aoc.year22;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class HillClimbingAlgorithmBFS extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 12: Hill Climbing Algorithm (BFS)");
		PuzzleApp app = new HillClimbingAlgorithmBFS();
		app.run();
	}
	
	public String filename() {
		return "data/data12";
	}
	
	public void setup() {
	}

	private List<String> inputLines = new ArrayList<>();
	
	public void processLine(String line) {
		inputLines.add(line);
	}
	
	private char[][] grid;
	private Loc gridSize;
	private Loc start;
	private Loc end;

	public void loadGrid() {
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
			}
		}
	}
	
	private char at(Loc loc) {
		return grid[loc.x()][loc.y()];
	}
	
	private int height(Loc loc) {
		return 1 + grid[loc.x()][loc.y()] - 'a';
	}

	private boolean heightMatches(Loc from, Loc to) {
		return (height(to) - height(from)) <= 1;
	}
	
	private int printDepth = 0;
	
	Optional<Cell<Character>> search() {
		Queue<Cell<Character>> queue = new ArrayDeque<>();
		queue.add(new Cell<>(start,0,at(start)));

		Cell<Character> current;
		Set<Loc> alreadyQueuedLocs = new HashSet<>();
		alreadyQueuedLocs.add(start);
		
		while (!queue.isEmpty()) {
			current = queue.remove();
			
			if (current.depth() > printDepth) {
				printDepth = current.depth();
				// System.out.println("Depth " + printDepth);
			}
			
			// System.out.println("Examining " + current);

			if (current.loc().equals(end)) {
				return Optional.of(current);
			} else {				
				// Build the list of Locs we can reach from here:
				
				List<Loc> adjacent = current.loc().adjacent()
						.filter(l -> l.within(gridSize))
						.filter(l -> !alreadyQueuedLocs.contains(l))
						.toList();

				for(Loc l: adjacent) {
					if (heightMatches(current.loc(),l)) {
						Cell<Character> newCell = new Cell<>(l,current.depth()+1,at(l));
						// System.out.println("  Adding to queue: " + newCell);
						queue.add(newCell);
						alreadyQueuedLocs.add(l);

					}
				}
			}
		}
		
		return Optional.empty();
	}
	
	
	public void results() {
		loadGrid();
		Optional<Cell<Character>> result = search();
		
		if (result.isPresent()) {
			System.out.println("Found End at depth " + result.get().depth());
		}
		
		System.out.println("\n--- part 2 ---\n");

		int bestDepth = result.get().depth();
		
		for (int i = 0; i < gridSize.x(); i++) {
			for (int j = 0; j < gridSize.y(); j++) {
				// RESTTTING THE INSTANCE VARIABLE!
				start = new Loc(i,j);

				if (at(start) == 'a') {
					// Treat this as the starting point and see how it goes...
					// System.out.println("Starting at " + start);
					result = search();
					if (result.isPresent()) {
						// System.out.println("--> Found End at depth " + result.get().depth());
						if (result.get().depth() < bestDepth ) { bestDepth = result.get().depth(); }
					}
				}
			}
		}
		
		System.out.println("Found best depth " + bestDepth);

	}
	
	public class Cell<T> {
		private Loc loc;
		private int depth;
		private T value;
		
		public Cell(Loc loc, int depth, T value) {
			this.loc = loc;
			this.depth = depth;
			this.value = value;
		}
		
		public Loc loc() {
			return loc;
		}
		
		public int depth() {
			return depth;
		}
		
		public T value() {
			return value;
		}
		
		public void setLoc(Loc loc) {
			this.loc = loc;
		}
		
		public void setDepth(int depth) {
			this.depth = depth;
		}
		
		public void setValue(T value) {
			this.value = value;
		}
		
		public String toString() {
			return loc + " " + depth + " " + value;
		}
	}
}
