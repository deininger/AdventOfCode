package aoc.year24;

import aoc.util.PuzzleApp;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day24 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 24: Crossed Wires");
        PuzzleApp app = new Day24();
        app.run();
    }

    public String filename() {
        return "data/year24/day24-small";
    }

    private final Map<String,Integer> wireValues = new HashMap<>();
    private final Set<Gate> gates = new HashSet<>();

    public void parseLine(String line) throws IOException {
        if (! line.isEmpty()) {
            if (line.contains(":")) {
                String[] parts = line.split(":");
                wireValues.put(parts[0],Integer.parseInt(parts[1].trim()));
            } else if (line.contains("->")) {
                String[] parts = line.split(" ");
                if (parts[3].equals("->")) {
                    gates.add(new Gate(parts[0], parts[1], parts[2], parts[4]));
                    wireValues.putIfAbsent(parts[0], null);
                    wireValues.putIfAbsent(parts[2], null);
                    wireValues.putIfAbsent(parts[4], null);
                } else {
                    throw new IOException("Unable to parse " + line);
                }
            } else {
                throw new IOException("Unable to parse " + line);
            }
        }
    }

    public void process() {
        System.out.println(wireValues);
        gates.forEach(System.out::println);

        while (wireValues.values().stream().anyMatch(Objects::isNull)) {
            gates.forEach(g -> {
                g.operate(wireValues);
            });
        }

        System.out.println(wireValues);

        System.out.println(wireValues.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted().map(Map.Entry::getValue).collect(Collectors.toStr))
    }

    static final class Gate {
        private static final String AND = "AND";
        private static final String OR = "OR";
        private static final String XOR = "XOR";

        private String inputOne;
        private String operator;
        private String inputTwo;
        private String output;

        public Gate(String inputOne, String operator, String inputTwo, String output) {
            this.inputOne = inputOne;
            this.operator = operator;
            this.inputTwo = inputTwo;
            this.output = output;
        }

        public void operate(Map<String,Integer> wireValues) {
            if (wireValues.get(output) == null
                    && wireValues.get(inputOne) != null
                    && wireValues.get(inputTwo) != null) {
                wireValues.put(output, switch (operator) {
                    case AND -> wireValues.get(inputOne) & wireValues.get(inputTwo);
                    case OR -> wireValues.get(inputOne) | wireValues.get(inputTwo);
                    case XOR -> wireValues.get(inputOne) ^ wireValues.get(inputTwo);
                    default -> throw new IllegalStateException("Unexpected operator: " + operator);
                });
            }
        }

        public String toString() {
            return inputOne + " " + operator + " " + inputTwo + " -> " + output;
        }
    }
}
