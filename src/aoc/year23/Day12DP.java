package aoc.year23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class Day12DP extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 12: Hot Springs");
		PuzzleApp app = new Day12DP();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day12-part1";
	}
	
	List<ConditionRecord> conditionRecords = new ArrayList<>();
	
	public void parseLine(String line) {
		String[] parts = line.split(" ");
		String springs = parts[0];
		List<Integer> counts = Stream.of(parts[1].split(",")).map(Integer::valueOf).collect(Collectors.toCollection(ArrayList::new));;
		ConditionRecord cr = new ConditionRecord(springs, counts);
		conditionRecords.add(cr);
		// System.out.println("Parsed line into: " + cr);
	}
	
	private Map<ConditionRecord,Long> analysisResults = new HashMap<>();
	
	private long analyze(ConditionRecord cr) {
		if (analysisResults.containsKey(cr)) {
			return analysisResults.get(cr);
		}
		
		// System.out.println("Analyzing " + cr);

		long solutionCount = 0;
		
		ConditionRecord stepCR = new ConditionRecord(cr); // Avoid altering our input CR!
		int result = stepCR.step();
		
		if (result == -1) {
			ConditionRecord rockCR = new ConditionRecord(stepCR).pushSpring("#");
			ConditionRecord spaceCR = new ConditionRecord(stepCR).pushSpring(".");
			// System.out.println("  Branching into " + rockCR + " and " + spaceCR);
			solutionCount = analyze(rockCR) + analyze(spaceCR);
		} else {
			solutionCount += result;
		}
		
		analysisResults.put(cr, solutionCount);
		return solutionCount;
	}
	
	private long totalSolutions = 0;
		
	public void process() {
		for (int i = 0; i < conditionRecords.size(); i++) {
			ConditionRecord cr = conditionRecords.get(i);
			System.out.println("Starting " + cr);
			long result = analyze(new ConditionRecord(cr));
			System.out.println("Completed solution count: " + result + " (memo size " + analysisResults.size() + ")");
			totalSolutions += result;
		}
	}
	
	public void results() {
		System.out.println("Total solution count = " + totalSolutions);
	}
	
	class ConditionRecord {
		private static final int REPLICANTS = 5;
		
		private String springs;
		private List<Integer> counts;
		private boolean consuming = false;
		
		public ConditionRecord(String springs, List<Integer> counts) {
			this.springs = springs;
			this.counts = new ArrayList<>(counts);

			for (int i = 1; i < REPLICANTS; i++) {
				this.springs = this.springs + "?" + springs;
				this.counts.addAll(counts);
			}
		}

		public ConditionRecord(ConditionRecord other) {
			this.springs = other.springs;
			this.counts = new ArrayList<>(other.counts);
			this.consuming = other.consuming;
		}
		
		public ConditionRecord pushSpring(String c) {
			springs = c + springs;
			return this;
		}
		
		public char popSpring() {
			if (springs.isEmpty()) return (char)0;
			char c = springs.charAt(0);
			springs = springs.substring(1);
			return c;
		}
		
		public int decrementCount() {
			if (counts.size() == 0) return -1;
			int count = counts.remove(0);
			counts.add(0, --count);
			return count;
		}
		
		public int popCount() {
			if (counts.size() == 0) return -1;
			return counts.remove(0);
		}
		
		/*
		 * Steps through the springs String a character at a time, stopping when we
		 * encounter a question mark, and returning -1;
		 * 
		 * Returns 0 if at any point the step encounters an un-solvable state.
		 * 
		 * Returns 1 if the entire record is solved (we reach the end with no question
		 * marks encountered and the springs string and counts list match).
		 */
		public int step() {
			
			while (springs.length() > 0) {
				char c = popSpring();
				if (c == '#') {
					consuming = true;
					if (decrementCount() < 0) return 0;					
				} else if (c == '.') {
					if (consuming) {
						if (popCount() > 0) return 0;
						consuming = false;
					}
				} else if (c == '?') {
					if (consuming && counts.size() > 0 && counts.get(0) > 0) {
						// we know that we must keep consuming so this '?' must be a '#'
						pushSpring("#");
					} else {
						return -1; // Indicates the recursive analysis must try both paths
					}
				} else {
					throw new UnsupportedOperationException("Encountered '" + c + "' when popping");
				}
				
				// System.out.println("    Stepping " + this);
			}
			
			if (counts.size() == 0 || (counts.size() == 1 && counts.get(0) == 0) ) {
				// System.out.println("      Found solution!");
				return 1;
			}			
			
			return 0; // We didn't count enough springs			
		}

		public String toString() {
			return springs + " " + counts;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(consuming, counts, springs);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConditionRecord other = (ConditionRecord) obj;
			return consuming == other.consuming && Objects.equals(counts, other.counts)
					&& Objects.equals(springs, other.springs);
		}
	}
}
