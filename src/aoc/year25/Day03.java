package aoc.year25;

import aoc.util.PuzzleApp;

import java.util.ArrayList;
import java.util.List;

public class Day03 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 3: Lobby");
        PuzzleApp app = new Day03();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day03";
    }

    List<String> banks = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        banks.add(line);
    }

    private long joltage(String bank, int digits) {
        if (digits == 0) return 0;
        if (bank == null || bank.isEmpty()) return 0;
        if (bank.length() <= digits) return Long.parseLong(bank);

        // Find the first occurrence of the largest digit (avoiding the end of the string):
        int positionOfLargestDigit = 0;
        int largestDigit = 0;
        for (int i = 0; i < bank.length() - digits + 1; i++) {
            if (Integer.parseInt(String.valueOf(bank.charAt(i))) > largestDigit) {
                largestDigit = Integer.parseInt(String.valueOf(bank.charAt(i)));
                positionOfLargestDigit = i;
            }
        }

        // Use recursion to find the largest digit in the remainder of the string:
        return largestDigit * (long)Math.pow(10,digits-1) + joltage(bank.substring(positionOfLargestDigit + 1), digits - 1);
    }

    private long totalJoltage = 0;

    @Override
    public void process() {
        banks.forEach(bank -> totalJoltage += joltage(bank, 2));
    }
    
    @Override
    public void results() {
        System.out.println("Part 1 results: total joltage " + totalJoltage);
    }

    private long totalJoltagePartTwo = 0;

    @Override
    public void processPartTwo() {
        banks.forEach(bank -> totalJoltagePartTwo += joltage(bank, 12));
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 results: total joltage " + totalJoltagePartTwo);
    }
}
