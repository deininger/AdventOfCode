package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Day06 extends PuzzleApp {
    public static final char START = '^';
    public static final char OBSTACLE = '#';

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

        Loc pos = map.locate(START);
        Direction facing = Direction.UP;

        while (map.contains(pos)) {
            path.add(pos);
            Loc next = pos.step(facing);
            while (map.contains(next) && map.at(next) == OBSTACLE) { // Turn until we can move forward
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

    private final AtomicInteger loopCount = new AtomicInteger(0);

    public void processPartTwo() {
        // Try placing an obstruction at each point of the path:
        path.remove(map.locate(START)); // Can't place an obstruction at the starting position

        path.parallelStream().forEach(obstruction -> {
            Set<Pair<Loc, Direction>> visited = new HashSet<>();
            Loc pos = map.locate(START);
            Direction facing = Direction.UP;

            while (map.contains(pos) && !visited.contains(Pair.of(pos, facing))) {
                visited.add(Pair.of(pos, facing));
                Loc next = pos.step(facing);

                while (map.contains(next) && (map.at(next) == OBSTACLE || next.equals(obstruction))) {
                    facing = facing.turnRight();
                    next = pos.step(facing);
                }
                pos = next;
            }

            if (map.contains(pos)) {
                // We didn't exit the map so we must be in a loop
                // System.out.println("Loop detected:\n" + obstructedMap.overlayPath(visited.stream().map(Pair::getLeft).toList(), 'X'));
                loopCount.incrementAndGet();
            }
        });
    }

    public void resultsPartTwo() {
        System.out.println("Day 6 part 2 result: " + loopCount);
    }
}