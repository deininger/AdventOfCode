package aoc.year23;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aoc.util.PuzzleApp;

public class Day02 extends PuzzleApp {
	private static final String GAME = "Game";

	public static final void main(String[] args) {
		System.out.println("December 02: Cube Conundrum");
		PuzzleApp app = new Day02();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day02-part1";
	}
	
//	private Node root;
//	
//	public void parse() {
//		try (BufferedReader reader = new BufferedReader(new FileReader(filename()))) {
//			String line;
//			StringBuilder buffer = new StringBuilder();
//			
//			while ((line = reader.readLine()) != null) {
//				buffer.append(line).append('\n');
//			}
//
//			System.out.println("Creating parser...");
//			
//			Day2Parser parser = new Day2Parser(buffer);
//
//			parser.Root();
//			root = parser.rootNode();
//			System.out.println("Dumping the AST...");
//			root.dump();
//		} catch (IOException e) {
//			System.err.println("Exception while reading data file: " + e.getLocalizedMessage());
//		}
//
//	}
	
//	private static final String LINE_REGEX = "Game (\\d+): (.*)";
//	private static final Pattern LINE_PATTERN = Pattern.compile(LINE_REGEX);
//
//	public void parseLine(String line) {
//		System.out.println(line);
//		Matcher matcher = LINE_PATTERN.matcher(line);
//		if (matcher.matches()) {
//			int game = Integer.parseInt(matcher.group(1));
//			String remainingLine = matcher.group(2);
//			System.out.println("Match returned game " + game + ": " + remainingLine);
//		} else {
//			System.out.println("Input line didn't match REGEX");
//		}
//	}

	private Map<Integer, List<Measurement>> games = new HashMap<>();

	public void parseLine(String line) {
		StringReader reader = new StringReader(line);
		StreamTokenizer streamTokenizer = new StreamTokenizer(reader);

		try {
			int currentToken = streamTokenizer.nextToken();

			Integer currentGame = null;

			while (currentToken != StreamTokenizer.TT_EOF) {
				switch (streamTokenizer.ttype) {
				case StreamTokenizer.TT_WORD:
					if (GAME.equals(streamTokenizer.sval)) {
						streamTokenizer.nextToken(); // Next token will be game #
						Integer game = Double.valueOf(streamTokenizer.nval).intValue();
						games.put(game, new ArrayList<>());
						currentGame = game;
						// System.out.println("Set current game to " + game);
					}
					break;
				case StreamTokenizer.TT_NUMBER:
					Integer quantity = Double.valueOf(streamTokenizer.nval).intValue();
					streamTokenizer.nextToken(); // Next token will be a color
					String color = streamTokenizer.sval;
					Measurement m = new Measurement(color, quantity);
					games.get(currentGame).add(m);
					// System.out.println("Added Measurement " + m + " to game " + currentGame);
				}

				currentToken = streamTokenizer.nextToken();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}	
	
	private int sumOfIds = 0;
	private int sumOfPowers = 0;
	
	public void process() {
		// Find out which games might occur with only 12 red cubes, 13 green cubes, and 14 blue cubes
		
		for (int game = 1; game <= games.size(); game++) {
			boolean goodGame = true; // assume it's ok until proven otherwise...
			List<Measurement> measurements = games.get(game);
			
			for (int j = 0; j < measurements.size(); j++) {
				Measurement m = measurements.get(j);
				if (m.isBlue() && m.quantity() > 14) goodGame = false;
				if (m.isGreen() && m.quantity() > 13) goodGame = false;
				if (m.isRed() && m.quantity() > 12) goodGame = false;
			}
			
			if (goodGame) {
				// All measurements are good
				sumOfIds += game;
			}
		}
		
		// Now for part 2, we calculate "power"
		
		for (int game = 1; game <= games.size(); game++) {
			List<Measurement> measurements = games.get(game);

			Integer largestRed = 0;
			Integer largestGreen = 0;
			Integer largestBlue = 0;
			
			for (int j = 0; j < measurements.size(); j++) {
				Measurement m = measurements.get(j);
				
				if (m.isRed() && largestRed < m.quantity()) largestRed = m.quantity;
				if (m.isBlue() && largestBlue < m.quantity()) largestBlue = m.quantity;
				if (m.isGreen() && largestGreen < m.quantity()) largestGreen = m.quantity;
			}
			
			System.out.println("Game " + game + " largest red is " + largestRed + ", largest blue is " + largestBlue + ", largest green is " + largestGreen);
			Integer power = largestRed * largestGreen * largestBlue;
			System.out.println("Game " + game + " power is " + power);
			sumOfPowers += power;
		}
	}
	
	public void results() {
		System.out.println("Sum of IDs of good games: " + sumOfIds);
		System.out.println("Sum of powers of games: " + sumOfPowers);
	}
	
	class Measurement {
		public static final String BLUE = "blue";
		public static final String GREEN = "green";
		public static final String RED = "red";
		
		private Integer quantity;
		private String color;
		
		public Measurement(String color, Integer quantity) {
			this.color = color;
			this.quantity = quantity;
		}
		
		public Integer quantity() {
			return quantity;
		}
		
		public boolean isBlue() {
			return BLUE.equals(color);
		}

		public boolean isGreen() {
			return GREEN.equals(color);
		}

		public boolean isRed() {
			return RED.equals(color);
		}

		public String toString() {
			return color + "(" + quantity + ")";
		}
	}

}
