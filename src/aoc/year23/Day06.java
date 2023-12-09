package aoc.year23;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import aoc.util.ParseException;
import aoc.util.PuzzleApp;

public class Day06 extends PuzzleApp {
	public static final String TIME = "Time";
	public static final String DISTANCE = "Distance";

	public static final void main(String[] args) {
		System.out.println("December 06: Wait For It");
		PuzzleApp app = new Day06();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day06-part2";
	}
	
	public List<Long> times = new ArrayList<>();
	public List<Long> records = new ArrayList<>();
	
	public void parseLine(String line) {
		try {
			StringReader reader = new StringReader(line);
			StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
			
			int currentToken = streamTokenizer.nextToken();
			
			if (TIME.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); 
				currentToken = streamTokenizer.nextToken(); // Skip the colon

				while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the seed numbers
					long time = Double.valueOf(streamTokenizer.nval).longValue();
					times.add(time);
					currentToken = streamTokenizer.nextToken();
				}				
				
				System.out.println("Parsed times: " + times);
			} else if (DISTANCE.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); 
				currentToken = streamTokenizer.nextToken(); // Skip the colon

				while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the seed numbers
					long dist = Double.valueOf(streamTokenizer.nval).longValue();
					records.add(dist);
					currentToken = streamTokenizer.nextToken();
				}				
				
				System.out.println("Parsed record distances: " + records);
			} else {
				throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected TIME or DISTANCE"); 
			}		
		} catch (IOException e) {
			System.out.flush();
			System.err.println(e.getMessage()); 
			System.err.flush();
		}
	}	
	
	private long result = 1;
	
	public void process() {
		for (int race = 0; race < times.size(); race++) {
			long successCount = 0;
			for (long t = 1; t < times.get(race); t++) {
				if (t * (times.get(race)-t) > records.get(race) ) {
					successCount++;
				}
			}
			System.out.println("Race " + race + " success count = " + successCount);
			result *= successCount; 
		}
	}
	
	public void results() {
		System.out.println("Part 1: result = " + result);
	}
	
}
