package aoc.year25;

import aoc.util.CharacterGrid;
import aoc.util.Direction;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.*;

public class Day07Alternate extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 7: Laboratories (Alternate solution)");
        PuzzleApp app = new Day07Alternate();
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
    private long timelineCounter;
    private final Queue<Loc> activeBeams = new LinkedList<>();
    private final Map<Loc,Long> beamPaths = new HashMap<>(); // This is my "seen locations" set

    private void track(Loc beam, Long value) {
        if (!beamPaths.containsKey(beam)) {
            activeBeams.add(beam);
            beamPaths.put(beam, value);
        } else {
            // If the location is already in beamPaths, then sum up the timelines
            beamPaths.put(beam, beamPaths.get(beam) + value);
        }
    }

    public void process() {
        Loc start = grid.locate('S');
        activeBeams.add(start);
        beamPaths.put(start, 1L);

        while (!activeBeams.isEmpty()) {
            Loc beam = activeBeams.poll();
            Loc orig = beam;

            beam = beam.move(Direction.DOWN);

            if (!grid.contains(beam)) {
                // We've reached the bottom of the grid, add the timelines to the timeline counter:
                timelineCounter += beamPaths.get(orig);
                continue;
            }

            if (grid.at(beam) == '.') {
                // empty space, beam just moves down, timeline count remains the same
                track(beam, beamPaths.get(orig));
            } else if (grid.at(beam) == '^') {
                splitCounter++;
                // split the beam in two, left and right, each inherits the timelines of the original beam
                // we don't have to worry about encountering another splitter when we split,
                // or exiting the grid when splitting.
                track(beam.move(Direction.LEFT), beamPaths.get(orig));
                track(beam.move(Direction.RIGHT), beamPaths.get(orig));
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

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 result: " + timelineCounter);
    }
}
