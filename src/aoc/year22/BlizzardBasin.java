package aoc.year22;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class BlizzardBasin extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 24: Blizzard Basin");
		PuzzleApp app = new BlizzardBasin();
		app.run();
	}

	public String filename() {
		return "data/data24";
	}

	private static final String REGEX1 = "^#.#+$";
	private static final String REGEX2 = "^#([\\^<>v.]+)#$";
	private static final String REGEX3 = "^#+.#$";
	private static final Pattern PATTERN1 = Pattern.compile(REGEX1);
	private static final Pattern PATTERN2 = Pattern.compile(REGEX2);
	private static final Pattern PATTERN3 = Pattern.compile(REGEX3);

	int mazeWidth = 0;
	int mazeHeight = 0;

	private static final Loc START = new Loc(0, -1);
	private static Loc END;

	private Set<Blizzard> blizzards = new HashSet<>();
	
	Map<Loc,Long> locationCounts(int t) {
		return blizzards.stream().collect(Collectors.groupingBy(b -> b.loc(t), Collectors.counting()));
	}
	
	boolean blizzardsAt(Loc loc, int t) {
		return blizzards.stream().anyMatch(b -> b.loc(t).equals(loc));
	}
	
	void createBlizzards(int y, String line) {
		for (int x = 0; x < line.length(); x++) {
			char c = line.charAt(x);
			if (c != '.') blizzards.add(new Blizzard(x, y, c));
		}
	}
	
	public void parseLine(String line) {
		Matcher matcher1 = PATTERN1.matcher(line);
		Matcher matcher2 = PATTERN2.matcher(line);
		Matcher matcher3 = PATTERN3.matcher(line);

		if (matcher1.matches()) {
			mazeWidth = line.length() - 2;
		} else if (matcher2.matches()) {
			createBlizzards(mazeHeight, matcher2.group(1));
			mazeHeight++;
		} else if (matcher3.matches()) {
			Blizzard.setMax(new Loc(mazeWidth, mazeHeight));
			END = new Loc(mazeWidth-1, mazeHeight);
		} else {
			throw new IllegalArgumentException("Cannot parse line: '" + line + "'");
		}
		
	}
	
	int tickPrinter = 0;
	
	Optional<Expedition> search(Expedition start) {
		Queue<Expedition> queue = new ArrayDeque<>();
		Set<Expedition> alreadyQueued = new HashSet<>();
		
		queue.add(start);
		alreadyQueued.add(start);
		
		System.out.println("Tick " + tickPrinter + ", queue depth = " + queue.size());
		// System.out.println(printMap(tickPrinter, expedition));

		while (!queue.isEmpty()) {
			Expedition current = queue.remove();
			alreadyQueued.remove(current);
			
			if (current.time() > tickPrinter) {
				tickPrinter = current.time();
				System.out.println("Tick " + tickPrinter + ", queue depth = " + queue.size());
				// System.out.println(printMap(tickPrinter, current));
			}
			
			if (current.atDestination()) {
				return Optional.of(current);
			}
			
			// Find all the places we can safely be at the next tick:
			
			current.loc().adjacent()
				.filter(l -> l.within(Blizzard.max()) || l.equals(START) || l.equals(END))
				.filter(l -> !blizzardsAt(l, current.time() + 1))
				.map(l -> new Expedition(current, l))
				.forEach(e -> { 
					if (!alreadyQueued.contains(e)) {
						queue.add(e); 
						alreadyQueued.add(e); 
						}
					});
			
			// Also try staying where we are:
			
			if (!blizzardsAt(current.loc(),current.time() + 1)) {
				Expedition e = new Expedition(current, current.loc());
				if (!alreadyQueued.contains(e)) {
					queue.add(e); 
					alreadyQueued.add(e); 
					}
			}
		}
		
		return Optional.empty();
	}
	
	public void process() {
		System.out.println("Traversing from START to END:");
		Expedition expedition = new Expedition(START, END, 0);
		
		Optional<Expedition> result = search(expedition);
		
		if (result.isPresent()) {
			expedition = result.get();
			System.out.println("Found the exit at turn " + expedition.time());
			// System.out.println(result.get());
		} else {
			System.out.println("Queue empty, found no path from start to end");
		}

		System.out.println("Traversing from END to START:");
		expedition = new Expedition(END, START, expedition.time());
		result = search(expedition);
		
		if (result.isPresent()) {
			expedition = result.get();
			System.out.println("Found the exit at turn " + expedition.time());
			// System.out.println(result.get());
		} else {
			System.out.println("Queue empty, found no path back to origin");
		}

		System.out.println("Traversing from START to END again:");
		expedition = new Expedition(START, END, expedition.time());
		result = search(expedition);
		
		if (result.isPresent()) {
			expedition = result.get();
			System.out.println("Found the exit at turn " + expedition.time());
			// System.out.println(result.get());
		} else {
			System.out.println("Queue empty, found no path to end again");
		}
	}
	
	public void results() {
		// System.out.println(printMap(0, expedition));
		
		// 242 is correct for Part 1
	}
	
	public String printMap(int t, Expedition expedition) {
		Map<Loc,Long> locationCounts = locationCounts(t);
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n#");
		
		if (expedition.atOrigin()) {
			sb.append('E');
		} else {
			sb.append('.');
		}
		
		for (int x = 1; x < mazeWidth; x++) sb.append('#');
		sb.append("#\n");

		for (int y = 0; y < mazeHeight; y++) {
			sb.append('#');

			for (int x = 0; x < mazeWidth; x++) {
				Loc l = new Loc(x,y);
				if (expedition.loc().equals(l)) {
					sb.append('E');
				} else {
					Long count = locationCounts.get(l);
					if (count == null || count == 0) {
						sb.append('.');
					} else if (count < 10 ) {
						sb.append(String.valueOf(count));
					} else {
						sb.append("*"); // More than 10
					}
				}
			}
			sb.append("#\n");
		}
		
		sb.append("#");
		for (int x = 0; x < mazeWidth - 1; x++) sb.append('#');
		
		if (expedition.atDestination()) {
			sb.append('E');
		} else {
			sb.append('.');
		}
		
		sb.append("#\n");

		return sb.toString();
	}
	
	class Expedition {
		private Loc origin;
		private Loc destination;		
		private Loc loc;
		private int time;
		// private Expedition previous;
		
		public Expedition(Loc origin, Loc destination, int time) {
			this.origin = origin;
			this.destination = destination;
			this.loc = origin;
			this.time = time;
		}

		public Expedition(Expedition previous, Loc newLoc) {
			this.origin = previous.origin;
			this.destination = previous.destination;
			// this.previous = previous;
			this.loc = newLoc;
			this.time = previous.time + 1;
		}

		public Loc loc() {
			return loc;
		}
		
		public void setLoc(Loc loc) {
			this.loc = loc;
		}
		
		private int time() {
			return time;
		}
		
		public boolean atOrigin() {
			return (origin.equals(loc));
		}
		
		public boolean atDestination() {
			return (destination.equals(loc));
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(loc, time);
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Expedition other = (Expedition) obj;
			return Objects.equals(loc, other.loc) && time == other.time;
		}

		public String toString() {
		//	if (previous != null) {
		//		return previous.toString() + "->" + loc.toString();
		//	} else {
				return loc.toString();
		//	}
		}
	}
	
	class Blizzard {
		private static Loc max;
		
		public static void setMax(Loc loc) {
			max = loc;
		}
		
		public static Loc max() {
			return max;
		}
		
		private Loc loc;
		private char c;
		private int deltaX = 0;
		private int deltaY = 0;

		public Blizzard(int x, int y, char c) {
			this.loc = new Loc(x,y);
			this.c = c;
			
			switch (c) {
				case '<': deltaX = -1; break;
				case '>': deltaX = 1; break;
				case '^': deltaY = -1; break;
				case 'v': deltaY = 1; break;
				default: throw new IllegalArgumentException("Unknown Blizzard direction '" + c + "'");
			}
		}
		
		public Loc loc(int t) {
			return new Loc(Math.floorMod(loc.x() + deltaX * t, max.x()), 
						   Math.floorMod(loc.y() + deltaY * t, max.y()));
		}
		
		public String toString() {
			return String.valueOf(c);
		}
	}
}
