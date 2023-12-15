package aoc.year23;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import aoc.util.PuzzleApp;

public class Day12 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 12: Hot Springs");
		PuzzleApp app = new Day12();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day12-part1";
	}
	
	List<ConditionRecord> conditionRecords = new ArrayList<>();
	
	public void parseLine(String line) {
		String[] parts = line.split(" ");
		String[] groups = parts[0].split("\\.");
		String[] counts = parts[1].split(",");

		ConditionRecord cr = new ConditionRecord(groups, counts);
		conditionRecords.add(cr);
		// System.out.println("Parsed line into: " + cr);
	}
	
	private int analyze(ConditionRecord cr) {
		Deque<ConditionRecord> queue = new ArrayDeque<>();
		Set<ConditionRecord> alreadyAnalyzed = new HashSet<>();
		int solutionCount = 0;
		
		System.out.println("Analyzing " + cr);
		queue.add(cr);

		while (!queue.isEmpty()) {
			ConditionRecord r = queue.removeLast();
			if (alreadyAnalyzed.contains(r)) continue;
			alreadyAnalyzed.add(r);
			if (r.nonSolution()) continue;
			
			System.out.println("Examining " + r);
			
			if (r.isSolution()) {
				// solutions.add(r);
				solutionCount++;
			} else if (r.requiresSplit()) {
				queue.addAll(r.splits());
			} else {
				// queue.addAll(r.reductions());
				solutionCount += r.countCombinations();
			}
			
			// System.out.println("Queue depth = " + queue.size());
		}
		
		System.out.println("Solution count: " + solutionCount);
		return solutionCount;
	}
	
	private int totalSolutions = 0;
	
	public void process() {
		for (int i = 0; i < conditionRecords.size(); i++) {
			totalSolutions += analyze(conditionRecords.get(i));
		}
	}
	
	public void results() {
		System.out.println("Total solution count = " + totalSolutions);
	}
	
	class ConditionRecord {
		private List<String> groups;
		private int[] counts;
		
		public ConditionRecord(String[] groups, String[] counts) {
			this.groups = new ArrayList<>();
			for (int i = 0; i < groups.length; i++) {
				if (!groups[i].isBlank()) {
					this.groups.add(groups[i]);
				}
			}

			this.counts = new int[counts.length];
			for (int i = 0; i < counts.length; i++) {
				this.counts[i] = Integer.valueOf(counts[i]);
			}
		}
		
		public ConditionRecord(ConditionRecord parent, int groupToSplit, int charToSplit) {
			this.groups = new ArrayList<>();
			this.counts = parent.counts; // Safe because we never change this array

			for (int i = 0; i < parent.groups.size(); i++) {
				String group = parent.groups.get(i);
				if (i == groupToSplit) {
					if (charToSplit > 0) {
						this.groups.add(group.substring(0, charToSplit));
					}
					if (charToSplit < group.length()-1) {
						this.groups.add(group.substring(charToSplit+1));
					}
				} else {
					this.groups.add(group);
				}
			}
		}

		public ConditionRecord(ConditionRecord parent, int groupToReplace, int charToReplace, char newChar) {
			this.groups = new ArrayList<>();
			this.counts = parent.counts; // Safe because we never change this array

			for (int i = 0; i < parent.groups.size(); i++) {
				String group = parent.groups.get(i);
				if (i == groupToReplace) {
					String s = "";
					if (charToReplace > 0) {
						s += group.substring(0, charToReplace);
					}
					s += newChar;
					if (charToReplace < group.length()-1) {
						s += group.substring(charToReplace+1);
					}
					this.groups.add(s);
				} else {
					this.groups.add(group);
				}
			}
		}

		private int questionMarkCount(String s) {
			int qCount = 0;
			for (int j = 0; j < s.length(); j++) {
				if (s.charAt(j) == '?') qCount++;
			}
			return qCount;
		}
		
		public int questionMarkCount() {
			int qCount = 0;
			for (int i = 0; i < groups.size(); i++) {
				qCount += questionMarkCount(groups.get(i));
			}
			return qCount;
		}
		
		public boolean groupsMatchCounts() {
			boolean result = true;
			for (int i = 0; i < counts.length; i++) {
				if (groups.get(i).length() != counts[i]) {
					result = false;
				}
			}
			return result;
		}
		
		public boolean nonSolution() {
			boolean result = false;
			
			if (groups.size() == counts.length) {
				for (int i = 0; i < counts.length; i++) {
					if (groups.get(i).length() < counts[i]) result = true; // Can't have a solution
				}
			}
			
			return result;
		}
		
		public boolean isSolution() {
			return groups.size() == counts.length && groupsMatchCounts();
		}
		
		/*
		 * Any ConditionRecord with fewer groups than counts will
		 * require splitting.
		 */
		public boolean requiresSplit() {
			return groups.size() < counts.length;
		}
		
		/*
		 * Returns a set of all possible splits: groups split at a
		 * question mark into sub-groups. 
		 */
		public Set<ConditionRecord> splits() {
			Set<ConditionRecord> results = new HashSet<>();
			
			for (int i = 0; i < groups.size(); i++) {
				String group = groups.get(i);
				for (int j = 1; j < group.length() - 1; j++) {
					if (group.charAt(j) == '?') {
						ConditionRecord cr = new ConditionRecord(this, i, j);
						// System.out.println("Splitting " + this + " into " + cr);
						if (!cr.nonSolution()) { results.add(cr); }
					}
				}
			}
			
			if (!results.isEmpty()) System.out.println("Splits: " + results);
			return results;
		}
		
		private int countCombinations(String s, int count) {						
			if (s.length() == count) return 1; // Only one combination possible;
			
			if (!s.contains("#")) return 1 + s.length() - count; // All '?'
			
			// Count the '?' at the beginning & end of the string, if we don't have enough to
			// shrink the string down to size 'count' then there are no solutions
			
			int startingQuestionMarkCount = s.indexOf('#');
			int endingQuestionMarkCount = s.length() - s.lastIndexOf('#') - 1;
			
			// if (startingQuestionMarkCount + endingQuestionMarkCount < s.length() - count) return 0;
			
			int lengthOfSubStringWeMustKeep = s.length() - startingQuestionMarkCount - endingQuestionMarkCount;
			
			int additionalCharactersWeMustGrow = count - lengthOfSubStringWeMustKeep;
			
			if (additionalCharactersWeMustGrow == 0) return 1; // No growth necessary
			
			int result = Math.min(additionalCharactersWeMustGrow, startingQuestionMarkCount)
					+ Math.min(additionalCharactersWeMustGrow, endingQuestionMarkCount);
			
			return result;
		}
		
		public int countCombinations() {
			int result = 1;

			for (int i = 0; i < groups.size(); i++) {
				String group = groups.get(i);
				result *= countCombinations(group, counts[i]);
			}
			
			System.out.println("Combination count = " + result);
			return result;
		}
		
		public Set<ConditionRecord> reductions() {
			Set<ConditionRecord> results = new HashSet<>();
			
			for (int i = 0; i < groups.size(); i++) {
				String group = groups.get(i);
				
				if (group.length() > counts[i]) {
					if (group.charAt(0) == '?') {
						ConditionRecord cr = new ConditionRecord(this, i, 0);
						results.add(cr);
					}
					if (group.charAt(group.length()-1) == '?') {
						ConditionRecord cr = new ConditionRecord(this, i, 0);
						results.add(cr);
					}
				}
				
				if (!results.isEmpty()) break;
			}
			
			System.out.println("Reductions: " + results);
			return results;
		}
		
		public String toString() {
			return groups.toString() + " " + Arrays.toString(counts);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(counts);
			result = prime * result + Objects.hash(groups);
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
			return Arrays.equals(counts, other.counts) && Objects.equals(groups, other.groups);
		}
	}
}
