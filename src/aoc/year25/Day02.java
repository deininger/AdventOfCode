package aoc.year25;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

public class Day02 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 2: Gift Shop");
        PuzzleApp app = new aoc.year25.Day02();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day02";
    }

    List<Pair<Long,Long>> ranges = new ArrayList<>();

    @Override
    public void parseLine(String line) {
        String[] parts = line.split(",");
        for (String part : parts) {
            String[] range = part.split("-");
            ranges.add(Pair.of(Long.parseLong(range[0]), Long.parseLong(range[1])));
        }
    }
    
    List<Long> partOneInvalidIds = new ArrayList<>();
    List<Long> partTwoInvalidIds = new ArrayList<>();

    private boolean invalidPartOne(Long id) {
        String sid = String.valueOf(id);
        int length = sid.length();
        if (length % 2 == 1) return false;
        return (sid.substring(0, length / 2).equals(sid.substring(length / 2)));
    }
    
    @Override
    public void process() {
        ranges.forEach(range -> LongStream.range(range.getLeft(), range.getRight() + 1).forEach(id -> {
            if (invalidPartOne(id)) partOneInvalidIds.add(id);
        }));
    }
    
    @Override
    public void results() {
        System.out.println("Part 1 result: " + partOneInvalidIds.stream().mapToLong(Long::longValue).sum());
    }

    private boolean invalidPartTwo(Long id) {
        String sid = String.valueOf(id);
        int length = sid.length();

        for (int chunkSize = 1; chunkSize < length; chunkSize++) {
            if (length % chunkSize == 0) {
                String[] chunks = sid.split("(?<=\\G.{" + chunkSize + "})");
                if (Arrays.stream(chunks)
                        .distinct()
                        .count() == 1) return true;
            }
        }

        return false;
    }

    @Override
    public void processPartTwo() {
        ranges.forEach(range -> LongStream.range(range.getLeft(), range.getRight() + 1).forEach(id -> {
            if (invalidPartTwo(id)) partTwoInvalidIds.add(id);
        }));
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 result: " + partTwoInvalidIds.stream().mapToLong(Long::longValue).sum());
    }
}
