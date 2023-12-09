package aoc.year23;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import aoc.util.ParseException;
import aoc.util.PuzzleApp;

public class Day05 extends PuzzleApp {
	public static final String SEEDS = "seeds";
	public static final String MAP = "map";

	public static final void main(String[] args) {
		System.out.println("December 05: If You Give A Seed A Fertilizer");
		PuzzleApp app = new Day05();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day05-part1";
	}
	
	public List<Long> seeds = new ArrayList<>();
	public List<Pair<Long,Long>> seedRanges = new ArrayList<>();
	public List<SeedMap> maps = new ArrayList<>();
	
	public void parse() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename()));
			StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
			
			int currentToken = streamTokenizer.nextToken();

			if (SEEDS.equals(streamTokenizer.sval)) {
				currentToken = streamTokenizer.nextToken(); // Skip the colon
				currentToken = streamTokenizer.nextToken();

				while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the seed numbers
					long seedRangeStart = Double.valueOf(streamTokenizer.nval).longValue();
					seeds.add(seedRangeStart);
					currentToken = streamTokenizer.nextToken();
					long seedRangeLength = Double.valueOf(streamTokenizer.nval).longValue();
					seeds.add(seedRangeLength);
					currentToken = streamTokenizer.nextToken();
					seedRanges.add(new ImmutablePair<>(seedRangeStart, seedRangeLength));
				}				
				
				// System.out.println("Parsed seeds: " + seeds);
				// System.out.println("Parsed seed ranges: " + seedRanges);
			} else {
				throw new ParseException("Encountered " + streamTokenizer.toString() + ", expected SEEDS"); 
			}
		
			while (currentToken != StreamTokenizer.TT_EOF) {
				SeedMap sm = new SeedMap(streamTokenizer.sval);
				maps.add(sm);
				currentToken = streamTokenizer.nextToken();

				if (!MAP.equals(streamTokenizer.sval)) {
					System.err.println("Encountered unexpected token " + streamTokenizer.toString() + " when expecting MAP");
				}
				currentToken = streamTokenizer.nextToken(); // Skip the map
				currentToken = streamTokenizer.nextToken(); // Skip the colon

				while (currentToken == StreamTokenizer.TT_NUMBER) { // Parse the mapping ranges
					long destinationRangeStart = Double.valueOf(streamTokenizer.nval).longValue();
					currentToken = streamTokenizer.nextToken();
					long sourceRangeStart = Double.valueOf(streamTokenizer.nval).longValue();
					currentToken = streamTokenizer.nextToken();
					long rangeLength = Double.valueOf(streamTokenizer.nval).longValue();
					currentToken = streamTokenizer.nextToken();
					sm.addRange(destinationRangeStart, sourceRangeStart, rangeLength);
				}
				
				// System.out.println("Parsed: " + sm);
			}
		} catch (IOException e) {
			System.out.flush();
			System.err.println(e.getMessage()); 
			System.err.flush();
		}
	}	
	
	private Long minimumResult = null;
	private Long minimumRangeResult = null;
	
	public void process() {
		System.out.println("\nProcessing seeds (part 1)...");
		
		for (Long seed : seeds) {
			long result = seed;
			for (SeedMap sm : maps) {
				result = sm.doRangeMappings(result);
			}
			System.out.println("    Mapped seed " + seed + " to " + result);
			if (minimumResult == null || minimumResult > result) {
				minimumResult = result;
			}
		}
		
		System.out.println("\nProcessing seed ranges (part 2)...");
		
		minimumRangeResult = seedRanges.stream()
			.parallel()
			.mapToLong(seedRange -> {
				return LongStream.range(seedRange.getLeft(), seedRange.getLeft() + seedRange.getRight())
					.parallel()
					.map(seed -> {
						long result = seed;
						for (SeedMap sm : maps) {
							result = sm.doRangeMappings(result);
						}
						return result;
					})
				.min().orElse(Long.MAX_VALUE);
				})
			.min().orElse(Long.MAX_VALUE);
						
//		for (Pair<Long,Long> seedRange : seedRanges) {
//			System.out.println("    Mapping seed range " + seedRange);
//			for (long seed = seedRange.getLeft(); seed < seedRange.getLeft() + seedRange.getRight(); seed++) {
//				long result = seed;
//				for (SeedMap sm : maps) {
//					result = sm.doRangeMappings(result);
//				}
//
//				if (minimumRangeResult == null || minimumRangeResult > result) {
//					minimumRangeResult = result;
//				}
//			}
//		}
	}
	
	// Part 1: Minimum result = 199602917
	// Part 2: Minimum range result = 2254686
			
	public void results() {
		System.out.println("Part 1: Minimum result = " + minimumResult);
		System.out.println("Part 2: Minimum range result = " + minimumRangeResult);
	}
	
	class SeedMap {
		private String name;
		private List<Range> ranges = new ArrayList<>();
		
		public SeedMap(String name) {
			this.name = name;
		}
		
		public String name() {
			return name;
		}
		
		public void addRange(long destinationRangeStart, long sourceRangeStart, long rangeLength) {
			ranges.add(new Range(destinationRangeStart, sourceRangeStart, rangeLength));
		}

		public long doRangeMappings(long source) {
			for (Range r : ranges) {
				if (r.inRange(source)) {
					return r.doRangeMapping(source);
				}
			}
			return source;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name).append(" map:").append('\n');
			for (Range r : ranges) {
				sb.append(r.toString()).append('\n');
			}
			return sb.toString();
		}
	}
	
	class Range {
		private long destinationRangeStart;
		private long sourceRangeStart;
		private long rangeLength;
		
		public Range(long destinationRangeStart, long sourceRangeStart, long rangeLength) {
			this.destinationRangeStart = destinationRangeStart;
			this.sourceRangeStart = sourceRangeStart;
			this.rangeLength = rangeLength;
		}
		
		public boolean inRange(long source) {
			return source >= sourceRangeStart && source < sourceRangeStart + rangeLength;
		}

		public long doRangeMapping(long source) {
			if (source >= sourceRangeStart && source < sourceRangeStart + rangeLength) {
				return source + destinationRangeStart - sourceRangeStart;
			} else {
				return source;
			}
		}
		public String toString() {
			return " " + destinationRangeStart + " " + sourceRangeStart + " " + rangeLength;
		}
	}
}
