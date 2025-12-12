package aoc.year25;

import aoc.util.PuzzleApp;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day12 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 12: Christmas Tree Farm");
        PuzzleApp app = new Day12();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day12-small";
    }

    private int shapeCounter;
    private final List<String> currentShapeData = new ArrayList<>();
    private final Map<Integer,Shape> shapes = new HashMap<>();
    private final Set<Region> regions = new HashSet<>();
    private final Map<Region,List<Integer>> presentCounts = new HashMap<>();

    @Override
    public void parseLine(String line) {
        if (line.isBlank()) {
            if (!currentShapeData.isEmpty()) {
                Shape s = Shape.fromData(currentShapeData);
                shapes.put(shapeCounter, s);
                currentShapeData.clear();
            }
        } else if (line.endsWith(":")) {
            shapeCounter = Integer.parseInt(line.replace(":", ""));
        } else if (line.contains(":")) {
            String[] parts = line.split(":");
            String[] dimensions = parts[0].trim().split("x");
            String[] presentsStr = parts[1].trim().split(" ");
            List<Integer> presents = Arrays.stream(presentsStr).mapToInt(Integer::parseInt).boxed().toList();
            Region r = new Region(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
            regions.add(r);
            presentCounts.put(r, presents);
        } else {
            currentShapeData.add(line);
        }
    }

    private List<Shape> getAllShapesForRegion(Region r) {
        List<Shape> allShapesInRegion = new LinkedList<>();
        for (int present = 0; present < shapes.size(); present++) {
            for (int i = 0; i < presentCounts.get(r).get(present); i++) {
                allShapesInRegion.add(shapes.get(present));
            }
        }
        return allShapesInRegion;
    }

    private boolean solve(boolean[][] grid, List<Shape> shapesToPlace) {
        if (shapesToPlace.isEmpty()) {
            return true; // Success! All shapes placed.
        }

        // 1. Find the first empty cell (r, c)
        int r = -1, c = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (!grid[i][j]) {
                    r = i;
                    c = j;
                    break;
                }
            }
            if (r != -1) break;
        }

        if (r == -1) {
            // Grid is full, but we still have shapes to place.
            return false;
        }

        // 2. Iterate through the list of shapes to place
        for (int i = 0; i < shapesToPlace.size(); i++) {
            Shape shapeToTry = shapesToPlace.get(i);

            // 3. Try each unique orientation of the shape
            for (int mask : shapeToTry.getVariationMasks()) {
                // 4. Try to place the shape so it covers the empty cell (r, c)
                for (int sr = 0; sr < 3; sr++) {
                    for (int sc = 0; sc < 3; sc++) {
                        // If the current part of the shape (sr, sc) is solid
                        if ((mask & (1 << (sr * 3 + sc))) != 0) {
                            // Calculate the top-left position of the shape
                            int placeR = r - sr;
                            int placeC = c - sc;

                            if (fits(grid, mask, placeR, placeC)) {
                                place(grid, mask, placeR, placeC, true);
                                Shape placedShape = shapesToPlace.remove(i);

                                if (solve(grid, shapesToPlace)) {
                                    return true; // Solution found!
                                }

                                // Backtrack
                                shapesToPlace.add(i, placedShape);
                                place(grid, mask, placeR, placeC, false);
                            }
                        }
                    }
                }
            }
        }

        return false; // No solution found from this path
    }

    private boolean fits(boolean[][] grid, int shapeMask, int r, int c) {
        if (r < 0 || c < 0) return false; // Ensure placement is within bounds from top-left

        for (int sr = 0; sr < 3; sr++) {
            for (int sc = 0; sc < 3; sc++) {
                if ((shapeMask & (1 << (sr * 3 + sc))) != 0) {
                    int gr = r + sr;
                    int gc = c + sc;

                    if (gr >= grid.length || gc >= grid[0].length || grid[gr][gc]) {
                        return false; // Out of bounds or collides with another shape
                    }
                }
            }
        }
        return true;
    }

    private void place(boolean[][] grid, int shapeMask, int r, int c, boolean value) {
        for (int sr = 0; sr < 3; sr++) {
            for (int sc = 0; sc < 3; sc++) {
                if ((shapeMask & (1 << (sr * 3 + sc))) != 0) {
                    grid[r + sr][c + sc] = value;
                }
            }
        }
    }
    
    private String visualize(boolean[][] grid) {
        StringBuilder sb = new StringBuilder("\n");
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                sb.append(grid[r][c] ? "#" : ".");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private final AtomicInteger successfulRegions = new AtomicInteger();

    public void process() {
        for (Region r : regions) {
            List<Shape> allShapes = getAllShapesForRegion(r);
            boolean[][] grid = new boolean[r.height()][r.width()];

            System.out.println("Attempting to place " + allShapes.size() + " shapes into region " + r);
            if (solve(grid, allShapes)) {
                successfulRegions.incrementAndGet();
                System.out.println("Success: " + visualize(grid));
            }
        }
    }

    @Override
    public void results() {
        System.out.println("Part one results: " + successfulRegions);
    }

    @Override
    public void processPartTwo() { }

    @Override
    public void resultsPartTwo() { }

    /*
     * A representation of a 3 by 3 "present" shape,
     * which can be rotated and/or flipped in order to be placed within a Region.
     */
    static class Shape {
        private final int mask;
        private final Set<Integer> variationMasks = new HashSet<>();

        public static Shape fromData(List<String> data) {
            int mask = 0;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (data.get(r).charAt(c) == '#') {
                        mask |= (1 << (r * 3 + c));
                    }
                }
            }
            return new Shape(mask);
        }

        private Shape(int mask) {
            this.mask = mask;
        }

        public Set<Integer> getVariationMasks() {
            if (variationMasks.isEmpty()) generateVariations();
            return variationMasks;
        }

        private int rotate(int currentMask) {
            int newMask = 0;
            // This mapping performs a 90-degree clockwise rotation on the 3x3 grid bits.
            // 0->2, 1->5, 2->8, 3->1, 4->4, 5->7, 6->0, 7->3, 8->6
            int[] rotationMap = {6, 3, 0, 7, 4, 1, 8, 5, 2};
            for (int i = 0; i < 9; i++) {
                if ((currentMask & (1 << i)) != 0) newMask |= (1 << rotationMap[i]);
            }
            return newMask;
        }

        private int flip(int currentMask) {
            int newMask = 0;
            // This mapping performs a horizontal flip on the 3x3 grid bits.
            // 0->2, 1->1, 2->0, 3->5, 4->4, 5->3, 6->8, 7->7, 8->6
            int[] flipMap = {2, 1, 0, 5, 4, 3, 8, 7, 6};
            for (int i = 0; i < 9; i++) {
                if ((currentMask & (1 << i)) != 0) newMask |= (1 << flipMap[i]);
            }
            return newMask;
        }

        private void generateVariations() {
            int current = this.mask;
            for (int i = 0; i < 4; i++) {
                variationMasks.add(current);
                variationMasks.add(flip(current));
                current = rotate(current);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return this.mask == ((Shape) o).mask;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(mask);
        }

        public String visualize() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");

            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int bitIndex = r * 3 + c;
                    if ((1 << bitIndex & mask) > 0) sb.append("#");
                    else sb.append(".");
                }
                sb.append("\n");
            }

            return sb.toString();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toBinaryString(mask)).append(" (").append(variationMasks.size()).append(" variations)\n");
            sb.append(this.visualize());
            sb.append("\n");
            sb.append("Variations:\n");
            Integer[] variationsArray = variationMasks.toArray(new Integer[8]);

            for (int r = 0; r < 3; r++) {
                for (int v = 0; v < variationMasks.size(); v++) {
                    for (int c = 0; c < 3; c++) {
                        int bitIndex = r * 3 + c;
                        if ((1 << bitIndex & variationsArray[v]) > 0) sb.append("#");
                        else sb.append(".");
                    }
                    sb.append("  ");
                }
                sb.append("\n");
            }

            return sb.toString();
        }
    }

    static class Region {
        private final int width;
        private final int height;

        public Region(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int width() { return width; }
        public int height() { return height; }

        @Override
        public String toString() {
            return width + "x" + height;
        }
    }
}
