package aoc.year24;

import aoc.util.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static aoc.util.Direction.*;

public class Day20 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 20: Race Condition");
        PuzzleApp app = new aoc.year24.Day20();
        app.run();
    }

    public String filename() {
        return "data/year24/day20";
    }

    private final CharacterGrid maze = new CharacterGrid();

    public void parseLine(String line) {
        maze.addRow(line);
    }

    MazeSolver.Node solution;
    private final Map<Long, Set<Pair<Loc,Loc>>> cheats = new HashMap<>();

    /*
     * For part 1 at least, we know that the non-cheat route through the maze touches
     * every open point in the grid, which means that every cheat is a shortcut from one
     * point on our path to another point on our path.
     *
     * Each "cheat" can be located by finding a sequence of ".#." in the maze either
     * horizontally or vertically. The savings for that cheat would be the distance
     * (number of nodes) between the start and end of the cheat, minus one.
     *
     * The maze (mine, at least) doesn't have any opporunities for diagonal cheating.
     *
     * I don't really need to collect all the cheats into a HashMap, I could just increment
     * a counter, but having the map is helpful when debugging.
     */
    public void process() {
        // System.out.println(maze);

        Loc start = maze.locate('S');
        Loc end = maze.locate('E');

        solution = MazeSolver.solveWithBreadthFirstSearch(start, end, l -> maze.contains(l) && maze.at(l) != '#');

        if (solution == null) {
            System.out.println("No solution found!");
        } else {
            List<Loc> path = solution.path();
            // System.out.println("Path: " + path);
            System.out.println("Non-cheat path length: " + solution.path().size());

            for (int r = 1; r < maze.height()-1; r++) {
                for (int c = 1; c < maze.width()-1; c++) {
                    Loc l = new Loc(r,c);
                    if (maze.at(l.step(LEFT)) != '#' && maze.at(l) == '#' && maze.at(l.step(RIGHT)) != '#') {
                        Pair<Loc,Loc> cheat = Pair.of(l.step(LEFT), l.step(RIGHT));
                        long savings = Math.abs(path.indexOf(cheat.getLeft()) - path.indexOf(cheat.getRight())) - 2;
                        cheats.computeIfAbsent(savings, _ -> new HashSet<>()).add(cheat);
                    }
                    if (maze.at(l.step(UP)) != '#' && maze.at(l) == '#' && maze.at(l.step(DOWN)) != '#') {
                        Pair<Loc,Loc> cheat = Pair.of(l.step(UP), l.step(DOWN));
                        long savings = Math.abs(path.indexOf(cheat.getLeft()) - path.indexOf(cheat.getRight())) - 2;
                        cheats.computeIfAbsent(savings, _ -> new HashSet<>()).add(cheat);
                    }
                }
            }

            // System.out.println("Total number of cheats: " + cheats.values().stream().mapToLong(Collection::size).sum());
            // cheats.forEach((k, v) -> System.out.println("There are " + v.size() + " cheats of value " + k + ": " + v));
        }
    }

    public void results() {
        // cheats.forEach((k, v) -> { if (k >= 100) System.out.println("There are " + v.size() + " cheats of value " + k + ": " + v); });
        int result = cheats.entrySet().stream()
                .filter(e -> e.getKey() >= 100)
                .mapToInt(e -> e.getValue().size())
                .sum();

        System.out.println("Day 20 part 1 result: " + result);
    }

    private final ConcurrentMap<Long, Set<Pair<Loc,Loc>>> cheatsPartTwo = new ConcurrentHashMap<>();

    public void processPartTwo() {
        List<Loc> path = solution.path();

        for (int r = 1; r < maze.height()-1; r++) {
            for (int c = 1; c < maze.width()-1; c++) {
                Loc l = new Loc(r,c);
                if (maze.at(l) == '#') continue;

                l.nearby(20).parallelStream()
                        .filter(a -> maze.contains(a) && maze.at(a) != '#')
                        .forEach(a -> {
                            long savings = path.indexOf(a) - path.indexOf(l) - l.manhattanDistance(a);
                            if (savings > 0) {
                                cheatsPartTwo.computeIfAbsent(savings, _ -> new HashSet<>()).add(Pair.of(l, a));
                            }
                        });

            }
        }
    }

    public void resultsPartTwo() {
        int result = cheatsPartTwo.entrySet().stream()
                .filter(e -> e.getKey() >= 100)
                .mapToInt(e -> e.getValue().size())
                .sum();

        System.out.println("Day 20 part 2 result: " + result);

/*
        Map<Long,Integer> cheatCounts = new HashMap<>();

        cheatsPartTwo.forEach((k, v) -> {
                if (k >= 100) cheatCounts.put(k, v.size());
            });

        cheatCounts.keySet().stream().sorted().forEach(k -> System.out.println("There are " + cheatCounts.get(k) + " cheats of value " + k));
*/
    }
}
