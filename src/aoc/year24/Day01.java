package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Day01 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 1: Historian Hysteria");
        PuzzleApp app = new aoc.year24.Day01();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year24/day01-part1";
    }

    List<Integer> locationGroupOne = new ArrayList<>();
    List<Integer> locationGroupTwo = new ArrayList<>();

    public void parseLine(String line) {
        String[] locations = line.split("\\s+");
        locationGroupOne.add(Integer.parseInt(locations[0]));
        locationGroupTwo.add(Integer.parseInt(locations[1]));
    }

    int sum = 0;

    public void process() {
        Collections.sort(locationGroupOne);
        Collections.sort(locationGroupTwo);

        sum = IntStream.range(0, locationGroupOne.size())
                .map(i -> Math.abs(locationGroupOne.get(i) - locationGroupTwo.get(i)))
                .sum();
    }

    public void results() {
        System.out.println("Day 1 part 1 result: " + sum);
    }

    int sumPartTwo = 0;

    public void processPartTwo() {
        sumPartTwo = locationGroupOne.stream()
                .mapToInt(loc -> loc * Collections.frequency(locationGroupTwo, loc))
                .sum();
    }

    public void resultsPartTwo() {
        System.out.println("Day 1 part 2 result: " + sumPartTwo);
    }
}
