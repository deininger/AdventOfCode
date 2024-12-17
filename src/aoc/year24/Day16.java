package aoc.year24;

import aoc.util.*;
import java.util.*;

public class Day16 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 16: Reindeer Maze");
        PuzzleApp app = new aoc.year24.Day16();
        app.run();
    }

    public String filename() {
        return "data/year24/day16";
    }

    private final CharacterGrid map = new CharacterGrid();

    public void parseLine(String line) {
        map.addRow(line);
    }

    private int minCost = Integer.MAX_VALUE;
    private final Set<Loc> allPaths = new HashSet<>();

    public void process() {
        // System.out.println(map);
        Loc start = map.locate('S');
        Loc end = map.locate('E');
        Direction d = Direction.RIGHT;
        // System.out.println("Starting at " + start + " facing " + d);

        // Set up a PriorityQueue so that we walk through the maze in order of cost (Djiksra's algorithm)
        PriorityQueue<CostLocDir> pq = new PriorityQueue<>();
        pq.add(new CostLocDir(0, start, d, new HashSet<>()));

        Set<CostLocDir> seen = new HashSet<>();

        while (!pq.isEmpty()) {
            CostLocDir cld = pq.remove();
            seen.add(cld);

            if (cld.loc().equals(end)) {
                if (cld.cost() < minCost) { // We found a cheaper path
                    minCost = cld.cost();
                    allPaths.clear(); // Clear all the paths we may have been tracking since they're more expensive
                    allPaths.addAll(cld.path());
                    // System.out.println(map.overlayPath(cld.path(),'O'));
                } else if (cld.cost() == minCost) { // We found a path with the same cost
                    allPaths.addAll(cld.path());
                    // System.out.println(map.overlayPath(cld.path(),'O'));
                } else { // Our path is already more expensive than the best-cost path, so we don't need to continue
                    break;
                }
            }

            for (Direction newD : Direction.values()) { // Turn (or not) and take a step in each direction
                if (newD.isOpposite(cld.dir())) continue; // Don't turn around
                Loc newLoc = cld.loc().step(newD);
                int newCost = cld.cost() + 1 + (newD.equals(cld.dir()) ? 0 : 1000); // Cost goes up by 1, plus 1000 if we've turned
                if (map.at(newLoc) == '#') continue; // Don't run into walls
                CostLocDir newCld = new CostLocDir(newCost, newLoc, newD, cld.path());
                if (seen.contains(newCld)) continue; // Don't revisit the same loc & dir
                pq.add(newCld);
            }
        }
    }

    public void results() {
        System.out.println("Day 16 part 1 result: " + minCost);
    }

    public void resultsPartTwo() {
        System.out.println("Day 16 part 2 result: " + allPaths.size());
        // System.out.println(map.overlayPath(allPaths,'O'));
    }

    /*
     * Helper class to keep a Loc and a Direction along with the cost to get there from the starting position.
     * Also keeps track of the path taken to get there, for part 2 of the puzzle.
     *
     * Note that this class has a couple of interesting characteristics:
     *
     * 1) The equals and hashcode methods are set up so that two CostLocDirs are equal when they
     *    have the same Loc and Direction. This makes the "seen" HashSet work the way we want.
     *
     * 2) The compareTo method is set up so that two CostLocDirs are compared by their cost.
     *    This makes the "pq" PriorityQueue work the way we want.
     */
    static class CostLocDir implements Comparable<CostLocDir> {
        private final int cost;
        private final Loc loc;
        private final Direction dir;
        private final Set<Loc> path = new HashSet<>();

        public CostLocDir(int cost, Loc loc, Direction dir, Set<Loc> path) {
            this.cost = cost;
            this.loc = loc;
            this.dir = dir;
            this.path.addAll(path);
            this.path.add(loc);
        }

        public int cost() {
            return cost;
        }

        public Loc loc() {
            return loc;
        }

        public Direction dir() {
            return dir;
        }

        public Set<Loc> path() {
            return path;
        }

        public int compareTo(CostLocDir other) {
            return Integer.compare(this.cost, other.cost);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CostLocDir that)) return false;
            return Objects.equals(loc, that.loc) && dir == that.dir;
        }

        @Override
        public int hashCode() {
            return Objects.hash(loc, dir);
        }
    }
}
