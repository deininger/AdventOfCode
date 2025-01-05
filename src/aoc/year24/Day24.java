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
    private final Map<String,Gate> zGates = new HashMap<>();
    private final Map<String,Gate> intermediateGates = new HashMap<>();

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
                    Gate g = new Gate(gates.size(), parts[0], parts[1], parts[2], parts[4]);
                    gates.add(g);
                    if (parts[4].startsWith("z")) {
                        z.setRight(z.getRight() | 1L << (Integer.parseInt(parts[4].substring(1)))); // Set the "unknown" bit for this z
                        zGates.put(g.getOutputName(),g);
                    } else {
                        intermediateGates.put(g.getOutputName(),g);
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

    private boolean passingAllTests(Collection<Gate> lgates) {
        return test(lgates,0,0, allZUnknown(lgates))
                && test(lgates,1,1, allZUnknown(lgates))
                && test(lgates,allZUnknown(lgates) >> 1, allZUnknown(lgates) >> 1, allZUnknown(lgates));
    }

    private void swapOutputs(Gate g1, Gate g1s, Map<String,Gate> gateMap) {
        String tout = g1.getOutputName();
        g1.setNewOutputName(g1s.getOutputName());
        g1s.setNewOutputName(tout);
        // Add them back into the gateMap to fix the mappings!
        gateMap.put(g1.getOutputName(), g1);
        gateMap.put(g1s.getOutputName(), g1s);
    }

    private long run(Collection<Gate> lgates, Map<String,Boolean> lwires, long lx, long ly, MutablePair<Long,Long> lz) {
        while (lz.getRight() > 0) {
            lgates.forEach(g -> g.operate(lx, ly, lz, lwires));
        }
        return lz.getLeft();
    }

    private boolean test(Collection<Gate> lgates, long x, long y, long mask) {
        MutablePair<Long,Long> p = MutablePair.of(0L, mask); // allZUnknown(lgates);
        Map<String,Boolean> lwires = new HashMap<>();
        long result = run(lgates, lwires, x, y, p);
        boolean correct = ((result & mask) == (x+y));
        // System.out.println("Testing " + x + "+" + y + "=" + (x+y) + ": " + (result & mask) + " == " + (x+y));
        return correct;
    }

    /**
     * Tests individual bits of a circuit's functionality by iterating through a series
     * of logical tests, starting from the least significant bit. This method ensures that
     * the gates in the provided gate map produce correct outputs under various input scenarios.
     * If a bit test fails, the process stops, and the index of the failing bit is returned.
     *
     * @param gateMap a map containing the circuit's gates with their names as keys and their
     *                corresponding {@code Gate} instances as values
     * @param goodGates a set to collect all gates that contribute to passing bit tests
     * @param quiet a boolean flag indicating whether to suppress verbose output
     * @return the index of the first failing bit or 44 if all bits pass successfully
     */
    private int testBits(Map<String,Gate> gateMap, Set<Gate> goodGates, boolean quiet) {
        boolean passing = true;
        int bit;

        for (bit = 0; bit < 44; bit++) {
            long value = 1L << bit;
            long mask = (1L << (bit+1)) - 1;

            if (!quiet) System.out.println("Testing bit " + bit + " (" + Long.toString(value, 2)
                    + ") with mask " + Long.toString(mask,2) + "...");

            passing = test(gates, 0L, 0L, mask);
            if (!passing) break;
            passing = test(gates, value, 0, mask);
            if (!passing) break;
            passing = test(gates, 0L, value, mask);
            if (!passing) break;
            if (bit > 0) { // test "carry" works
                passing = test(gates, value/2, value/2, mask);
                if (!passing) break;
            }

            Gate passingGate = gateMap.get("z" + (bit < 10 ? "0" : "") + bit);

            if (!quiet) System.out.println("Passed tests for bit " + bit + ": " + passingGate.fullInputString(gateMap));

            goodGates.addAll(passingGate.allInputGates(gateMap));
        }

        if (!passing) {
            String failingGateName = "z" + (bit < 10 ? "0" : "") + bit;
            Gate failingGate = gateMap.get(failingGateName);

            if (!quiet) System.out.println("FAILED tests for bit " + bit + ": " + failingGate.fullInputString(gateMap));
        }

        return bit;
    }

    private void bigTest(Map<String,Gate> gateMap) {
        Set<Gate> goodGates = new HashSet<>();

        int failingBit = testBits(gateMap, goodGates, false);

        String failingGateName = "z" + (failingBit < 10 ? "0" : "") + failingBit;
        Gate failingGate = gateMap.get(failingGateName);

        Set<Gate> allFailingGates = failingGate.allInputGates(gateMap);

        // Try swapping with any of the intermediate gates:
/*
        System.out.println("Gate-swapping over " + allFailingGates.size() + " * " + (gates.size() - goodGates.size()) + " gates");

        int i = 0, j = 0;
        for (Gate g1: allFailingGates) {
            System.out.println("    " + (++i));
            for (Gate g2 : gates) {
                if (goodGates.contains(g2)) continue;
                System.out.println("        " + (++j));

                swapOutputs(g1, g2, gateMap);

                int newFailingBit = testBits(gateMap, goodGates, true);
                if (newFailingBit > failingBit) {
                    System.out.println("It got better when we swapped " + failingGate + " for " + g2 + "!");
                    System.out.println("Old failing bit = " + failingBit + ", new failing bit = " + newFailingBit);
                }

                swapOutputs(g1, g2, gateMap); // Swap back
            }
        }
        */
    }

    private void gateAnalysis(Map<String,Gate> gateMap) {
        zGates.keySet().stream().sorted().forEach( k -> {
            Gate g = gateMap.get(k);
            System.out.println( "Analyzing gate " + g);

            if (!g.getOperator().equals(Gate.XOR)) {
                System.out.println("Gate " + k + " doesn't start with XOR!");

                // Can we find the appropriate XOR somewhere?

                intermediateGates.forEach( (k2,g2) -> {
                    if (g2.getOperator().equals(Gate.XOR)) {
                        Gate one = gateMap.get(g2.getInputOneName());
                        Gate two = gateMap.get(g2.getInputTwoName());
                        if (one != null && one.getOperator().equals(Gate.XOR)
                                && ((one.getInputOneName().startsWith("x") && one.getInputTwoName().startsWith("y"))
                                || (one.getInputOneName().startsWith("y") && one.getInputTwoName().startsWith("x")))) {
                            System.out.println("  Consider substituting " + g2 + " which has child " + one);
                        }
                        if (two != null && two.getOperator().equals(Gate.XOR)
                                && ((two.getInputOneName().startsWith("x") && two.getInputTwoName().startsWith("y"))
                                || (two.getInputOneName().startsWith("y") && two.getInputTwoName().startsWith("x")))) {
                            System.out.println("  Consider substituting " + g2 + " which has child " + two);
                        }
                    }
                });
            } else {
                System.out.println("  Checking for alternating ORs and ANDs...");

                if (g.id() == 124) {
                    System.out.println(g.fullInputString(gateMap));
                }

                // Ok we are starting with an XOR, let's check both sides
                Gate one = gateMap.get(g.getInputOneName());
                Gate two = gateMap.get(g.getInputTwoName());

                if (one != null && one.getOperator().equals(Gate.XOR)
                        && ((one.getInputOneName().startsWith("x") && one.getInputTwoName().startsWith("y"))
                        || (one.getInputOneName().startsWith("y") && one.getInputTwoName().startsWith("x")))) {
                    System.out.println("  Input One is XOR of x and y: " + one + ", checking other side...");

                    // Look for alternating ANDs and ORs...
                    String expecting = Gate.OR;

                    while (two != null) {
                        if (((two.getInputOneName().startsWith("x") && two.getInputTwoName().startsWith("y"))
                                || (two.getInputOneName().startsWith("y") && two.getInputTwoName().startsWith("x")))) {
                            break; // Stop when we encounter a Gate with x and y inputs
                        }

                        if (! two.getOperator().equals(expecting)) {
                            System.out.println("    Input Two should be " + expecting + " but is " + two);
                            break;
                        }

                        // Go "down" the side which doesn't have x and y inputs
                        Gate gg = gateMap.get(two.getInputOneName());

                        if (gg.getInputOneName().startsWith("x") || gg.getInputOneName().startsWith("y")) {
                            gg = gateMap.get(two.getInputTwoName());
                        }

                        two = gg;
                        expecting = (expecting.equals(Gate.OR)) ? Gate.AND : Gate.OR;
                    }

                } else if (two != null && two.getOperator().equals(Gate.XOR)
                        && ((two.getInputOneName().startsWith("x") && two.getInputTwoName().startsWith("y"))
                        || (two.getInputOneName().startsWith("y") && two.getInputTwoName().startsWith("x")))) {
                    System.out.println("  Input Two is XOR of x and y: " + two + ", checking other side...");

                    // Look for alternating ANDs and ORs...
                    String expecting = Gate.OR;

                    while (one != null) {
                        if (((one.getInputOneName().startsWith("x") && one.getInputTwoName().startsWith("y"))
                                || (one.getInputOneName().startsWith("y") && one.getInputTwoName().startsWith("x")))) {
                            break; // Stop when we encounter a Gate with x and y inputs
                        }

                        if (! one.getOperator().equals(expecting)) {
                            System.out.println("    Input One should be " + expecting + " but is " + two);
                            break;
                        }

                        // Go "down" the side which doesn't have x and y inputs
                        Gate gg = gateMap.get(one.getInputOneName());

                        if (gg.getInputOneName().startsWith("x") || gg.getInputOneName().startsWith("y")) {
                            gg = gateMap.get(one.getInputTwoName());
                        }

                        one = gg;
                        expecting = (expecting.equals(Gate.OR)) ? Gate.AND : Gate.OR;
                    }
                }
            }
        });
    }

    public void processPartTwo() {
        Map<String,Gate> gateMap = new HashMap<>();
        gates.forEach(g -> gateMap.put(g.getOutputName(), g));

        gateAnalysis(gateMap);

        // bigTest(gateMap);
    }

    // Swapping:
    // line 101: ghk XOR pnr -> z09
    // line 171: y08 AND x08 -> z08




    /*
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
*/

    public void resultsPartTwo() {
        System.out.println("Day 24 part 2 results: ");
    }

    static final class Gate {
        public static final String AND = "AND";
        public static final String OR = "OR";
        public static final String XOR = "XOR";

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

        public int id() {
            return id;
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

        public String getInputOneName() {
            return inputOne;
        }

        public String getInputTwoName() {
            return inputTwo;
        }

        public String getOperator() {
            return operator;
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

        public String fullInputString(Map<String,Gate> gateMap) {
            StringBuilder sb = new StringBuilder();
            if (inputOne.startsWith("x") || inputOne.startsWith("y")) {
                sb.append(inputOne);
            } else {
                sb.append(inputOne).append("[");
                sb.append(gateMap.get(inputOne).fullInputString(gateMap));
                sb.append("]");
            }
            sb.append(" ");
            sb.append(operator);
            sb.append(" ");
            if (inputTwo.startsWith("x") || inputTwo.startsWith("y")) {
                sb.append(inputTwo);
            } else {
                sb.append(inputTwo).append("[");
                sb.append(gateMap.get(inputTwo).fullInputString(gateMap));
                sb.append("]");
            }
            return sb.toString();
        }

        public Set<Gate> allInputGates(Map<String,Gate> gateMap) {
            Set<Gate> gates = new HashSet<>();
            gates.add(this);
            Gate inOne = gateMap.get(inputOne);
            if (inOne != null) gates.addAll(inOne.allInputGates(gateMap));
            Gate inTwo = gateMap.get(inputTwo);
            if (inTwo != null) gates.addAll(inTwo.allInputGates(gateMap));
            return gates;
        }
    }
}
