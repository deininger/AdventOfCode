package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.*;

public class Day11 extends PuzzleApp { // Day 11: Plutonian Pebbles
    public static void main(String[] args) {
        System.out.println("Day 11: Plutonian Pebbles");
        PuzzleApp app = new aoc.year24.Day11();
        app.run();
    }

    public String filename() {
        return "data/year24/day11";
    }

    private Map<Long,Long> stones = new HashMap<>();

    public void parseLine(String line) {
        Arrays.stream(line.split("\\s+")).map(Long::parseLong).forEach(stone -> stones.merge(stone, 1L, Long::sum));
    }

    private Map<Long,Long> blink(Map<Long,Long> stones) {
        Map<Long,Long> newStones = new HashMap<>();
        Iterator<Map.Entry<Long,Long>> iterator = stones.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long,Long> entry = iterator.next();
            Long stone = entry.getKey();
            Long count = entry.getValue();
            iterator.remove();

            if (stone == 0L) {
                newStones.merge(1L, count, Long::sum);
            } else {
                String stringValue = String.valueOf(stone);
                if (stringValue.length() % 2 == 0) {
                    Long firstStone = Long.parseLong(stringValue.substring(0, stringValue.length() / 2));
                    Long secondStone = Long.parseLong(stringValue.substring(stringValue.length() / 2));
                    newStones.merge(firstStone, count, Long::sum);
                    newStones.merge(secondStone, count, Long::sum);
                } else {
                    newStones.merge(stone * 2024, count, Long::sum);
                }
            }
        }

        return newStones;
    }

    public void process() {
        // System.out.println(stones);
        for (int i = 0; i < 25; i++) {
            stones = blink(stones);
            // System.out.println(stones);
        }
    }
// 2097446912 14168 4048 2 0 2 4 40 48 2024 40 48 80 96 2 8 6 7 6 0 3 2
    public void results() {
        long result = stones.values().stream().mapToLong(Long::longValue).sum();
        System.out.println("Day 11 part 1 result: " + result);
    }

    public void processPartTwo() {
        for (int i = 25; i < 75; i++) {
            // System.out.println(i);
            stones = blink(stones);
        }
    }

    public void resultsPartTwo() {
        long result = stones.values().stream().mapToLong(Long::longValue).sum();
        System.out.println("Day 11 part 2 result: " + result);
    }
}
