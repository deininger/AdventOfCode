package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day17 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 17: Chronospatial Computer");
        PuzzleApp app = new aoc.year24.Day17();
        app.run();
    }

    public String filename() {
        return "data/year24/day17";
    }

    private static final String REGISTER_REGEX = "Register ([ABC]): (-?\\d+)";
    private static final Pattern REGISTER_PATTERN = Pattern.compile(REGISTER_REGEX);

    private static final String PROGRAM_REGEX = "Program: (-?\\d+(?:,-?\\d+)+)";
    private static final Pattern PROGRAM_PATTERN = Pattern.compile(PROGRAM_REGEX);

    private final Map<String,Long> registers = new HashMap<>();
    private final List<Integer> program = new ArrayList<>();
    private final List<Integer> output = new ArrayList<>();

    public void parseLine(String line) {
        if (line.isEmpty()) return;

        Matcher matcher = REGISTER_PATTERN.matcher(line);
        if (matcher.matches()) {
            registers.put(matcher.group(1),Long.parseLong(matcher.group(2)));
        } else {
            matcher = PROGRAM_PATTERN.matcher(line);
            if (matcher.matches()) {
                Arrays.stream(matcher.group(1).split(",")).map(Integer::parseInt).forEach(program::add);
            } else {
                throw new IllegalArgumentException("Unexpected input line '" + line + "'");
            }
        }
    }

    private long combo(int operand, long regA, long regB, long regC) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> regA;
            case 5 -> regB;
            case 6 -> regC;
            default -> throw new IllegalArgumentException("Unexpected operand " + operand);
        };
    }

    private long literal(int operand) {
        return operand;
    }

    private List<Integer> run(Long regA, Long regB, Long regC) {
        List<Integer> out = new ArrayList<>();

        for (int instructionPointer = 0; instructionPointer < program.size(); instructionPointer++)
        {
            Operators instruction = Operators.withValue(program.get(instructionPointer));
            int operand;

            switch (instruction) {
                case ADV:
                    operand = program.get(++instructionPointer);
                    regA = regA / ((long) Math.pow(2, combo(operand, regA, regB, regC)));
                    break;
                case BXL:
                    operand = program.get(++instructionPointer);
                    regB = regB ^ literal(operand);
                    break;
                case BST:
                    operand = program.get(++instructionPointer);
                    regB = combo(operand, regA, regB, regC) % 8;
                    break;
                case JNZ:
                    operand = program.get(++instructionPointer);
                    if (regA != 0) instructionPointer = (int)literal(operand) - 1;
                    break;
                case BXC:
                    operand = program.get(++instructionPointer);
                    regB = regB ^ regC;
                    break;
                case OUT:
                    operand = program.get(++instructionPointer);
                    out.add((int)(combo(operand, regA, regB, regC) % 8));
                    break;
                case BDV:
                    operand = program.get(++instructionPointer);
                    regB = regA / ((long) Math.pow(2, combo(operand, regA, regB, regC)));
                    break;
                case CDV:
                    operand = program.get(++instructionPointer);
                    regC = regA / ((long) Math.pow(2, combo(operand, regA, regB, regC)));
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected instruction " + instruction);
            }

            // System.out.println("Debug[" + instructionPointer + "] instruction " + instruction + " operand " + operand + " registers " + registers + " output " + output);
        }

        return out;
    }

    public void process() {
        System.out.println("Registers:" + registers);
        System.out.println("Program: " + program);
        output.addAll(run(registers.get("A"), registers.get("B"), registers.get("C")));
    }

    public void results() {
        System.out.println("Day 17 part 1 result: " + output.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }

    private final AtomicLong magicRegisterAValue = new AtomicLong(Long.MAX_VALUE);

    /*
     * Brute force approach: Does not work, search space is too large.
     *
    public void processPartTwo() {
        LongStream.iterate(0, i -> i + 1).parallel()
                .peek(i -> {if (i % 1000000 == 0) System.out.println("looping " + i);})
                .anyMatch(i -> {
            List<Integer> out = run(i, 0L, 0L);
            if (program.equals(out)) {
                magicRegisterAValue.set( i );
                return true;
            }
            return false;
        });
    }*/

    private boolean matches(List<Integer> out, List<Integer> program, int depth) {
        if (out.size() < depth) return false;
        boolean matches = true;
        for (int i = 0; i < depth; i++) {
            if (!out.get(i).equals(program.get(program.size()-depth+i))) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    private Set<Long> test(long A, int depth) {
        Set<Long> results = new HashSet<>();
        for (int b = 0; b < 8; b++) {
            List<Integer> out = run(A+b,0L, 0L);
            // System.out.println("Trying " + A + " + " + b + " = " + (A+b) + " -> " + out);
            if (matches(out, program, depth)) {
                // System.out.println("Found a solution so far..." + (A+b) + " " + program + " " + out);
                results.add(A+b);
            }
        }
        return results;
    }

    public void processPartTwo() {
        Deque<Pair<Integer,Long>> dq = new ArrayDeque<>();
        dq.add(Pair.of(1, 0L));

        while (!dq.isEmpty()) {
            Pair<Integer,Long> depthAndA = dq.pop();
            int depth = depthAndA.getLeft();
            Long registerA = depthAndA.getRight();
            Set<Long> results = test(registerA, depth);
            if (depth == program.size() && !results.isEmpty()) {
                long result = results.stream().min(Long::compare).get();
                if (result < magicRegisterAValue.get()) magicRegisterAValue.set( result );
            } else {
                results.forEach(r -> dq.add(Pair.of(depthAndA.getLeft() + 1, r << 3)));
            }
        }
    }

    public void resultsPartTwo() {
        System.out.println("Day 17 part 2 result: " + magicRegisterAValue);
    }

    // 109019476355480 is too high
    // 13627434544435 is too low
    // 109019476355482 can't be correct...
    // 109019476330651

    enum Operators {
        ADV(0), BXL(1), BST(2), JNZ(3), BXC(4), OUT(5), BDV(6), CDV(7);

        public static Operators withValue(int value) {
            return switch(value) {
                case 0 -> ADV;
                case 1 -> BXL;
                case 2 -> BST;
                case 3 -> JNZ;
                case 4 -> BXC;
                case 5 -> OUT;
                case 6 -> BDV;
                case 7 -> CDV;
                default -> throw new IllegalArgumentException("Unexpected value " + value);
            };
        }

        private final int value;

        Operators(int value) { this.value = value; }

        public int value() { return value; }

        // The adv instruction (opcode 0) performs division. The numerator is the value in the A register. The denominator is found by raising 2 to the power of the instruction's combo operand. (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.) The result of the division operation is truncated to an integer and then written to the A register.

        // The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's literal operand, then stores the result in register B.

        // The bst instruction (opcode 2) calculates the value of its combo operand modulo 8 (thereby keeping only its lowest 3 bits), then writes that value to the B register.

        // The jnz instruction (opcode 3) does nothing if the A register is 0. However, if the A register is not zero, it jumps by setting the instruction pointer to the value of its literal operand; if this instruction jumps, the instruction pointer is not increased by 2 after this instruction.

        // The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C, then stores the result in register B. (For legacy reasons, this instruction reads an operand but ignores it.)

        // The out instruction (opcode 5) calculates the value of its combo operand modulo 8, then outputs that value. (If a program outputs multiple values, they are separated by commas.)

        // The bdv instruction (opcode 6) works exactly like the adv instruction except that the result is stored in the B register. (The numerator is still read from the A register.)

        // The cdv instruction (opcode 7) works exactly like the adv instruction except that the result is stored in the C register. (The numerator is still read from the A register.)
    }
}
