package aoc.year24;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Generated by Google Gemini with the prompt: "write a java method to parse
 * the data file year24/day01-part1 into a suitable data structure"
 */
public class Day01DataParser {

    public List<Pair<Integer, Integer>> parseData(String filename) {
        List<Pair<Integer, Integer>> locationPairs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                locationPairs.add(parseLine(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return locationPairs;
    }

    private Pair<Integer, Integer> parseLine(String line) {
        String[] parts = line.split("\\s+");
        try {
            int firstLocation = Integer.parseInt(parts[0]);
            int secondLocation = Integer.parseInt(parts[1]);
            return new ImmutablePair<>(firstLocation, secondLocation);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in line: " + line);
            return null; // Or throw an exception if you prefer to halt processing
        }
    }

    public static void main(String[] args) {
        Day01DataParser parser = new Day01DataParser();
        List<Pair<Integer, Integer>> data = parser.parseData("data/year24/day01-part1"); // Replace with your file path

        if (data != null) {
            System.out.println("Parsed data:");
            data.forEach(System.out::println);
        }
    }
}
