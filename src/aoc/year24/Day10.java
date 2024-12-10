package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day10 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 10: Hoof It");
        PuzzleApp app = new aoc.year24.Day10();
        app.run();
    }

    public String filename() {
        return "data/year24/day10";
    }

    CharacterGrid map = new CharacterGrid();

    public void parseLine(String line) {
        map.addRow(line);
    }

    private Set<Loc> step(Set<Loc> starts, char next) {
        Set<Loc> newLocs = new HashSet<>();

        // System.out.println("Stepping " + starts.size() + " locs to '" + next + "'");

        starts.forEach(start ->
            newLocs.addAll(start.adjacent().filter(loc -> map.contains(loc)).filter(loc -> map.at(loc) == next).toList())
        );

        return newLocs;
    }

    private final AtomicInteger count = new AtomicInteger(0);

    public void process() {
        // System.out.println(map);

        Set<Loc> trailheads = map.locateAll('0');
        // System.out.println("0: " + trailheads);

        trailheads.parallelStream().forEach(start -> {
            Set<Loc> locs = Set.of(start);

            for (char c = '1'; c <= '9'; c++) {
                locs = step(locs, c);
                // System.out.println(c + ": " + locs);
            }

            // System.out.println("Trailhead " + start + " has score " + locs.size());
            count.addAndGet(locs.size());
        });
    }

    public void results() {
        System.out.println("Day 10 part 1 result: " + count);
    }

    private Set<Loc> step(Loc start, char next) {
        return start.adjacent().filter(loc -> map.contains(loc)).filter(loc -> map.at(loc) == next).collect(Collectors.toSet());
    }

    private int buildPath(Loc start, char next) {
        if (map.at(start) == '9') return 1;
        Set<Loc> nextSteps = step(start, next);
        return nextSteps.stream().mapToInt(loc -> buildPath(loc, (char)(next + 1))).sum();
    }

    private int rating = 0;

    public void processPartTwo() {
        Set<Loc> trailheads = map.locateAll('0');

        trailheads.parallelStream().forEach(start -> {
            int pathCount = buildPath(start, '1');
            // System.out.println("Path count from " + start + " is " + pathCount);
            rating += pathCount;
        });
    }

    public void resultsPartTwo() {
        System.out.println("Day 10 part 2 result: " + rating);
    }
}
