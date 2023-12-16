package aoc.year23;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import aoc.util.PuzzleApp;

public class Day15 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 15: Lens Library");
		PuzzleApp app = new Day15();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day15-part1";
	}
	
	String[] steps;
	
	public void parseLine(String line) {
		steps = line.split(","); // Just one input line 
	}
	
	private long sumOfHashResults = 0;
	private Box[] boxes = new Box[256];
	private long sumOfFocusingPower = 0;

	/**
	 * Calculates a hash value for the given string.
	 * 
	 * This uses a simple hashing algorithm that sums the character values,
	 * multiplies by 17, and takes the result modulo 256.
	 * 
	 * @param s The string to hash
	 * @return The hash value as an integer between 0-255
	 */
	int hash(String s) {
		return s.chars().reduce(0, (acc, val) -> {
			acc += val;
			acc *= 17;
			acc %= 256;
			return acc;
		});
	}

	public void process() {
		for (int b = 0; b < 256; b++) {
			boxes[b] = new Box(b);
		}
		
		for (int i = 0; i < steps.length; i++) {
			// Part 1:
			int hash = hash(steps[i]);
			sumOfHashResults += hash;
			
			// Part 2:
			String[] parts = steps[i].split("[-=]"); // Split on either - or =
			String code = parts[0];
			char op = '-';
			int focalLength = 0;
			
			if (parts.length == 2) {
				op = '=';
				focalLength = Integer.parseInt(parts[1]);
			}
			
			int box = hash(code);
			// System.out.println("Parsed " + steps[i] + " into " + code + op + slot + " box " + box);		
			
			Lens lens = new Lens(code, focalLength);
			
			if (op == '=') {
				boxes[box].addLens(lens);
			} else {
				boxes[box].removeLens(lens);
			}
			
			// System.out.println("After " + steps[i]);
			// printBoxes();
			// System.out.println();
		}
	}
	
	/*
	private void printBoxes() {
		for (int b = 0; b < 256; b++) {
			if (!boxes[b].isEmpty()) {
				System.out.println(boxes[b]);
			}
		}
	}	
	*/

	public void results() {
		System.out.println("Part 1: Sum of HASH results is " + sumOfHashResults);
		
		// Part 2:
		
		for (int b = 0; b < 256; b++) {
			sumOfFocusingPower += boxes[b].focusingPower();
		}
		
		System.out.println("Part 2: Sum of focusing power is " + sumOfFocusingPower);
	}
	
	class Box {
		private int boxNumber;
		private List<Lens> slots = new ArrayList<>();
		
		public Box(int boxNumber) {
			this.boxNumber = boxNumber;
		}
		
		public void removeLens(Lens lens) {
			slots.remove(lens);
		}
		
		public void addLens(Lens lens) {
 			if (slots.contains(lens)) {
				int index = slots.indexOf(lens);
				slots.remove(lens); // remove old lens
				slots.add(index, lens); // add new lens in same spot
			} else {
				slots.add(lens);
			}
		}
		
		/*
		 * The focusing power of a single lens is the result of multiplying together:
		 * - One plus the box number of the lens in question. 
		 * - The slot number of the lens within the box: 1 for the first lens, 2 for the second lens, and so on. 
		 * - The focal length of the lens.
		 */
		public long focusingPower() {
			long focusingPower = 0;
			
			for (int slotNumber = 0; slotNumber < slots.size(); slotNumber++) {
				focusingPower += (boxNumber + 1) * (slotNumber + 1) * slots.get(slotNumber).focalLength();
			}
			
			return focusingPower;
		}
		
		public boolean isEmpty() {
			return slots.isEmpty();
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Box ").append(boxNumber).append(": ");
			slots.forEach(slot -> sb.append(slot).append(' '));
			return sb.toString();
		}
	}
	
	class Lens {
		private String label;
		private int focalLength;
		
		public Lens(String label, int focalLength) {
			this.label = label;
			this.focalLength = focalLength;
		}
		
		public int focalLength() {
			return focalLength;
		}
		
		public void setFocalLength(int focalLength) {
			this.focalLength = focalLength;
		}
		
		public String toString() {
			return "[" + label + " " + focalLength + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(label);
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
			Lens other = (Lens) obj;
			return Objects.equals(label, other.label);
		}
	}
}
