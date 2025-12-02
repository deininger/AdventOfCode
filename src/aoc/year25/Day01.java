package aoc.year25;

import aoc.util.Direction;
import aoc.util.PuzzleApp;

import java.util.ArrayList;
import java.util.List;

public class Day01 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 1: Secret Entrance");
        PuzzleApp app = new aoc.year25.Day01();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day01";
    }

    private final List<Rotation> rotations = new ArrayList<>();
    private final Dial dial = new Dial();

    public void parseLine(String line) {
        rotations.add(new Rotation(line));
    }

    public void process() {
        rotations.forEach(dial::turn);
    }

    public void results() {
        System.out.println("The dial is now at " + dial);
    }

    public static class Rotation {
        private final Direction d;
        private final int tickCount;

        public Rotation(String line) {
            this(Direction.withCode(line.substring(0,1)), Integer.parseInt(line.substring(1)));
        }

        public Rotation(Direction d, int tickCount) {
            this.d = d;
            this.tickCount = tickCount;
        }

        public Direction getDirection() {
            return d;
        }

        public int getTickCount() {
            return tickCount;
        }

        public int getSignedTickCount() {
            int signedTickCount = getTickCount();

            if (getDirection() == Direction.LEFT) {
                signedTickCount = -signedTickCount;
            }

            return signedTickCount;
        }

        public String toString() {
            return "Rotation{" + d + "," + tickCount + "}";
        }
    }

    public static class Dial {
        private static final int SIZE = 100;

        private int position;
        private int partOneZeroCounter = 0;
        private int partTwoZeroCounter = 0;

        public Dial() {
            this(50);
        }

        public Dial(int startingPosition) {
            this.position = startingPosition;
        }

        public int partTwoZeroCounter() {
            return partTwoZeroCounter;
        }

        public String toString() {
            return "Dial{position=" + position + ",partOneZeroCounter = " + partOneZeroCounter + ",partTwoZeroCounter = " + partTwoZeroCounter + "}";
        }

        public void turn(Rotation r) {
            int originalPosition = position;
            position = Math.floorMod(position + r.getSignedTickCount(), SIZE);

            partTwoZeroCounter += r.getTickCount() / SIZE;

            // Handle the edge cases:

            if (r.getDirection() == Direction.RIGHT && position < originalPosition && position != 0) {
                partTwoZeroCounter++;
            } else if (r.getDirection() == Direction.LEFT && position > originalPosition && originalPosition != 0) {
                partTwoZeroCounter++;
            }

            if (position == 0) {
                partOneZeroCounter++;
                partTwoZeroCounter++;
            }
        }
    }
}
