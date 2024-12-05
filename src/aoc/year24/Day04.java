package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.PuzzleApp;

public class Day04 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 4: Ceres Search");
        PuzzleApp app = new aoc.year24.Day04();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year24/day04";
    }

    private final CharacterGrid data = new CharacterGrid();

    @Override
    public void parseLine(String line) {
        data.addRow(line);
    }

    private int xmasCount = 0;
    private int masCount = 0;

    @Override
    public void process() {
        // System.out.println(data);

        for (int row = 0; row < data.height(); row++) {
            for (int col = 0; col < data.width(); col++) {
                if (data.at(row, col) == 'X') {
                    xmasCount += countXmas(row, col);
                } else if (data.at(row, col) == 'A') {
                    masCount += isMasX(row, col);
                }
            }
        }
    }

    private int countXmas(int row, int col) {
        int count = 0;
        // right
        if (data.safeAt(row, col+1) == 'M' && data.safeAt(row, col+2) == 'A' && data.safeAt(row, col+3) == 'S') count++;
        // left
        if (data.safeAt(row, col-1) == 'M' && data.safeAt(row, col-2) == 'A' && data.safeAt(row, col-3) == 'S') count++;
        // down
        if (data.safeAt(row+1, col) == 'M' && data.safeAt(row+2, col) == 'A' && data.safeAt(row+3, col) == 'S') count++;
        // up
        if (data.safeAt(row-1, col) == 'M' && data.safeAt(row-2, col) == 'A' && data.safeAt(row-3, col) == 'S') count++;
        // right-down
        if (data.safeAt(row+1, col+1) == 'M' && data.safeAt(row+2, col+2) == 'A' && data.safeAt(row+3, col+3) == 'S') count++;
        // right-up
        if (data.safeAt(row-1, col+1) == 'M' && data.safeAt(row-2, col+2) == 'A' && data.safeAt(row-3, col+3) == 'S') count++;
        // left-down
        if (data.safeAt(row+1, col-1) == 'M' && data.safeAt(row+2, col-2) == 'A' && data.safeAt(row+3, col-3) == 'S') count++;
        // left-up
        if (data.safeAt(row-1, col-1) == 'M' && data.safeAt(row-2, col-2) == 'A' && data.safeAt(row-3, col-3) == 'S') count++;
        return count;
    }

    private int isMasX(int row, int col) {
        // M M
        //  A
        // S S
        if (data.safeAt(row-1, col-1) == 'M' && data.safeAt(row-1, col+1) == 'M'
                && data.safeAt(row+1, col-1) == 'S' && data.safeAt(row+1, col+1) == 'S') return 1;

        // M S
        //  A
        // M S
        if (data.safeAt(row-1, col-1) == 'M' && data.safeAt(row-1, col+1) == 'S'
                 && data.safeAt(row+1, col-1) == 'M' && data.safeAt(row+1, col+1) == 'S') return 1;

        // S S
        //  A
        // M M
        if (data.safeAt(row-1, col-1) == 'S' && data.safeAt(row-1, col+1) == 'S'
                && data.safeAt(row+1, col-1) == 'M' && data.safeAt(row+1, col+1) == 'M') return 1;

        // S M
        //  A
        // S M
        if (data.safeAt(row-1, col-1) == 'S' && data.safeAt(row-1, col+1) == 'M'
                && data.safeAt(row+1, col-1) == 'S' && data.safeAt(row+1, col+1) == 'M') return 1;

        return 0;
    }

    @Override
    public void results() {
        System.out.println("Day 4 part 1 result: " + xmasCount);
    }

    public void resultsPartTwo() {
        System.out.println("Day 4 part 2 result: " + masCount);
    }
}
