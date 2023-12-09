package aoc.year22;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import aoc.util.Parser;
import aoc.util.PuzzleApp;
import aoc.util.Parser.Token;

public class DistressSignal extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 13: Distress Signal");
		PuzzleApp app = new DistressSignal();
		app.run();
	}
	
	private Token dividerOne;
	private Token dividerTwo;
	
	public String filename() {
		return "data/data13";
	}
	
	public void setup() {
		Parser parser = new Parser("[[2]]");
		dividerOne = parser.nextToken();
		allPackets.add(dividerOne);
		
		parser = new Parser("[[6]]");
		dividerTwo = parser.nextToken();
		allPackets.add(dividerTwo);
	}

	private boolean firstLine = true;
	private Token n1;
	private Token n2;
	private int index = 1;
	private int sum = 0;
	private List<Token> allPackets = new ArrayList<>();
	
	void processLine(String line) {
		if (line.isEmpty()) { // Blank line separating pairs
			index++;
		} else if (firstLine) { // First line of pair
			Parser parser = new Parser(line);
			n1 = parser.nextToken();
			allPackets.add(n1);
			firstLine = false;
		} else {  // Second line of pair
			Parser parser = new Parser(line);
			n2 = parser.nextToken();
			allPackets.add(n2);
			firstLine = true;
			int result = compare(n1, n2);
			if (result == -1) {
				sum += index;
			}
		}
	}

	class TokenComparator implements Comparator<Token> {

		public int compare(Token left, Token right) {
			// System.out.println("** comparing " + left + " to " + right + " **");
			int result = 0;
			
			if (left.hasValue() && right.hasValue()) {
				// Both sides are numbers
				result = left.value().compareTo(right.value());
			} else {
				// Treat both sides as lists
				for (int i = 0; result == 0 && i < right.subtokens().size() && i < left.subtokens().size(); i++) {
					// System.out.println("** within list, comparing " + left.subtokens().get(i) + " to " + right.subtokens().get(i) + " **");
					result = compare(left.subtokens().get(i), right.subtokens().get(i));
				}
				if (result == 0) {
					result = Integer.signum(left.subtokens().size() - right.subtokens().size());
				}
			}
			
			// System.out.println("** comparison result = " + result + " **");
			return result;
		}
	}
	
	int compare(Token left, Token right) {
		System.out.println("Comparing " + left + " to " + right);
		
		int result = new TokenComparator().compare(left, right);
		
		switch (result) {
		case -1: System.out.println("  Packets are in the CORRECT order"); break;
		case  1: System.out.println("  Packets are in the WRONG order"); break;
		case  0: System.out.println("  Packets are identical"); break;
		default: throw new IllegalArgumentException("Unexpected comparison result " + result);
		}
		
		System.out.println();
		return result;
	}

	private TokenComparator tokenComparator = new TokenComparator();
	
	public void results() {
		System.out.println("The sum of correct packet indices is " + sum);

		allPackets.sort(tokenComparator);
		
		// System.out.println(allPackets);
		
		int firstDivider = 1 + allPackets.indexOf(dividerOne);
		int secondDivider = 1 + allPackets.indexOf(dividerTwo);
		
		System.out.println("The product of the divider indices is " + (firstDivider * secondDivider));
	}
	
}
