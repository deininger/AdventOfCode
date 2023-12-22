package aoc.year23;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import aoc.util.PuzzleApp;

public class Day22 extends PuzzleApp {

	public static final void main(String[] args) {
		System.out.println("December 22: Sand Slabs");
		PuzzleApp app = new Day22();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day22-part1";
	}

	private BlockCollection blocks = new BlockCollection();

	public void parseLine(String line) {
		String[] parts = line.split("~");
		String[] start = parts[0].split(",");
		String[] end = parts[1].split(",");

		Block b = new Block( 
			new Coordinate( Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2])),
			new Coordinate( Integer.parseInt(end[0]), Integer.parseInt(end[1]), Integer.parseInt(end[2]))
		);

		blocks.add(b);
	}

	private int distintegrationCount = 0;
	private int sumOfFallenBricks = 0;

	public void process() {
		blocks.sort();
		// System.out.println("Before Settling: " + blocks);
		blocks.settle();
		// System.out.println("After Settling: " + blocks);

		for (Block candidate : blocks.blocks()) {
			BlockCollection bc = new BlockCollection(blocks);
			bc.remove(candidate);
			int settleCount = bc.settle();
			// System.out.println("Removing " + candidate + " causes " + settleCount + " bricks to fall");
			if (settleCount == 0) distintegrationCount++;
			sumOfFallenBricks += settleCount;
		}
	}

	public void results() {
		System.out.println("Part 1: " + distintegrationCount + " blocks can be distintegrated safely");
		System.out.println("Part 2: " + sumOfFallenBricks + " blocks have fallen");
	}

	class BlockCollection {
		private List<Block> blocks = new ArrayList<>();

		public BlockCollection() {}

		public BlockCollection(BlockCollection other) {
			other.blocks.stream().forEach(b -> this.blocks.add(new Block(b)));
		}

		public void add(Block b) {
			blocks.add(b);
		}

		public void remove(Block b) {
			blocks.remove(b);
		}

		public List<Block> blocks() {
			return blocks;
		}

		/*
	 	 * Sort the Blocks by Z (height):
		 */
		public void sort() {
			blocks.sort(new BlockHeightComparator());
		}

		/*
		 * Settle the Blocks:
		 */
		private int settle() {
			int settleCount = 0;
			for (Block b : blocks) {
				int distance = canFall(b);
				if (distance > 0) {
					b.move(0, 0, -distance);
					settleCount++;
				}
			}

			return settleCount;
		}

		/*
		 * Return the Z distance the given block can fall before hitting another block or the ground
		 */
		public int canFall(Block b) {
			if (b.start.z() == 1) return 0; // On the ground

			int maxZBelowThisBlock = 0;

			for (Block x : blocks) {
				if (x.isBelow(b)) {
					maxZBelowThisBlock = Math.max(maxZBelowThisBlock, x.end.z());
				}
			}

			return b.start.z() - maxZBelowThisBlock - 1;
		}

		public String toString() {
			return blocks.toString();
		}
	}

	class Block {
		private static int idGenerator = 0;

		private int id;
		private Coordinate start; 	// The lesser of the two coordinates
		private Coordinate end;		// The greater of the two coordinates

		private static int generateId() {
			return ++idGenerator;
		}

		public Block(Coordinate start, Coordinate end) {
			this.id = generateId();
			this.start = start;
			this.end = end;
		}

		public Block(Block other) {
			this.id = other.id;
			this.start = other.start;
			this.end = other.end;
		}

		/*
		 * Move both ends of the block
		 */
		public void move(int deltaX, int deltaY, int deltaZ) {
			start = new Coordinate(start.x() + deltaX, start.y() + deltaY, start.z() + deltaZ);
			end = new Coordinate(end.x() + deltaX, end.y() + deltaY, end.z() + deltaZ);
		}

		/*
		 * Determine whether this block is below the given block, by determining if it shares
		 * any x,y coordinate and has a max z value less than the given block's min z value.
		 */
		public boolean isBelow(Block other) {
			if (this.id == other.id) return false; // we can't be below ourself
			if (this.start.z() > other.end.z()) return false; // we are above
			if (this.start.x() > other.end.x() || other.start.x() > this.end.x()) return false; // We are to the side on X axis
			if (this.start.y() > other.end.y() || other.start.y() > this.end.y()) return false; // We are to the side on Y axis
			return true;
		}

		public String toString() {
			return id + ": " + start + " ~ " + end;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Block other = (Block) obj;
			if (id != other.id)
				return false;
			return true;
		}		
	}

	class BlockHeightComparator implements Comparator<Block> {
	    @Override
    	public int compare(Block a, Block b) {
       		return Integer.compare(a.start.z(), b.start.z());
    	}
	}

	class Coordinate {
		private int x;
		private int y;
		private int z;

		public Coordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int x() {
			return x;
		}

		public int y() {
			return y;
		}

		public int z() {
			return z;
		}

		public String toString() {
			return "(" + x + "," + y + "," + z + ")";
		}
	}
}