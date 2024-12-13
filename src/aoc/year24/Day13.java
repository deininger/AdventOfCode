package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 13: Claw Contraption");
        PuzzleApp app = new aoc.year24.Day13();
        app.run();
    }

    public String filename() {
        return "data/year24/day13";
    }

    private static final int BUTTON_A_TOKEN_COST = 3;
    private static final int BUTTON_B_TOKEN_COST = 1;

    private static final String BUTTON_REGEX = "Button ([AB]): X\\+(\\d+), Y\\+(\\d+)";
    private static final Pattern BUTTON_PATTERN = Pattern.compile(BUTTON_REGEX);

    private static final String PRIZE_REGEX = "Prize: X=(\\d+), Y=(\\d+)";
    private static final Pattern PRIZE_PATTERN = Pattern.compile(PRIZE_REGEX);

    List<ClawMachine> machines = new ArrayList<>();
    ClawMachine current = new ClawMachine(1);

    public void parseLine(String line) {
        if (line.isEmpty()) return;

        Matcher matcher = BUTTON_PATTERN.matcher(line);
        if (matcher.matches()) {
            switch (matcher.group(1)) {
                case "A":
                    current.setButtonA(Pair.of(Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3))));
                    break;
                case "B":
                    current.setButtonB(Pair.of(Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3))));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown button: " + matcher.group(1));
            }
        } else {
            matcher = PRIZE_PATTERN.matcher(line);
            if (matcher.matches()) {
                current.setPrize(Pair.of(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2))));
                machines.add(current);
                current = new ClawMachine(machines.size()+1);
            } else {
                throw new IllegalArgumentException("Unmatched line: '" + line + "'");
            }
        }
    }

    private long tokens(Pair<Long,Long> buttonPresses) {
        return BUTTON_A_TOKEN_COST * buttonPresses.getLeft() + BUTTON_B_TOKEN_COST * buttonPresses.getRight();
    }

    private long dumbSolve(ClawMachine machine) {
        long solution = 0;
        Deque<Pair<Long, Long>> queue = new ArrayDeque<>();
        queue.add(Pair.of(0L, 0L));

        Set<Pair<Long,Long>> seen = new HashSet<>();

        while (!queue.isEmpty()) {
            Pair<Long,Long> buttonPresses = queue.removeFirst();

            if (buttonPresses.getLeft() > 100 || buttonPresses.getRight() > 100) continue;

            Pair<Long,Long> position = Pair.of(machine.buttonA().getLeft() * buttonPresses.getLeft() + machine.buttonB().getLeft() * buttonPresses.getRight(),
                    machine.buttonA().getRight() * buttonPresses.getLeft() + machine.buttonB().getRight() * buttonPresses.getRight());

            if (position.getLeft() > machine.prize().getLeft() || position.getRight() > machine.prize().getRight()) continue;

            long tokens = tokens(buttonPresses);

            if (solution > 0 && tokens > solution) continue;

            if (position.equals(machine.prize())) {
                // System.out.println(buttonPresses + " --> " + tokens);
                solution = tokens;
            }

            Pair<Long,Long> pLeft = Pair.of(buttonPresses.getLeft() + 1, buttonPresses.getRight());
            if (!seen.contains(pLeft)) {
                queue.add(pLeft);
                seen.add(pLeft);
            }

            Pair<Long,Long> pRight = Pair.of(buttonPresses.getLeft(), buttonPresses.getRight() + 1);
            if (!seen.contains(pRight)) {
                queue.add(pRight);
                seen.add(pRight);
            }
        }

        // System.out.println("Machine " + machine.machineNumber() + " solution: " + solution);
        return solution;
    }


    private long oldSmartSolve(ClawMachine machine) {
        // Find the largest number of Button B presses we can do without going over either the X or Y prize coordinates.
        long buttonBMax = Math.min(machine.prize().getLeft() / machine.buttonB().getLeft(), machine.prize().getRight() / machine.buttonB().getRight());

        MutablePair<Long,Long> buttonPresses = MutablePair.of(0L, buttonBMax);

        if (machine.isSolution(buttonPresses)) return tokens(buttonPresses);

        // long maxIterations = Math.max(Math.max(machine.buttonA().getLeft(), machine.buttonA().getRight()),
        //        Math.max(machine.buttonB().getLeft(), machine.buttonB().getRight())); // figure this out...

        long maxIterations = 10000;

        System.out.println("Old smart solve machine " + machine.machineNumber() + " with max iterations " + maxIterations);

        while (buttonPresses.getRight() >= Math.max(0L, buttonBMax - maxIterations)) {
            // System.out.println("Trying " + buttonPresses);

            buttonPresses.setLeft(0L);
            // Try increasing the number of button A presses until we're at the solution out of bounds
            while (machine.isBelowSolution(buttonPresses)) {
                buttonPresses.setLeft(buttonPresses.getLeft() + 1);
            }

            if (machine.isSolution(buttonPresses)) return tokens(buttonPresses);

            buttonPresses.setRight(buttonPresses.getRight() - 1); // Decrease B and try again...
        }

        return 0L;
    }


    private long smartSolve(ClawMachine machine) {

        // Find the largest number of Button B presses we can do without going over either the X or Y prize coordinates.
        long buttonB = Math.min(machine.prize().getLeft() / machine.buttonB().getLeft(), machine.prize().getRight() / machine.buttonB().getRight());
        // long maxIterations = machine.buttonA().getRight() * machine.buttonB().getRight() * machine.buttonA().getLeft() * machine.buttonB().getLeft();
        long maxIterations = buttonB;

        System.out.println("new smart solve machine " + machine.machineNumber() + " with button B starting at " + buttonB + " and max iterations " + maxIterations);

        while (buttonB > 0 && maxIterations > 0) {
            if (((machine.prize().getLeft() - machine.buttonB().getLeft() * buttonB) % (machine.buttonA().getLeft()) == 0)
                    && (machine.prize().getRight() - machine.buttonB().getRight() * buttonB) % (machine.buttonA().getRight()) == 0) {
                long buttonAleft = (machine.prize().getLeft() - machine.buttonB().getLeft() * buttonB) / (machine.buttonA().getLeft());
                long buttonAright = (machine.prize().getRight() - machine.buttonB().getRight() * buttonB) / (machine.buttonA().getRight());
                if (buttonAleft == buttonAright) {
                    Pair<Long, Long> solution = Pair.of(buttonAleft, buttonB);
                    checkSolution(machine, solution);
                    long tokens = tokens(solution);
                    System.out.println("Solved machine " + machine.machineNumber() + ": " + solution + " -> " + tokens);
                    return tokens;
                }
            }
            buttonB--;
            maxIterations--;
        }

        return 0;
    }

    private void checkSolution(ClawMachine machine, Pair<Long,Long> solution) {
        long computedPrizeX = machine.buttonA().getLeft() * solution.getLeft() + machine.buttonB().getLeft() * solution.getRight();
        long computedPrizeY = machine.buttonA().getRight() * solution.getLeft() + machine.buttonB().getRight() * solution.getRight();

        if (Pair.of(computedPrizeX, computedPrizeY).equals(machine.prize())) {
            System.out.println("Solution check is good!");
        } else {
            System.out.println("Solution check is bad: " + Pair.of(computedPrizeX, computedPrizeY) + " != " + machine.prize());
        }
    }

    private long result;

    public void process() {
        result = machines.stream().mapToLong(this::smartSolve).sum();
    }

    public void results() {
        System.out.println("Day 13 Part 1 Result: " + result);
    }

    private long resultPartTwo;

    private static final long OFFSET = 10000000000000L;

    public void processPartTwo() {
        resultPartTwo = machines.stream()
                .peek(machine -> machine.setPrize(Pair.of(machine.prize().getLeft() + OFFSET,
                        machine.prize().getRight() + OFFSET)))
                .mapToLong(this::smartSolve)
                .sum();
    }

    public void resultsPartTwo() {
        System.out.println("Day 13 Part 2 Result: " + resultPartTwo);
    }

    static class ClawMachine {
        private final int machineNumber;
        private Pair<Long,Long> buttonA;
        private Pair<Long,Long> buttonB;
        private Pair<Long,Long> prize;

        public ClawMachine(int machineNumber) {
            this.machineNumber = machineNumber;
        }

        public void setButtonA(Pair<Long,Long> loc) {
            buttonA = loc;
        }

        public void setButtonB(Pair<Long,Long> loc) {
            buttonB = loc;
        }

        public void setPrize(Pair<Long,Long> loc) {
            prize = loc;
        }

        public int machineNumber() {
            return machineNumber;
        }

        public Pair<Long,Long> buttonA() {
            return buttonA;
        }

        public Pair<Long,Long> buttonB() {
            return buttonB;
        }

        public Pair<Long,Long> prize() {
            return prize;
        }

        public boolean isSolution(Pair<Long,Long> buttonPresses) {
            Pair<Long,Long> position = Pair.of(buttonA().getLeft() * buttonPresses.getLeft() + buttonB().getLeft() * buttonPresses.getRight(),
                    buttonA().getRight() * buttonPresses.getLeft() + buttonB().getRight() * buttonPresses.getRight());
            return position.equals(prize());
        }

        public boolean isBelowSolution(Pair<Long,Long> buttonPresses) {
            Pair<Long,Long> position = Pair.of(buttonA().getLeft() * buttonPresses.getLeft() + buttonB().getLeft() * buttonPresses.getRight(),
                    buttonA().getRight() * buttonPresses.getLeft() + buttonB().getRight() * buttonPresses.getRight());
            return position.getLeft() < prize().getLeft() && position.getRight() < prize().getRight();
        }

        public String toString() {
            return "Button A: " + buttonA + "\nButton B: " + buttonB + "\nPrize: " + prize + "\n";
        }
    }
}
