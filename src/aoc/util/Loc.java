package aoc.util;

import java.util.*;
import java.util.stream.Stream;

public class Loc {
	public static final Loc ORIGIN = new Loc(0,0);
	
	private int x;
	private int y;
	
	public Loc(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Loc(int[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
	}

	public Loc(Loc other) {
		this.x = other.x;
		this.y = other.y;
	}

	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}

	public Loc setX(int newX) {
		return new Loc(newX, y);
	}
	
	public Loc setY(int newY) {
		return new Loc(x, newY);
	}
	
	public Loc step(Direction d) {
		Loc newLoc = new Loc(this);
		switch (d) {
			case UP: newLoc.y--; break;
			case DOWN: newLoc.y++; break;
			case LEFT: newLoc.x--; break;
			case RIGHT: newLoc.x++; break;
		}
		return newLoc;
	}

	public Loc move(Direction d) {
		Loc newLoc = new Loc(this);
		switch (d) {
			case UP: newLoc.y--; break;
			case DOWN: newLoc.y++; break;
			case LEFT: newLoc.x--; break;
			case RIGHT: newLoc.x++; break;
		}
		return newLoc;
	}

	public Loc move(Direction d, int steps) {
		Loc newLoc = new Loc(this);
		switch (d) {
			case UP: newLoc.y -= steps; break;
			case DOWN: newLoc.y += steps; break;
			case LEFT: newLoc.x -= steps; break;
			case RIGHT: newLoc.x += steps; break;
		}
		return newLoc;

	}

	/**
	 * The distance between two Locs is the larger of the absolute differences of
	 * their Cartesian coordinates.
	 */
	public int distance(Loc l) {
		return Integer.max(Math.abs(l.x - x), Math.abs(l.y - y));
	}
	
	public int manhattanDistance(Loc l) {
		return Math.abs(l.x - x) + Math.abs(l.y - y);
	}

	public Loc difference(Loc l) {
		return new Loc(this.x() - l.x(), this.y() - l.y());
	}

	public Loc sum(Loc l) { return new Loc(this.x() + l.x(), this.y() + l.y()); }

	public Stream<Loc> adjacent() {
		return Stream.of(new Loc(x, y + 1), new Loc(x + 1, y), new Loc(x - 1, y), new Loc(x, y - 1));
	}

	/*
	 * Builds a stream of Locs which are all "distance" away from this Loc,
	 * using the manhattan distance.
	 */
	public Stream<Loc> adjacent(int distance) {
		List<Loc> results = new ArrayList<>();
		
		for(int i = 0; i < distance; i++) {
			results.add(new Loc(x+i, y-distance+i));
			results.add(new Loc(x+distance-i, y+i));
			results.add(new Loc(x-i, y+distance-i));
			results.add(new Loc(x-distance+i, y-i));
		}
		
		return results.stream();
	}

	/*
	 * Returns all Locs within "distance" of this Loc, using manhattan distance.
	 */
	public Set<Loc> nearby(int distance) {
		Set<Loc> results = new HashSet<>();

		for(int i = 0; i < distance; i++) {
			results.add(new Loc(x+i, y-distance+i));
			results.add(new Loc(x+distance-i, y+i));
			results.add(new Loc(x-i, y+distance-i));
			results.add(new Loc(x-distance+i, y-i));
		}

		if (distance > 1) results.addAll(this.nearby(distance-1));
		return results;
	}

	public Stream<Loc> pathTo(Loc end) {
		List<Loc> points = new ArrayList<>(distance(end));
		points.add(this);
		
		if(this.x == end.x) {
			if (this.y < end.y) {
				for (int i = this.y; i < end.y; i++) {
					points.add(new Loc(x,i));
				}
			} else {
				for (int i = this.y; i > end.y; i--) {
					points.add(new Loc(x,i));
				}
			}
		} else if (this.y == end.y) {
			if (this.x < end.x) {
				for (int i = this.x; i < end.x; i++) {
					points.add(new Loc(i,y));
				}
			} else {
				for (int i = this.x; i > end.x; i--) {
					points.add(new Loc(i,y));
				}
			}
		} else {
			throw new UnsupportedOperationException("Cannot create path from " + this + " to " + end + " (only horizontal and vertical paths supported)");
		}

		points.add(end);
		return points.stream();
	}
	
	public Loc adjacentLoc(String direction) {
        return switch (direction) {
            case "N", "U" -> new Loc(x, y - 1);
            case "S", "D" -> new Loc(x, y + 1);
            case "W", "L" -> new Loc(x - 1, y);
            case "E", "R" -> new Loc(x + 1, y);
            default -> throw new IllegalArgumentException("Unsupported direction '" + direction + "'");
        };
	}

	/**
	 * Tests to see if this is within the rectangle bounded by "corner" and (0,0).
	 */
	public boolean within(Loc corner) {
		return this.within(ORIGIN, corner);
	}

	/**
	 * Tests to see if this is within the rectangle bounded by the upperLeftCorner and lowerRightCorner.
	 */
	public boolean within(Loc upperLeftCorner, Loc lowerRightCorner) {
		return (this.x >= upperLeftCorner.x && this.y >= upperLeftCorner.y && this.x < lowerRightCorner.x && this.y < lowerRightCorner.y);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Loc other = (Loc) obj;
		return x == other.x && y == other.y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
}

