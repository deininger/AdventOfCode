package aoc.year22;

import java.util.ArrayList;
import java.util.List;

import aoc.util.PuzzleApp;

public class PyroclasticFlow extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 17: Pyroclastic Flow");
		PuzzleApp app = new PyroclasticFlow();
		app.run();
	}
	
	public String filename() {
		return "data/data17";
	}

//  >>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
	
//	####
//
//	.#.
//	###
//	.#.
//
//	..#
//	..#
//	###
//
//	#
//	#
//	#
//	#
//
//	##
//	##
	
	private long MAX_ROCKS = 1000000000000L; 
	private long CALCULATED_EXTRA_HEIGHT = 0;
	
	private Chamber chamber = new Chamber();
	
	private char[] jetPattern;
	private long jetPatternPosition = 0;
	private long rockNumber = 0;
	
	public void parseLine(String line) {
		jetPattern = line.toCharArray();
		System.out.println("Jet Pattern length is " + jetPattern.length);
	}

	void setup2() {
		long hugeIterations = 1000000000000L;
		
		long repetitions = hugeIterations / jetPattern.length;
		long lastIteration = hugeIterations % jetPattern.length;
		System.out.println("Huge iterations will repeat " + repetitions + " times, with remainder " + lastIteration);
		
		// each iteration except the first (and the "partial" last) will add 2737 to the height:
		CALCULATED_EXTRA_HEIGHT = 2737 * (repetitions - 1);
		System.out.println("Calculated extra height = " + CALCULATED_EXTRA_HEIGHT);
		
		MAX_ROCKS = jetPattern.length + lastIteration;
		System.out.println("Running simulation for rocks = " + MAX_ROCKS);
	}
	
	long previousRockNumber = 0;
	long previousChamberHeight = 0;
	
	public void process() {
		// setup2();
		
		// System.out.println("0 Adding new rock " + (rockNumber % 5));
		chamber.addRock((int)(rockNumber++ % 5));
		
		while (rockNumber <= MAX_ROCKS ) {
			char direction = jetPattern[(int)(jetPatternPosition++ % jetPattern.length)];

			if (jetPatternPosition % jetPattern.length == 0) {
				int trimDepth = chamber.depths().stream().mapToInt(Integer::intValue).max().orElse(0);

				System.out.println(rockNumber + " " + (rockNumber % 5) + " Trimming chamber to depth: " + trimDepth);
				System.out.println("Chamber is " + chamber.height() + " rows high");

				chamber.trim(trimDepth);
				
				System.out.println("RockNumber increased by " + (rockNumber - previousRockNumber)
						+ ", ChamberHeight increased by " + (chamber.height() - previousChamberHeight) );
				
				previousRockNumber = rockNumber;
				previousChamberHeight = chamber.height();
				
				// Pattern repeats! Every 1760 rocks!
				// First iteration takes 1744 rocks, every iteration after adds 1760.
				// First height is 2750, each additional adds 2737
				
				// Loop forward quickly, incrementing the rockNumber by 1760
				// and the chamber height by 2737, until we get close to the
				// maximum:
				
				if (rockNumber > 5000) { // Arbitrary starting point just to observe a few iterations
					while ((rockNumber + 1760) < MAX_ROCKS) {
						rockNumber += 1760;
						chamber.increaseHeight(2737);
					}
				}
			}
			
			// System.out.println(rockNumber + " Pushing rock " + direction + " ...");
			
			boolean collision = chamber.pushRock(direction);
			
			// System.out.println(chamber);
			// System.out.println();

			// System.out.println(rockNumber + " Dropping rock...");
			
			collision = chamber.dropRock();
			
			// System.out.println(chamber);
			// System.out.println();
			
			// System.out.println(rockNumber + " Chamber height is " + chamber.height());
			
			if (collision) {
				// System.out.println((rockCounter) + " Adding new rock " + (rockNumber % 5));

				chamber.addRock((int)(rockNumber++ % 5));

				// System.out.println(chamber);
				// System.out.println();
			}
		}
	}
	
	public void results() {
		System.out.println("Chamber is " + chamber.height() + " rows high");
		System.out.println();
		// System.out.println("With extrapolation, final answer is " + (CALCULATED_EXTRA_HEIGHT + chamber.height()) + " rows high");
		// System.out.println();
		// System.out.println("Jet Pattern position is " + jetPatternPosition);
		// System.out.println();
		// System.out.println(chamber.depths());
		
		// 1514285714288 is answer from example

		// 1555113636385 is answer for my data
	}
	
	class Chamber {
		private static final int WIDTH = 7;
		private static final char[] EMPTY_ROW = new char[] { '.', '.', '.', '.', '.', '.', '.' };
		
		private static List<char[][]> rocks = new ArrayList<>();

		private List<char[]> fallingRock = new ArrayList<>();
		private int rockPosition; // Relative to the "top" of the chamber (top of highest fixed rock point)
		
		private List<char[]> rows = new ArrayList<>();
		private long trimmedDepth = 0;
		
		static {
			rocks.add(new char[][] {{ '.', '.', '@', '@', '@', '@', '.' } });

			rocks.add(new char[][] {{ '.', '.', '.', '@', '.', '.', '.' }, 
									{ '.', '.', '@', '@', '@', '.', '.' },
									{ '.', '.', '.', '@', '.', '.', '.' }});

			rocks.add(new char[][] {{ '.', '.', '.', '.', '@', '.', '.' }, 
									{ '.', '.', '.', '.', '@', '.', '.' },
									{ '.', '.', '@', '@', '@', '.', '.' } });

			rocks.add(new char[][] {{ '.', '.', '@', '.', '.', '.', '.' }, 
									{ '.', '.', '@', '.', '.', '.', '.' },
									{ '.', '.', '@', '.', '.', '.', '.' }, 
									{ '.', '.', '@', '.', '.', '.', '.' }});

			rocks.add(new char[][] {{ '.', '.', '@', '@', '.', '.', '.' }, 
									{ '.', '.', '@', '@', '.', '.', '.' }});
		}
		
		public Chamber() {
		}
		
		public void addRock(int rockNumber) {
			rockPosition = 3; // Rocks always start 3 rows above chamber
			char[][] r = rocks.get(rockNumber);
			for (int i = r.length; i > 0; i--) {
				fallingRock.add(r[i-1]);
			}
		}
		
		public List<Integer> depths() {
			List<Integer> depths = new ArrayList<>();
			
			for (int i = 0; i < WIDTH; i++) {
				int depth = 0;
				for (int j = rows.size(); j > 0; j--) {
					if (rows.get(j-1)[i] == '.') depth++;
					else break;
				}
				depths.add(depth);
			}
			
			return depths;
		}
		
		public void trim(int depth) {
			ArrayList<char[]> newRows = new ArrayList<>();
			
			for (int i = 0; i < depth; i++) {
				newRows.add(rows.get(rows.size()-depth+i));
			}
			
			trimmedDepth += rows.size() - newRows.size();
			rows = newRows;
		}
		
		private boolean collision(List<char[]> hypotheticalRock) {
			if (rockPosition >= 0) {
				return false; // No collision possible while rock is above top of chamber
			}

			int overlap = -rockPosition; // Number of overlapping rows to check
			
			if (overlap > rows.size()) {
				// We have encountered the bottom of the chamber
				return true;
			}
			
			for (int i = 0; i < overlap && i < hypotheticalRock.size(); i++) {
				char[] rockRow = hypotheticalRock.get(i);
				char[] chamberRow = rows.get(rows.size()-overlap+i);
				
				for (int j = 0; j < WIDTH; j++) {
					if (rockRow[j] != '.' && chamberRow[j] != '.') {
						return true; // Collision!
					}
				}
			}
			
			return false; // No collision detected
		}
		
		private char[] convert(char[] row) {
			char[] converted = new char[WIDTH];
			for (int i = 0; i < WIDTH; i++) {
				converted[i] = (row[i] == '@' ? '#' : row[i]);
			}
			return converted;
		}
		
		private void mergeInto(char[] rock, char[] row) {
			for (int i = 0; i < WIDTH; i++) {
				row[i] = (rock[i] == '@' ? '#' : row[i]);
			}	
		}
		
		private void convertRockToRows() {
			int overlap = -rockPosition;
			for (int i = 0; i < fallingRock.size(); i++) {
				if (i < overlap) {
					mergeInto(fallingRock.get(i), rows.get(rows.size()-overlap+i));
				} else {
					rows.add(convert(fallingRock.get(i)));
				}
			}
		}
		
		public boolean dropRock() {
			rockPosition--;
			
			if (collision(fallingRock)) {
				rockPosition++; // Couldn't drop
				convertRockToRows();
				fallingRock.clear();
				rockPosition = 0;
				return true;
			}
			
			return false;
		}
		
		public boolean pushRock(char direction) {
			boolean hitWall = false;
			List<char[]> newFallingRock = new ArrayList<>();
			
			switch (direction) {
			case '<':				
				for (int i = 0; i < fallingRock.size(); i++) {
					char[] rockRow = fallingRock.get(i);
					if (rockRow[0] != '.') {
						hitWall = true;
						break;
					} else {
						char[] newRockRow = new char[rockRow.length];
						for (int j = 1; j < rockRow.length; j++) {
							newRockRow[j-1] = rockRow[j];
						}
						newRockRow[rockRow.length-1] = '.';
						newFallingRock.add(newRockRow);
					}
				}
				
				break;
			case '>':				
				for (int i = 0; i < fallingRock.size(); i++) {
					char[] rockRow = fallingRock.get(i);
					if (rockRow[rockRow.length-1] != '.') {
						hitWall = true;
						break;
					} else {
						char[] newRockRow = new char[rockRow.length];
						newRockRow[0] = '.';
						for (int j = 0; j < rockRow.length-1; j++) {
							newRockRow[j+1] = rockRow[j];
						}
						newFallingRock.add(newRockRow);
					}
				}
				
				break;
			default:
				throw new IllegalArgumentException("Unknown jet direction '" + direction + "'");
			}
			
			boolean hitStructure = collision(newFallingRock);
			if (!hitWall && !hitStructure) fallingRock = newFallingRock;
			return hitWall || hitStructure;
		}
		
		private char[] rockOrRow(char[] rock, char[] row) {
			char[] result = new char[WIDTH];
			
			for (int i = 0; i < WIDTH; i++) {
				if (rock[i] != '.' && row[i] == '.') result[i] = rock[i];
				else if (rock[i] == '.' && row[i] != '.') result[i] = row[i];
				else if (rock[i] == '.' && row[i] == '.') result[i] = '.';
				else result[i] = '!'; // should never happen!
			}
			
			return result;
		}
		
		public long height() {
			return rows.size() + trimmedDepth;
		}
		
		public void increaseHeight(int extraHeight) {
			this.trimmedDepth += extraHeight;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			
			if (rockPosition >= 0) {
				for (int i = fallingRock.size(); i > 0; i--) {
					sb.append('|').append(fallingRock.get(i-1)).append('|').append('\n');
				}
				for (int i = rockPosition; i > 0; i--) {
					sb.append('|').append(EMPTY_ROW).append('|').append('\n');
				}
				
				for (int i = rows.size(); i > 0; i--) {
					sb.append('|').append(rows.get(i-1)).append('|').append('\n');
				}
			} else {
				// Rock is overlapping with chamber
				int overlap = -rockPosition;
				
				for (int i = fallingRock.size(); i > 0; i--) {
					if (i > overlap) {
						// non-overlapping part of rock
						sb.append('|').append(fallingRock.get(i-1)).append('|').append('\n');
					} else {
						sb.append('|').append(rockOrRow(fallingRock.get(i-1),rows.get(rows.size()-overlap+i-1))).append('|').append('\n');
					}
				}
				
				for (int i = rows.size() - overlap; i > 0; i--) {
					sb.append('|').append(rows.get(i-1)).append('|').append('\n');
				}
			}
			
			sb.append("+-------+").append('\n');
			return sb.toString();
		}
	}
}
