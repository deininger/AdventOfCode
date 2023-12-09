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
		long timerEnd = System.currentTimeMillis();
		System.out.println("Time: " + ((timerEnd - timerStart) / 1000) + " seconds");
	}
	
	public abstract String filename();
	
	public void setup() {
	}

	public void parse() {
		String line;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename()))) {
			line = reader.readLine();

			while (line != null) {
				this.parseLine(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Exception while reading data file: " + e.getLocalizedMessage());
		}
	}

	public void parseLine(String line) {
		System.out.println("Reading line '" + line + "'");
	}
	
	public void process() {
	}

	public void results() {
	}
}
