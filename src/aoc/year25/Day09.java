package aoc.year25;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;

public class Day09 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 9: Movie Theater");
        PuzzleApp app = new Day09();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day09";
    }

    List<Loc> redTiles = new ArrayList<>();
    int min_x = Integer.MAX_VALUE, min_y = Integer.MAX_VALUE, max_x = Integer.MIN_VALUE, max_y = Integer.MIN_VALUE;

    @Override
    public void parseLine(String line) {
        String[] parts = line.split(",");
        Loc l = new Loc(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        redTiles.add(l);
        min_x = Math.min(min_x, l.x());
        min_y = Math.min(min_y, l.y());
        max_x = Math.max(max_x, l.x());
        max_y = Math.max(max_y, l.y());
     }

    private long largestArea = 0;

    public void process() {
        for (Loc tile1 : redTiles) {
            for (Loc tile2 : redTiles) {
                long area = (Math.abs(tile2.x() - tile1.x()) + 1L) * (Math.abs(tile2.y() - tile1.y()) + 1L);
                if (area > largestArea) {
                    largestArea = area;
                }
            }
        }
    }

    @Override
    public void results() {
        System.out.println("Part 1: Largest area = " + largestArea);
    }

    private final Map<Loc,Boolean> cache = new HashMap<>();

    private boolean cachedPointWithinRedTilesPolygon(Loc l) {
        if (cache.containsKey(l)) return cache.get(l);
        boolean result = l.within(redTiles);
        cache.put(l, result);
        return result;
    }

    private long partTwoLargestArea = 0;

    @Override
    public void processPartTwo() {
        for (Loc tile1 : redTiles) {
            System.out.println("..." + (redTiles.indexOf(tile1) + 1) + "/" + redTiles.size());

            for (Loc tile2 : redTiles) {
                System.out.println("......" + (redTiles.indexOf(tile2) + 1) + "/" + redTiles.size());

                long area = (Math.abs(tile2.x() - tile1.x()) + 1L) * (Math.abs(tile2.y() - tile1.y()) + 1L);
                if (area > partTwoLargestArea) {
                    boolean allPointsWithin = true;
                    for (int x = 1 + Math.min(tile1.x(), tile2.x()); allPointsWithin && x < Math.max(tile1.x(), tile2.x()); x++) {
                        for (int y = 1 + Math.min(tile1.y(), tile2.y()); allPointsWithin && y < Math.max(tile1.y(), tile2.y()); y++) {
                            Loc testPoint = new Loc(x, y);
                            if (!cachedPointWithinRedTilesPolygon(testPoint)) {
                                allPointsWithin = false;
                            }
                        }
                    }
                    if (allPointsWithin) {
                        System.out.println("Rectangle at " + tile1 + " and " + tile2 + " has all points within polygon, area = " + area);
                        partTwoLargestArea = area;
                    }
                }
            }
        }
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2: Largest area = " + partTwoLargestArea);
    }
}
