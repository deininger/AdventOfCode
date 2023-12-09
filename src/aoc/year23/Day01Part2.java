package aoc.year23;

import aoc.util.PuzzleApp;

public class Day01Part2 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 01: Trebuchet?! (Part 2)");
		PuzzleApp app = new Day01Part2();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day01-part1";
	}

	int sum = 0;
	
	private boolean matchesDigitNamed(String str, int startingPosition, String match) {
		try {
			// System.out.println("Comparing '" 
			//		+ str.subSequence(startingPosition, startingPosition + match.length())
			//		+ "' to '" + match + "'");
			return str.subSequence(startingPosition, startingPosition + match.length()).equals(match);
		}
		catch (IndexOutOfBoundsException e) {
		}
		return false;
	}
	private int matchesDigitName(String str, int startingPosition) {
		if (matchesDigitNamed(str, startingPosition, "one")) return 1;
		if (matchesDigitNamed(str, startingPosition, "two")) return 2;
		if (matchesDigitNamed(str, startingPosition, "three")) return 3;
		if (matchesDigitNamed(str, startingPosition, "four")) return 4;
		if (matchesDigitNamed(str, startingPosition, "five")) return 5;
		if (matchesDigitNamed(str, startingPosition, "six")) return 6;
		if (matchesDigitNamed(str, startingPosition, "seven")) return 7;
		if (matchesDigitNamed(str, startingPosition, "eight")) return 8;
		if (matchesDigitNamed(str, startingPosition, "nine")) return 9;
		// System.out.println("no match");
		return -1;
	}
	
	private int findFirstDigit(String str) {
		for (int i = 0; i < str.length(); i++) {
		    char c = str.charAt(i);
		    
		    if (c >= '0' && c <= '9') {
		    	// System.out.println("First Digit is " + c);
		    	return c - '0';
		    }
		    
		    int matchesDigit = matchesDigitName(str, i);
		    
		    if (matchesDigit > 0) {
		    	// System.out.println("First Digit has value " + matchesDigit);
		    	return matchesDigit;
		    }
		}
		
		return 0;
	}
	
	private int findLastDigit(String str) {
		for (int i = str.length()-1; i >= 0; i--) {
		    char c = str.charAt(i);
		    
		    if (c >= '0' && c <= '9') {
		    	// System.out.println("Last Digit is " + c);
		    	return c - '0';
		    }
		    
		    int matchesDigit = matchesDigitName(str, i);
		    
		    if (matchesDigit > 0) {
		    	// System.out.println("Last Digit has value " + matchesDigit);
		    	return matchesDigit;
		    }
		}
		
		return 0;

	}
	
	public void parseLine(String line) {
		// System.out.println("Analyzing line '" + line + "'");
		
		int tensDigit = findFirstDigit(line);
		int onesDigit = findLastDigit(line);
		
		System.out.println("Line: '" + line + "' -> " + tensDigit + "" + onesDigit);
		
		sum += (tensDigit * 10 + onesDigit);
	}
	
	public void results() {
		System.out.println(sum);
	}
}
