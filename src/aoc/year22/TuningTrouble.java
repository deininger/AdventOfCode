package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import aoc.util.SlidingWindowSpliterator;

public class TuningTrouble {
	private static final int WINDOW_SIZE = 14;
	
	private static int position = 0;
	
	public static final void main(String[] args) throws IOException {
		System.out.println("December  6: Tuning Trouble");

		BufferedReader reader = new BufferedReader(new FileReader("data/Data6"));
		String line = reader.readLine();
		
		while (line != null) {
			analyze(line);
			System.out.println();
			position = 0;
			line = reader.readLine();
		}
		
		reader.close();
	}
	
	private static void analyze(String line) {
		List<Character> characters = line.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
		
		SlidingWindowSpliterator.windowed(characters, WINDOW_SIZE)
		    .forEach(group -> {
		    	String s = group.map(c->c.toString()).collect(Collectors.joining());
		    	boolean distinct = s.chars().distinct().count() == WINDOW_SIZE;
		    	// boolean distinct = group.distinct().count() == WINDOW_SIZE;
		    	
		    	if (distinct) {
		    	 	 System.out.println(position + " " + s + " is distinct! position: " + (position + WINDOW_SIZE));
		    	} else {
		    		 // System.out.println(position + " " + s);
		    	}
		    	
		    	if (distinct) {
		    	//	System.out.println("Detected Signal at position " + (position + WINDOW_SIZE));
		    	}
		    	
		    	position++;
		    });
	}
}
