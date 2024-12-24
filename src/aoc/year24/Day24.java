package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.IOException;
import java.util.*;

public class Day24 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 24: Crossed Wires");
        PuzzleApp app = new Day24();
        app.run();
    }

    public String filename() {
        return "data/year24/day24";
    }

    private final Map<String,Boolean> wires = new HashMap<>();
    private long x = 0;
    private long y = 0;
    private final MutablePair<Long,Long> z = MutablePair.of(0L, 0L); // right-side of Pair tracks which bits of z are still unknown (1 = unknown)
    private final List<Gate> gates = new ArrayList<>();

    public void parseLine(String line) throws IOException {
        if (! line.isEmpty()) {
            if (line.contains(":")) {
                String[] parts = line.split(":");
                if (parts[0].startsWith("x")) {
                    x |= (Long.parseLong(parts[1].trim())) << (Integer.parseInt(parts[0].substring(1)));
                } else if (parts[0].startsWith("y")) {
                    y |= (Long.parseLong(parts[1].trim())) << (Integer.parseInt(parts[0].substring(1)));
                } else {
                    throw new IOException("Unable to parse " + line);
                }
            } else if (line.contains("->")) {
                String[] parts = line.split(" ");
                if (parts[3].equals("->")) {
                    gates.add(new Gate(gates.size(), parts[0], parts[1], parts[2], parts[4]));
                    if (parts[4].startsWith("z")) {
                        wires.putIfAbsent(parts[4], null); // need this for now to track which z values we haven't determine yet.
                        z.setRight(z.getRight() | 1L << (Integer.parseInt(parts[4].substring(1)))); // Set the "unknown" bit for this z
                    }
                } else {
                    throw new IOException("Unable to parse " + line);
                }
            } else {
                throw new IOException("Unable to parse " + line);
            }
        }
    }

    public void process() {
        // System.out.println(wireValues);
        // gates.forEach(System.out::println);

        while (z.getRight() > 0) {
            gates.forEach(g -> g.operate(x, y, z, wires));
        }

        // System.out.println(wireValues);
    }

    public void results() {
        /* String xValues = wires.entrySet().stream()
                .filter(e -> e.getKey().startsWith("x"))
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(Map.Entry::getValue).map(b -> b ? "1" : "0")
                .collect(Collectors.joining());
        long xDecimal = Long.parseLong(xValues, 2);
        System.out.println("X inputs:  " + xValues + " = " + xDecimal + " (" + x + ")"); */

        // System.out.println("X inputs:  " + Long.toString(x,2) + " = " + x);

        /* String yValues = wires.entrySet().stream()
                .filter(e -> e.getKey().startsWith("y"))
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(Map.Entry::getValue).map(b -> b ? "1" : "0")
                .collect(Collectors.joining());
        long yDecimal = Long.parseLong(yValues, 2);

        System.out.println("Y inputs:  " + yValues + " = " + yDecimal + " (" + y + ")"); */

        // System.out.println("Y inputs:  " + Long.toString(y,2) + " = " + y);

        /* String zValues = wires.entrySet().stream()
                .filter(e -> e.getKey().startsWith("z"))
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .map(Map.Entry::getValue)
                .map(b -> b == null ? "?" : b ? "1" : "0")
                .collect(Collectors.joining());
        long zDecimal = Long.parseLong(zValues, 2);

        System.out.println("Z result: " + zValues + " = " + zDecimal + " (" + z + ")"); */

        // System.out.println("Z output:  " + Long.toString(z.getLeft(),2) + " = " + z.getLeft());
        // System.out.println("(" + x + " + " + y + " = " + (x+y) + ")");

        System.out.println("Day 24 part 1 result: " + z.getLeft());
    }

    // "small" result should be 100 (4) with inputs 7 and 2
    // "larger" result should be 0011111101000 (2024) with inputs 13 and 31
    // my puzzle input result should be 1100100010000001101111100100101110001011001110 (55114892239566)

    private boolean run(Collection<Gate> lgates, long lx, long ly, MutablePair<Long,Long> lz) {
        while (lz.getRight() > 0) {
            lgates.forEach(g -> g.operate(lx, ly, lz, wires));
        }
        return lz.getLeft() == (lx + ly);
    }

    /*
     * This ought to be done just once...
     */
    private long allZUnknown(Collection<Gate> lgates) {
        long result = 0;
        for (Gate g: lgates) {
            String outputName = g.getOutputName();
            if (outputName.startsWith("z")) {
                result |= 1L << Integer.parseInt(outputName.substring(1));
            }
        }
        return result;
    }

    private boolean test(Collection<Gate> lgates, long x, long y) {
        MutablePair<Long,Long> p = MutablePair.of(0L, allZUnknown(lgates));
        return run(lgates, x, y, p);
        // System.out.println("Testing " + x + "+" + y + "=" + (x+y) + ": " + result + " (" + p.getLeft() + ")");
    }

    private boolean passingAllTests(Collection<Gate> lgates) {
        return test(lgates,0,0)
            && test(lgates,1,1)
            && test(gates,allZUnknown(lgates) >> 1, allZUnknown(lgates) >> 1);
    }

    private void swapOutputs(Gate g1, Gate g1s) {
        String tout = g1.getOutputName();
        g1.setNewOutputName(g1s.getOutputName());
        g1s.setNewOutputName(tout);
    }

    public void processPartTwo() {
        // Try all combinations of swapping 4 gates!

        for (Gate g1 : gates) {
            for (Gate g1s: gates) {
                if (g1s.equals(g1)) continue;
                System.out.println("Outermost swap: " + g1 + " <-> " + g1s);
                swapOutputs(g1, g1s);

                for (Gate g2 : gates) {
                    if (g2.equals(g1)) continue;
                    if (g2.equals(g1s)) continue;
                    for (Gate g2s : gates) {
                        if (g2s.equals(g1)) continue;
                        if (g2s.equals(g1s)) continue;
                        if (g2s.equals(g2)) continue;
                        System.out.println("  Second level swap: " + g2 + " <-> " + g2s);
                        swapOutputs(g2, g2s);

                        for (Gate g3 : gates) {
                            if (g3.equals(g1)) continue;
                            if (g3.equals(g1s)) continue;
                            if (g3.equals(g2)) continue;
                            if (g3.equals(g2s)) continue;
                            for (Gate g3s : gates) {
                                if (g3s.equals(g1)) continue;
                                if (g3s.equals(g1s)) continue;
                                if (g3s.equals(g2)) continue;
                                if (g3s.equals(g2s)) continue;
                                if (g3s.equals(g3)) continue;
                                System.out.println("    Third level swap: " + g3 + " <-> " + g3s);
                                swapOutputs(g3, g3s);

                                for (Gate g4 : gates) {
                                    if (g4.equals(g1)) continue;
                                    if (g4.equals(g1s)) continue;
                                    if (g4.equals(g2)) continue;
                                    if (g4.equals(g2s)) continue;
                                    if (g4.equals(g3)) continue;
                                    if (g4.equals(g3s)) continue;

                                    for (Gate g4s : gates) {
                                        if (g4s.equals(g1)) continue;
                                        if (g4s.equals(g1s)) continue;
                                        if (g4s.equals(g2)) continue;
                                        if (g4s.equals(g2s)) continue;
                                        if (g4s.equals(g3)) continue;
                                        if (g4s.equals(g3s)) continue;
                                        if (g4s.equals(g4)) continue;
                                        // System.out.println("      Innermost swap: " + g4 + " <-> " + g4s);
                                        swapOutputs(g4, g4s);

                                        if (passingAllTests(gates)) {
                                            System.out.println("Found an answer!");
                                            System.out.println(g1);
                                            System.out.println(g1s);
                                            System.out.println(g2);
                                            System.out.println(g2s);
                                            System.out.println(g3);
                                            System.out.println(g3s);
                                            System.out.println(g4);
                                            System.out.println(g4s);
                                            return;
                                        }

                                        swapOutputs(g4, g4s);
                                    }
                                }

                                swapOutputs(g3, g3s);
                            }
                        }

                        swapOutputs(g2, g2s);
                    }
                }

                swapOutputs(g1, g1s);
            }
        }

        System.out.println("Did not find any cobminations of swaps which passed all tests.");
    }

    public void resultsPartTwo() {
        System.out.println("Day 24 part 2 results: ");
    }

    static final class Gate {
        private static final String AND = "AND";
        private static final String OR = "OR";
        private static final String XOR = "XOR";

        private final int id;
        private final String inputOne;
        private final String operator;
        private final String inputTwo;
        private String output;

        public Gate(int id, String inputOne, String operator, String inputTwo, String output) {
            this.id = id;
            this.inputOne = inputOne;
            this.operator = operator;
            this.inputTwo = inputTwo;
            this.output = output;
        }

        /*
         * Used for swapping outputs
         */
        public void setNewOutputName(String newOutput) {
            this.output = newOutput;
        }

        public String getOutputName() {
            return output;
        }

        private Boolean getInput(String inputName, long x, long y, Map<String,Boolean> wires) {
            if (inputName.startsWith("x")) {
                return (x >> Integer.parseInt(inputName.substring(1)) & 1L) == 1L;
            } else if (inputName.startsWith("y")) {
                return (y >> Integer.parseInt(inputName.substring(1)) & 1L) == 1L;
            } else {
                return wires.get(inputName);
            }
        }

        private void setOutput(String outputName, Boolean value, MutablePair<Long,Long> z, Map<String,Boolean> wires) {
            if (outputName.startsWith("z")) {
                if (value) {
                    // Set the appropriate bit value of z (left) and clear the "unknown" flag (right):
                    z.setLeft(z.getLeft() | 1L << Integer.parseInt(outputName.substring(1)));
                    z.setRight(z.getRight() & ~(1L << Integer.parseInt(outputName.substring(1))));
                } else {
                    // Clear the appropriate bit value of z (left) and clear the "unknown" flag (right):
                    z.setLeft(z.getLeft() & ~(1L << Integer.parseInt(outputName.substring(1))));
                    z.setRight(z.getRight() & ~(1L << Integer.parseInt(outputName.substring(1))));
                }
            } else {
                // Intermediate results are stored in the wires map:
                wires.put(outputName, value);
            }
        }

        public void operate(long x, long y, MutablePair<Long,Long> z, Map<String,Boolean> wires) {
            Boolean outputValue = wires.get(output);
            Boolean inputOneValue = getInput(inputOne, x, y, wires);
            Boolean inputTwoValue = getInput(inputTwo, x, y, wires);

            if (outputValue == null && inputOneValue != null && inputTwoValue != null) {
                outputValue = switch (operator) {
                    case AND -> inputOneValue & inputTwoValue;
                    case OR -> inputOneValue | inputTwoValue;
                    case XOR -> inputOneValue ^ inputTwoValue;
                    default -> throw new IllegalStateException("Unexpected operator: " + operator);
                };
                setOutput(output, outputValue, z, wires);
            }
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Gate other = (Gate) o;
            return this.id == other.id;
        }

        public String toString() {
            return id + ": " + inputOne + " " + operator + " " + inputTwo + " -> " + output;
        }
    }
}
