package aoc.year25;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.HashSet;
import java.util.Set;

public class Day04 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 4: Printing Department");
        PuzzleApp app = new Day04();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day04";
    }

    private CharacterGrid grid = new CharacterGrid();

    @Override
    public void parseLine(String line) {
        grid.addRow(line);
    }

    private Set<Loc> findAccessibleRolls(CharacterGrid grid) {
        Set<Loc> accessibleRolls = new HashSet<>();

        for (int row = 0; row < grid.height(); row++) {
            for (int col = 0; col < grid.width(); col++) {
                Loc loc = new Loc(col, row);
                if (grid.at(loc) == '@') {
                    if (loc.adjacentWithDiagonals().filter(grid::contains).filter(l -> grid.at(l) == '@').count() < 4) {
                        accessibleRolls.add(loc);
                    }
                }
            }
        }

        return accessibleRolls;
    }

    private Set<Loc> removedRolls = new HashSet<>();

    @Override
    public void process() {
        removedRolls = findAccessibleRolls(grid);
     }
    
    @Override
    public void results() {
        // System.out.println(grid.overlayPath(removedRolls, 'x'));
        System.out.println("Part 1: " + removedRolls.size() + " rolls");
    }

    Set<Loc> allRemovedRolls = new HashSet<>();

    @Override
    public void processPartTwo() {
        while (!removedRolls.isEmpty()) {
            grid = grid.overlayPath(removedRolls, ' ');
            allRemovedRolls.addAll(removedRolls);
            removedRolls = findAccessibleRolls(grid);
        }
    }

    @Override
    public void resultsPartTwo() {
        // System.out.println(grid);
        System.out.println("Part 2: " + allRemovedRolls.size() + " rolls");
    }
}
