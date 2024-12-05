package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 3: Mull It Over");
        PuzzleApp app = new aoc.year24.Day03();
        app.run();
    }

    private static final Pattern MUL_PATTERN = Pattern.compile("(mul|don't|do)\\((?:(\\d{1,3}),(\\d{1,3}))?\\)");

    @Override
    public String filename() {
        return "data/year24/day03";
    }

    private static final boolean PART_1 = false;

    private long total = 0;
    private boolean enabled = true;

    @Override
    public void parseLine(String line) {
        List<Pair<Integer, Integer>> results = new ArrayList<>();
        Matcher matcher = MUL_PATTERN.matcher(line);

        while (matcher.find()) {
            String action = matcher.group(1);
            switch (action) {
                case "mul":
                    if (enabled || PART_1) {
                        try {
                            int left = Integer.parseInt(matcher.group(2));
                            int right = Integer.parseInt(matcher.group(3));
                            results.add(new ImmutablePair<>(left, right));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format found: " + e.getMessage()
                                    + " with group count: " + matcher.groupCount()
                                    + " in match: " + matcher.group(1) + matcher.group(2) + matcher.group(3)
                                    + " at position: " + matcher.start());
                        }
                    }
                    break;
                case "do":
                    enabled = true;
                    break;
                case "don't":
                    enabled = false;
                    break;
            }
        }

        // System.out.println(results);
        total += results.stream().mapToInt(pair -> pair.getLeft() * pair.getRight()).sum();
    }

    public void results() {
        System.out.println("Day 3 part 1 result: " + total); // 159892596
    }
}
