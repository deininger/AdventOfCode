package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CathodeRayTube {
	private static CathodeRayTube app = new CathodeRayTube();

	public static final void main(String[] args) throws IOException {
		System.out.println("December 10: Cathode-Ray Tube");
		BufferedReader reader = new BufferedReader(new FileReader("data/Data10"));
		app.process(reader);
		reader.close();
	}

	public void process(BufferedReader reader) {
		app.setup();
		
		String line;
		try {
			line = reader.readLine();

			while (line != null) {
				app.process(line);
				line = reader.readLine();
			}

			app.results();
		} catch (IOException e) {
			System.err.println("Exception while reading data file: " + e.getLocalizedMessage());
		}
	}
	
	private int counter = 0;
	private int accumulator = 1;
	private HashMap<Integer,Integer> samples = new HashMap<>();
	
	public void setup() {
		samples.put(20, 0);
		samples.put(60, 0);
		samples.put(100, 0);
		samples.put(140, 0);
		samples.put(180, 0);
		samples.put(220, 0);
	}
	
	public void measure() {
		if (samples.containsKey(counter)) {
			samples.put(counter, accumulator);
		}
	}
	
	private StringBuffer sb = new StringBuffer();
	
	public void draw() {
		int hPosition = counter % 40;
		if (hPosition == accumulator || hPosition == accumulator - 1 || hPosition == accumulator + 1) {
			sb.append('#');
		} else {
			sb.append('.');
		}
	}
	public void tick() {
		draw();
		counter++;
		measure();
	}
	
	public void add(int value) {
		accumulator += value;
	}
	
	public void process(String line) {
		String[] pieces = line.split(" ");
		
		switch (pieces[0]) {
		case "noop": tick(); break;
		case "addx": tick(); tick(); add(Integer.parseInt(pieces[1])); break;
		default: throw new IllegalArgumentException("Unknown instruction '" + pieces[0] + "'");
		}
	}
	
	private int total = 0;
	
	public void results() {
		System.out.println(samples);
		samples.forEach((k,v) -> total += k*v );
		System.out.println("Total = " + total);
		
		for(int i = 0; i < sb.length(); i+= 40) {
			System.out.println(sb.substring(i, i+40));
		}
	}
}
