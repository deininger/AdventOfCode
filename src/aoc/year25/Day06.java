package aoc.year25;

import aoc.util.Operator;
import aoc.util.PuzzleApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day06 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 6: Trash Compactor");
        PuzzleApp app = new Day06();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day06";
    }

    final List<String> originalLines = new ArrayList<>();
    final List<Problem> problems = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        originalLines.add(line); // for Part 2
        String[] parts = line.split("\\s+");
        // Need to ignore the first part if it's empty (happens when the line starts with whitespace)
        if (parts.length > 0 && parts[0].isEmpty()) parts = Arrays.copyOfRange(parts, 1, parts.length);
        // System.out.println("Split " + line + " into " + parts.length + " parts: " + Arrays.toString(parts));
        while (problems.size() < parts.length) problems.add(new Problem());
        for (int i = 0; i < parts.length; i++) {
            problems.get(i).add(parts[i]);
        }
    }

    long resultPartOne;

    public void process() {
        // System.out.println(problems);
        resultPartOne = problems.stream().mapToLong(Problem::solve).sum();
    }
    
    @Override
    public void results() {
        System.out.println("Part 1: " + resultPartOne);
    }

    private List<String> transpose(List<String> strings) {
        int maximumLineLength = originalLines.stream().mapToInt(String::length).max().orElse(0);
        List<String> transpose = new ArrayList<>();
        for (int i = 0; i < maximumLineLength; i++) {
            StringBuilder s = new StringBuilder();
            for (String string : strings) {
                if (string.length() > i )
                    s.append(string.charAt(i));
            }
            transpose.add(s.toString());
        }
        return transpose;
    }

    private final List<Problem> problemsPartTwo = new ArrayList<>();
    long resultPartTwo;

    @Override
    public void processPartTwo() {
        List<String> transposedLines = transpose(originalLines);

        // Iterate through the transposed strings, creating a new set of
        // Problems to solve:

        Problem p = new Problem();
        problemsPartTwo.add(p);

        for (String line : transposedLines) {
            if (line.isBlank()) {
                // Switch to a new Problem each time we encounter a blank row:
                p = new Problem();
                problemsPartTwo.add(p);
            } else {
                p.add(line.trim());
            }
        }

        // System.out.println(problemsPartTwo);
        resultPartTwo = problemsPartTwo.stream().mapToLong(Problem::solve).sum();
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2: " + resultPartTwo);
    }

    private static class Problem {
        private final List<Long> operands = new ArrayList<>();
        private Operator operator;

        public Problem() {}

        public void add(String operandOrOperation) {
            if (operandOrOperation.isEmpty()) return;

            // Check for operation at the end of the string (for Part 2):
            if (operandOrOperation.length() > 1 && !Character.isDigit(operandOrOperation.charAt(operandOrOperation.length()-1))) {
                char op = operandOrOperation.charAt(operandOrOperation.length()-1);
                operator = Operator.getOperator(op);
                operandOrOperation = operandOrOperation.substring(0,operandOrOperation.length()-1).trim();
            }

            try {
                operands.add(Long.parseLong(operandOrOperation));
            } catch (NumberFormatException e) {
                operator = Operator.getOperator(operandOrOperation.charAt(0));
            }
        }

        public long solve() {
            return operands.stream().reduce(operator::apply).orElse(0L);
        }

        public String toString() {
            return operands + " " + operator;
        }
    }
}
