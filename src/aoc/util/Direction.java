package aoc.util;

import java.util.HashMap;
import java.util.Map;

public enum Direction {
	RIGHT(0), DOWN(1), LEFT(2), UP(3);

	private static final Map<Integer, Direction> BY_INTVALUE = new HashMap<>();

	static {
		for (Direction d : values()) {
			BY_INTVALUE.put(d.intValue(), d);
		}
	}

	public static Direction withIntValue(int intValue) {
		return BY_INTVALUE.get(intValue);
	}

	public static Direction withCode(String code) {
		return switch(code) {
			case "R": yield Direction.RIGHT;
			case "L": yield Direction.LEFT;
			case "U": yield Direction.UP;
			case "D": yield Direction.DOWN;
			default: throw new IllegalArgumentException("Unknown direction code: " + code);
		};
	}

	private int intValue;

	private Direction(int intValue) {
		this.intValue = intValue;
	}

	public int intValue() {
		return intValue;
	}
	
	public Direction turnRight() {
		return Direction.withIntValue((this.intValue + 1) % 4);
	}
	
	public Direction turnAround() {
		return Direction.withIntValue((this.intValue + 2) % 4);
	}		

	public Direction turnLeft() {
		return Direction.withIntValue((this.intValue + 3) % 4);
	}		
}
