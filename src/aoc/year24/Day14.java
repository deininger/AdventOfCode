package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 14: Restroom Redoubt");
        PuzzleApp app = new aoc.year24.Day14();
        app.run();
    }

    public String filename() {
        return "data/year24/day14";
    }

    private static final String ROBOT_REGEX = "p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)";
    private static final Pattern ROBOT_PATTERN = Pattern.compile(ROBOT_REGEX);

    private final List<Robot> robots = new ArrayList<>();

    public void parseLine(String line) {
        Matcher matcher = ROBOT_PATTERN.matcher(line);
        if (matcher.matches()) {
            robots.add(new Robot(new Loc(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2))),
                    new Loc(Integer.parseInt(matcher.group(3)),Integer.parseInt(matcher.group(4)))));
        }
    }

    private final CharacterGrid floor = new CharacterGrid(103, 101, '.');

    public void process() {
        // System.out.println(robots);
        // System.out.println(floor.overlayCount(robots.stream().map(Robot::position).toList()));
        robots.forEach(r -> { r.step(100); r.wrap(floor.width(), floor.height()); });
        // System.out.println(floor.overlayCount(robots.stream().map(Robot::position).toList()));
    }

    public void results() {
        long[][] quadrantCounts = new long[2][2];

        int mid_x = floor.width() / 2; // 7 -> 3, 101 -> 50
        int mid_y = floor.height() / 2; // 11 -> 5, 103 -> 51

        robots.stream().map(Robot::position).forEach(r -> {
            if (r.x() < mid_x && r.y() < mid_y) quadrantCounts[0][0]++;
            if (r.x() < mid_x && r.y() > mid_y) quadrantCounts[0][1]++;
            if (r.x() > mid_x && r.y() < mid_y) quadrantCounts[1][0]++;
            if (r.x() > mid_x && r.y() > mid_y) quadrantCounts[1][1]++;
        });

        // System.out.println(quadrantCounts[0][0] + " " + quadrantCounts[0][1] + " " + quadrantCounts[1][0] + " " + quadrantCounts[1][1]);

        long result = quadrantCounts[0][0] * quadrantCounts[0][1] * quadrantCounts[1][0] * quadrantCounts[1][1];

        System.out.println("Day 14 part 1 result: " + result);
    }

    private int largestCenterBlock(List<Robot> robots) {
        Loc start =  new Loc(floor.width()/2+1, floor.height()/2+2);
        Set<Loc> locs = robots.stream().map(Robot::position).collect(Collectors.toSet());
        int blockSize = 0;
        if (!locs.contains(start)) return 0;

        Deque<Loc> connectedLocs = new ArrayDeque<>();
        connectedLocs.add(start);

        while (!connectedLocs.isEmpty()) {
            Loc l = connectedLocs.pop();
            if (!locs.contains(l)) continue;
            locs.remove(l);
            blockSize++;
            l.adjacent().forEach(connectedLocs::add);
        }

        return blockSize;
    }

    public void processPartTwo() {
        long stepCounter = 100;

        // Look for a set of robots in the middle line of the grid:
//        while (robots.stream().map(Robot::position).filter(l -> l.x() == 50).filter(l -> l.y() > 20 && l.y() < 80).count() < 20
//        || robots.stream().map(Robot::position).filter(l -> l.x() == 51).filter(l -> l.y() > 20 && l.y() < 80).count() < 20
//        || robots.stream().map(Robot::position).filter(l -> l.x() == 49).filter(l -> l.y() > 20 && l.y() < 80).count() < 20 )
//        {
//            robots.forEach(r -> { r.step(1); r.wrap(floor.width(), floor.height()); });
//            stepCounter++;
//        }

        // Look for an iteration with a large block of connected robots:
//        int b = 0;
//        while ((b = largestCenterBlock(robots)) < 100) {
//            robots.forEach(r -> { r.step(1); r.wrap(floor.width(), floor.height()); });
//            stepCounter++;
//        }
//
//        System.out.println("Iteration " + stepCounter + " blocks = " + b);

        // Look for a point at which all robots occupy their own squares (no overlap):
        while (robots.stream().map(Robot::position).distinct().count() < robots.size())
        {
            robots.forEach(r -> { r.step(1); r.wrap(floor.width(), floor.height()); });
            stepCounter++;
        }

        // System.out.println(floor.overlayCount(robots.stream().map(Robot::position).toList()));

        System.out.println("Day 14 part 2 result: " + stepCounter);
    }

    static class Robot {
        Loc position;
        Loc velocity;

        public Robot(Loc position, Loc velocity) {
            this.position = position;
            this.velocity = velocity;
        }

        public Loc position() {
            return position;
        }

        public Loc velocity() {
            return velocity;
        }

        public void step(int steps) {
            position = new Loc(position.x() + velocity.x() * steps, position.y() + velocity.y() * steps);
        }

        public void wrap(int width, int height) {
            position = new Loc(Math.floorMod(position.x(), width), Math.floorMod(position.y(), height));
        }

        public String toString() {
            return "p=" + position + ", v=" + velocity;
        }
    }
}
