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
        return "data/year24/day15";
    }

    private final CharacterGrid originalMap = new CharacterGrid();
    private String moves = "";

    public void parseLine(String line) {
        if (!line.isEmpty()) {
            if (line.startsWith("#")) {
                originalMap.addRow(line);
            } else {
                moves = moves + line;
            }
        }
    }

    private static final Map<Character,Direction> directions = new HashMap<>();

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
                    case '@' -> "@.";
                    case '#' -> "##";
                    case 'O' -> "[]";
                    case '.' -> "..";
                    default -> "XX";
                }).collect(Collectors.joining(""));
    }

    private final CharacterGrid wideMap = new CharacterGrid();

    /*
     * Test to see if we can move all locs in the given direction. If we can, then move them, otherwise don't.
     */
    private Set<Loc> wideMove(Set<Loc> locs, Direction d) {
        if (locs.isEmpty()) {
            return locs;
        }

        if (locs.stream().anyMatch(l -> wideMap.at(l.step(d)) == '#')) { // At least one loc hits a wall
            System.out.println("Locs " + locs + " hit a wall " + d);
            return locs; // return without moving
        }

        if ((d.equals(Direction.LEFT) || d.equals(Direction.RIGHT))) {
            Set<Loc> boxes = locs.stream().filter(l -> wideMap.at(l.step(d)) == '[' || wideMap.at(l.step(d)) == ']').collect(Collectors.toSet());
            if (!boxes.isEmpty()) {
                System.out.println("Locs " + boxes + " pushing " + d);
                wideMove(boxes.stream().map(l -> l.step(d)).collect(Collectors.toSet()), d);
            }
        }

        if ((d.equals(Direction.UP) || d.equals(Direction.DOWN))) { // Here's the hard part...
            Set<Loc> boxes = locs.stream().filter(l -> wideMap.at(l.step(d)) == '[' || wideMap.at(l.step(d)) == ']').collect(Collectors.toSet());

            if (!boxes.isEmpty()) {
                // Identify counterparts of each box ('[' to go with ']' and vice-versa):
                Set<Loc> moreboxes = new HashSet<>();
                boxes.forEach(box -> {
                    moreboxes.add(box);
                    if (wideMap.at(box.step(d)) == '[') moreboxes.add(box.step(Direction.RIGHT));
                    if (wideMap.at(box.step(d)) == ']') moreboxes.add(box.step(Direction.LEFT));
                });
                System.out.println("Moreboxes " + moreboxes + " pushing " + d);
                wideMove(moreboxes.stream().map(l -> l.step(d)).collect(Collectors.toSet()), d);
            }
        }

        // Now we can check the map and see if the way is clear for us to move

        if (locs.stream().allMatch(l -> wideMap.at(l.step(d)) == '.')) {
            System.out.println("Locs " + locs + " can move " + d + "!");
            Set<Loc> newLocs = new HashSet<>();// The whole group can move!
            locs.forEach(l -> {
                char c = wideMap.at(l);
                wideMap.set(l, '.');
                l = l.step(d);
                newLocs.add(l);
                wideMap.set(l, c);
            });
            return newLocs;
        }

        return locs;
    }

    public void processPartTwo() {
        originalMap.rows().forEach(l -> wideMap.addRow(widen(l)));

        System.out.println(wideMap);

        Loc robot = wideMap.locate('@');

        for (char c : moves.toCharArray()) {
            Set<Loc> moveResult = wideMove(Set.of(robot), directions.get(c));
            if (!robot.equals(moveResult.stream().findFirst().get())) {
                System.out.println("Move " + directions.get(c) + " robot moved from " + robot + " to " + moveResult);
            }
            robot = moveResult.stream().findFirst().get();
            System.out.println(wideMap);
        }
    }

    public void resultsPartTwo() {
        long gpsTotal = wideMap.locateAll('[').stream().mapToLong(l -> 100L * l.y() + l.x()).sum();
        System.out.println("Day 15 part 2 result: " + gpsTotal);
    }
}
