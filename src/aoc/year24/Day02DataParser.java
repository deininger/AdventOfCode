package aoc.year24;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day02DataParser {
    public List<List<Integer>> parseData(String filename) {
        List<List<Integer>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<Integer> parsedLine = parseLine(line);
                if (parsedLine != null) {
                    data.add(parsedLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return data;
    }

    private List<Integer> parseLine(String line) {
        try {
            return Arrays.stream(line.split("\\s+"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in line: " + line);
            return null; // Or throw an exception to halt processing
        }
    }
}
