package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class Day06 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 6: Guard Gallivant");
        PuzzleApp app = new aoc.year24.Day06();
        app.run();
    }

    public String filename() {
        return "data/year24/day06";
    }

    CharacterGrid map = new CharacterGrid();

    public void parseLine(String line) {
        map.addRow(line);
    }

    Set<Loc> path = new HashSet<>();

    public void process() {
        // System.out.println(map);

        Loc pos = map.locate('^');
        Direction facing = Direction.UP;

        while (map.contains(pos)) {
            path.add(pos);
            Loc next = pos.step(facing);
            while (map.contains(next) && map.at(next) == '#') { // Turn until we can move forward
                facing = facing.turnRight();
                next = pos.step(facing);
            }
            pos = next;
        }

    }

    public void results() {
        // System.out.println(map.overlayPath(path, 'X'));
        System.out.println("Day 6 part 1 result: " + path.size());
    }

    private int loopCount = 0;

    public void processPartTwo() {
        // Try placing an obstruction at each point of the path:
        for (Loc obstruction: path) {
            if (map.at(obstruction) != '^') { // Can't place an obstruction at the starting position
                Set<Pair<Loc,Direction>> visited = new HashSet<>();
                Loc pos = map.locate('^');
                Direction facing = Direction.UP;

                while (map.contains(pos) && !visited.contains(Pair.of(pos,facing))) {
                    visited.add(Pair.of(pos,facing));
                    Loc next = pos.step(facing);

                    while (map.contains(next) && (map.at(next) == '#' || next.equals(obstruction))) {
                        facing = facing.turnRight();
                        next = pos.step(facing);
                    }
                    pos = next;
                }

                if (map.contains(pos)) {
                    // We didn't exit the map so we must be in a loop
                    // System.out.println("Loop detected:\n" + obstructedMap.overlayPath(visited.stream().map(Pair::getLeft).toList(), 'X'));
                    loopCount++;
                }
            }
        }
    }

    public void resultsPartTwo() {
        System.out.println("Day 6 part 2 result: " + loopCount);
    }
}