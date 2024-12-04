package aoc.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class PuzzleApp implements Runnable {
	public PuzzleApp() {
	}

	public void run() {
		long timerStart = System.currentTimeMillis();
		setup();
		parse();
		process();
		results();
		processPartTwo();
		resultsPartTwo();
		long timerEnd = System.currentTimeMillis();
		System.out.println("Time: " + ((timerEnd - timerStart) / 1000) + " seconds");
	}
	
	public abstract String filename();
	
	public void setup() {
	}

	public void parse() {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename()))) {
			String line;

			while ((line = reader.readLine())!= null) {
				this.parseLine(line);
			}
		} catch (IOException e) {
			System.err.println("Exception while reading data file: " + e.getMessage());
		}
	}

	public void parseLine(String line) {
		System.out.println("Reading line '" + line + "'");
	}
	
	public void process() {
	}

	public void results() {
	}

	public void processPartTwo() {}

	public void resultsPartTwo() {}
}
