package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Day19 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 19: Linen Layout");
        PuzzleApp app = new aoc.year24.Day19();
        app.run();
    }

    public String filename() {
        return "data/year24/day19";
    }

    private final List<String> originalPatterns = new ArrayList<>();
    private final List<String> designs = new ArrayList<>();

    public void parseLine(String line) {
        if (line.isEmpty()) return;

        if (line.contains(",")) {
            Arrays.stream(line.split(",")).map(String::trim).forEach(originalPatterns::add);
        } else {
            designs.add(line);
        }
    }

    /*
     * Use recursion to find out if the design can be assembled from the list of patterns.
     * If the design starts with a pattern, then check the remaining string recurisvely.
     */
    private boolean findMatch(String design, List<String> patterns) {
        if (design.isEmpty()) return true;

        return patterns.stream()
                .filter(design::startsWith)
                .anyMatch(p -> findMatch(design.substring(p.length()), patterns));
    }

    /*
     * This method uses "memoization" to track previous calculations of the same input,
     * to avoid recalculation costs. In this case, the relevant input is the "design" string,
     * as the patterns aren't changing. The memo map doesn't have to be a parameter,
     * it could be a class variable instead (along with the patterns).
     *
     * The logic of this method is otherwise similar to the findMatch method above,
     * other than we're counting the number of matches rather than just finding one.
     */
    private long countMatches(String design, List<String> patterns, Map<String,Long> memo) {
        if (design.isEmpty()) return 1L;
        if (memo.containsKey(design)) return memo.get(design);

        long count = patterns.stream()
                .filter(design::startsWith)
                .mapToLong(p -> countMatches(design.substring(p.length()), patterns, memo))
                .sum();

        memo.put(design, count);
        return count;
    }

    private long partOneResults;

    public void process() {
        List<String> uniquePatterns = new ArrayList<>();

        // Sort the patterns from shortest to longest:

        originalPatterns.sort(Comparator.comparingInt(String::length));

        // Trim the set of patterns down to unique ones, to make the search faster.
        // We get rid of any pattern which can be made up of already-added patterns.
        // This works for part 1, but won't help for part 2.

        // Now that part 2 is written, I could rewrite part 1 to use the same countMatches()
        // mechanism with the memoization, rather than the "unique patterns" approach.

        originalPatterns.forEach(p -> {
            if (!findMatch(p, uniquePatterns)) uniquePatterns.add(p);
        });

        // Count how many designs have a solution (can be made from the unique patterns):

        partOneResults = designs.parallelStream().filter(d -> findMatch(d, uniquePatterns)).count();
    }

    public void results() {
        System.out.println("Day 19 part 1 result: " + partOneResults);
    }

    private long partTwoResults;

    public void processPartTwo() {
        Map<String,Long> memo = new ConcurrentHashMap<>();

        // We have to go back to the original set of patterns, in order to count all
        // the ways we can make the designs.

        partTwoResults = designs.parallelStream().mapToLong(d -> countMatches(d, originalPatterns, memo)).sum();

        // designs.forEach(d-> System.out.println("Design " + d + " has " + memo.get(d) + " variants"));
    }

    public void resultsPartTwo() {
        System.out.println("Day 19 part 2 result: " + partTwoResults);
    }
}
