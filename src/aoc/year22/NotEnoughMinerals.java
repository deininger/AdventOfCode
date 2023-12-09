package aoc.year22;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class NotEnoughMinerals extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 19: Not Enough Minerals");
		PuzzleApp app = new NotEnoughMinerals();
		app.run();
	}
	
	public String filename() {
		return "data/data19part2";
	}

	private static final String REGEX = "^Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	private Map<Integer,Blueprint> blueprints = new HashMap<>();
	
	public void parseLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			Blueprint blueprint = new Blueprint(Integer.parseInt(matcher.group(1)));
			
			blueprint.setOreRobotCost(Integer.parseInt(matcher.group(2)))
				.setClayRobotCost(Integer.parseInt(matcher.group(3)))
				.setObsidianRobotCost(Integer.parseInt(matcher.group(4)), 
						Integer.parseInt(matcher.group(5)))
				.setGeodeRobotCost(Integer.parseInt(matcher.group(6)), 
						Integer.parseInt(matcher.group(7)));
			
			blueprints.put(blueprint.id(), blueprint);
		}
	}
	
	private static final int TOTAL_MINUTES = 32; 
	// 24;

	public void setup() {
	}
	
	private List<Integer> results = new ArrayList<>();
	
	public void process() {
		int totalQuality = 0;
		
		for (Blueprint blueprint : blueprints.values()) {
			mostGeodesAtEachMinute.clear();
	
			State state = new State(blueprint);
			State finalState = tick(state);
			
			results.add(finalState.resource(Resource.GEODE));
			int quality = blueprint.id() * finalState.resource(Resource.GEODE);

			System.out.println("Blueprint " + blueprint.id() + " has quality " + quality + ", final state: " + finalState);
			
			totalQuality += quality;
		}
		
		System.out.println("Part 1: total quality = " + totalQuality);
		System.out.println("Part 2: first three blueprints = " + results + " = " + results.stream().collect(Collectors.summingInt(Integer::intValue)));
	}
		

	State tickDFS(State startingState) {
		Queue<int[]> queue = new ArrayDeque<>();
		queue.add(startingState.toIntArray());
		State bestStateSoFar = startingState;
		State mostGeodeRobotsSoFar = startingState;
		int maxMinute = 0;

		while (!queue.isEmpty()) {
			State current = new State(queue.remove());
						
//			if (current.robot(Resource.GEODE) < mostGeodeRobotsSoFar.robot(Resource.GEODE)) {
//				break;
//			}

			if (current.minute() > maxMinute) {
				maxMinute = current.minute();
				System.out.println("Minute " + maxMinute + " (queue depth " + queue.size() + ")");
			}
			
			current.generateResourcesFromRobots();

			if (current.resource(Resource.GEODE) > bestStateSoFar.resource(Resource.GEODE)
					|| ( current.resource(Resource.GEODE) == bestStateSoFar.resource(Resource.GEODE) 
					&& current.minute() > bestStateSoFar.minute())) {
				// System.out.println(current);
				bestStateSoFar = current;
			}
			
			if (current.minute() < TOTAL_MINUTES) {
				current.buildableRobots().parallel().forEach(r -> {
					// Don't build anything except a geode robot on the penultimate tick!
					if (current.minute() < TOTAL_MINUTES - 1 || r.equals(Resource.GEODE)) {
						State s = new State(current);
						s.buildRobot(r);
						queue.add(s.toIntArray());
					}
				});
			}
			
			if ((current.robot(Resource.GEODE)) >= mostGeodeRobotsSoFar.robot((Resource.GEODE))
					&& current.minute() >= mostGeodeRobotsSoFar.minute()) {
				mostGeodeRobotsSoFar = current;
			}
		}
		
		return bestStateSoFar;
	}
	
	private Map<Integer,State> mostGeodesAtEachMinute = new ConcurrentHashMap<>();
	
	State tick(State state) {
		if (state.minute() == 1) {
			mostGeodesAtEachMinute.clear();
			System.out.println("Starting " + state.blueprint());
		}
		
		if (mostGeodesAtEachMinute.get(state.minute()) == null) {
			mostGeodesAtEachMinute.put(state.minute(), state);
			// System.out.println("Most Geode Robots at " + state.minute() + " is now " + state.robot(Resource.GEODE));
		}
		
		if (state.robot(Resource.GEODE) > mostGeodesAtEachMinute.get(state.minute()).robot(Resource.GEODE)) {
			mostGeodesAtEachMinute.put(state.minute(), state);
			// System.out.println("Most Geode Robots at " + state.minute() + " is now " + state.robot(Resource.GEODE));
		}

		// Attempted pruning:
		if (state.robot(Resource.GEODE) 
				< mostGeodesAtEachMinute.get(state.minute()).robot(Resource.GEODE) - 1) {
			return state;
		}
		
		if (state.minute() == TOTAL_MINUTES) {
			// no point in creating any more robots in the last minute!
			state.generateResourcesFromRobots();
			return state;
		}

		// Each minute, the robot factory can produce 1 robot if we have sufficient resources
		// We need to try building each possible type of robot (or none at all) each minute,
		// looking for the maximum geode result;
		
		List<State> results = state.buildableRobots().map(r -> {
			State s = new State(state);
			s.generateResourcesFromRobots();
			s.buildRobot(r);
			return tick(s);
		}).collect(Collectors.toList());
		
		results.sort((s1, s2) -> s2.resource(Resource.GEODE) - s1.resource(Resource.GEODE));
		
//		if ( results.get(0).resource(Resource.GEODE) > 0 ) {
//			System.out.println("Returning state with " + results.get(0).resource(Resource.GEODE) + " geode(s)");
//		}
		
		return results.get(0);
	}
	
	public void results() {
	}
	
	class State {
		private Blueprint blueprint;
		private int minute;
		private EnumMap<Resource,Integer> resources;
		private EnumMap<Resource,Integer> robots;

		public State(Blueprint blueprint) {
			this.blueprint = blueprint;
			
			minute = 1;
			
			this.resources = new EnumMap<>(Resource.class);
			resources.put(Resource.ORE, 0);
			resources.put(Resource.CLAY, 0);
			resources.put(Resource.OBSIDIAN, 0);
			resources.put(Resource.GEODE, 0);
			
			this.robots = new EnumMap<>(Resource.class);
			robots.put(Resource.ORE, 1); // We start with one ore-collecting robot
			robots.put(Resource.CLAY, 0);
			robots.put(Resource.OBSIDIAN, 0);
			robots.put(Resource.GEODE, 0);
		}
		
		public State(State s) {
			this.blueprint = s.blueprint;
			this.minute = s.minute + 1;
			this.resources = s.resources.clone();
			this.robots = s.robots.clone();
		}

		public State(int[] intArray) {
			this.minute = intArray[0];
			this.blueprint = blueprints.get(intArray[1]);
			this.resources = new EnumMap<>(Resource.class);
			resources.put(Resource.ORE, intArray[2]);
			resources.put(Resource.CLAY, intArray[3]);
			resources.put(Resource.OBSIDIAN, intArray[4]);
			resources.put(Resource.GEODE, intArray[5]);
			this.robots = new EnumMap<>(Resource.class);
			robots.put(Resource.ORE, intArray[6]);
			robots.put(Resource.CLAY, intArray[7]);
			robots.put(Resource.OBSIDIAN, intArray[8]);
			robots.put(Resource.GEODE, intArray[9]);
		}
		
		public int[] toIntArray() {
			int[] intArray = new int[10];
			intArray[0] = minute;
			intArray[1] = blueprint.id();
			intArray[2] = resources.get(Resource.ORE);
			intArray[3] = resources.get(Resource.CLAY);
			intArray[4] = resources.get(Resource.OBSIDIAN);
			intArray[5] = resources.get(Resource.GEODE);
			intArray[6] = robots.get(Resource.ORE);
			intArray[7] = robots.get(Resource.CLAY);
			intArray[8] = robots.get(Resource.OBSIDIAN);
			intArray[9] = robots.get(Resource.GEODE);
			return intArray;
		}
		
		public Blueprint blueprint() {
			return blueprint;
		}
		
		public int minute() {
			return minute;
		}
		
		public int resource(Resource r) {
			return resources.get(r);
		}
		
		public int robot(Resource r) {
			return robots.get(r);
		}

		public Map<Resource,Integer> resources() {
			return resources;
		}
		
		public Map<Resource,Integer> robots() {
			return robots;
		}
		
		public void generateResourcesFromRobots() {
			robots.entrySet().forEach(e -> 
				resources.put(e.getKey(), resources.get(e.getKey()) + e.getValue()));
		}
		
		public Stream<Resource> buildableRobots() {			
			return Resource.stream().filter(resource -> blueprint.canBuild(resource, resources, robots));			
		}
		
		public void buildRobot(Resource robot) {
			if (robot != Resource.NONE) {
				blueprint.buildRobot(robot, resources);
				robots.put(robot, robots.get(robot) + 1);
			}
		}
		
		public String toString() {
			return "[" + minute + "] Resources: " + resources() + " Robots: " + robots();
		}
	}
	
	class Blueprint {
		private static final int MAX_ROBOTS = 12; // per type
		
		int id;
		int oreRobotOreCost;
		int clayRobotOreCost;
		int obsidianRobotOreCost;
		int obsidianRobotClayCost;
		int geodeRobotOreCost;
		int geodeRobotObsidianCost;
				
		public Blueprint(int id) {
			this.id = id;
		}
		
		public int id() {
			return id;
		}
		
		Blueprint setOreRobotCost(int oreRobotOreCost) {
			this.oreRobotOreCost = oreRobotOreCost;
			return this;
		}
		
		Blueprint setClayRobotCost(int clayRobotOreCost) {
			this.clayRobotOreCost = clayRobotOreCost;
			return this;
		}
		
		Blueprint setObsidianRobotCost(int obsidianRobotOreCost, int obsidianRobotClayCost) {
			this.obsidianRobotOreCost = obsidianRobotOreCost;
			this.obsidianRobotClayCost = obsidianRobotClayCost;
			return this;
		}
		
		Blueprint setGeodeRobotCost(int geodeRobotOreCost, int geodeRobotObsidianCost) {
			this.geodeRobotOreCost = geodeRobotOreCost;
			this.geodeRobotObsidianCost = geodeRobotObsidianCost;
			return this;
		}
		
		/*
		 * The maximum is the smaller of either the hardcoded arbitrary MAX_ROBOTS 
		 * or the largest cost of the resource produced by this robot in this blueprint.
		 */
		private int maxRobots(Resource robot) {
			switch(robot) {
			case ORE:
				return Math.min(MAX_ROBOTS, Math.max(Math.max(oreRobotOreCost, clayRobotOreCost), Math.max(obsidianRobotOreCost, geodeRobotOreCost)));
			case CLAY:
				return Math.min(MAX_ROBOTS, obsidianRobotClayCost);
			case OBSIDIAN:
				return Math.min(MAX_ROBOTS, geodeRobotObsidianCost);
			case GEODE:
				return MAX_ROBOTS;
			default:
				throw new IllegalArgumentException("Unknown robot " + robot);
			}
		}
		
		boolean canBuild(Resource robot, Map<Resource,Integer> resources, Map<Resource,Integer> robots) {			
			switch(robot) {
				case ORE:
					return resources.get(Resource.ORE) >= oreRobotOreCost
						&& robots.get(robot) < maxRobots(robot);
				case CLAY:
					return resources.get(Resource.ORE) >= clayRobotOreCost
						&& robots.get(robot) < maxRobots(robot);
				case OBSIDIAN:
					return resources.get(Resource.ORE) >= obsidianRobotOreCost
						&& resources.get(Resource.CLAY) >= obsidianRobotClayCost
						&& robots.get(robot) < maxRobots(robot);
				case GEODE:
					return resources.get(Resource.ORE) >= geodeRobotOreCost
						&& resources.get(Resource.OBSIDIAN) >= geodeRobotObsidianCost
						&& robots.get(robot) < maxRobots(robot);
				case NONE:
					return true;
				default:
					throw new IllegalArgumentException("Unknown robot " + robot);
			}
		}

		void buildRobot(Resource robot, Map<Resource,Integer> resources) {			
			switch(robot) {
				case ORE:
					resources.put(Resource.ORE, resources.get(Resource.ORE) - oreRobotOreCost);
					break;
				case CLAY:
					resources.put(Resource.ORE, resources.get(Resource.ORE) - clayRobotOreCost);
					break;
				case OBSIDIAN:
					resources.put(Resource.ORE, resources.get(Resource.ORE) - obsidianRobotOreCost);
					resources.put(Resource.CLAY, resources.get(Resource.CLAY) - obsidianRobotClayCost);
					break;
				case GEODE:
					resources.put(Resource.ORE, resources.get(Resource.ORE) - geodeRobotOreCost);
					resources.put(Resource.OBSIDIAN, resources.get(Resource.OBSIDIAN) - geodeRobotObsidianCost);
					break;
				case NONE:
					break;
				default:
					throw new IllegalArgumentException("Unknown robot " + robot);
			}
		}

		public String toString() {
			return "Blueprint " + id + ": Ore (" + oreRobotOreCost 
					+ "), Clay (" + clayRobotOreCost 
					+ "), Obsidian (" + obsidianRobotOreCost + "," + obsidianRobotClayCost
					+ "), Geode (" + geodeRobotOreCost + "," + geodeRobotObsidianCost + ")";
		}
	}
	
	enum Resource { 
		GEODE, OBSIDIAN, CLAY, ORE, NONE;
		
		public static Stream<Resource> stream() {
			return Stream.of(Resource.values()); 
	    }
	}
}
