package aoc.year23;

import java.util.ArrayList;
import java.util.List;

import aoc.util.PuzzleApp;

public class Day03 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 03: ...");
		PuzzleApp app = new Day03();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day03-part1";
	}

	int partNumberSum = 0;
	int gearRatioSum = 0;
	Schematic schematic = new Schematic();
	
	public void parseLine(String line) {
		schematic.addLine(line);
	}
	
	/*
	 * row and col are the starting point of a possibly multi-digit number.
	 * Returns 0 if the number isn't adjacent to a non-dot symbol
	 */
	private int determinePartNumber(int row, int col) {
		// First determine all the digits of the number:
		int number = 0;
		int endCol = col;
		
		char c = schematic.charAt(row, endCol);

		while (Character.isDigit(c)) {
			number = number * 10 + (c - '0');
			c = schematic.charAt(row, ++endCol);
		}
				
		System.out.println("Parsed number " + number + " on row " + row + " columns " + col + "-" + endCol );

		// Now figure out if there's an adjacent symbol:
		boolean hasSymbol = false;
		
		
		for (int j = col-1; j <= endCol; j++) { // Looking above and below the number:
			hasSymbol = hasSymbol | schematic.isSymbol(row-1, j);
			hasSymbol = hasSymbol | schematic.isSymbol(row+1, j);
		}
		
		// Looking left and right of the number:
		hasSymbol = hasSymbol | schematic.isSymbol(row, col-1);
		hasSymbol = hasSymbol | schematic.isSymbol(row, endCol);

		if (hasSymbol) {
			System.out.println("Number " + number + " has an adjacent symbol!");
			return number;
		} else {
			System.out.println("Number " + number + " does not have an adjacent symbol!");
			return 0;
		}
	}
	
	/*
	 * Search the schematic for numbers which have adjacent symbols.
	 */
	public void findPartNumbers() {
		for (int i = 0; i < schematic.numRows(); i++) {
			for (int j = 0; j < schematic.numCols(); j++) {
				char c = schematic.charAt(i, j);
				if (Character.isDigit(c)) { // First digit of a number
					int partNumber = determinePartNumber(i, j);
					partNumberSum += partNumber;
					while (j < schematic.numCols() && Character.isDigit(c)) { // Skip the remaining digits of the number
						c = schematic.charAt(i, ++j);
					}
				}
			}
		}
	}
	
	/*
	 * Search the schematic for * symbols with EXACTLY two adjacent numbers.
	 */
	public void findGearRatios() {
		for (int i = 0; i < schematic.numRows(); i++) {
			for (int j = 0; j < schematic.numCols(); j++) {
				if (schematic.isStar(i, j)) {
					// Look around the star, count the digits, but careful of the top and bottom!
					List<Integer> adjacentNumbers = new ArrayList<>();
					
					if (schematic.isDigit(i, j-1)) adjacentNumbers.add(schematic.getFullNumber(i, j-1)); // left
					if (schematic.isDigit(i, j+1)) adjacentNumbers.add(schematic.getFullNumber(i, j+1)); // right
					if (schematic.isDigit(i-1, j)) adjacentNumbers.add(schematic.getFullNumber(i-1, j)); // top
					if (schematic.isDigit(i+1, j)) adjacentNumbers.add(schematic.getFullNumber(i+1, j)); // bottom
					
					// Count the corners ONLY if the top and bottom aren't digits,
					// because if they are, then the corners are part of a larger string of digits
					
					if (!schematic.isDigit(i-1, j)) { // top isn't a digit
						if (schematic.isDigit(i-1, j-1)) adjacentNumbers.add(schematic.getFullNumber(i-1, j-1)); // top-left
						if (schematic.isDigit(i-1, j+1)) adjacentNumbers.add(schematic.getFullNumber(i-1, j+1)); // top-right
					}

					if (!schematic.isDigit(i+1, j)) { // bottom isn't a digit
						if (schematic.isDigit(i+1, j-1)) adjacentNumbers.add(schematic.getFullNumber(i+1, j-1)); // bottom-left
						if (schematic.isDigit(i+1, j+1)) adjacentNumbers.add(schematic.getFullNumber(i+1, j+1)); // bottom-right
					}

					System.out.println("Star at (" + i + "," + j + ") has " + adjacentNumbers.size() + " adjacent numbers");

					if (adjacentNumbers.size() == 2) {
						gearRatioSum += adjacentNumbers.get(0) * adjacentNumbers.get(1);
					}
				}
			}
		}
	}
	
	public void process() {
		findPartNumbers(); // Part 1
		findGearRatios(); // Part 2
	}
	
	public void results() {
		System.out.println("Sum of part numbers = " + partNumberSum);
		System.out.println("Sum of gear ratios = " + gearRatioSum);
	}
	
	class Schematic {
		public static final char DOT = '.';
		public static final char STAR = '*';
		
		private List<String> lines = new ArrayList<>();
				
		public void addLine(String line) {
			lines.add(line);
		}
		
		public int numRows() {
			return lines.size();
		}
		
		public int numCols() {
			return lines.get(0).length();
		}
		
		public char charAt(int row, int col) {
			if (row >= 0 && row < numRows() && col >= 0 && col < numCols()) {
				return lines.get(row).charAt(col);
			} else {
				return DOT;
			}
		}
		
		public boolean isSymbol(int row, int col) {
			return this.charAt(row, col) != DOT && !Character.isDigit(this.charAt(row, col));
		}
		
		public boolean isStar(int row, int col) {
			return this.charAt(row, col) == STAR;
		}
		
		public boolean isDigit(int row, int col) {
			return Character.isDigit(this.charAt(row, col));
		}
		
		/*
		 * Works its way left & right to get all the digits of the number 
		 * which has a digit at the given coordinates.
		 */
		public int getFullNumber(int row, int col) {
			if (!isDigit(row, col)) return 0;
			
			while (isDigit(row,col-1)) col--;
			int number = 0;
			while (isDigit(row,col)) {
				number = 10 * number + (charAt(row, col) - '0');
				col++;
			}
			return number;
		}
	}
}
