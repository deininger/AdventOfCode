package aoc.year23;

import java.util.ArrayList;
import java.util.List;

import aoc.util.PuzzleApp;

public class Day13 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 13: Point of Incidence");
		PuzzleApp app = new Day13();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day13-part1";
	}
	
	private List<Pattern> patterns = new ArrayList<>();
	private Pattern currentPattern;
	
	public void parseLine(String line) {
		if (currentPattern == null || line.isEmpty()) {
			currentPattern = new Pattern();
			patterns.add(currentPattern);
		}
		
		if (!line.isEmpty()) {
			currentPattern.add(line);
		}
	}
	
	
	private int[] findReflection(Pattern p, int[] ignore) {
		// System.out.println(p);
		
		int v = p.verticalReflectionPoint(ignore[0]);
		// System.out.println("Vertical Reflection Point = " + v);

		int h = p.horizontalReflectionPoint(ignore[1]);
		// System.out.println("Horizontal Reflection Point = " + h);

		// if (v > 0 && h > 0) System.out.println("--> Pattern has both!");
				
		// System.out.println();

		return new int[] {v,h};
	}
	
	int total = 0;
	int smudgeTotal = 0;

	public void process() {
		for(Pattern p : patterns) {
			int[] clearReflections = findReflection(p, new int[] {0,0});

			if (clearReflections[0] > 0 || clearReflections[1] > 0) {
				System.out.println(p);
				System.out.println("Recording clear reflection at " + clearReflections[0] + " " + clearReflections[1]);
				System.out.println("----");
			}

			total += clearReflections[0] + 100 * clearReflections[1];
			
			// Now let's do it all again with smudges, for part 2:
			processWithSmudges(p, clearReflections);
		}
	}
	
	public void processWithSmudges(Pattern p, int[] clearReflections) {
		for (int i = 0; i < p.height(); i++) {
			for (int j = 0; j < p.width(); j++) {
				p.smudge(i,j);
				int [] smudgeReflections = findReflection(p, clearReflections);
				
				// Don't count the same reflection that we had without the smudge:
				// if (smudgeReflections[0] == clearReflections[0]) smudgeReflections[0] = 0;
				// if (smudgeReflections[1] == clearReflections[1]) smudgeReflections[1] = 0;
				
				if (smudgeReflections[0] > 0 || smudgeReflections[1] > 0) {
					System.out.println(p);
					System.out.println("Recording smudge reflection at " + smudgeReflections[0] + " " + smudgeReflections[1]);
					System.out.println("----");
				}
				
				smudgeTotal += smudgeReflections[0] + 100 * smudgeReflections[1];
				p.smudge(i,j); // to undo the smudge
				
				if (smudgeReflections[0] > 0 || smudgeReflections[1] > 0) {
					return; // Once we've found the reflection, stop looking, or we'll find it again and again!
				}
			}
		}
	}
	
	public void results() {
		System.out.println("Part 1: total = " + total);
		System.out.println("Part 2: smudge total = " + smudgeTotal);
	}
	
	class Pattern {
		private List<String> lines = new ArrayList<>();
		
		public void add(String line) {
			lines.add(line);
		}

		public void smudge(int r, int c) {
			String line = lines.remove(r);
			char x = line.charAt(c);
			line = line.substring(0,c) + (x == '#' ? '.' : '#') + line.substring(c+1);
			lines.add(r, line);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			lines.forEach(l -> sb.append(l).append('\n'));
			return sb.toString();
		}

		public int height() {
			return lines.size();
		}

		public int width() {
			return lines.get(0).length();
		}

		public String column(int c) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < height(); i++) {
				sb.append(lines.get(i).charAt(c));
			}
			return sb.toString();
		}

		public String row(int r) {
			return lines.get(r);
		}
		
		public int verticalReflectionPoint(int ignore) {
			for (int i = 1; i < width(); i++) {
				if (i == ignore) continue;
				// System.out.println("Testing column " + i);
				boolean match = true;
				
				for (int j = 1; match && i - j >= 0 && i + j - 1 < width(); j++) {
					// System.out.println("Comparing column " + (i - j) + " to column " + (i + j - 1));
					// System.out.println(column(i-j));
					// System.out.println(column(i+j-1));
					if (!column(i - j).equals(column(i + j - 1))) match = false;
				}
				
				if (match) {
					return i;
				}
			}
			
			return 0; // No vertical reflection point found
		}
		
		public int horizontalReflectionPoint(int ignore) {
			for (int i = 1; i < height(); i++) {
				if (i == ignore) continue;
				// System.out.println("Testing row " + i);
				boolean match = true;
				
				for (int j = 1; match && i - j >= 0 && i + j - 1 < height(); j++) {
					// System.out.println("Comparing row " + (i - j) + " to row " + (i + j - 1));
					if (!row(i - j).equals(row(i + j - 1))) match = false;
				}
				
				if (match) {
					return i;
				}
			}
			
			return 0; // No horizontal reflection point found
		}
	}
}
