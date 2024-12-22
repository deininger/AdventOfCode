package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Day21Part2 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 21: Keypad Conundrum");
        PuzzleApp app = new Day21Part2();
        app.run();
    }

    public String filename() {
        return "data/year24/day21";
    }

    private final List<String> codes = new ArrayList<>();

    public void parseLine(String line) {
        codes.add(line);
    }

    Map<Pair<Character,Character>,Set<String>> numericKeypadPaths = new HashMap<>();
    Map<Pair<Character,Character>,Set<String>> directionalKeypadPaths = new HashMap<>();

    Map<Pair<Character,Character>,Long> numericKeypadLengths = new HashMap<>();
    Map<Pair<Character,Character>,Long> directionalKeypadLengths = new HashMap<>();

    /*
     * Precompute the number of paths from each key to each other key on both the
     * numeric keypad and the directional keypad. Add the "A" at the end needed to
     * "push the button".
     *
     * Also precompute the "best" path length for each key pair (at this "depth 1"
     * level all paths have the same length).
     */
    private void precompute() {
        for (Character c1 : Day21Part1.numericKeypadCharacterPositions.keySet()) {
            for (Character c2 : Day21Part1.numericKeypadCharacterPositions.keySet()) {
                Set<String> s = Day21Part1.allMoves(Day21Part1.numericKeypadCharacterPositions.get(c1),
                        Day21Part1.numericKeypadCharacterPositions.get(c2),
                        Day21Part1.UNSAFE_NUMERIC);

                if (s.isEmpty()) {
                    s = Set.of("A");
                } else {
                    s = s.stream().map(r -> r + "A").collect(Collectors.toSet());
                }

                numericKeypadPaths.put(Pair.of(c1,c2),s);

                long length = s.stream().mapToLong(String::length).min().orElse(0);
                numericKeypadLengths.put(Pair.of(c1,c2), length);
            }
        }

        for (Character c1 : Day21Part1.directionalKeypadCharacterPositions.keySet()) {
            for (Character c2 : Day21Part1.directionalKeypadCharacterPositions.keySet()) {
                 Set<String> s = Day21Part1.allMoves(Day21Part1.directionalKeypadCharacterPositions.get(c1),
                                Day21Part1.directionalKeypadCharacterPositions.get(c2),
                                Day21Part1.UNSAFE_DIRECTIONAL);

                if (s.isEmpty()) {
                    s = Set.of("A");
                } else {
                    s = s.stream().map(r -> r + "A").collect(Collectors.toSet());
                }

                directionalKeypadPaths.put(Pair.of(c1,c2),s);

                long length = s.stream().mapToLong(String::length).min().orElse(0);
                directionalKeypadLengths.put(Pair.of(c1,c2), length);
            }
        }
    }

    private final Map<Pair<Pair<Character,Character>,Integer>,Long> bestLengthCache = new HashMap<>();

    /*
     * For all the directional keypad steps, we use a recursive approach, with the
     * "depth" variable controlling how deep to go. The "bestLengthCache" caches
     * each result, keyed by all inputs (the "from" character, the "to" character,
     * and the depth).
     *
     * When we're at depth 1, we can just use our precomputed lookup table to determine
     * the best (shortest) path length.
     *
     * At any depth above 1, we iterate over all possible paths from the "from" character
     * to the "to" character, and for each, we recursively call this method with each pair
     * of characters in those paths (at depth minus one), to determine which is best.
     *
     * The key to this approach is understanding that the total number of steps/characters
     * it takes to perform a series of kepad-button-presses is just the sum of each individual
     * button-press.
     *
     * Without the caching this recursive approach would take forever (and/or run out of memory).
     *
     */
    private long bestLengthPath(Pair<Character,Character> fromTo, int depth) {
        Pair<Pair<Character,Character>,Integer> cacheKey = Pair.of(fromTo,depth);
        if (bestLengthCache.containsKey(cacheKey)) {
            return bestLengthCache.get(cacheKey);
        }

        long result = Long.MAX_VALUE;

        if (depth == 1) {
            result = directionalKeypadLengths.get(fromTo);
        } else {
            for (String path: directionalKeypadPaths.get(fromTo)) {
                long totalLength = 0;
                for (int i = 0; i < path.length(); i++) {
                    char prev = i > 0 ? path.charAt(i-1) : 'A'; // We always start at A
                    char next = path.charAt(i);
                    totalLength += bestLengthPath(Pair.of(prev,next), depth-1);
                }
                if (totalLength < result) {
                    result = totalLength;
                }
            }
        }

        // System.out.println(" bestLengthPath(" + fromTo + "," + depth + ") = " + result);
        bestLengthCache.put(cacheKey, result);
        return result;
    }

    /*
     * For the numeric keypad step, we generate all the strings which can
     * produce the required code on the numeric keypad. This is done one
     * character at a time, starting with moving the robot-finger from "A"
     * to the first character of the code, then from the first to the second,
     * and so on. Eacn of these individual character-to-character movements
     * was precomputed and stored in the "numericKeypadPaths" variable,
     * for efficiency (though since we only do this step once that's not
     * really necessary). Each precomputed set of up/down/left/right
     * commands is terminated with the "A" necessary to make the robot-finger
     * "press down" at the key it's moved to.
     */
    private Set<String> doNumericKeypadStep(String code) {
        Set<String> results = new HashSet<>();

        for (int i = 0; i < code.length(); i++) {
            char prev = i > 0 ? code.charAt(i-1) : 'A'; // We always start at A
            char next = code.charAt(i);

            Set<String> ss = numericKeypadPaths.get(Pair.of(prev,next));

            Set<String> x = new HashSet<>();
            if (results.isEmpty()) {
                x.addAll(ss);
            } else {
                results.forEach(r -> ss.forEach(s -> x.add(r + s)));
            }
            results = x;
        }

        return results;
    }

    private static final int DEPTH = 25;

    private final Map<String,Long> codeBestLengths = new HashMap<>();

    public void processPartTwo() {
        precompute();

        for (String code: codes) {
            long minLength = Long.MAX_VALUE;
            Set<String> s1 = doNumericKeypadStep(code);
            // System.out.println(code + ": " + s1);

            for (String path: s1) {
                // System.out.println("Evaluating '" + path + "'...");
                long length = 0L;

                for (int i = 0; i < path.length(); i++) {
                    char prev = i > 0 ? path.charAt(i - 1) : 'A'; // We always start at A
                    char next = path.charAt(i);
                    length += bestLengthPath(Pair.of(prev, next), DEPTH);
                }

                if (length < minLength) minLength = length;
            }

            System.out.println("Code " + code + " best length " + minLength);
            codeBestLengths.put(code, minLength);
        }
    }

    public void resultsPartTwo() {
        // System.out.println("Numeric keypad precomputed paths: " + numericKeypadPaths);
        // System.out.println("Directional keypad precomputed paths: " + directionalKeypadPaths);

        // System.out.println("Numeric keypad precomputed lengths: " + numericKeypadLengths);
        // System.out.println("Directional keypad precomputed lengths: " + directionalKeypadLengths);

        long resultPartTwo = codeBestLengths.entrySet().stream().mapToLong(e -> Long.parseLong(e.getKey().substring(0,3)) * e.getValue()).sum();
        System.out.println("Day 21 part 2 results: " + resultPartTwo);
    }
}
