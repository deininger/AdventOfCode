package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day05 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 5: Print Queue");
        PuzzleApp app = new aoc.year24.Day05();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year24/day05";
    }

    private final List<Pair<Integer,Integer>> pageOrderingRules = new ArrayList<>();
    private final List<List<Integer>> updates = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        if (line.isEmpty()) {
            return;
        } else if (line.contains("|")) {
            String[] parts = line.split("\\|");
            pageOrderingRules.add(Pair.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        } else {
            updates.add(Arrays.stream(line.split(","))
                    .map(String::trim)  // Remove leading/trailing whitespace
                    .map(Integer::parseInt)
                    .collect(Collectors.toList()));
        }
    }

    private final List<List<Integer>> correctlyOrdered = new ArrayList<>();
    private final List<List<Integer>> incorrectlyOrdered = new ArrayList<>();

    @Override
    public void process() {
        // System.out.println("pageOrderingRules: " + pageOrderingRules);
        // System.out.println("updates:" + updates);

        for (List<Integer> update : updates) {
            boolean correctOrder = true;
            for (int j = 1; j < update.size(); j++) {
                Pair<Integer, Integer> opposite = Pair.of(update.get(j), update.get(j - 1));
                if (pageOrderingRules.contains(opposite)) {
                    correctOrder = false;
                    System.out.println("Update " + update + " failed due to broken rule: " + opposite);
                }
            }
            if (correctOrder) {
                correctlyOrdered.add(update);
                // System.out.println("Update " + update + " passed!");
            } else {
                incorrectlyOrdered.add(update);
            }
        }
    }

    @Override
    public void results() {
        int total = 0;
        for (List<Integer> update : correctlyOrdered) {
            // System.out.println("Adding: " + update.get(update.size()/2));
            total += update.get(update.size()/2);
        }
            System.out.println("Day 5 part 1 result: " + total);
    }

    @Override
    public void processPartTwo() {
        for (List<Integer> update : incorrectlyOrdered) {
            for (int j = 1; j < update.size(); j++) {
                Pair<Integer, Integer> opposite = Pair.of(update.get(j), update.get(j - 1));
                if (pageOrderingRules.contains(opposite)) {
                    System.out.println("Swapping " + Pair.of(update.get(j-1), update.get(j)) + " -> " + opposite );
                    // Swap the rule-breaking pair:
                    update.set(j - 1, opposite.getLeft());
                    update.set(j, opposite.getRight());
                    j = 0; // re-check the whole thing?
                }
            }

            System.out.println("Update " + update + " reordered!");
        }
    }

    @Override
    public void resultsPartTwo() {
        int total = 0;

        for (List<Integer> update : incorrectlyOrdered) {
            // System.out.println("Adding: " + update.get(update.size()/2));
            total += update.get(update.size()/2);
        }

        System.out.println("Day 5 part 2 result: " + total);
    }
}
