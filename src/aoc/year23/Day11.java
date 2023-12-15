package aoc.year23;

import java.util.HashSet;
import java.util.Set;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class Day11 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 11: Cosmic Expansion");
		PuzzleApp app = new Day11();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day11-part1";
	}
	
	private static final char GALAXY = '#';
	// private static final char SPACE = '.';
	private static final int EXPANSION_FACTOR = 1000000;
	
	private Set<Loc> galaxies = new HashSet<>();
	private int lineCount = 0;
	
	public void parseLine(String line) {
		for (int col = 0; col < line.length(); col++) {
			if (line.charAt(col) == GALAXY) {
				galaxies.add(new Loc(lineCount, col));
			}
		}
		lineCount++;
	}
	
	private Loc galaxySize() {
		int x = 0, y = 0;
		
		for (Loc l : galaxies) {
			if (l.x() > x) x = l.x();
			if (l.y() > y) y = l.y();
		}
		
		return new Loc(x, y);
	}
	
//	private String galaxyToString() {
//		StringBuilder sb = new StringBuilder();
//		Loc size = galaxySize();
//		for (int r = 0; r <= size.x(); r++) {
//			for (int c = 0; c <= size.y(); c++) {
//				if (galaxies.contains(new Loc(r, c))) {
//					sb.append(GALAXY);
//				} else {
//					sb.append(SPACE);
//				}
//			}
//			sb.append('\n');
//		}
//		return sb.toString();
//	}
	
	private long totalDistance = 0;

	public void process() {
		// System.out.println(galaxies);

		// Expand the galaxies:
		Loc originalSize = galaxySize();

		for (int r = originalSize.x(); r >= 0; r--) {
			final int x = r;
			if (! galaxies.stream().anyMatch(l -> l.x() == x)) {
				new HashSet<>(galaxies).stream().filter(l -> l.x() > x).forEach(l -> {
					galaxies.remove(l);
					galaxies.add(new Loc(l.x()+EXPANSION_FACTOR-1,l.y()));
				});
				// System.out.println("Expanded " + r + ": " + galaxies);
			}
		}
		
		for (int c = originalSize.y(); c >= 0; c--) {
			final int y = c;
			if (! galaxies.stream().anyMatch(l -> l.y() == y)) {
				new HashSet<>(galaxies).stream().filter(l -> l.y() > y).forEach(l -> {
					galaxies.remove(l);
					galaxies.add(new Loc(l.x(),l.y()+EXPANSION_FACTOR-1));
				});
				// System.out.println("Expanded " + c + ": " + galaxies);
			}
		}
		
		// System.out.println(galaxies);
		
		
		for (Loc a : galaxies) {
			for (Loc b : galaxies) {
				totalDistance += a.manhattanDistance(b);
			}
		}

		totalDistance = totalDistance / 2;
	}
	
	public void results() {
		System.out.println("Part 1: Total distance = " + totalDistance);
		// System.out.println(galaxyToString());
	}
	
}
