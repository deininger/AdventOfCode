package aoc.year23;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class Day20 extends PuzzleApp {
	private static final long PART_ONE_ITERATION_COUNT = 1000;
	private static final long PART_TWO_ITERATION_COUNT = 10000;

	public static final void main(String[] args) {
		System.out.println("December 20: Pulse Propagation");
		PuzzleApp app = new Day20();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day20-part1";
	}

	public void parseLine(String line) {
		String[] lineParts = line.split(" -> ");
		ModuleType moduleType = ModuleType.withSymbol(lineParts[0].charAt(0));
		String moduleName = lineParts[0].substring(1);
		if (moduleType == ModuleType.BROADCAST) moduleName = lineParts[0]; // Put the "b" back!
		String[] destinationModuleNames = lineParts[1].split(", ");

		/* Module m = */ new Module(moduleType, moduleName, destinationModuleNames);
		// System.out.println("Parsed Module " + m);
	}

	private Deque<Pulse> pulseQueue = new ArrayDeque<>();
	private long lowPulseCounter = 0;
	private long highPulseCounter = 0;

	private List<Module> generators = new ArrayList<>();
	private HashMap<Module,Long> counters = new HashMap<>();

	public void pressButtonAndProcess(Module button, long buttonPressCounter) {
		pulseQueue.addAll(button.process(null)); // Start by pushing the button
	
		while (!pulseQueue.isEmpty()) {
			Pulse p = pulseQueue.pop();
			// System.out.println("Processing pulse: " + p);

			if (generators.contains(p.source()) && p.type() == PulseType.HIGH) {
				// System.out.println("Recording HIGH pulse from " + p.source().name() + " at " + buttonPressCounter);
				counters.putIfAbsent(p.source(), buttonPressCounter);
			}

			if (buttonPressCounter <= PART_ONE_ITERATION_COUNT) {
				if (p.type() == PulseType.HIGH) {
					highPulseCounter++;
				} else {
					lowPulseCounter++;
				}
			}

			pulseQueue.addAll(p.destination().process(p));
		}
	}

	public void process() {
		// Create the button module:

		Module button = new Module(ModuleType.BUTTON, "button", new String[] {"broadcaster"});

		// Create any destination modules which weren't part of our input file.

		Set<String> allDestinations = new HashSet<>();
		Module.modules.values().forEach(m -> allDestinations.addAll(m.destinationNames()));

		// System.out.println("All Destinations: " + allDestinations);
		
		allDestinations.forEach(dn -> {
			Module m = Module.withName(dn);
			if (m == null) {
				Module terminal = new Module(ModuleType.TERMINAL, dn, new String[] {});
				System.out.println("Creating terminal module " + terminal.name());
				Module.add(terminal);
			}
		});

		// Also initialize the Conjunction modules so all their input memories start LOW.

		Module.modules.values().forEach(m -> {
			m.destinations().filter(d -> d.type() == ModuleType.CONJUNCTION).forEach(d -> {
				// System.out.println("Initializing " + d.name() + " with source " + m.name() + " LOW");
				d.initializeMemory(m, PulseType.LOW);
			});
		});

		// Find the series of Conjunction Modules that feeds the "rx" mdoule 
		// (we assume there's only one, because we looked at the data):

		Module m = Module.withName("rx");
		List<Module> sources = m.sources();

		while (sources.size() == 1 && sources.get(0).type() == ModuleType.CONJUNCTION) {
			// Work backward until we find something with more than one Conjunction source
			m = sources.get(0);
			sources = m.sources();
		}

		generators = m.sources();

		System.out.println("Generators of RX: " + generators);

		// Press the button 1000 times (for part 1), more for part 2:

		for (long i = 1; i < PART_TWO_ITERATION_COUNT; i++) {
			pressButtonAndProcess(button, i);
			// System.out.println("iteration " + i + " rx high = " + rxHighPulseCounter + " rx low = " + rxLowPulseCounter);
		}
	}

	public void results() {
		System.out.println("Part 1: Pulse Multiplier = " + (highPulseCounter * lowPulseCounter)); // 869395600

		System.out.println("Part 2: generatorCounts: " + counters.values() 
			+ ", product = " + counters.values().stream().reduce(1L, (x, y) -> x * y)); // 232605773145467
	}

	class Module {
		public static Map<String,Module> modules = new HashMap<>();

		private static Module withName(String name) {
			return modules.get(name);
		}

		private static void add(Module m) {
			modules.put(m.name(), m);
		}

		private ModuleType type;
		private String name;
		private List<String> destinationNames;

		private boolean flipFlopState = false;
		private Map<Module,PulseType> conjunctionMemory = new HashMap<>();

		public Module(ModuleType type, String name, String[] destinationNames) {
			this.type = type;
			this.name = name;
			this.destinationNames = Arrays.asList(destinationNames);
			add(this);
		}

		public String name() {
			return name;
		}

		public ModuleType type() {
			return type;
		}

		public List<String> destinationNames() {
			return destinationNames;
		}

		public Stream<Module> destinations() {
			return destinationNames.stream().map(n -> withName(n));
		}

		public List<Module> sources() {
			return modules.values().stream().filter(m -> m.destinationNames().contains(this.name())).collect(Collectors.toList());
		}

		public void initializeMemory(Module m, PulseType pt) {
			conjunctionMemory.putIfAbsent(m, pt);
		}

		public List<Pulse> process(Pulse p) {
			switch (this.type) {
				case BUTTON:
					return destinations().map(d -> new Pulse(PulseType.LOW, this, d)).collect(Collectors.toList());
				case BROADCAST:
					return destinations().map(d -> new Pulse(p.type(), this, d)).collect(Collectors.toList());
				case FLIPFLOP:
					if (p.type == PulseType.LOW) {
						flipFlopState = !flipFlopState;
						PulseType pulseType = flipFlopState ? PulseType.HIGH : PulseType.LOW;
						return destinations().map(d -> new Pulse(pulseType, this, d)).collect(Collectors.toList());
					} else {
						// System.out.println("Ignoring " + p.type().name() + " pulse to " + this.type.name() + " " + this.name() );
						break;
					}
				case CONJUNCTION:
					conjunctionMemory.put(p.source(), p.type());
					boolean allMemoryHigh = conjunctionMemory.values().stream().allMatch(pt -> pt == PulseType.HIGH);
					PulseType pulseType = allMemoryHigh ? PulseType.LOW : PulseType.HIGH;
					return destinations().map(d -> new Pulse(pulseType, this, d)).collect(Collectors.toList());
				case TERMINAL:
					// System.out.println("Ignoring " + p.type().name() + " pulse to " + this.type.name() + " " + this.name() );
					break;
			}

			return new ArrayList<>();
		}

		public String toString() {
			return type.name() + " " + name + " -> " + destinationNames;
		}
	}

	class Pulse {
		private PulseType type;
		private Module source;
		private Module destination;

		public Pulse(PulseType type, Module source, Module destination) {
			this.type = type;
			this.source = source;
			this.destination = destination;
		}

		public PulseType type() {
			return type;
		}

		public Module source() {
			return source;
		}

		public Module destination() {
			return destination;
		}

		public String toString() {
			return "Pulse " + source.name() + " -" + type.name() + "-> " + destination.name();
		}
	}

	enum ModuleType {
		BROADCAST('b'), FLIPFLOP('%'), CONJUNCTION('&'), BUTTON('*'), TERMINAL('!');

		public static ModuleType withSymbol(char symbol) {
			return Arrays.stream(ModuleType.values()).filter(t -> t.symbol == symbol).findFirst().orElse(null);
		}

		private char symbol;

		private ModuleType(char symbol) {
			this.symbol = symbol;
		}

		public String toString() {
			return Character.toString(symbol);
		}
	}

	enum PulseType {
		HIGH, LOW;
	}
}