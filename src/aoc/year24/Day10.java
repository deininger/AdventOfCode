package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        Set<Loc> newLocs = ConcurrentHashMap.newKeySet();

        // System.out.println("Stepping " + starts.size() + " locs to '" + next + "'");

        starts.stream().forEach(start -> {
            newLocs.addAll(start.adjacent().filter(loc -> map.contains(loc)).filter(loc -> map.at(loc) == next).toList());
        });

        return newLocs;
    }

    private AtomicInteger count = new AtomicInteger(0);

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
}
