package aoc.year24;

import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 21: Keypad Conundrum");
        PuzzleApp app = new aoc.year24.Day21();
        app.run();
    }

    public String filename() {
        return "data/year24/day21";
    }

    private final List<String> codes = new ArrayList<>();

    public void parseLine(String line) {
        codes.add(line);
    }

    private final Map<Character, Loc> numericKeypadCharacterPositions = Stream.of(new Object[][]{
            {'7', 0, 0},
            {'8', 1, 0},  // +---+---+---+
            {'9', 2, 0},  // | 7 | 8 | 9 |
            {'4', 0, 1},  // +---+---+---+
            {'5', 1, 1},  // | 4 | 5 | 6 |
            {'6', 2, 1},  // +---+---+---+
            {'1', 0, 2},  // | 1 | 2 | 3 |
            {'2', 1, 2},  // +---+---+---+
            {'3', 2, 2},  //     | 0 | A |
            {'0', 1, 3},  //     +---+---+
            {'A', 2, 3}
    }).collect(Collectors.toMap(data -> (char) data[0], data -> new Loc((int) data[1], (int) data[2])));

    private final Map<Loc, Character> invertedNumericKeypad = numericKeypadCharacterPositions.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final Map<Character, Loc> directionalKeypadCharacterPositions = Stream.of(new Object[][]{
            {'^', 1, 0},  //     +---+---+
            {'A', 2, 0},  //     | ^ | A |
            {'<', 0, 1},  // +---+---+---+
            {'v', 1, 1},  // | < | v | > |
            {'>', 2, 1}   // +---+---+---+
    }).collect(Collectors.toMap(data -> (char) data[0], data -> new Loc((int) data[1], (int) data[2])));

    private final Map<Loc, Character> invertedDirectionalKeypad = directionalKeypadCharacterPositions.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final char[] keypadPositions = new char[26];

    private static final Loc UNSAFE_NUMERIC = new Loc(0, 3);
    private static final Loc UNSAFE_DIRECTIONAL = new Loc(0, 0);


    private Set<String> allMoves(Loc current, Loc target, Loc unsafe) {
        Set<String> results = new HashSet<>();

        Loc delta = target.difference(current);

        if (delta.x() == 0 && delta.y() == 0) {
            results.add(""); // No movement necessary
        }

        if (delta.x() > 0) {
            Loc l = current.move(Direction.RIGHT);
            if (!l.equals(unsafe)) {
                // System.out.println("Moved RIGHT from " + current + " through " + l  + " to " + target);
                Set<String> s = allMoves(l, target, unsafe);
                s.forEach(r -> results.add(">" + r));
            }
        } else if (delta.x() < 0) {
            Loc l = current.move(Direction.LEFT);
            if (!l.equals(unsafe)) {
                // System.out.println("Moved LEFT from " + current + " through " + l  + " to " + target);
                Set<String> s = allMoves(l, target, unsafe);
                s.forEach(r -> results.add("<" + r));
            }
        }

        if (delta.y() > 0) {
            Loc l = current.move(Direction.DOWN);
            if (!l.equals(unsafe)) {
                // System.out.println("Moved DOWN from " + current + " through " + l  + " to " + target);
                Set<String> s = allMoves(l, target, unsafe);
                s.forEach(r -> results.add("v" + r));
            }
        } else if (delta.y() < 0) {
            Loc l = current.move(Direction.UP);
            if (!l.equals(unsafe)) {
                // System.out.println("Moved UP from " + current + " through " + l  + " to " + target);
                Set<String> s = allMoves(l, target, unsafe);
                s.forEach(r -> results.add("^" + r));
            }
        }

        // System.out.println("allMoves from " + current + " to " + target + ": " + results);
        return results;
    }

    private Set<String> mapToNumericAll(String code) {
        Set<String> results = new HashSet<>();

        for (char c : code.toCharArray()) {
            Loc current = numericKeypadCharacterPositions.get(keypadPositions[0]);
            Loc target = numericKeypadCharacterPositions.get(c);

            Set<String> ss = allMoves(current, target, UNSAFE_NUMERIC);

            Set<String> x = new HashSet<>();
            if (results.isEmpty()) {
                ss.forEach(s -> x.add(s + "A"));
            } else {
                results.forEach(r -> ss.forEach(s -> x.add(r + s + "A")));
            }
            results = x;

            keypadPositions[0] = c;
        }

        return results;
    }

    private Set<String> mapToDirectionalAll(String code, int keypadNumber) {
        Set<String> results = new HashSet<>();

        for (char c : code.toCharArray()) {
            Loc current = directionalKeypadCharacterPositions.get(keypadPositions[keypadNumber]);
            Loc target = directionalKeypadCharacterPositions.get(c);

            Set<String> ss = allMoves(current, target, UNSAFE_DIRECTIONAL);

            Set<String> x = new HashSet<>();
            if (results.isEmpty()) {
                ss.forEach(s -> x.add(s + "A"));
            } else {
                results.forEach(r -> ss.forEach(s -> x.add(r + s + "A")));
            }
            results = x;

            keypadPositions[keypadNumber] = c;
        }

        return results;
    }

    private String mapToNumeric(String code) {
        StringBuilder result = new StringBuilder();

        for (char c : code.toCharArray()) {
            Loc current = numericKeypadCharacterPositions.get(keypadPositions[0]);
            Loc target = numericKeypadCharacterPositions.get(c);
            Loc delta = target.difference(current);
            while (delta.x() > 0) {
                delta = delta.setX(delta.x() - 1);
                result.append('>');
            }
            while (delta.y() < 0) {
                delta = delta.setY(delta.y() + 1);
                result.append('^');
            }
            while (delta.x() < 0) {
                delta = delta.setX(delta.x() + 1);
                result.append('<');
            }
            while (delta.y() > 0) {
                delta = delta.setY(delta.y() - 1);
                result.append('v');
            }
            result.append('A');
            keypadPositions[0] = c;
        }

        return result.toString();
    }

    private String testNumeric(String code) {
        StringBuilder result = new StringBuilder();
        Loc pos = numericKeypadCharacterPositions.get('A');

        for (char c : code.toCharArray()) {
            switch (c) {
                case '<':
                    pos = pos.move(Direction.LEFT);
                    break;
                case '>':
                    pos = pos.move(Direction.RIGHT);
                    break;
                case '^':
                    pos = pos.move(Direction.UP);
                    break;
                case 'v':
                    pos = pos.move(Direction.DOWN);
                    break;
                case 'A':
                    result.append(invertedNumericKeypad.get(pos));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported character '" + c + "'");
            }

            if (!invertedNumericKeypad.containsKey(pos)) {
                throw new RuntimeException("Illegal position " + pos + " for numeric keypad");
            }
        }

        return result.toString();
    }

    private String mapToDirectional(String code, int keypadNumber) {
        StringBuilder result = new StringBuilder();

        for (char c : code.toCharArray()) {
            Loc current = directionalKeypadCharacterPositions.get(keypadPositions[keypadNumber]);
            Loc target = directionalKeypadCharacterPositions.get(c);
            Loc delta = target.difference(current);
            while (delta.x() > 0) {
                delta = delta.setX(delta.x() - 1);
                result.append('>');
            }
            while (delta.y() > 0) {
                delta = delta.setY(delta.y() - 1);
                result.append('v');
            }
            while (delta.x() < 0) {
                delta = delta.setX(delta.x() + 1);
                result.append('<');
            }
            while (delta.y() < 0) {
                delta = delta.setY(delta.y() + 1);
                result.append('^');
            }
            result.append('A');
            keypadPositions[keypadNumber] = c;
        }

        return result.toString();
    }

    private String testDirectional(String code) {
        StringBuilder result = new StringBuilder();
        Loc pos = directionalKeypadCharacterPositions.get('A');

        for (char c : code.toCharArray()) {
            switch (c) {
                case '<':
                    pos = pos.move(Direction.LEFT);
                    break;
                case '>':
                    pos = pos.move(Direction.RIGHT);
                    break;
                case '^':
                    pos = pos.move(Direction.UP);
                    break;
                case 'v':
                    pos = pos.move(Direction.DOWN);
                    break;
                case 'A':
                    result.append(invertedDirectionalKeypad.get(pos));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported character '" + c + "'");
            }

            if (!invertedDirectionalKeypad.containsKey(pos)) {
                throw new RuntimeException("Illegal position " + pos + " for directional keypad");
            }
        }

        return result.toString();
    }

    private int partOneResult = 0;

    public void process() {
        Arrays.fill(keypadPositions, 'A');

        for (String code : codes) {
            // String mapLevelOne = mapToNumeric(code); // First robot
            // String mapLevelTwo = mapToDirectional(mapLevelOne, 1); // Second robot
            // String result = mapToDirectional(mapLevelTwo, 2); // Me controlling third robot
            // System.out.println(code);
            // System.out.println(mapLevelOne + " -> " + testNumeric(mapLevelOne));
            // System.out.println(mapLevelTwo + " -> " + testDirectional(mapLevelTwo) + " -> " + testNumeric(testDirectional(mapLevelTwo)));
            // System.out.println(result + " -> " + testDirectional(result) + " -> " + testDirectional(testDirectional(result)) + " -> " + testNumeric(testDirectional(testDirectional(result))));
            // System.out.println("*** length=" + result.length() + " ***");
            // partOneResult += (result.length() * Integer.parseInt(code.substring(0,3)));

            Set<String> s1 = mapToNumericAll(code);
            int minLength = s1.stream().mapToInt(String::length).min().orElse(0);
            int finalMinLength1 = minLength;
            s1 = s1.stream().filter(s -> s.length() == finalMinLength1).collect(Collectors.toSet());
            System.out.println("Code " + code + " iteration " + 0 + " complete, set size = " + s1.size());

            for (int i = 1; i <= 2; i++) {
                int finalI = i;
                s1 = s1.stream().flatMap(s -> mapToDirectionalAll(s, finalI).stream()).collect(Collectors.toSet());
                minLength = s1.stream().mapToInt(String::length).min().orElse(0);
                int finalMinLength2 = minLength;
                s1 = s1.stream().filter(s -> s.length() == finalMinLength2).collect(Collectors.toSet());
                System.out.println("Code " + code + " iteration " + i + " complete, set size = " + s1.size());
            }

            minLength = s1.stream().mapToInt(String::length).min().orElse(0);
            int codeValue = Integer.parseInt(code.substring(0, 3));
            partOneResult += (minLength * codeValue);
            System.out.println("Code " + code + ": " + minLength + " * " + codeValue + " = " + (minLength * codeValue));
        }
    }

    public void results() {
        System.out.println("Day 21 part 1 result: " + partOneResult);
    }

    private final Map<String,Long> shortestPathForEachCharacter = new HashMap<>();

    public void processPartTwo() {
        Arrays.fill(keypadPositions, 'A');

        String[] keypadCharacters = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A"};

        for (String code : keypadCharacters) {
            Set<String> s1 = mapToNumericAll(code);
            long minLength = s1.stream().mapToLong(String::length).min().orElse(0);
            long finalMinLength = minLength;
            s1 = s1.stream().filter(s -> s.length() == finalMinLength).collect(Collectors.toSet());
            System.out.println("Code " + code + " iteration " + 0 + " complete, set size = " + s1.size());

            for (int i = 1; i <= 25; i++) {
                int finalI = i;
                s1 = s1.stream().flatMap(s -> mapToDirectionalAll(s, finalI).stream()).collect(Collectors.toSet());
                minLength = s1.stream().mapToLong(String::length).min().orElse(0);
                long finalMinLength1 = minLength;
                s1 = s1.stream().filter(s -> s.length() == finalMinLength1).collect(Collectors.toSet());
                System.out.println("Code " + code + " iteration " + i + " complete, set size = " + s1.size());
            }

            minLength = s1.stream().mapToLong(String::length).min().orElse(0);
            shortestPathForEachCharacter.put(code, minLength);
            System.out.println("Code " + code + ": " + minLength);
        }
    }

    public void resultsPartTwo() {
        long partTwoResult = 0;

        for (String code : codes) {
            long codeValue = Long.parseLong(code.substring(1,3));
            long codeLength = code.chars().mapToLong(c -> shortestPathForEachCharacter.get(String.valueOf((char)c))).sum();
            System.out.println("Code " + code + " has total length " + codeLength);
            partTwoResult += (codeLength * codeValue);
        }

        System.out.println("Day 21 part 2 result: " + partTwoResult);
    }
}

