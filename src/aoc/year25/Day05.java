package aoc.year25;

import aoc.util.PuzzleApp;
import aoc.util.RangeFinder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Day05 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 5: Cafeteria");
        PuzzleApp app = new Day05();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day05";
    }

    private final List<Pair<Long,Long>> freshRanges = new ArrayList<>();
    private final List<Long> ingredients = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        if (line.isBlank()) return;

        if (line.contains("-")) {
            String[] parts = line.split("-");
            freshRanges.add(Pair.of(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
        } else {
            ingredients.add(Long.parseLong(line));
        }
    }

    private long freshCount;

    @Override
    public void process() {
        freshCount = ingredients.stream().filter(i -> freshRanges.stream().anyMatch(r -> r.getLeft() <= i && i <= r.getRight() )).count();
     }
    
    @Override
    public void results() {
        // System.out.println("freshRanges: " + freshRanges);
        // System.out.println("ingredients: " + ingredients);
        System.out.println("fresh count = " + freshCount);
    }

    private final RangeFinder rangeFinder = new RangeFinder();

    @Override
    public void processPartTwo() {
        freshRanges.forEach(rangeFinder::addRange);
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2: " + rangeFinder.count() + " (" + rangeFinder.size() + " ranges)");
    }
}
