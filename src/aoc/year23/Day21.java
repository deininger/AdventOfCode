package aoc.year23;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day21 extends PuzzleApp {

	public static final void main(String[] args) {
		System.out.println("December 21: Step Counter");
		PuzzleApp app = new Day21();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day21-part1";
	}

	CharacterGrid grid = new CharacterGrid();
	
	public void parseLine(String line) {
		grid.addRow(line);
	}

	private Set<Loc> takeStep(Set<Loc> currentLocs) {
		Set<Loc> nextSteps = new HashSet<>();

		currentLocs.stream().flatMap(l -> l.adjacent()).forEach( loc -> {
			if (grid.contains(loc) && grid.at(loc) != '#') {
				nextSteps.add(loc);
			}
		});

		return nextSteps;
	}

	private static final int PART_1_MAX_STEPS = 64;

	private void part1() {
		Loc start = new Loc(grid.locate('S'));

		System.out.println("Starting at "+ start);

		Set<Loc> currentLocs = new HashSet<>();
		currentLocs.add(start);

		for (int step = 1; step <= PART_1_MAX_STEPS; step++) {
			currentLocs = takeStep(currentLocs);
			System.out.println("Step " + step + " has " + currentLocs.size() + " current locs"); // 3689
		}
	}

	/*
	 * Determine the number of steps it takes to visit every visitable cell in the grid, from the given starting point.
	 */
	private int stepsToVisitAllLocationsInGrid(LocWithCount start) {
		Set<LocWithCount> visited = new HashSet<>();
		Queue<LocWithCount> currentLocs = new ArrayDeque<>();
		int maxCount = 0;

		currentLocs.add(start);
		visited.add(start);

		while (!currentLocs.isEmpty()) {
			LocWithCount current = currentLocs.remove();
			visited.add(current);
			maxCount = current.count();

			current.adjacent().forEach(loc -> {
				LocWithCount newLoc = new LocWithCount(loc, current.count()+1);
				if (grid.contains(newLoc) && grid.at(newLoc) != '#' && !visited.contains(newLoc) && !currentLocs.contains(newLoc)) {
					currentLocs.add(newLoc);
				}
			});

			// System.out.println("CurrentLocs size is "+ currentLocs.size() + ", visitedLocs size is " + visited.size() + ", max count = " + maxCount);
		}

		return maxCount;
	}

	private int numberOfVisitableCells(LocWithCount start) {
		Set<LocWithCount> visited = new HashSet<>();
		Queue<LocWithCount> currentLocs = new ArrayDeque<>();

		currentLocs.add(start);

		while (!currentLocs.isEmpty()) {
			LocWithCount current = currentLocs.remove();
			
			if (current.count() == 0) {
				visited.add(current); // This point is reachable in the given number of steps
				continue;
			}

			if (current.count() % 2 == 0) { 
				visited.add(current); // Shortcut: If there are an even number of steps remaining, we know we can get back to this spot at the end.
			} 
			
			current.adjacent().forEach(loc -> {
				LocWithCount newLoc = new LocWithCount(loc, current.count()-1);
				if (grid.contains(newLoc) && grid.at(newLoc) != '#' && !visited.contains(newLoc) && !currentLocs.contains(newLoc)) {
					currentLocs.add(newLoc);
				}
			});
		}

		return visited.size();
	}

	private static final long PART_2_MAX_STEPS = 26501365;

	private void part2() {
		if (grid.height() != grid.width()) {
			System.err.println("Expected grid to be square!");
			System.exit(0);
		}

		LocWithCount start = new LocWithCount(grid.locate('S'), 0); // (65, 65)
		int gridSize = grid.width(); // 131
		System.out.println("Starting at " + start + ", grid size is " + gridSize); // (65,65) and 131
		int stepsToVisitEntireGridFromStart = stepsToVisitAllLocationsInGrid(start); 
		// Could probably just assume that this is:
		//   (distance from start to right/left edge) + (distance from start to top/bottom edge))
		//   which would be 65+65=130
		System.out.println("Steps to navigate the initial grid, from Start: " + stepsToVisitEntireGridFromStart); // 130

		start = new LocWithCount(0, 0, 0);
		int stepsToVisitEntireGridFromCorner = stepsToVisitAllLocationsInGrid(start); 
		// Could probably just assume that this is:
		//   (distance from start to right/left edge) + (distance from start to top/bottom edge))
		//   which would be 65+65=130
		System.out.println("Steps to navigate the initial grid, from (0,0) corner: " + stepsToVisitEntireGridFromCorner); // 260
		// I could check the other 3 corners but based on the nice clear paths in the data file they'd all be the same

		// Now that we know the maximum number of steps, let's find out how many cells we visited:
		start = new LocWithCount(grid.locate('S'), gridSize * 2); // Multiply by 2 just to ensure it's even
		int evenFullGridCellCount = numberOfVisitableCells(start);
		System.out.println("Fully navigated grid (with even start) has " + evenFullGridCellCount + " cells"); // 7451

		// What if we have an odd number of starting steps, instead of an even number? Does the answer change?
		start = new LocWithCount(grid.locate('S'), gridSize * 2 + 1); // Multiply by 2 and add one just to ensure it's odd
		int oddFullGridCellCount = numberOfVisitableCells(start);
		System.out.println("Fully navigated grid (with odd start) has " + oddFullGridCellCount + " cells"); // 7458

		// As a double-check, let's see if this gives the same answer as Part 1:
		start = new LocWithCount(grid.locate('S'), 64);
		int partOneResult = numberOfVisitableCells(start);
		System.out.println("Should be the same answer as for Part 1 (3689): " + partOneResult); // 3689

		// Let's look at the "grid of grids" produced by our infinite garden.
		// All of them except the edges should be "fully visitable".

		long remainder = PART_2_MAX_STEPS % gridSize; // 65
		long fullyReachableBlocksInOneDirection = PART_2_MAX_STEPS / gridSize; // 202300 (the last one of these isn't fully visitable)

		System.out.println("Calculated we can reach " + fullyReachableBlocksInOneDirection + " full blocks in one direction");
		System.out.println("Remaining steps after jumping (center to center) full blocks as far as we can in one direction: " + remainder);
	
		// Unfortunately we need to divied up the fully visitable grids into 
		// "start with an even number of steps" and "start with an odd number of steps" 
		// categories, because they contain different totals (7451 for even, 7459 for odd).
		//
		// The original "center square" is Odd, because our starting number (PART_2_MAX_STEPS = 26501365) is odd.
		//
		// The outermost edges of our diamond of grids are also Odd, because we have 202300 grid in each direction,
		// numbering the origin 1 makes the outermost edges 202301. Therefore all outer edges are Odd.
		//
		// odd = (grid_width // 2 * 2 + 1) ** 2
		// even = ((grid_width + 1) // 2 * 2) ** 2

		long oddStepCountFullyVisitableGrids = (long)Math.pow((fullyReachableBlocksInOneDirection-1)/2 * 2 + 1, 2);
		long evenStepCountFullyVisitableGrids = (long)Math.pow((fullyReachableBlocksInOneDirection)/2 * 2, 2);

		System.out.println("oddCount = " + oddStepCountFullyVisitableGrids + ", evenCount = " + evenStepCountFullyVisitableGrids
			+ ", total = " + (oddStepCountFullyVisitableGrids+evenStepCountFullyVisitableGrids));
			// odd = 40924885401   even = 40925290000    total = 81850175401

		// Now we need to deal with all the edge cases. There are eight possiblities:
		//
		// 1. Starting from the left center. One instance of this.
		// 2. Starting from the right center. Once instance of this.
		// 3. Starting from the bottom center. One instance of this.
		// 4. Starting from the top center. One instance of this.
		//
		// Each of the four above has a step count of PART_2_MAX_STEPS - (gridsize / 2 + 1) - ((fullyReachableBlocksInOneDirection-1) * gridSize) 

		int stepCount = (int)(PART_2_MAX_STEPS - (gridSize / 2 + 1) - ((fullyReachableBlocksInOneDirection-1) * gridSize)); // 131
		System.out.println("Step count for 4 'center side' blocks: " + stepCount);
	
		LocWithCount leftCenter = new LocWithCount(0, start.y(), stepCount);
		long leftCount = numberOfVisitableCells(leftCenter);

		LocWithCount rightCenter = new LocWithCount(gridSize-1, start.y(), stepCount);
		long rightCount = numberOfVisitableCells(rightCenter);

		LocWithCount topCenter = new LocWithCount(start.x(), gridSize-1, stepCount);
		long topCount = numberOfVisitableCells(topCenter);

		LocWithCount bottomCenter = new LocWithCount(start.x(), 0, stepCount);
		long bottomCount = numberOfVisitableCells(bottomCenter);

		System.out.println("leftCount = " + leftCount + ", rightCount = " + rightCount + ", topCount = " + topCount + ", bottomCount = " + bottomCount);
		
		// The remaining 4 edge cases are starting from the four corners:
		//
		// UPDATE: I had thought all the 'on the diagonal' blocks could be treated the same, but it turns out I need to
		//         split each category into two subcategories, again because there's a different number of starting steps
		//         for every other block as you work your way up/down the diagonals.

		// 5. Starting from the bottom left corner. 
		// 6. Starting from the bottom right corner.
		// 7. Starting from the top left corner. 
		// 8. Starting from the top right corner.
		//
		// For each of the above, we have two subcategories, differing on starting step count:
		// a) The squares immediately above/below or left/right of the far edges have a starting step count of
		//    (far edge step count, 130) - (half the grid size + 1) = 130 - 66 = 64
		// B) The grid diagonally above/below and left/right of the far edges have a starting step count of
		//    (subcategory A step count) + (the grid size) = 64 + 131 = 195
		//
		// We'll call the (A) corners "small" and the (B) corners "large" based on their relative step counts.
		//
		// There's overall an odd number of corners per diagonal, and there will always be one more small than large

		int smallCornerCount = (int)(fullyReachableBlocksInOneDirection);
		int largeCornerCount = (int)(fullyReachableBlocksInOneDirection-1);
		int smallStepCount = (int)(PART_2_MAX_STEPS - ((fullyReachableBlocksInOneDirection-1) * gridSize) - gridSize - 1); // 64
		int largeStepCount = (int)(PART_2_MAX_STEPS - ((fullyReachableBlocksInOneDirection-1) * gridSize) - 1); // 195


		System.out.println("Step count for 4 'corner' blocks: small = " + smallStepCount + ", large = " + largeStepCount);

		LocWithCount smallBottomLeftCorner = new LocWithCount(gridSize-1, 0, smallStepCount);
		long smallBottomLeftCount = numberOfVisitableCells(smallBottomLeftCorner);
		LocWithCount largeBottomLeftCorner = new LocWithCount(gridSize-1, 0, largeStepCount);
		long largeBottomLeftCount = numberOfVisitableCells(largeBottomLeftCorner);

		LocWithCount smallBottomRightCorner = new LocWithCount(gridSize-1, gridSize-1, smallStepCount);
		long smallBottomRightCount = numberOfVisitableCells(smallBottomRightCorner);
		LocWithCount largeBottomRightCorner = new LocWithCount(gridSize-1, gridSize-1, largeStepCount);
		long largeBottomRightCount = numberOfVisitableCells(largeBottomRightCorner);

		LocWithCount smallTopLeftCorner = new LocWithCount(0, 0, smallStepCount);
		long smallTopLeftCount = numberOfVisitableCells(smallTopLeftCorner);
		LocWithCount largeTopLeftCorner = new LocWithCount(0, 0, largeStepCount);
		long largeTopLeftCount = numberOfVisitableCells(largeTopLeftCorner);

		LocWithCount smallTopRightCorner = new LocWithCount(0, gridSize-1, smallStepCount);
		long smallTopRightCount = numberOfVisitableCells(smallTopRightCorner);
		LocWithCount largeTopRightCorner = new LocWithCount(0, gridSize-1, largeStepCount);
		long largeTopRightCount = numberOfVisitableCells(largeTopRightCorner);

		System.out.println("smallBottomLeftCount = " + smallBottomLeftCount 
				+ ", largeBottomLeftCount = " + largeBottomLeftCount 
				+ ", smallBottomRightCount = " + smallBottomRightCount 
				+ ", largeBottomRightCount = " + largeBottomRightCount 
				+ ", smallTopLeftCount = " + smallTopLeftCount 
				+ ", largeTopLeftCount = " + largeTopLeftCount 
				+ ", smallTopRightCount = " + smallTopRightCount
				+ ", largeTopRightCount = " + largeTopRightCount);

		long answer = (evenStepCountFullyVisitableGrids * evenFullGridCellCount)
				+ (oddStepCountFullyVisitableGrids * oddFullGridCellCount)
				+ leftCount + rightCount + topCount + bottomCount
				+ (smallBottomLeftCount + smallBottomRightCount + smallTopLeftCount + smallTopRightCount) * smallCornerCount
				+ (largeBottomLeftCount + largeBottomRightCount + largeTopLeftCount + largeTopRightCount) * largeCornerCount
				;
				 // 304933622311682 is too low
				 // 304933622315606 is also too low
				 // 610193849840139 is too high
				 // 610199137737177 is also wrong (too high)
				 // 610158212851776 is also wrong...
				 // 610158187362102

		System.out.println("Total number of visitable cells = " + answer);
	}

	public void process() {
		// part1();
		part2();
	}

	public void results() {
		// System.out.println(grid);
	}

	class LocWithCount extends Loc {
		private int count = 0;

		public LocWithCount(int x, int y) {
			super(x, y);
		}

		public LocWithCount(int x, int y, int count) {
			super(x, y);
			this.count = count;
		}

		public LocWithCount(LocWithCount other) {
			super(other);
		}

		public LocWithCount(Loc other, int count) {
			super(other);
			this.count = count;
		}

		public int count() {
			return count;
		}

		public void inc() {
			count++;
		}
	}
}