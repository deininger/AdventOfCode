package aoc.year24;

import aoc.util.PuzzleApp;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Day11 extends PuzzleApp { // Day 11: Plutonian Pebbles
    public static void main(String[] args) {
        System.out.println("Day 11: Plutonian Pebbles");
        PuzzleApp app = new aoc.year24.Day11();
        app.run();
    }

    private static final BigInteger MULTIPLIER = new BigInteger("2024");

    public String filename() {
        return "data/year24/day11";
    }

    private Map<BigInteger,BigInteger> stones = new HashMap<>();

    public void parseLine(String line) {
        Arrays.stream(line.split("\\s+")).map(Long::parseLong).map(BigInteger::valueOf).forEach(stone -> stones.merge(stone, BigInteger.ONE, BigInteger::add));
    }

    private Map<BigInteger,BigInteger> blink(Map<BigInteger,BigInteger> stones) {
        Map<BigInteger,BigInteger> newStones = new ConcurrentHashMap<>();

        stones.entrySet().parallelStream().forEach(entry -> {
            BigInteger stone = entry.getKey();
            BigInteger count = entry.getValue();

            if (stone.equals(BigInteger.ZERO)) {
                newStones.merge(BigInteger.ONE, count, BigInteger::add);
            } else {
                String stringValue = stone.toString();
                if (stringValue.length() % 2 == 0) {
                    BigInteger firstStone = BigInteger.valueOf(Long.parseLong(stringValue.substring(0, stringValue.length() / 2)));
                    BigInteger secondStone = BigInteger.valueOf(Long.parseLong(stringValue.substring(stringValue.length() / 2)));
                    newStones.merge(firstStone, count, BigInteger::add);
                    newStones.merge(secondStone, count, BigInteger::add);
                } else {
                    newStones.merge(stone.multiply(MULTIPLIER), count, BigInteger::add);
                }
            }
        });

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
        BigInteger result = stones.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Day 11 part 1 result: " + result);
    }

    public void processPartTwo() {
        for (int i = 25; i < 75; i++) {
            // System.out.println(i);
            stones = blink(stones);
        }
    }

    public void resultsPartTwo() {
        BigInteger result = stones.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Day 11 part 2 result: " + result + " (" + stones.size() + " unique values)");

        extraCredit();
    }

    public void extraCredit() {
        for (int i = 75; i < 750; i++) {
            stones = blink(stones);
        }

        BigInteger result = stones.values().stream().reduce(BigInteger.ZERO, BigInteger::add);
        System.out.println("Day 11 extra credit (750 iterations) result: " + result + " (" + stones.size() + " unique values)");
    }
}
