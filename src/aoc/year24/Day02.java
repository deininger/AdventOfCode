package aoc.year24;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import aoc.util.PuzzleApp;

public class Day02 extends PuzzleApp {
    private static Day02DataParser parser = new Day02DataParser();

    public static void main(String[] args) {
        System.out.println("Day 2: Red-Nosed Reports");
        PuzzleApp app = new aoc.year24.Day02();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year24/day02";
    }

    int safeCount = 0;
    List<List<Integer>> reports = parser.parseData(filename());

    public void process() {
        for(List<Integer> report: reports) {
            Set<Integer> deltas = IntStream.range(1, report.size())
                    .map(i -> report.get(i) - report.get(i - 1))
                    .boxed()
                    .collect(Collectors.toSet());


            if(isSafe(deltas)) safeCount++;
        }
    }

    public void results() {
        System.out.println("Day 2 part 1 result: " + safeCount);
    }

    private boolean isSafe(Set<Integer> deltas) {
        List<Integer> sortedDeltas = deltas.stream().sorted().toList();

        return ! (sortedDeltas.getFirst() < -3
                || sortedDeltas.getLast() > 3
                || (sortedDeltas.getFirst() < 0 && sortedDeltas.getLast() > 0)
                || deltas.contains(0));
    }

    int dampenedSafeCount = 0;

    public void processPartTwo() {
        for(List<Integer> report: reports) {
            Set<Integer> deltas = IntStream.range(1, report.size())
                    .map(i -> report.get(i) - report.get(i - 1))
                    .boxed()
                    .collect(Collectors.toSet());

            if (isSafe(deltas)) {
                dampenedSafeCount++;
                break;
            }

            for (int r = 0; r < report.size(); r++) {
                List<Integer> dampenedReport = new ArrayList<>(report);
                dampenedReport.remove(r);

                deltas = IntStream.range(1, dampenedReport.size())
                        .map(i -> dampenedReport.get(i) - dampenedReport.get(i - 1))
                        .boxed()
                        .collect(Collectors.toSet());

                if (isSafe(deltas)) {
                    dampenedSafeCount++;
                    break;
                }
            }
        }
    }

    public void resultsPartTwo() {
        System.out.println("Day 2 part 2 result: " + dampenedSafeCount);
    }
}
