package aoc.year22;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import aoc.util.PuzzleApp;

public class ProboscideaVolcanium2 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 16: Proboscidea Volcanium (take 2)");
		PuzzleApp app = new ProboscideaVolcanium2();
		app.run();
	}
	
	public String filename() {
		return "data/data16";
	}

	private final TunnelMap tunnelMap = new TunnelMap();

	private static final String START = "AA";
	
	private static final String REGEX = "^Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (\\w+(, \\w+)*)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	public void parseLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			tunnelMap.addValve(new Valve(matcher.group(1), Integer.parseInt(matcher.group(2))));
			for (String tunnel: matcher.group(3).split(", ")) {
				tunnelMap.addTunnel(new Tunnel(matcher.group(1), tunnel, 1));
			}
		} else {
			throw new IllegalArgumentException( "Could not match pattern with line '" + line + "'");
		}
	}
	
	
	public void process() {
		tunnelMap.calculateDistances();
				
		int MAX_TIME = 30;
		
		// Start with initial valve open, which we can do since it has a 0 flow rate.
		// This will prevent the recursion from revisiting the starting valve needlessly.

		int openedValves = 1 << tunnelMap.indexOf(START); 
		
		// Keep track of all the best values for all the varieties of valve-opening:
		int[] memo = new int[1 << tunnelMap.numValves()];
		
		int best = tunnelMap.calculateFlow(0, MAX_TIME, tunnelMap.indexOf(START), openedValves, 0, 0, 0, memo);
				
		System.out.println("Total Pressure Released: " + best);

        int best2 = Arrays.stream(memo).max().orElseThrow();
        System.out.println("Best value from memo: " + best2);
		
		
		// Part 1 answer: Total Pressure Released: 2119 (real data)
		// Part 1 answer: 1651 (small dataset)
                
        
        MAX_TIME = 26;
		openedValves = 1 << tunnelMap.indexOf(START); 
		for (int i = 0; i < ((1 << tunnelMap.numValves())-1); i++) {
			memo[i] = 0;
		}
		
		best = tunnelMap.calculateFlow(0, MAX_TIME, tunnelMap.indexOf(START), openedValves, 0, 0, 0, memo);
        
		System.out.println("Total Pressure Released (for part 2, one actor): " + best);

        int[][] condensedMemo = IntStream.range(0, (1 << tunnelMap.numValves()))
                .filter(i -> memo[i] > 0)
                .mapToObj(i -> new int[]{i, memo[i]})
                .sorted((a, b) -> Integer.compare(b[1], a[1]))
                .toArray(int[][]::new);

        int startingValveBitmask = 1 << tunnelMap.indexOf(START);
        
        for (int i = 0; i < condensedMemo.length; i++) {
            if (condensedMemo[i][1] * 2 < best) {
                break; // Since the data is sorted, no second number will be big enough
            }
            
            for (var j = i + 1; j < condensedMemo.length; j++) {
                if ((condensedMemo[i][0] & condensedMemo[j][0]) == startingValveBitmask) {
                    if (condensedMemo[i][1] + condensedMemo[j][1] > best) {
                    	System.out.println("New best pair: " + condensedMemo[i][1] + " + " + condensedMemo[j][1] + " at " + Integer.toBinaryString(condensedMemo[i][0]) + ", " + Integer.toBinaryString(condensedMemo[j][0]));
                    	best = condensedMemo[i][1] + condensedMemo[j][1];
                    	break;
                    }
                }
            }
        }

        System.out.println("Part 2 best total pressure released: " + best);
        
        // Part 2 answer: 2615 (1398 + 1217)
	}
	
	public void results() {
	}
	
	class TunnelMap {
		private Map<String,Valve> valves = new HashMap<>();
		private Set<Tunnel> tunnels = new HashSet<>();
				
		private List<Valve> sortedValves;
		private int[][] valveDistances;
		private int allValvesOpen; // all bits set to 1 for all valves

		public TunnelMap() {
		}
		
		public void addValve(Valve v) {
			valves.put(v.name(), v);
		}
		
		public void addTunnel(Tunnel tunnel) {
			tunnels.add(tunnel);
		}
		
		public void calculateDistances() {
			Map<Pair<String,String>,Integer> distances = new HashMap<>();
			
			// Set all valves to maximum distance apart:
			for (Valve v1: valves.values()) {
				for (Valve v2: valves.values()) {
					distances.put(Pair.of(v1.name(), v2.name()), valves.size()+1);
				}
			}
			
			// Capture all valves which are adjacent from the map data:
			for (Valve v: valves.values()) {
				distances.put(Pair.of(v.name(), v.name()), 0);
				for (Tunnel tunnel : tunnels) {
					distances.put(Pair.of(valves.get(tunnel.from()).name(), valves.get(tunnel.to()).name()), tunnel.distance());
					distances.put(Pair.of(valves.get(tunnel.to()).name(), valves.get(tunnel.from()).name()), tunnel.distance());
				}
			}
			
			// Use Floyd-Warshall to calculate the shortest distance between all pairs of valves:
			for (Valve k: valves.values()) {
				for (Valve i: valves.values()) {
					for (Valve j: valves.values()) {
						int d = distances.get(Pair.of(i.name(), k.name())) + distances.get(Pair.of(k.name(), j.name()));
						if (distances.get(Pair.of(i.name(), j.name())) > d) {
							distances.put(Pair.of(i.name(), j.name()), d);
						}
					}
				}
			}
			
			// Create a collection of Valves and sort it by flow rate descending:
			sortedValves = new ArrayList<>(valves.values());

			// Remove any Valves which have 0 flow rate:
			sortedValves.removeIf(v -> v.flowRate == 0 && !START.equals(v.name));

			sortedValves.sort((v1,v2) -> v2.flowRate - v1.flowRate);
						
			// Set up the matrix of distances between valves:
			valveDistances = new int[numValves()][numValves()];
			
			for (Valve v1: sortedValves) {
				for (Valve v2: sortedValves) {
					valveDistances[sortedValves.indexOf(v1)][sortedValves.indexOf(v2)] = distances.get(Pair.of(v1.name(), v2.name()));
				}
			}
			
			// Set the "all valves open" bitmask:
			for (int i = 0; i < numValves(); i++) {
				allValvesOpen += 1 << i;
			}			
		}
		
		public final int distanceBetween(int v1, int v2) {
			return valveDistances[v1][v2];
		}
		
		public final int numValves() {
			return sortedValves.size();
		}
		
		public final int indexOf(String valveName) {
			return sortedValves.indexOf(valves.get(valveName));
		}

		public int calculateFlow(int minute, int maxTime, int current, int openedValves, int flowRate, int accumulatedFlow, int depth, int[] memo) {
			// We have two options: (1) move to another valve and open it, or 
			// (2) stay put for the rest of the minutes. 
			// We only do #2 if there are no more valves to open, 
			// or we cannot reach any more in the remaining time.
			
			int best = accumulatedFlow + flowRate * (maxTime - minute);

            memo[openedValves] = Math.max(memo[openedValves], best);

			if (allValvesOpen == openedValves) {
				return best;
			}				
			
			for (int i = 0; i < numValves(); i++) {
				if ((openedValves & (1 << i)) == 0 
						&& (minute + distanceBetween(current, i) + 1 < maxTime)) {

					// The "+1" in the minute increment is the additional time it takes to open the
					// destination valve.
					//
					// FlowRate is the flow rate once we get there and the valve is opened.
					//
					// AccumulatedFlow includes the increase due to the time spent traveling & opening.
					
/*					System.out.println(depth + " Moving from " + sortedValves.get(current) 
						+ " to " + sortedValves.get(i) 
						+ " at distance " + distanceBetween(current, i)
						+ " (time " + minute + ", flow rate " + flowRate + "->" + (flowRate + sortedValves.get(i).flowRate()) 
						+ ", total flow " + accumulatedFlow + "->" + (accumulatedFlow + flowRate * (distanceBetween(current, i) + 1))
						+ ", result if we stop here = " + best + ")");
*/
					int flow = calculateFlow(minute + distanceBetween(current, i) + 1,
							maxTime,
							i,
							openedValves | (1 << i), 
							flowRate + sortedValves.get(i).flowRate(),
							accumulatedFlow + flowRate * (distanceBetween(current, i) + 1),
							depth + 1,
							memo);

					if (flow > best) {
						best = flow;
					}
				}
			}
			
			return best;
		}
	}
	
	class Valve {
		private String name;
		private Integer flowRate;
		
		public Valve(String name, Integer flowRate) {
			this.name = name;
			this.flowRate = flowRate;
		}
		
		public String name() {
			return name;
		}
		
		public int flowRate() {
			return flowRate;
		}
		
		public String toString() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode();
		}
		
		public boolean equals(Object other) {
			return other instanceof Valve v && v.name.equals(name);
		}
	}
	
	class Tunnel {
		private String from;
		private String to;
		private int distance;
		
		public Tunnel(String from, String to, int distance) {
			this.from = from;
			this.to = to;
			this.distance = distance;
		}
		
		public String from() {
			return from;
		}
		
		public String to() {
			return to;
		}
		
		public int distance() {
			return distance;
		}
	}
}
