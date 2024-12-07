package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class Day07 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 7: Bridge Repair");
        PuzzleApp app = new aoc.year24.Day07();
        app.run();
    }

    public String filename() {
        return "data/year24/day07";
    }

    private final List<Pair<Long,List<Long>>> equations = new ArrayList<>();

    public void parseLine(String line) {
        String[] parts = line.split("\\s");
        Long testValue = Long.parseLong(parts[0].substring(0, parts[0].length() - 1));
        List<Long> numbers = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            numbers.add(Long.parseLong(parts[i]));
        }
        equations.add(Pair.of(testValue,numbers));
    }

    public LongStream evaluate(List<Long> numbers, Long maximum, boolean concatenate) {
        if (numbers.isEmpty()) {
            return LongStream.empty();
        } else if (numbers.size() == 1) {
            return LongStream.of(numbers.getFirst());
        } else {
            LongStream a = evaluate(numbers.subList(0, numbers.size()-1), maximum, concatenate)
                    .map(x -> numbers.getLast() + x)
                    .filter(x -> x <= maximum);

            LongStream m = evaluate(numbers.subList(0, numbers.size()-1), maximum, concatenate)
                    .map(x -> numbers.getLast() * x)
                    .filter(x -> x <= maximum);

            LongStream result = LongStream.concat(a,m);

            if (concatenate) {
                LongStream c = evaluate(numbers.subList(0, numbers.size()-1), maximum, concatenate)
                        .map(x -> Long.parseLong(Long.toString(x) + Long.toString(numbers.getLast())))
                        .filter(x -> x <= maximum);

                result = LongStream.concat(result,c);
            }

            return result;
        }
    }

    long totalCalibrationResult = 0;

    public void process() {
        // System.out.println(equations);

        equations.parallelStream().forEach(equation -> {
            boolean result = evaluate(equation.getRight(), equation.getLeft(), false)
                    .anyMatch(x -> x == equation.getLeft());

            if (result) {
                totalCalibrationResult += equation.getLeft();
            }
        });
    }

    public void results() {
        System.out.println("Day 7 part 1 result: " + totalCalibrationResult);
    }

    long totalCalibrationResultPartTwo = 0;

    public void processPartTwo() {
        equations.parallelStream().forEach(equation -> {
            boolean result = evaluate(equation.getRight(), equation.getLeft(), true)
                    .anyMatch(x -> x == equation.getLeft());

            if (result) {
                totalCalibrationResultPartTwo += equation.getLeft();
            }
        });
    }

    public void resultsPartTwo() {
        System.out.println("Day 7 part 2 result: " + totalCalibrationResultPartTwo);
    }

    /*
Day 7: Bridge Repair
Day 7 part 1 result: 1289579105366
Day 7 part 2 result: 92148721834692
Time: 16.7 seconds
     */
}
