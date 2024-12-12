package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day12 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 12: Garden Groups");
        PuzzleApp app = new aoc.year24.Day12();
        app.run();
    }

    public String filename() {
        return "data/year24/day12";
    }

    CharacterGrid map = new CharacterGrid();

    public void parseLine(String line) {
        map.addRow(line);
    }

    private Set<Region> determineRegions(CharacterGrid map) {
        CharacterGrid mapped = new CharacterGrid(map);
        Set<Region> regions = new HashSet<>();

        for (int r = 0; r < mapped.height(); r++) {
            for (int c = 0; c < mapped.width(); c++) {
                char symbol = mapped.at(r,c);
                if (symbol == '.') continue;
                Loc start = new Loc(c,r);
                Region region = new Region(symbol, start);
                region.grow(mapped, start);
                regions.add(region);
                // System.out.println("After growing '" + symbol + "':\n" + mapped.overlayPath(region.locations(),'*'));
            }
        }

        return regions;
    }

    int totalPrice = 0;
    int totalSideCount = 0;

    public void process() {
        // System.out.println(map);

        Set<Region> regions = determineRegions(map);

        System.out.println("Number of regions: " + regions.size());

        // regions.forEach(region -> System.out.println("Region '" + region.symbol() + "' has area " + region.area() + " and perimiter " + region.perimiter() + " and side count " + region.sides()));

        totalPrice = regions.stream().mapToInt(region -> region.area() * region.perimiter()).sum();
        totalSideCount = regions.stream().mapToInt(region -> region.area() * region.sides()).sum();
    }

    public void results() {
        System.out.println("Day 12 part 1 result: " + totalPrice);
    }

    public void resultsPartTwo() {
        System.out.println("Day 12 part 2 result: " + totalSideCount);
    }

    static class Region {
        private final char symbol;
        private final Loc start;
        private final Set<Loc> locations = new HashSet<>();

        Region(char symbol, Loc start) {
            this.symbol = symbol;
            this.start = start;
        }

        public char symbol() {
            return symbol;
        }

        public void addLocation(Loc loc) {
            locations.add(loc);
        }

        public boolean hasLocation(Loc loc) {
            return locations.contains(loc);
        }

        public void grow(CharacterGrid map, Loc loc) {
            if (hasLocation(loc) || !map.contains(loc) || map.at(loc) != symbol) return;
            addLocation(loc);
            map.set(loc, '.'); // mark this location as "used" in the map
            loc.adjacent().forEach(l -> grow(map, l));
        }

        public int area() {
            return locations.size();
        }

        public int perimiter() {
            AtomicInteger perimiter = new AtomicInteger();
            locations.forEach(loc -> perimiter.addAndGet((int) loc.adjacent().filter(l -> !locations.contains(l)).count()));
            return perimiter.get();
        }

        public int sides() {
            Map<Direction,List<Loc>> sides = new HashMap<>();

            sides.put(Direction.UP,new ArrayList<>());
            sides.put(Direction.DOWN,new ArrayList<>());
            sides.put(Direction.LEFT,new ArrayList<>());
            sides.put(Direction.RIGHT,new ArrayList<>());

            // grab every location that borders a not-in-our-region location
            // make sure to check all four directions! Keep them organized by direction,
            // to make it easy to find adjacent ones which form a single side

            locations.forEach(l -> Direction.stream().filter(d -> !hasLocation(l.step(d))).forEach(d -> sides.get(d).add(l)));

            AtomicInteger sideCounter = new AtomicInteger();

            // Count in connected groups:
            Direction.stream().forEach(d -> {
                List<Loc> locs = sides.get(d);

                while (!locs.isEmpty()) { // Pull out adjacent locations until we've counted them all
                    Loc l = locs.getFirst();
                    locs.remove(l);
                    Loc l2 = l.step(d.turnLeft());
                    while (locs.contains(l2)) { // step in one direction...
                        locs.remove(l2);
                        l2 = l2.step(d.turnLeft());
                    }
                    Loc l3 = l.step(d.turnRight());
                    while (locs.contains(l3)) { // step in the other direction...
                        locs.remove(l3);
                        l3 = l3.step(d.turnRight());
                    }
                    sideCounter.getAndIncrement(); // count the whole group as 1 side
                }
            });

            // System.out.println("Counted " + sideCounter + " sides for region '" + symbol + "'");
            return sideCounter.get();
        }


        // This algorithm works fine for counting "outside" sides,
        // but misses any "inside" sides, sides completely contained
        // within the region.
        public int outsides() {
            int perimiterSideCount = 0;

            Loc l = start; // start is guaranteed to have no valid step above or to the left
            Direction d = Direction.RIGHT;

            // System.out.println("Determining sides for '" + symbol + " starting at " + l + " facing " + d);

            while (true) {
                // move forward until the side to our left is gone or until we hit a side in front
                while (!hasLocation(l.step(d.turnLeft())) && hasLocation(l.step(d))) {
                    l = l.step(d);
                }

                perimiterSideCount++; // we've reached the end of a side, count it
                // System.out.println("Counted '" + symbol + "' wall " + d.turnLeft() + " ending at " + l);

                if (hasLocation(l.step(d.turnLeft()))) {
                    // lf we can turn left, do so, and take a step forward
                    d = d.turnLeft();
                    l = l.step(d);
                    // System.out.println("  turning left to face " + d);
                } else {
                    // we can't turn left, we can't keep going forward, turn right
                    d = d.turnRight();
                    // System.out.println("  turning right to face " + d);
                }

                if (l.equals(start) && d.equals(Direction.RIGHT)) break;
                // do it all again...
            }

            // System.out.println("Region '" + symbol + "' has " + perimiterSideCount + " sides");
            return perimiterSideCount;
        }
    }
}
