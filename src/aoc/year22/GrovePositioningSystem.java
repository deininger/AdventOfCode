package aoc.year22;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class GrovePositioningSystem extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 20: Grove Positioning System");
		PuzzleApp app = new GrovePositioningSystem();
		app.run();
	}
	
	public String filename() {
		return "data/data20";
	}

	private static final long DECRYPTION_KEY = 811589153;
	private static final int ITERATIONS = 10;
	
	private Ring ring = new Ring();
	private RingNumber zero;
	int counter = 0;
	
	public void parseLine(String line) {
		RingNumber rn = new RingNumber(Integer.parseInt(line) * DECRYPTION_KEY,counter++);
		ring.add(rn);
		if (rn.value() == 0) { zero = rn; }
	}
		
	public void process() {
		Ring original = ring.clone();
		// System.out.println("Original: " + original);
		
		for (int i = 0; i < ITERATIONS; i++) {
			System.out.println("Iteration " + i);
			original.stream().forEach(rn -> ring.moveNumber(rn, rn.value()));
			// System.out.println("After iteration " + i + ": " + ring);
		}
	}
	
	public void results() {
		// System.out.println(ring);

		int zeroLocation = ring.indexOf(zero);
		System.out.println("result: (" + ring.at(zeroLocation + 1000) + ", " 
				+ ring.at(zeroLocation + 2000) + ", " + ring.at(zeroLocation + 3000) + ") : " 
				+ (ring.at(zeroLocation + 1000).value() + ring.at(zeroLocation + 2000).value() 
						+ ring.at(zeroLocation + 3000).value()));
	}
	
	class Ring {
		private LinkedList<RingNumber> values;
		
		public Ring() {
			this.values = new LinkedList<>();
		}
		
		public Ring clone() {
			Ring r = new Ring();
			r.values = (LinkedList<RingNumber>) values.clone();
			return r;
		}
		
		public void add(RingNumber value) {
			values.add(value);
		}
		
		public int indexOf(RingNumber value) {
			return values.indexOf(value);
		}
		
		public int size() {
			return values.size();
		}
		
		public RingNumber at(long position) {
			return values.get((int)(position % values.size()));
		}
		
		public void moveNumber(RingNumber rn, long distance) {
			int index = values.indexOf(rn);
			
			if (index >= 0) {
				// System.out.println("       Removing " + rn + " from list to move it " + distance + "...");
				values.remove(index);
				distance = distance % values.size();
				
				if (distance > 0) {
					long newPosition = (index + distance) % values.size();
					// System.out.println("       Adding " + rn + " to list at " + newPosition + "...");
					values.add((int)newPosition, rn);
				} else if (distance < 0) {
					long newPosition = (index + distance + values.size()) % values.size();
					// System.out.println("       Adding " + rn + " to list at " + newPosition + "...");
					values.add((int)newPosition, rn);
				} else {
					// System.out.println("      Putting " + rn + " back where we found it...");
					values.add(index, rn);
				}				
			} else {
				throw new IllegalArgumentException("Could not find " + rn + " in Ring");
			}
			
			// System.out.println(this);
		}
		
		public Stream<RingNumber> stream() {
			return values.stream();
		}
		
		public String toString() {
			return values.toString();
		}
	}
	
	class RingNumber {
		private long value;
		private int originalPosition;
		private int currentPosition;
		
		public RingNumber(long value, int originalPosition) {
			this.value = value;
			this.originalPosition = originalPosition;
			this.currentPosition = originalPosition;
		}
		
		public long value() {
			return value;
		}
		
		public int originalPosition() {
			return originalPosition;
		}
		
		public void setOriginalPosition(int originalPosition) {
			this.originalPosition = originalPosition;
		}
		
		public int currentPosition() {
			return currentPosition;
		}
		
		public void setCurrentPosition(int currentPosition) {
			this.currentPosition = currentPosition;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(originalPosition, value);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RingNumber other = (RingNumber) obj;
			return originalPosition == other.originalPosition && value == other.value;
		}

		public String toString() {
			return Long.toString(value);
		}
	}
}
