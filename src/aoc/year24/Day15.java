package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;
import java.util.stream.Collectors;

public class Day15 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 15: Warehouse Woes");
        PuzzleApp app = new aoc.year24.Day15();
        app.run();
    }

    public String filename() {
        return "data/year24/day15-sample2";
    }

    private final CharacterGrid originalMap = new CharacterGrid();
    private String moves = "";

    public void parseLine(String line) {
        if (line.isEmpty()) {
            return;
        } else if (line.startsWith("#")) {
            originalMap.addRow(line);
        } else {
            moves = moves + line;
        }
    }

    private static Map<Character,Direction> directions = new HashMap<>();

    static {
        directions.put('^', Direction.UP);
        directions.put('v', Direction.DOWN);
        directions.put('<', Direction.LEFT);
        directions.put('>', Direction.RIGHT);
    }

    private CharacterGrid map;

    private Loc move(Loc l, Direction d) {
        if (map.at(l.step(d)) == '#') { // wall, can't move
            return l;
        } else if (map.at(l.step(d)) == 'O') { // first move any boxes in the way, if possible
            move(l.step(d),d);
        }

        if (map.at(l.step(d)) == '.') { // if there's room to move now, then move
            char c = map.at(l);
            // System.out.println("Moving '" + c + "' at " + l + " " + d);
            map.set(l, '.');
            l = l.step(d);
            map.set(l, c);
        }

        return l;
    }

    public void process() {
        map = new CharacterGrid(originalMap);

        // System.out.println(map);
        // System.out.println(moves);

        Loc robot = map.locate('@');

        for (char c : moves.toCharArray()) {
            robot = move(robot, directions.get(c));
            // System.out.println(map);
        }
    }

    public void results() {
        long gpsTotal = map.locateAll('O').stream().mapToLong(l -> 100L * l.y() + l.x()).sum();
        System.out.println("Day 15 part 1 result: " + gpsTotal);
    }

    private String widen(String line) {
        return line.chars().mapToObj(c ->  switch(c) {
                    case '@' -> "@ ";
                    case '#' -> "##";
                    case 'O' -> "[]";
                    case '.' -> "..";
                    default -> "XX";
                }).collect(Collectors.joining(""));
    }

    private CharacterGrid wideMap = new CharacterGrid();

    private Set<Loc> canMove(Set<Loc> boxes, Direction d) {
        if (boxes.isEmpty()) return new HashSet<>();

        Set<Loc> newBoxes = new HashSet<>();

        boxes.forEach(b -> {
             if (wideMap.at(b.step(d)) == '[') {
                 newBoxes.add(b.step(d));
                 newBoxes.add(b.step(d).step(Direction.RIGHT));
             } else if (wideMap.at(b.step(d)) == ']') {
                 newBoxes.add(b.step(d));
                 newBoxes.add(b.step(d).step(Direction.LEFT));
             }
        });

        System.out.println("boxes = " + boxes + ", newBoxes = " + newBoxes);

        Set<Loc> result = new HashSet<>();

        if (newBoxes.isEmpty()) { // See if we can move what we've already got
            boxes.forEach(box -> {
                if (wideMap.at(box.step(d)) == '.') {
                    System.out.println("Box " + box + " can move!");
                    result.add(box);
                } else {
                    System.out.println("Box " + box + " CAN'T move!");
                }
            });
        } else {
            result.addAll(boxes);
            result.addAll(newBoxes);
        }

        return result;
    }

    private Loc wideMove(Loc l, Direction d) {
        if (wideMap.at(l.step(d)) == '#') { // wall, can't move
            return l;
        } else if ((d.equals(Direction.LEFT) || d.equals(Direction.RIGHT))
                && (wideMap.at(l.step(d)) == '[' || wideMap.at(l.step(d)) == ']')) {
            wideMove(l.step(d),d);
        } else if ((d.equals(Direction.UP) || d.equals(Direction.DOWN))
                && (wideMap.at(l.step(d)) == '[' || wideMap.at(l.step(d)) == ']')) {
            Set<Loc> boxes = new HashSet<>();
            boxes.add(l.step(d));
            if (wideMap.at(l.step(d)) == '[') boxes.add(l.step(d).step(Direction.RIGHT));
            if (wideMap.at(l.step(d)) == ']') boxes.add(l.step(d).step(Direction.LEFT));
            boxes = canMove(boxes,d);
            if (boxes.isEmpty()) {
                System.out.println("can't move boxes: " + boxes);
                return l;
            } else {
                System.out.println("moving all boxes: " + boxes);
                while (!boxes.isEmpty()) {
                    Iterator<Loc> it = boxes.iterator();
                    while (it.hasNext()) {
                        Loc b = it.next();
                        if (wideMap.at(b.step(d)) == '.') {
                            System.out.println( "swapping " + b + " and " + b.step(d));
                            char c = wideMap.at(b);
                            wideMap.set(b, '.');
                            wideMap.set(b.step(d), c);
                            it.remove();
                        }
                    }
                }
            }
        }

        if (wideMap.at(l.step(d)) == '.') { // if there's room to move now, then move
            char c = wideMap.at(l);
            // System.out.println("Moving '" + c + "' at " + l + " " + d);
            wideMap.set(l, '.');
            l = l.step(d);
            wideMap.set(l, c);
        }

        return l;
    }

    public void processPartTwo() {
        originalMap.rows().forEach(l -> wideMap.addRow(widen(l)));

        System.out.println(wideMap);

        Loc robot = wideMap.locate('@');

        for (char c : moves.toCharArray()) {
            robot = wideMove(robot, directions.get(c));
            System.out.println(wideMap);
        }
    }
}
