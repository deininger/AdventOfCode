package aoc.year22;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class ProboscideaVolcanium extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 16: Proboscidea Volcanium");
		PuzzleApp app = new ProboscideaVolcanium();
		app.run();
	}
	
	public String filename() {
		return "data/data16small";
	}

	private static final String REGEX = "^Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (\\w+(, \\w+)*)";
	private static final Pattern PATTERN = Pattern.compile(REGEX);
	
	public void parseLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			Valve v = new Valve(matcher.group(1),Integer.parseInt(matcher.group(2)));
			String[] tunnelNames = matcher.group(3).split(", ");
			
			for (String tunnelName : tunnelNames) {
				v.addTunnel(tunnelName);
			}
		} else {
			throw new IllegalArgumentException( "Could not match pattern with line '" + line + "'");
		}
	}

	private static final int MAX_TIME = 52; // 30 for no helper, 52 for helper (26*2)
	private static final boolean HELPER = true; // true,false
	
	public void process() {
		Valve.allValves().forEach(Valve::computeAdjacentValves);
		
		State state = new State();
		StateResult result = step(state);
		
		// System.out.println("Result: " + result);
		System.out.println("Total Pressure Released: " + result.totalPressureReleased());

		// Part 1 answer: Total Pressure Released: 2119

	}
	
	public void results() {
		System.out.println("Cache contains "+ StateResult.cache.size() + " items");
	}
	
	private int cacheCounter = 1;
	
	StateResult step(State state) {
		if (state.depth >= MAX_TIME) {
			return null;
		}
		
		if (StateResult.cache.size() > cacheCounter) {
			System.out.println("Cache size " + StateResult.cache.size());
			cacheCounter *= 2;
		}
		
		StateResult result = StateResult.cache.get(state);

		if (result != null) {
			// Found a result in the cache!
			return result;
		}
		
		// Need to compute the total pressure released at this state...
		
		result = new StateResult(state);
		StateResult.cache.put(state, result);
						
		// System.out.println("== Minute " + timer + " (at " + state.current.name + ", " + released + ") ==");

		// System.out.println(" " + timer + " Released " + released);

		StateResult bestResult = null;

		if (state.allValvesOpen()) {
			// Nothing left to do but wait
			if (HELPER) {
				bestResult = step(state.tick().tick());
			} else {
				bestResult = step(state.tick());
			}
			// result.setPreviousResult(bestResult);
			return result;
		}
		
		// Two things we can try:
		// (1) Open this valve (might already be open)
		// (2) Move to another valve
		// no point in closing any valves...

		// With a helper, there are 4 things to try:
		// (1) We both open valves
		// (2) I move, helper opens a valve
		// (3) Helper moves, I open a valve,
		// (4) We both move,
		

		if (HELPER) {
			if (!state.valve.equals(state.helperValve) && state.valve.flowRate() > 0 && state.helperValve.flowRate() > 0) {
				// We both open valves:
				StateResult sr = step(state.openCurrentValve().openCurrentHelperValve());

				if (bestResult == null || (sr.totalPressureReleased() > bestResult.totalPressureReleased())) {
					bestResult = sr;
				}
			}
				
			if (state.helperValve.flowRate() > 0) {
				// I move, Helper opens valve:
				List<Valve> adjacentValves = state.valve.adjacentValves();
				
				for (Valve v : adjacentValves) {
					StateResult sr = step(state.openCurrentHelperValve().moveTo(v));

					if (bestResult == null || (sr.totalPressureReleased() > bestResult.totalPressureReleased())) {
						bestResult = sr;
					}
				}
			}
			
			if (state.valve.flowRate() > 0) {
				// I open valve, Helper moves:
				List<Valve> adjacentHelperValves = state.helperValve.adjacentValves();
				
				for (Valve v : adjacentHelperValves) {
					StateResult sr = step(state.openCurrentValve().moveHelperTo(v));

					if (bestResult == null || (sr.totalPressureReleased() > bestResult.totalPressureReleased())) {
						bestResult = sr;
					}
				}
			}
			
			// We both move:
			List<Valve> adjacentValves = state.valve.adjacentValves();
			for (Valve v : adjacentValves) {
				List<Valve> adjacentHelperValves = state.helperValve.adjacentValves();
				for (Valve hv : adjacentHelperValves) {
					StateResult sr = step(state.moveTo(v).moveHelperTo(hv));

					if (bestResult == null || (sr.totalPressureReleased() > bestResult.totalPressureReleased())) {
						bestResult = sr;
					}
				}
			}
		} else {
			// It's jut me, no helper
			
			if (state.valve.flowRate() > 0) {
				// System.out.println(" " + timer + " Opening valve " + valve);
				bestResult = step(state.openCurrentValve());
			}

			List<Valve> adjacentValves = state.valve.adjacentValves();

			for (Valve v : adjacentValves) {
				// System.out.println(" " + timer + " Moving to: " + v);
				StateResult sr = step(state.moveTo(v));

				if (bestResult == null || (sr.totalPressureReleased() > bestResult.totalPressureReleased())) {
					bestResult = sr;
				}
			}
		}
		
		// result.setPreviousResult(bestResult);
		
		if (bestResult == null) {
			result.setTotalPressureReleased(result.pressureReleased());
		} else {
			result.setTotalPressureReleased(result.pressureReleased() + bestResult.totalPressureReleased());
		}
		
		return result;
	}

	class StateResult {		
		public static Map<State,StateResult> cache = new HashMap<>();
		
		public State state;
		public int pressureReleased;
		public int totalPressureReleased;
		// public StateResult previousResult;
		
		public StateResult(State state) {
			this.state = state;
			this.pressureReleased = evaluatePressureReleased();
		}
		
		public int evaluatePressureReleased() {
			return state.allOpenValves().collect(Collectors.summingInt(Valve::flowRate));
		}
		
		public int pressureReleased() {
			return pressureReleased;
		}
		
		public void setTotalPressureReleased(int totalPressureReleased) {
			this.totalPressureReleased = totalPressureReleased;
		}
		
		public int totalPressureReleased() {
			return totalPressureReleased;
		}
		
		// public void setPreviousResult(StateResult previousResult) {
		// 	this.previousResult = previousResult;
		// }

		public String toString() {
		//	if (previousResult != null) {
		//		return state + " " + pressureReleased + " -> " + previousResult.toString();
		//	} else {
				return state + " " + pressureReleased;
		//	}
		}
	}
	
	class State {
		public Map<Valve,Boolean> valveStates;
		public Valve valve;
		public Valve helperValve;
		public int depth;
		
		public State() {
			valveStates = new HashMap<>();
			Valve.allValves().forEach(v -> valveStates.put(v,Boolean.FALSE));
			valve = Valve.lookup("AA");
			helperValve = Valve.lookup("AA");
			depth = 0;
		}
		
		public State(State s) {
			this.valveStates = new HashMap<>(s.valveStates);
			this.valve = s.valve;
			this.helperValve = s.helperValve;
			this.depth = s.depth + 1;
		}
				
		public State tick() {
			State s = new State(this);
			return s;
		}

		public State moveTo(Valve v) {
			State s = new State(this);
			s.valve = v;
			return s;
		}

		public State moveHelperTo(Valve v) {
			State s = new State(this);
			s.helperValve = v;
			return s;
		}

		public State openCurrentValve() {
			State s = new State(this);
			s.valveStates.put(valve, Boolean.TRUE);
			return s;
		}

		public State openCurrentHelperValve() {
			State s = new State(this);
			s.valveStates.put(helperValve, Boolean.TRUE);
			return s;
		}
		
		public Stream<Valve> allOpenValves() {
			return valveStates.entrySet().stream().filter(Entry::getValue).map(Entry::getKey);
		}
		
		public boolean allValvesOpen() {
			return valveStates.values().stream().allMatch(b -> b);
		}
		
		public int hashCode() {
			return depth + valveStates.hashCode() + valve.hashCode() + helperValve.hashCode();
		}
		
		public boolean equals(Object other) {
			if (other instanceof State) {
				State s = (State) other;
				return (this.depth == s.depth) 
						&& this.valve.equals(s.valve) 
						&& this.helperValve.equals(s.helperValve) 
						&& this.valveStates.equals(s.valveStates);
			}
			return false;
		}
		
		public String toString() {
			return "[" + depth + "](" + valve + "," + helperValve + ")";
		}
	}
	
	class Valve {
		private static Map<String,Valve> valves = new HashMap<>();

		public static Stream<Valve> allValves() {
			return valves.values().parallelStream();
		}
		
		public static int numValves() {
			return valves.size();
		}
		
		private static void add(Valve v) {
			valves.put(v.name, v);
		}
		
		private static Valve lookup(String name) {
			return valves.get(name);
		}
		
		private String name;
		private Integer flowRate;
		private Set<String> tunnelNames = new HashSet<>();
		private List<Valve> adjacentValves;
		
		public Valve(String name, Integer flowRate) {
			this.name = name;
			this.flowRate = flowRate;
			add(this);
		}
		
		public int flowRate() {
			return flowRate;
		}
		
		public void addTunnel(String tunnelName) {
			tunnelNames.add(tunnelName);
		}
		
		public void computeAdjacentValves() {
			adjacentValves = tunnelNames.stream().map(Valve::lookup).toList();
		}

		public List<Valve> adjacentValves() {
			return adjacentValves;
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
}
