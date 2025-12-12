package aoc.year25;

import aoc.util.PuzzleApp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day10 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 10: Factory");
        PuzzleApp app = new Day10();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day10";
    }

    private static final String REGEX = "^\\[([.#]+)] (.*) \\{([\\d,]+)}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final String REGEX2 = "\\(([\\d,]+)\\)";
    private static final Pattern PATTERN2 = Pattern.compile(REGEX2);

    private final List<Machine> machines = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.matches()) {
            String lights = matcher.group(1);

            Matcher matcher2 = PATTERN2.matcher(matcher.group(2));
            List<Set<Integer>> buttons = new ArrayList<>();

            while (matcher2.find()) {
                String numbersInsideParens = matcher2.group(1);

                Set<Integer> numbers = Arrays.stream(numbersInsideParens.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());

                buttons.add(numbers);
            }

            List<Integer> joltages = Arrays.stream(matcher.group(3).split(","))
                    .map(String::trim) // Good practice to trim whitespace
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            
            machines.add(new Machine(lights, buttons, joltages));
        } else {
            throw new IllegalArgumentException( "Could not match pattern " + REGEX + " with line '" + line + "'");
        }
     }

     private State search(Machine m, Queue<State> q, Set<State> seen) {
        while (!q.isEmpty()) {
            State s = q.poll();
            if (seen.contains(s)) continue;
            seen.add(s);

            if (m.solvedBy(s.bits())) return s;

            for (int i = 0; i < m.buttonCount(); i++) {
                State s2 = new State(s);
                s2.toggle(m, i);
                if (m.solvedBy(s2.bits())) return s2; // shortcut
                q.add(s2);
            }
        }

        return null;
     }

     private long partOneResult = 0;

    public void process() {
        /*
        for (Machine machine : machines) {
            System.out.println("Processing " + machine);
            State state = new State(machine.numLights());
            Queue<State> q = new LinkedList<>();
            q.add(state);
            Set<State> seen = new HashSet<>();
            State solution = search(machine, q, seen);
            if (solution != null) {
                System.out.println("Solved " + machine + " in " + solution.depth());
                partOneResult += solution.depth();
            } else {
                System.out.println("No solution for " + machine + "???");
            }
        }
        */
    }

    @Override
    public void results() {
        System.out.println("Part One result = " + partOneResult);
    }

    private StatePartTwo searchPartTwo(Machine m, Queue<StatePartTwo> q, Set<StatePartTwo> seen) {
        while (!q.isEmpty()) {
            StatePartTwo s = q.poll();
            if (seen.contains(s)) continue;
            seen.add(s);

            if (m.joltageMatch(s.counters())) return s;

            for (int i = 0; i < m.buttonCount(); i++) {
                StatePartTwo s2 = new StatePartTwo(s);
                s2.increment(m, i);
                if (m.joltageMatch(s2.counters())) return s2; // shortcut
                if (m.allowableJoltage(s2.counters())) q.add(s2);
            }
        }

        return null;
    }

    private long partTwoResult = 0;

    @Override
    public void processPartTwo() {
        for (Machine machine : machines) {
            System.out.println("Processing " + machine);
            StatePartTwo state = new StatePartTwo(machine.numLights());
            Queue<StatePartTwo> q = new LinkedList<>();
            q.add(state);
            Set<StatePartTwo> seen = new HashSet<>();
            StatePartTwo solution = searchPartTwo(machine, q, seen);
            if (solution != null) {
                System.out.println("Solved " + machine + " in " + solution.depth());
                partTwoResult += solution.depth();
            } else {
                System.out.println("No solution for " + machine + "???");
            }
        }
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part Two result = " + partTwoResult);
    }

    private static class StatePartTwo {
        private final int[] counters;
        private int depth;

        public StatePartTwo(int numCounters) {
            this.counters = new int[numCounters];
            for (int i = 0; i < numCounters; i++) counters[i] = 0;
            this.depth = 0;
        }

        public StatePartTwo(StatePartTwo other) {
            this.counters = Arrays.copyOf(other.counters, other.counters.length);
            this.depth = other.depth;
        }

        public void increment(Machine m, int button) {
            Set<Integer> actions = m.buttons.get(button);
            for (Integer action : actions) counters[action]++;
            depth++;
        }

        public int[] counters() {
            return counters;
        }

        public int depth() {
            return depth;
        }

        public String toString() {
            return Arrays.toString(counters) + " (" + depth +")";
        }
    }


    private static class State {
        private final BitSet bs;
        private int depth;

        public State(int numBits) {
            this.bs = new BitSet(numBits);
            this.depth = 0;
        }

        public State(State other) {
            this.bs = (BitSet) other.bs.clone();
            this.depth = other.depth;
        }

        public void toggle(Machine m, int button) {
            Set<Integer> actions = m.buttons.get(button);
            for (Integer action : actions) bs.flip(action);
            depth++;
        }

        public BitSet bits() {
            return bs;
        }

        public int depth() {
            return depth;
        }

        public String toString() {
            return bs.toString() + " (" + depth +")";
        }
    }

    private static class Machine {
        private final int numLights;
        private final String targetLights;
        private final int buttonCount;
        private final List<Set<Integer>> buttons;
        private final List<Integer> joltages;
        
        public Machine(String targetLights, List<Set<Integer>> buttons, List<Integer> joltages) {
            this.numLights = targetLights.length();
            this.targetLights = targetLights;
            this.buttonCount = buttons.size();
            this.buttons = buttons;
            this.joltages = joltages;
        }

        public int numLights() {
            return numLights;
        }

        public int buttonCount() {
            return buttonCount;
        }

        public boolean solvedBy(BitSet bs) {
            boolean solved = true;
            for (int i = 0; solved && i < numLights; i++) {
                solved = ((bs.get(i) && (targetLights.charAt(i) == '#')) || (!bs.get(i) && targetLights.charAt(i) == '.'));
            }
            return solved;
        }

        public boolean joltageMatch(int[] counters) {
            boolean solved = true;
            for (int i = 0; solved && i < numLights; i++) {
                solved = joltages.get(i).equals(counters[i]);
            }
            return solved;
        }

        public boolean allowableJoltage(int[] counters) {
            boolean ok = true;
            for (int i = 0; ok && i < numLights; i++) {
                ok = (joltages.get(i).compareTo(counters[i]) >= 0);
            }
            return ok;
        }

        public String toString() {
            return targetLights + " " + joltages;
        }
    }
}
