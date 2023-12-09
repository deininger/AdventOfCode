package aoc.year23;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import aoc.util.ParseException;
import aoc.util.PuzzleApp;

public class Day04 extends PuzzleApp {
	private static final String CARD = "Card";
	// private static final String COLON = ":";
	// private static final String BAR = "|";

	public static final void main(String[] args) {
		System.out.println("December 04: Scratchcards");
		PuzzleApp app = new Day04();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day04-part1";
	}
	
	private List<ScratchCard> scratchCards = new ArrayList<>();

	public void parseLine(String line) {
		System.out.println("parseLine('" + line + "')");
		StringReader reader = new StringReader(line);
		StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
		ScratchCard scratchCard;
		
		try {
			int currentToken = streamTokenizer.nextToken();

			if (CARD.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); // Next token will be card #
				Integer cardNumber = Double.valueOf(streamTokenizer.nval).intValue();
				System.out.println("Parsing card # " + cardNumber);
				scratchCard = new ScratchCard(cardNumber);
				scratchCards.add(scratchCard);
				currentToken = streamTokenizer.nextToken(); 
			} else {
				throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected CARD"); 
			}
		
			// if (COLON.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); // Skip the colon
			// } else {
			//	throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected COLON"); 
			// }

			List<Integer> winningNumbers = new ArrayList<>();
			
			while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the winning numbers
				winningNumbers.add(Double.valueOf(streamTokenizer.nval).intValue());
				scratchCard.addWinningNumber(Double.valueOf(streamTokenizer.nval).intValue());
				currentToken = streamTokenizer.nextToken();
			}
			
			System.out.println("Parsed winnning numbers: " + winningNumbers);
			
			// if (BAR.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); // Skip the bar
			// } else {
			//	throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected BAR"); 
			// }

			List<Integer> numbersYouHave = new ArrayList<>();
			
			while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the numbers you have
				numbersYouHave.add(Double.valueOf(streamTokenizer.nval).intValue());
				scratchCard.addMyNumber(Double.valueOf(streamTokenizer.nval).intValue());
				currentToken = streamTokenizer.nextToken();
			}

			System.out.println("Parsed numbers you have: " + numbersYouHave);

			if (currentToken == StreamTokenizer.TT_EOF) {
				System.out.println("Reached end of input line!");
			} else {
				throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected EOF"); 
			}
		} catch (IOException | ParseException e) {
			System.out.flush();
			System.err.println(e.getMessage()); 
			System.err.flush();
		}
	}	
	
	private int totalPointValue = 0;
	private int totalCardCount = 0;
	
	public void process() {
		// Part 1:
		for (ScratchCard card : scratchCards) {
			totalPointValue += card.score();
		}
		
		// Part 2:
		for (ScratchCard card : scratchCards) {
			totalCardCount += card.count();
			for (int i = 0; i < card.matches(); i++) {
				scratchCards.get(card.id()+i).incrementCount(card.count());
			}
		}
	}
	
	public void results() {
		System.out.println("Part 1: Total point value = " + totalPointValue);
		System.out.println("Part 1: Total card count = " + totalCardCount);
	}
	
	class ScratchCard {
		private int id;
		private int count = 1;
		private List<Integer> winningNumbers = new ArrayList<>();
		private List<Integer> myNumbers = new ArrayList<>();
		
		public ScratchCard(int id) {
			this.id = id;
		}
		
		public int id() {
			return id;
		}
		
		public void addWinningNumber(Integer winningNumber) {
			winningNumbers.add(winningNumber);
		}
		
		public void addMyNumber(Integer myNumber) {
			myNumbers.add(myNumber);
		}
		
		public void incrementCount(int increment) {
			count += increment;
		}
		
		public int count() {
			return count;
		}
		
		public int matches() {
			int matches = 0;
			for(Integer n : myNumbers) {
				if (winningNumbers.contains(n)) matches++;
			}
			return matches;
		}
		
		public int score() {
			int score = 1;
			for(Integer n : myNumbers) {
				if (winningNumbers.contains(n)) score = score << 1;
			}
			return score >> 1;
		}
	}
}
