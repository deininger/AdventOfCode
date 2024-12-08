package aoc.year24;

import aoc.util.CharacterGrid;
import aoc.util.Loc;
import aoc.util.PuzzleApp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day08 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 8: Resonant Collinearity");
        PuzzleApp app = new aoc.year24.Day08();
        app.run();
    }

    public String filename() {
        return "data/year24/day08";
    }

    CharacterGrid map = new CharacterGrid();
    Map<Character,Set<Loc>> nodeMap = new HashMap<>();
    Set<Loc> antinodes = new HashSet<>();

    public void parseLine(String line) {
        map.addRow(line);
    }

    public void process() {
        // System.out.println(map);

        for (int r = 0; r < map.height(); r++) {
            for (int c = 0; c < map.width(); c++) {
                char x = map.at(r, c);
                if (x == '.') continue;
                nodeMap.putIfAbsent(x, new HashSet<>());
                nodeMap.get(x).add(new Loc(c, r));
            }
        }

        // System.out.println("Nodes: " + nodeMap);

        for (Set<Loc> nodes: nodeMap.values()) {
            for (Loc node: nodes) {
                for (Loc otherNode: nodes) {
                    if (node.equals(otherNode)) continue;
                    Loc antinode = node.sum(node.difference(otherNode));
                    if (map.contains(antinode)) {
                        // System.out.println("The antinode of " + node + " and " + otherNode + " is " + antinode);
                        antinodes.add(antinode);
                    }
                }
            }
        }

        // System.out.println(map.overlayPath(antinodes, '#'));
    }

    public void results() {
        System.out.println("Day 8 part 1 result: " + antinodes.size());
    }

    /*
     * GCD by Euclid's Algorithm
     */
    private int gcd(int n1, int n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }

    Set<Loc> partTwoAntinodes = new HashSet<>();

    public void processPartTwo() {
        for (Set<Loc> nodes: nodeMap.values()) {
            for (Loc node: nodes) {
                for (Loc otherNode: nodes) {
                    if (node.equals(otherNode)) continue;
                    Loc delta = node.difference(otherNode);
                    int gcd = gcd(Math.abs(delta.x()), Math.abs(delta.y()));
                    Loc adjustedDelta = new Loc(delta.x() / gcd, delta.y() / gcd);
                    Loc l = new Loc(node);
                    while (map.contains(l)) {
                        partTwoAntinodes.add(l);
                        l = l.sum(adjustedDelta);
                    }
                    adjustedDelta = new Loc(-adjustedDelta.x(),-adjustedDelta.y());
                    l = new Loc(node);
                    while (map.contains(l)) {
                        partTwoAntinodes.add(l);
                        l = l.sum(adjustedDelta);
                    }
                }
            }
        }

        // System.out.println(map.overlayPath(partTwoAntinodes, '#'));
    }

    public void resultsPartTwo() {
        System.out.println("Day 8 part 2 result: " + partTwoAntinodes.size());
    }
}
