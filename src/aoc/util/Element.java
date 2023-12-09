package aoc.util;

import java.util.EnumMap;
import java.util.Map;

public class Element<T> {
	private T value;
	private Map<Direction,Element<T>> neighbors = new EnumMap<>(Direction.class);
	
	public Element() {	
	}
	
	public Element(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public void addNeighbor(Direction d, Element<T> e) {
		neighbors.put(d,e);
	}
	
	public Element<T> neighbor(Direction d) {
		return neighbors.get(d);
	}

	public String toString() {
		return value.toString();
	}
}
