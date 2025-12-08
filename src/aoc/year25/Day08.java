package aoc.year25;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Day08 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 8: Playground");
        PuzzleApp app = new Day08();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day08";
    }

    private final Set<Point> junctionBoxes = new HashSet<>();

    @Override
    public void parseLine(String line) {
        String[] coordinates = line.split(",");
        junctionBoxes.add(new Point(Long.parseLong(coordinates[0]), Long.parseLong(coordinates[1]), Long.parseLong(coordinates[2])));
    }

    private final Map<Pair<Point,Point>,Double> allDistances = new HashMap<>();
    private final Map<Point,Circuit> circuits = new HashMap<>();

    private void calculateAllDistances() {
        for (Point p1 : junctionBoxes) {
            for (Point p2 : junctionBoxes) {
                if (!p1.equals(p2) && !allDistances.containsKey(Pair.of(p2, p1))) {
                    allDistances.put(Pair.of(p1, p2), p1.distance(p2));
                }
            }
        }
    }

    private void connect(Point p1, Point p2) {
        if (!circuits.containsKey(p1) && !circuits.containsKey(p2)) {
            // neither point is part of a circuit, create a new circuit
            // System.out.println("Creating a new circuit with points " + p1 + " and " + p2);
            Circuit c = new Circuit(p1, p2);
            circuits.put(p1, c);
            circuits.put(p2, c);
        } else if (circuits.containsKey(p1) && !circuits.containsKey(p2)) {
            // p1 is part of a circuit, add p2 to that circuit
            // System.out.println("Adding`" + p2 + "` to circuit " + circuits.get(p1));
            circuits.get(p1).add(p2);
            circuits.put(p2, circuits.get(p1));
        } else if (!circuits.containsKey(p1) && circuits.containsKey(p2)) {
            // p2 is part of a circuit, add p1 to that circuit
            // System.out.println("Adding`" + p1 + "` to circuit " + circuits.get(p2));
            circuits.get(p2).add(p1);
            circuits.put(p1, circuits.get(p2));
        } else if (circuits.containsKey(p1) && circuits.containsKey(p2) && !circuits.get(p1).equals(circuits.get(p2))) {
            // both points are part of a circuit, merge the circuits
            // System.out.println("Points " + p1 + " and " + p2 + " are already in circuits " + circuits.get(p1) + " and " + circuits.get(p2) + ", merging...");
            Circuit c1 = circuits.get(p1);
            Circuit c2 = circuits.get(p2);
            c1.add(c2);
            // switch all references to p2 in circuits to p1
            circuits.entrySet().stream()
                    .filter(ee -> ee.getValue().equals(c2))
                    .forEach(ee -> circuits.put(ee.getKey(), c1));
            // System.out.println("Merged into circuit " + c1);
        } else {
            // p1 and p2 are already part of the same circuit, do nothing
            // System.out.println("Points " + p1 + " and " + p2 + " are both already in circuit " + circuits.get(p1) + ", skipping...");
        }
    }
    
    public void process() {
        calculateAllDistances();

        allDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(1000)
                .forEach(e -> {
                    Point p1 = e.getKey().getLeft();
                    Point p2 = e.getKey().getRight();
                    connect(p1, p2);
                });
    }

    @Override
    public void results() {
        // System.out.println("Junction boxes: " + junctionBoxes);

        Set<Circuit> uniqueCircuits = new HashSet<>(circuits.values());

        // System.out.println("circuits: " + uniqueCircuits);
        // System.out.println("circuit counts: " + uniqueCircuits.stream().map(Circuit::size).toList());

        int product = uniqueCircuits.stream()
                .sorted(Comparator.comparingInt(Circuit::size).reversed())
                .limit(3)
                .mapToInt(Circuit::size)
                .reduce(1, (a, b) -> a * b);

        System.out.println("Product of 3 largest circuits: " + product);
    }


    private long singularity;

    @Override
    public void processPartTwo() {
        // Reset circuits for part two processing
        circuits.clear();

        List<Map.Entry<Pair<Point, Point>, Double>> sortedDistances = allDistances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        for (Map.Entry<Pair<Point, Point>, Double> e : sortedDistances) {
            Point p1 = e.getKey().getLeft();
            Point p2 = e.getKey().getRight();
            connect(p1, p2);

            // Check if p1 is in a circuit, and if that circuit is now complete
            if (circuits.containsKey(p1) && circuits.get(p1).size() == junctionBoxes.size()) {
                System.out.println("Achieved singularity with points " + p1 + " and " + p2 + " at distance " + e.getValue());
                singularity = e.getKey().getLeft().x() * e.getKey().getRight().x();
                break;
            }
        }
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 result: " + singularity);
    }

     static class Point {
        private final long x;
        private final long y;
        private final long z;

        public Point(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public long x() {
            return x;
        }

        public double distance(Point p) {
            return Math.sqrt(Math.pow(x - p.x,2) + Math.pow(y - p.y,2) + Math.pow(z - p.z,2));
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y && z == point.z;
        }

        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }
    }

    static class Circuit {
        private final Set<Point> points = new HashSet<>();

        public Circuit(Point... p) {
            this.add(p);
        }

        public void add(Point... p) {
            points.addAll(Arrays.asList(p));
        }

        public void add(Circuit c) {
            points.addAll(c.points);
        }

        public boolean contains(Point p) {
            return points.contains(p);
        }

        public int size() {
            return points.size();
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Circuit circuit = (Circuit) o;
            return points.equals(circuit.points);
        }

        public int hashCode() {
            return Objects.hash(points);
        }

        public String toString() {
            return points.toString();
        }
    }
}
