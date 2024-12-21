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
        return "data/year24/day21-small";
    }

    private final List<String> codes = new ArrayList<>();

    public void parseLine(String line) {
        codes.add(line);
    }

    private final Map<Character, Loc> numericKeypadCharacterPositions = Stream.of(new Object[][] {
            { '7', 0, 0 },
            { '8', 1, 0 },  // +---+---+---+
            { '9', 2, 0 },  // | 7 | 8 | 9 |
            { '4', 0, 1 },  // +---+---+---+
            { '5', 1, 1 },  // | 4 | 5 | 6 |
            { '6', 2, 1 },  // +---+---+---+
            { '1', 0, 2 },  // | 1 | 2 | 3 |
            { '2', 1, 2 },  // +---+---+---+
            { '3', 2, 2 },  //     | 0 | A |
            { '0', 1, 3 },  //     +---+---+
            { 'A', 2, 3 }
    }).collect(Collectors.toMap(data -> (char)data[0], data -> new Loc((int)data[1], (int)data[2])));

    private final Map<Loc,Character> invertedNumericKeypad = numericKeypadCharacterPositions.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final Map<Character, Loc> directionalKeypadCharacterPositions = Stream.of(new Object[][] {
            { '^', 1, 0 },  //     +---+---+
            { 'A', 2, 0 },  //     | ^ | A |
            { '<', 0, 1 },  // +---+---+---+
            { 'v', 1, 1 },  // | < | v | > |
            { '>', 2, 1 }   // +---+---+---+
    }).collect(Collectors.toMap(data -> (char)data[0], data -> new Loc((int)data[1], (int)data[2])));

    private final Map<Loc,Character> invertedDirectionalKeypad = directionalKeypadCharacterPositions.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final char[] keypadPositions = { 'A', 'A', 'A' }; // index 0 is the numeric keypad, the rest are directional.

    private String mapToNumeric(String code) {
        StringBuilder result = new StringBuilder();

        for (char c : code.toCharArray()) {
            Loc current = numericKeypadCharacterPositions.get(keypadPositions[0]);
            Loc target = numericKeypadCharacterPositions.get(c);
            Loc delta = target.difference(current);
            while (delta.x() > 0) { delta = delta.setX(delta.x()-1); result.append( '>'); }
            while (delta.y() < 0) { delta = delta.setY(delta.y()+1); result.append( '^'); }
            while (delta.x() < 0) { delta = delta.setX(delta.x()+1); result.append( '<'); }
            while (delta.y() > 0) { delta = delta.setY(delta.y()-1); result.append( 'v'); }
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
                case '<': pos = pos.move(Direction.LEFT); break;
                case '>': pos = pos.move(Direction.RIGHT); break;
                case '^': pos = pos.move(Direction.UP); break;
                case 'v': pos = pos.move(Direction.DOWN); break;
                case 'A': result.append(invertedNumericKeypad.get(pos)); break;
                default: throw new UnsupportedOperationException("Unsupported character '" + c + "'");
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
            while (delta.x() > 0) { delta = delta.setX(delta.x()-1); result.append( '>'); }
            while (delta.y() > 0) { delta = delta.setY(delta.y()-1); result.append( 'v'); }
            while (delta.x() < 0) { delta = delta.setX(delta.x()+1); result.append( '<'); }
            while (delta.y() < 0) { delta = delta.setY(delta.y()+1); result.append( '^'); }
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
                case '<': pos = pos.move(Direction.LEFT); break;
                case '>': pos = pos.move(Direction.RIGHT); break;
                case '^': pos = pos.move(Direction.UP); break;
                case 'v': pos = pos.move(Direction.DOWN); break;
                case 'A': result.append(invertedDirectionalKeypad.get(pos)); break;
                default: throw new UnsupportedOperationException("Unsupported character '" + c + "'");
            }

            if (!invertedDirectionalKeypad.containsKey(pos)) {
                throw new RuntimeException("Illegal position " + pos + " for directional keypad");
            }
        }

        return result.toString();
    }

    private int partOneResult = 0;

    public void process() {
        for (String code: codes) {
            String mapLevelOne = mapToNumeric(code); // First robot
            String mapLevelTwo = mapToDirectional(mapLevelOne, 1); // Second robot
            String result = mapToDirectional(mapLevelTwo, 2); // Me controlling third robot
            System.out.println(code);
            System.out.println(mapLevelOne + " -> " + testNumeric(mapLevelOne));
            System.out.println(mapLevelTwo + " -> " + testDirectional(mapLevelTwo) + " -> " + testNumeric(testDirectional(mapLevelTwo)));
            System.out.println(result + " -> " + testDirectional(result) + " -> " + testDirectional(testDirectional(result)) + " -> " + testNumeric(testDirectional(testDirectional(result))));
            System.out.println("*** length=" + result.length() + " ***");
            System.out.println("keypad positions: " + Arrays.toString(keypadPositions));
            partOneResult += (result.length() * Integer.parseInt(code.substring(0,3)));
        }
    }

    public void result() {
        System.out.println("Day 21 part 1 result: " + partOneResult);
    }
}

// 029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
// 980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
// 179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
// 456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
// 379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A

// 179A
//   <<^A^^A>>AvvvA
//     v<<AA>^A>A<AA>AvAA^Av<AAA>^A
//       v<A<AA>>^AAvA<^A>AvA^Av<<A>>^AAvA^Av<A>^AA<A>Av<A<A>>^AAAvA<^A>A (length 64)
// 179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A

