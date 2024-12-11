package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Day11 extends PuzzleApp { // Day 11: Plutonian Pebbles
    public static void main(String[] args) {
        System.out.println("Day 11: Plutonian Pebbles");
        PuzzleApp app = new aoc.year24.Day11();
        app.run();
    }

    public String filename() {
        return "data/year24/day11";
    }

    private final List<Long> stones = new LinkedList<>();

    public void parseLine(String line) {
        Arrays.stream(line.split("\\s+")).map(Long::parseLong).forEach(stones::add);
    }

    private void blink() {
        ListIterator<Long> iterator = stones.listIterator();

        while (iterator.hasNext()) {
            Long stone = iterator.next();
            if (stone == 0L) {
                iterator.set(1L);
            } else {
                String stringValue = String.valueOf(stone);
                if (stringValue.length() % 2 == 0) {
                    iterator.set(Long.parseLong(stringValue.substring(0, stringValue.length() / 2)));
                    iterator.add(Long.parseLong(stringValue.substring(stringValue.length() / 2)));
                } else {
                    iterator.set(stone * 2024);
                }
            }
        }
    }

    public void process() {
        // System.out.println(stones);
        for (int i = 0; i < 25; i++) {
            System.out.println(i);
            blink();
            // System.out.println(stones);
        }
    }

    public void results() {
        System.out.println("Day 11 part 1 result: " + stones.size());
    }

    public void processPartTwo() {
        for (int i = 25; i < 75; i++) {
            System.out.println(i);
            blink();
        }
    }

    public void resultsPartTwo() {
        System.out.println("Day 11 part 2 result: " + stones.size());
    }

}
