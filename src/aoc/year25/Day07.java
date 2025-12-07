package aoc.year25;

import aoc.util.*;

import java.util.*;

public class Day07 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 7: Laboratories");
        PuzzleApp app = new Day07();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day07";
    }

    private final CharacterGrid grid = new CharacterGrid();

    @Override
    public void parseLine(String line) {
        grid.addRow(line);
    }

    private long splitCounter;
    private final Queue<Loc> activeBeams = new LinkedList<>();
    private final Set<Loc> beamPaths = new HashSet<>(); // This is my "seen locations" set

    private void track(Loc beam) {
        if (!beamPaths.contains(beam)) {
            activeBeams.add(beam);
            beamPaths.add(beam);
        }
    }

    public void process() {
        Loc start = grid.locate('S');
        activeBeams.add(start);

        while (!activeBeams.isEmpty()) {
            Loc beam = activeBeams.poll();
            beam = beam.move(Direction.DOWN);

            if (!grid.contains(beam)) continue;

            if (grid.at(beam) == '.') {
                // empty space, beam just moves down
                track(beam);
            } else if (grid.at(beam) == '^') {
                splitCounter++;
                // split the beam in two, left and right
                // we don't have to worry about encountering another splitter when we split,
                // or exiting the grid when splitting.

                track(beam.move(Direction.LEFT));
                track(beam.move(Direction.RIGHT));
            } else {
                System.err.println("Encountered unexpected element in grid: " + grid.at(beam));
            }
        }
    }

    @Override
    public void results() {
        // System.out.println(grid.overlayPath(beamPaths, '|'));
        System.out.println("Part 1 result: " + splitCounter);
    }

    private final Map<Loc,Long> cache = new HashMap<>();

    private long solve(Loc l) {
        if (cache.containsKey(l)) return cache.get(l);

        long solution;

        if (!grid.contains(l)) {
            solution = 1;
        } else if (grid.at(l) == '.' || grid.at(l) == 'S') {
            solution = solve(l.move(Direction.DOWN));
        } else if (grid.at(l) == '^') {
            solution = solve(l.move(Direction.RIGHT)) + solve(l.move(Direction.LEFT));
        } else {
            throw new UnsupportedOperationException("Encountered unexpected element in grid: " + grid.at(l));
        }

        cache.put(l, solution);
        return solution;
    }

    private long timelineCounter;

    @Override
    public void processPartTwo() {
        timelineCounter = solve(grid.locate('S'));
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 result: " + timelineCounter);
    }
}
