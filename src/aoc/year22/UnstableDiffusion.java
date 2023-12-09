package aoc.year22;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.Loc;
import aoc.util.PuzzleApp;

public class UnstableDiffusion extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 23: Unstable Diffusion");
		PuzzleApp app = new UnstableDiffusion();
		app.run();
	}
	
	public String filename() {
		return "data/data23";
	}

	Set<Elf> elves = new HashSet<>();
	Set<Loc> elfLocations = new HashSet<>();
	
	int x = 0, y = 0;
	int id = 1;
	
	public void parseLine(String line) {
		line.chars().forEach(c -> {
			if (c == '#') {
				Loc l = new Loc(x, y);
				elves.add(new Elf(id, l));
				elfLocations.add(l);
				id++;
			}
			x++; 
		});
		y++;
		x = 0;
	}
	
	private Direction proposedDirection = Direction.NORTH;
	
	public Set<Loc> adjacent(Elf elf, Direction d) {
		Set<Loc> s = null;
		int x = elf.x();
		int y = elf.y();
		
		switch (d) {
			case NORTH:
				s =  Set.of(new Loc(x - 1, y - 1), new Loc(x, y - 1), new Loc(x + 1, y - 1)); break;
			case SOUTH:
				s = Set.of(new Loc(x - 1, y + 1), new Loc(x, y + 1), new Loc(x + 1, y + 1)); break;
			case WEST:
				s = Set.of(new Loc(x - 1, y - 1), new Loc(x - 1, y), new Loc(x - 1, y + 1)); break;
			case EAST:
				s =  Set.of(new Loc(x + 1, y - 1), new Loc(x + 1, y), new Loc(x + 1, y + 1)); break;
			default:
				throw new IllegalArgumentException("Unknown direction '" + d + "'");
		}
		// System.out.println("Adjacent to " + elf + ": " + s);
		return s;
	}
	
	private boolean isAlone(Elf elf) {
		Set<Loc> allAdjacentLocations = new HashSet<>();
		allAdjacentLocations.addAll(adjacent(elf, Direction.NORTH));
		allAdjacentLocations.addAll(adjacent(elf, Direction.SOUTH));
		allAdjacentLocations.addAll(adjacent(elf, Direction.WEST));
		allAdjacentLocations.addAll(adjacent(elf, Direction.EAST));

		// System.out.println("All adjacent to " + elf + ": " + allAdjacentLocations);
		
		allAdjacentLocations.retainAll(elfLocations);
		
		// System.out.println("Occupied adjacent to " + elf + ": " + allAdjacentLocations);

		return allAdjacentLocations.isEmpty();
		
		// return elfLocations.stream()
		//	    .filter(adjacent(elf, Direction.NORTH)::contains)
		//	    .filter(adjacent(elf, Direction.SOUTH)::contains)
		//	    .filter(adjacent(elf, Direction.WEST)::contains)
		//	    .filter(adjacent(elf, Direction.EAST)::contains)
		//	    .findAny().isEmpty();
	}
	
	public boolean propose(Elf elf, Direction proposedDirection) {
		if (elfLocations.stream().filter(adjacent(elf, proposedDirection)::contains).findAny().isPresent()) {
			// System.out.println("Elf " + elf + " can't move " + proposedDirection);
			return false; // Can't move in the proposed direction
		} else {
			switch (proposedDirection) {
			case NORTH: elf.setProposedLocation( elf.currentLocation().adjacentLoc("N") ); break;
			case SOUTH: elf.setProposedLocation( elf.currentLocation().adjacentLoc("S") ); break;
			case WEST: elf.setProposedLocation( elf.currentLocation().adjacentLoc("W") ); break;
			case EAST: elf.setProposedLocation( elf.currentLocation().adjacentLoc("E") ); break;
			default: throw new IllegalArgumentException("Unknown direction '" + proposedDirection + "'");
			}
			// System.out.println("Elf " + elf + " moved " + proposedDirection);
			return true;
		}
	}

	void consider() {
		for (Elf elf : elves) {
			if (isAlone(elf)) {
				// System.out.println("Elf " + elf + " is alone");
				elf.setProposedLocation(elf.currentLocation());
			} else {
				boolean result = propose(elf, proposedDirection) 
					|| propose(elf, proposedDirection.next()) 
					|| propose(elf, proposedDirection.next().next()) 
					|| propose(elf, proposedDirection.next().next().next());
				
				if (!result) {
					// System.out.println("Elf " + elf + " cannot move");
					elf.setProposedLocation(elf.currentLocation());
				}
			}
		}
	}
	
	void resolveCollisions() {
		Map<Loc,List<Elf>> map = elves.stream()
		        .collect(Collectors.groupingBy(elf -> elf.proposedLocation()));
		
		map.forEach((k,v) -> {
			if( v.size() > 1 ) {
				// System.out.println("collision at " + k);
				v.forEach(elf -> elf.setProposedLocation(elf.currentLocation()));
			}
		});
	}
	
	void move() {
		elves.forEach(elf -> {
			// System.out.println("Moving elf from " + elf.currentLocation() + " to " + elf.proposedLocation());
			elf.setCurrentLocation(elf.proposedLocation());
		});
	}
	
	long round() {
		// System.out.println(elfMap());

		consider();
		resolveCollisions();
		
		long howManyMoved = elves.stream().filter(elf -> !elf.currentLocation().equals(elf.proposedLocation())).count();
		
		if (howManyMoved > 0) {
			move();
			proposedDirection = proposedDirection.next();
			elfLocations = elves.stream().map(Elf::currentLocation).collect(Collectors.toSet());
		}
	
		return howManyMoved;
	}
	
	int round;
	
	public void process() {
		for (round = 1; round <= 1000; round++) {
			long howManyMoved = round();
			System.out.println("Round " + round + ": " + howManyMoved);
			if (howManyMoved == 0) break;
		}
	}
	
	
	
	public void results() {
		// System.out.println("Final Map");
		// System.out.println(elfMap());
		System.out.println();
		System.out.println("Empty Tiles: " + countEmptyTiles());
		System.out.println();
		System.out.println("Number of Rounds: " + round);
	}
	
	private int countEmptyTiles() {
		int minY = elves.stream().mapToInt(Elf::y).min().getAsInt();
		int maxY = elves.stream().mapToInt(Elf::y).max().getAsInt();
		int minX = elves.stream().mapToInt(Elf::x).min().getAsInt();
		int maxX = elves.stream().mapToInt(Elf::x).max().getAsInt();
				
		int counter = 0;
		
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				if (!elfLocations.contains(new Loc(x,y))) {
					counter++;
				}
			}
		}
		return counter;
	}

	private String elfMap() {
		int minY = elves.stream().mapToInt(Elf::y).min().getAsInt();
		int maxY = elves.stream().mapToInt(Elf::y).max().getAsInt();
		int minX = elves.stream().mapToInt(Elf::x).min().getAsInt();
		int maxX = elves.stream().mapToInt(Elf::x).max().getAsInt();
		
		// Set<Loc> elfLocations = elves.stream().map(Elf::currentLocation).collect(Collectors.toSet());
		
		StringBuilder sb = new StringBuilder();
		sb.append('\n');

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				if (elfLocations.contains(new Loc(x,y))) {
					sb.append('#');
				} else {
					sb.append('.');
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	class Elf {
		int id;
		Loc currentLocation;
		Loc proposedLocation;
		
		public Elf(int id, Loc currentLocation) {
			this.id = id;
			this.currentLocation = currentLocation;
		}
		
		public int x() {
			return currentLocation.x();
		}
		
		public int y() {
			return currentLocation.y();
		}
		
		public Loc currentLocation() {
			return currentLocation;
		}

		public void setCurrentLocation(Loc currentLocation) {
			this.currentLocation = currentLocation;
		}

		public Loc proposedLocation() {
			return proposedLocation;
		}
		
		public void setProposedLocation(Loc proposedLocation) {
			this.proposedLocation = proposedLocation;
		}

		public Loc northLoc() {
			return currentLocation.adjacentLoc("N");
		}
		
		public String toString() {
			return currentLocation.toString();
		}
	}
	
	enum Direction {
		NORTH, SOUTH, WEST, EAST;
		
		public Direction next() {
			switch(this) {
			case NORTH: return SOUTH;
			case SOUTH: return WEST;
			case WEST: return EAST;
			case EAST: return NORTH;
			default: throw new IllegalArgumentException("Unknown direction '" + this + "'");
			}
		}
		
		public Stream<Direction> all() {
			return Stream.of(this, this.next(), this.next().next(), this.next().next().next());
		}
	}
}
