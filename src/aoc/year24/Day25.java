package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day25 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 25: Code Chronicle");
        PuzzleApp app = new Day25();
        app.run();
    }

    public String filename() {
        return "data/year24/day25";
    }

    Set<KeyLock> keysAndLocks = new HashSet<>();
    private KeyLock currentKeyLock;

    public void parseLine(String line) {
        if (currentKeyLock == null) {
            currentKeyLock = new KeyLock();
            keysAndLocks.add(currentKeyLock);
        }
        if (line.isEmpty()) {
            currentKeyLock = null;
        } else {
            currentKeyLock.addLine(line);
        }
    }

    private int fitCount = 0;

    public void process() {
        // keysAndLocks.forEach(System.out::println);
        // keysAndLocks.forEach(kl -> System.out.println(kl.heights()));

        keysAndLocks.stream().filter(KeyLock::isKey)
                .forEach(k -> keysAndLocks.stream().filter(KeyLock::isLock).forEach(l -> {
            if (k.isFit(l)) {
                // System.out.println("Key " + k.heights() + " fits lock " + l.heights());
                fitCount++;
            }
        }));
    }

    public void results() {
        System.out.println("Day 25 part 1 result: " + fitCount);
    }

    static class KeyLock {
        List<String> lines = new ArrayList<>();
        int height = 0;
        int width = 0;

        public KeyLock() {
        }

        public void addLine(String line) {
            lines.add(line);
            height++;
            if (line.length() > width) width = line.length();
        }

        public boolean isLock() {
            return lines.getFirst().equals("#####");
        }

        public boolean isKey() {
            return lines.getLast().equals("#####");
        }

        /**
         * Determines if the current KeyLock object "fits" with another given KeyLock object.
         * A KeyLock object is considered to fit with another if one is a key and the other is a lock,
         * they have the same width, and the sum of their heights for any given column does not
         * exceed the maximum allowable height.
         *
         * @param other the KeyLock object to test for compatibility with the current object
         * @return true if the current KeyLock object fits with the given KeyLock object, false otherwise
         */
        public boolean isFit(KeyLock other) {
            if ((this.isKey() && other.isKey()) || (this.isLock() && other.isLock())) return false;
            if (this.width != other.width) return false;

            List<Integer> thisHeights = this.heights();
            List<Integer> otherHeights = other.heights();

            for (int c = 0; c < width; c++) {
                if (thisHeights.get(c) + otherHeights.get(c) > height - 2) return false;
            }

            return true;
        }

        /**
         * Calculates the "heights" for each column of the structure based on its current state
         * (lock or key). The height of a column is determined by the count of consecutive `#`
         * characters starting from either the first row (if it's a lock) or the bottom row
         * (if it's a key).
         *
         * @return a list of integers representing the height of each column in the structure.
         */
        public List<Integer> heights() {
            List<Integer> heights = new ArrayList<>(width);

            for (int c = 0; c < width; c++) {
                heights.add(0);

                if (isLock()) {
                    for (int r = 1; r < height; r++) {
                        if (lines.get(r).charAt(c) == '#') {
                            heights.set(c, heights.get(c) + 1);
                        } else {
                            break;
                        }
                    }
                } else {
                        for (int r = height - 2; r >= 0; r--) {
                            if (lines.get(r).charAt(c) == '#') {
                                heights.set(c, heights.get(c) + 1);
                            } else {
                                break;
                            }
                        }
                }
            }

            return heights;
        }

        public String toString() {
            return String.join("\n", lines) + "\n";
        }
    }
}
