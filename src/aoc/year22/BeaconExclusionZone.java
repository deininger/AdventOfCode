package aoc.year22;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class BeaconExclusionZone extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 15: Beacon Exclusion Zone");
		PuzzleApp app = new BeaconExclusionZone();
		app.run();
	}

	public String filename() {
		return "data/data15";
	}
	
	public void setup() {
	}

	private static final String REGEX = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	private Set<Sensor> sensors = new HashSet<>();
	
	void processLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			// System.out.println("Matcher found " + matcher.groupCount() + " groups");
			int sensorX = Integer.parseInt(matcher.group(1));
			int sensorY = Integer.parseInt(matcher.group(2));
			Loc sensorLoc = new Loc(sensorX, sensorY);
			int beaconX = Integer.parseInt(matcher.group(3));
			int beaconY = Integer.parseInt(matcher.group(4));
			Loc beaconLoc = new Loc(beaconX, beaconY);
			
			Sensor sensor = new Sensor(sensorLoc, beaconLoc);
			sensors.add(sensor);
			
			// System.out.println(sensor);
		} else {
			throw new IllegalArgumentException( "Could not match pattern with line '" + line + "'");
		}
	}

	private static final int SCAN_ROW = 2000000; // 10
	private int partOneCounter = 0;

	public void process() {
		Integer leftmostSensorRange = sensors.stream().map(s -> s.loc().x() - s.radius()).sorted().findFirst().get();

		Integer rightmostSensorRange = sensors.stream().map(s -> s.loc().x() + s.radius()).sorted(Comparator.reverseOrder()).findFirst().get();

		System.out.println("Starting at " + leftmostSensorRange + " going to " + rightmostSensorRange);
		
		for (int x = leftmostSensorRange; x <= rightmostSensorRange; x++) {
			Loc l = new Loc(x, SCAN_ROW);
			
			boolean hasBeacon = false;
			for(Sensor sensor: sensors) {
				if(sensor.beacon().equals(l)) {
					hasBeacon = true;
				}
			}

			boolean possibleBeacon = true;
			for(Sensor sensor: sensors) {
				if (sensor.isWithinRadius(l)) {
					possibleBeacon = false;
				}
			}

			if (hasBeacon) {
				// System.out.print('B');
			} else {
				if (possibleBeacon) {
					// System.out.print('.');
				} else {
					partOneCounter++;
					// System.out.print("#");
				}
			}
		}
		
		System.out.println();
	}
	
	private static final Loc SEARCH_SPACE_SIZE = new Loc(4000000,4000000); // 20;
	
	public void results() {
		System.out.println("Part One result: " + partOneCounter); // 4883971

		System.out.println("Scaning space between " + Loc.ORIGIN + " and " + SEARCH_SPACE_SIZE);

		sensors.parallelStream().flatMap(Sensor::edges).forEach(l -> {
			if (l.within(SEARCH_SPACE_SIZE)) {
				// System.out.println("Examining " + l);

				boolean solution = true;
				
				for (Sensor sensor : sensors) {
					if (sensor.isWithinRadius(l)) {
						solution = false;
						break; // can't be the solution
					}
				}
				
				if (solution) {
					System.out.println("Found solution! " + l);
					System.out.println("Part Two Result: " + ((long)l.x()*(long)SEARCH_SPACE_SIZE.x()+(long)l.y()));

					// (3172756,2767556)  12691024000000
					
//					for (Sensor sensor : sensors) {
//						System.out.println(l + " is distance " + sensor.loc().manhattanDistance(l) + " from Sensor " + sensor + " (" + (sensor.loc().manhattanDistance(l) - sensor.radius()) + ")" );
//					}
					
					System.out.println();
				}
			}
		});
		
		System.out.println("done.");
		
//		for (int x = 0; x < SEARCH_SPACE_SIZE.x(); x++) {
//			for (int y = 0; y < SEARCH_SPACE_SIZE.y(); y++) {
//				Loc l = new Loc(x,y);
//				boolean solution = true;
//				for (Sensor sensor: sensors) {
//					if (sensor.isWithinRadius(l)) {
//						solution = false;
//						break; // can't be the solution
//					}
//				}
//				if (solution) {
//					System.out.println("Found solution! " + l);
//				}
//			}
//			if (x % (SEARCH_SPACE_SIZE / 100) == 0) { System.out.print("."); }
//		}
	}

	class Sensor {
		private Loc sensorLoc;
		private Loc closestBeaconLoc;
		private int radius;
		
		public Sensor(Loc sensor, Loc beacon) {
			this.sensorLoc = sensor;
			this.closestBeaconLoc = beacon;
			this.radius = sensorLoc.manhattanDistance(closestBeaconLoc);
		}
		
		public Loc loc() {
			return sensorLoc;
		}
		
		public Loc beacon() {
			return closestBeaconLoc;
		}
		
		public int radius() {
			return radius;
		}
		
		public boolean isWithinRadius(Loc l) {
			return sensorLoc.manhattanDistance(l) <= radius;
		}
		
		public Stream<Loc> edges() {
			return sensorLoc.adjacent(radius+1);
		}
		
		public String toString() {
			return sensorLoc + " with radius " + radius;
		}
	}
}
