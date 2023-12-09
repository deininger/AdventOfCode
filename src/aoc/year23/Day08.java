package aoc.year23;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aoc.util.NodePair;
import aoc.util.PuzzleApp;

public class Day08 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 08: Haunted Wasteland");
		PuzzleApp app = new Day08();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day08-part1";
	}
	
	public String instructions = null;
	public List<NodePair> startingNodes = new ArrayList<>();
	
	private static final String REGEX = "(\\w+) = \\((\\w+), (\\w+)\\)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	public void parseLine(String line) {
		if (instructions == null) {
			instructions = line;
			// System.out.println("Instructions: " + instructions);
		} else if (line.isBlank()) {
			// System.out.println("Skiping blank line");
		} else {
			// System.out.println("Matching " + line);
			try {
				Matcher matcher = PATTERN.matcher(line);
				if (matcher.matches()) {
					NodePair node = NodePair.addNodePair(matcher.group(1), matcher.group(2), matcher.group(3));
					if (node.name().endsWith("A")) { /* node.name().equals("AAA") */
						startingNodes.add(node);
					}
				} else {
					System.err.println("Unable to parse '" + line + "'");
				}
			} catch (IllegalStateException | IndexOutOfBoundsException e) {
				System.out.flush();
				System.err.println(e.getMessage()); 
				System.err.flush();
			}
		}
	}	
		
	public static final char LEFT = 'L';
	public static final char RIGHT = 'R';
	
	private List<BigInteger> loopCounts = new ArrayList<>();
	
	public void process() {
		System.out.println("Node count is " + NodePair.nodePairMap().size());
		System.out.println("Instruction length is " + instructions.length());
		System.out.println("Identified " + startingNodes.size() + " starting nodes");
		List<NodePair> nodes = new ArrayList<>(startingNodes);
		int position = 0;
		
		for (NodePair n : nodes) {
			long loopCount = 0;

			while (position > 0 || !n.name().endsWith("Z")) {
				if (instructions.charAt(position) == LEFT) {
					n = n.left();
				} else if (instructions.charAt(position) == RIGHT) {
					n = n.right();
				} else {
					System.err.println("Encountered unexpected instruction '" + instructions.charAt(position) + "'");
				}
				if (++position >= instructions.length()) {
					position = 0;
					loopCount++;
				}
			}
			
			System.out.println("Node " + n + " ends at " + loopCount + " loops");
			loopCounts.add(new BigInteger(String.valueOf(loopCount)));
			
			// Let's try another round, see if this is repeatable...
			if (instructions.charAt(position) == LEFT) {
				n = n.left();
			} else if (instructions.charAt(position) == RIGHT) {
				n = n.right();
			} else {
				System.err.println("Encountered unexpected instruction '" + instructions.charAt(position) + "'");
			}
			position++;
			
			while (position > 0 || !n.name().endsWith("Z")) {
				if (instructions.charAt(position) == LEFT) {
					n = n.left();
				} else if (instructions.charAt(position) == RIGHT) {
					n = n.right();
				} else {
					System.err.println("Encountered unexpected instruction '" + instructions.charAt(position) + "'");
				}
				if (++position >= instructions.length()) {
					position = 0;
					loopCount++;
				}
			}

			System.out.println("(again) Node " + n + " ends at " + loopCount + " loops");
		}
	}
	
	public static BigInteger lcm(BigInteger number1, BigInteger number2) {
	    BigInteger gcd = number1.gcd(number2);
	    BigInteger absProduct = number1.multiply(number2).abs();
	    return absProduct.divide(gcd);
	}
	
	public void results() {
		// System.out.println("Step Count = " + stepCount);
		System.out.println("Finding LCM of "+ loopCounts);
		BigInteger result = loopCounts.get(0);
		for (int i = 1; i < loopCounts.size(); i++) {
			result = lcm(result, loopCounts.get(i));
		}
		result = lcm(result, new BigInteger(String.valueOf(instructions.length())));
		System.out.println("Part 2 result = " + result); // 14299763833181
	}
}
