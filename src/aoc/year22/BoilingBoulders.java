package aoc.year22;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class BoilingBoulders extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 18: Boiling Boulders");
		PuzzleApp app = new BoilingBoulders();
		app.run();
	}
	
	public String filename() {
		return "data/data18";
	}

	private Set<Point> obsidian = new HashSet<>();

	private Set<Point> fullyContained = new HashSet<>();
	
	private Set<Point> outsidePoints = new HashSet<>();

	public void parseLine(String line) {
		String[] coordinates = line.split(",");
		
		obsidian.add(new Point(Integer.parseInt(coordinates[0]),
				Integer.parseInt(coordinates[1]),
				Integer.parseInt(coordinates[2])));
		
	}
	
	public void process() {
		
	}
	
	public void results() {
		// System.out.println(obsidian);
		
		// For Part 1, we just find all the adjacent Points and determine which are not obsidian:
		
		long totalExposedSides = obsidian.stream().flatMap(Point::adjacentPoints).filter(p -> !obsidian.contains(p)).count();
		System.out.println("Part 1: total exposed sides = " + totalExposedSides);

		// For Part 2, have have to somehow determine which exposed points are "inside"
		// (fully enclosed) and which are "outside" (not fully enclosed)
		
		// Start by collecting all adjacent points and putting them into two buckets:
		// (1) those which are not "blocked" in one or more of the 6 directions, these must be "outside".
		// (2) those which are "blocked in all 6 directions, these _might_ be inside.
		
		Set<Point> adjacentPoints = obsidian.stream().flatMap(Point::adjacentPoints).collect(Collectors.toSet());
		
		Set<Point> potentialInteriorPoints = new HashSet<>();
		
		adjacentPoints.forEach(p -> {
			// Set<Point> colinearPoints = obsidian.stream().filter(o -> o.colinear(p)).collect(Collectors.toSet());

			if (!obsidian.contains(p) && p.isSurrounded(obsidian)) {
				potentialInteriorPoints.add(p);
			}			
		});
		
		int currentPotentialInteriorSize = potentialInteriorPoints.size();

		System.out.println("Found " + currentPotentialInteriorSize + " potential interior points");

		// Now we attempt to "grow" each point in our collection by analyzing its adjacent points.

		potentialInteriorPoints.forEach(pip -> {
			if (fullyContained.contains(pip)) {
				// We already know this point is fully contained
			} else if (outsidePoints.contains(pip)) {
				// We already know this point is outside
			} else {
				Set<Point> growth = new HashSet<>();
				growth.add(pip);
				int x = 1;
				while((x = grow(growth)) > 0) {
					// System.out.println("Growing " + pip + " to size " + growth.size());
				}
				if (x < 0) {
					System.out.println("Growth of " + pip + " emerged outside obsidian, discarding...");
					outsidePoints.addAll(growth);
				} else {
					System.out.println("Growth of " + pip + " is fully contained, with size " + growth.size());
					fullyContained.addAll(growth);
				}
			}
		});
		
		System.out.println("Found " + fullyContained.size() + " fully interior points");

		// Now that we have identified all of the fully interior points,
		// we can calculate the surface area of the obsidian + fullyContained sets:
		
		Set<Point> obsidianPlusFullyContained = new HashSet<>(obsidian);
		obsidianPlusFullyContained.addAll(fullyContained);
		
		totalExposedSides = obsidianPlusFullyContained.stream().flatMap(Point::adjacentPoints).filter(p -> !obsidianPlusFullyContained.contains(p)).count();
		System.out.println("Part 2: total exposed sides = " + totalExposedSides);

		
	}

	/*
	 * Not a particularly efficient way to do this, let's see if it's performant enough...
	 * 
	 * Will return a positive integer if it's able to grow the set.
	 * Returns 0 if no more growth is possible (fully contained within the obsidian).
	 * Returns -1 if an adjacent point is "outside"
	 */
	int grow(Set<Point> growth) {
		int startingSize = growth.size();
		Set<Point> newGrowth = growth.stream().flatMap(g -> g.adjacentPoints()).filter(g -> !obsidian.contains(g)).collect(Collectors.toSet());

		if (newGrowth.stream().anyMatch(ng -> !ng.isSurrounded(obsidian))) {
			// We found an adjacent point which is "outside", therefore the whole growth
			// is reachable from outside
			return -1;
		} else {
			// All new growth is "inside" (but it might be zero growth)
			growth.addAll(newGrowth);
			return growth.size() - startingSize;
		}
	}
	
	class Point {
		private final int x;
		private final int y;
		private final int z;
		
		public Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		private Stream<Point> adjacentPoints() {
			return Stream.of(new Point(x-1,y,z), new Point(x+1,y,z), new Point(x,y-1,z), new Point(x,y+1,z), new Point(x,y,z-1), new Point(x,y,z+1));
		}
		
		/*
		 * Returns true if this is surrounded on all 6 sides by the given set of Points
		 */
		public boolean isSurrounded(Set<Point> points) {
			Set<Point> colinearPoints = points.stream().filter(o -> o.colinear(this)).collect(Collectors.toSet());
			if (colinearPoints.size() < 6) {
				return false; // Can't possibly be surrounded on all 6 sides
			} else {
				// Need to check all 6 directions to find out...
				
				return
				colinearPoints.stream().filter(cp -> cp.isAbove(this)).findFirst().isPresent() &&
				colinearPoints.stream().filter(cp -> cp.isBelow(this)).findFirst().isPresent() &&
				colinearPoints.stream().filter(cp -> cp.isNorthOf(this)).findFirst().isPresent() &&
				colinearPoints.stream().filter(cp -> cp.isSouthOf(this)).findFirst().isPresent() &&
				colinearPoints.stream().filter(cp -> cp.isEastOf(this)).findFirst().isPresent() &&
				colinearPoints.stream().filter(cp -> cp.isWestOf(this)).findFirst().isPresent();
			}
		}
		
		/*
		 * Returns true if this and other share 2 of the 3 coordinates
		 */
		public boolean colinear(Point other) {
			return (this.x == other.x && this.y == other.y) || (this.x == other.x && this.z == other.z) || (this.y == other.y && this.z == other.z);
		}

		public boolean isAbove(Point other) {
			return this.x == other.x && this.y == other.y && this.z > other.z;
		}

		public boolean isBelow(Point other) {
			return this.x == other.x && this.y == other.y && this.z < other.z;
		}

		public boolean isNorthOf(Point other) {
			return this.x == other.x && this.y > other.y && this.z == other.z;
		}

		public boolean isSouthOf(Point other) {
			return this.x == other.x && this.y < other.y && this.z == other.z;
		}

		public boolean isWestOf(Point other) {
			return this.x < other.x && this.y == other.y && this.z == other.z;
		}

		public boolean isEastOf(Point other) {
			return this.x > other.x && this.y == other.y && this.z == other.z;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, y, z);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;

			return x == other.x && y == other.y && z == other.z;
		}

		public String toString() {
			return "(" + x + "," + y + "," + z + ")";
		}
	}
}
