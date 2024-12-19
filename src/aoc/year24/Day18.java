package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.MazeSolver;
import aoc.util.PuzzleApp;

import java.util.*;

public class Day18 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 18: RAM Run");
        PuzzleApp app = new aoc.year24.Day18();
        app.run();
    }

    public String filename() {
        return "data/year24/day18";
    }

    private final List<Loc> coordinates = new ArrayList<>();

    public void parseLine(String line) {
        coordinates.add(new Loc(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).toArray()));
    }

    private MazeSolver.Node solutionPartOne = null;

    public void process() {
        // System.out.println("coordinates: " + coordinates);

        CharacterGrid space = new CharacterGrid(71, 71, '.');

        for (int i = 0; i < 1024; i++) {
            Loc l = coordinates.get(i);
            space.set(l, '#');
        }

        // System.out.println(space);

        Loc start = new Loc(0, 0);
        Loc end = new Loc(70, 70);

        solutionPartOne = MazeSolver.solve(start, end, l -> space.contains(l) && space.at(l) != '#');
    }

    public void results() {
        System.out.println("Day 18 part 1 result: " + solutionPartOne.totalDistance() + " steps (path length: " + solutionPartOne.path().size() + ")");
        // System.out.println(space.overlayPath(solutionPartOne.path(), 'O'));
    }

    private Loc solutionPartTwo = null;

    public void processPartTwo() {
        // Just for fun, let's do part 2 without creating a character grid for the maze, just use the collection of barriers

        Loc start = new Loc(0, 0);
        Loc end = new Loc(70, 70);

        Set<Loc> usedCoordinates = new HashSet<>();

        for (int i = 1; i < coordinates.size(); i++) {
            Loc c = coordinates.get(i);
            usedCoordinates.add(c);
            if (MazeSolver.solve(start, end, l -> l.x() >= 0 && l.x() <= 70 && l.y() >= 0 && l.y() <= 70
                    && !usedCoordinates.contains(l)) == null) {
                solutionPartTwo = c;
                break;
            }
        }
    }

    public void resultsPartTwo() {
        System.out.println("Day 18 part 2 result: " + solutionPartTwo + " is the coordinate which prevents reaching the end");
    }
}
