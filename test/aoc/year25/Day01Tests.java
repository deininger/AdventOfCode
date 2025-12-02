package aoc.year25;

import aoc.util.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day01Tests {
    @Test
    void testSmallRightTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 25));
        assertEquals(0, dial.partTwoZeroCounter());
    }

    @Test
    void testBigRightTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 125));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testHugeRightTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 525));
        assertEquals(5, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallRightTurnWithOverflow() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 75));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testBigRightTurnWithOverflow() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 175));
        assertEquals(2, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallLeftTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 25));
        assertEquals(0, dial.partTwoZeroCounter());
    }

    @Test
    void testBigLeftTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 125));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testHugeLeftTurn() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 525));
        assertEquals(5, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallLeftTurnWithOverflow() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 75));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testBigLeftTurnWithOverflow() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 175));
        assertEquals(2, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallRightTurnStartingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.RIGHT, 25));
        assertEquals(0, dial.partTwoZeroCounter());
    }

    @Test
    void testBigRightTurnStartingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.RIGHT, 125));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallLeftTurnStartingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.LEFT, 25));
        assertEquals(0, dial.partTwoZeroCounter());
    }

    @Test
    void testBigLeftTurnStartingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.LEFT, 125));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallRightTurnEndingAtZero() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 50));
        assertEquals(1, dial.partTwoZeroCounter());
    }

    @Test
    void testBigRightTurnEndingAtZero() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.RIGHT, 150));
        assertEquals(2, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallRightTurnStartingAndEndingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.RIGHT, 100));
        assertEquals(2, dial.partTwoZeroCounter());
    }

    @Test
    void testBigRightTurnStartingAndEndingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.RIGHT, 200));
        assertEquals(3, dial.partTwoZeroCounter());
    }

    @Test
    void testSmallLeftTurnStartingAndEndingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.LEFT, 100));
        assertEquals(2, dial.partTwoZeroCounter());
    }

    @Test
    void testBigLeftTurnStartingAndEndingAtZero() {
        Day01.Dial dial = new Day01.Dial(0);
        dial.turn(new Day01.Rotation(Direction.LEFT, 200));
        assertEquals(3, dial.partTwoZeroCounter());
    }

    @Test
    void testASeriesOfTurns() {
        Day01.Dial dial = new Day01.Dial();
        dial.turn(new Day01.Rotation(Direction.LEFT, 68));
        assertEquals(1, dial.partTwoZeroCounter());
        dial.turn(new Day01.Rotation(Direction.LEFT, 30));
        dial.turn(new Day01.Rotation(Direction.RIGHT, 48));
        assertEquals(2, dial.partTwoZeroCounter());
        dial.turn(new Day01.Rotation(Direction.LEFT, 5));
        dial.turn(new Day01.Rotation(Direction.RIGHT, 60));
        assertEquals(3, dial.partTwoZeroCounter());
        dial.turn(new Day01.Rotation(Direction.LEFT, 55));
        assertEquals(4, dial.partTwoZeroCounter());
        dial.turn(new Day01.Rotation(Direction.LEFT, 1));
        dial.turn(new Day01.Rotation(Direction.LEFT, 99));
        assertEquals(5, dial.partTwoZeroCounter());
        dial.turn(new Day01.Rotation(Direction.RIGHT, 14));
        dial.turn(new Day01.Rotation(Direction.LEFT, 82));
        assertEquals(6, dial.partTwoZeroCounter());
    }
}