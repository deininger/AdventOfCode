package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CampCleanup {
	public static class Range {
		private int start;
		private int end;
		
		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}
		
		public Range(String s) {
			String[] startEnd = s.split("-");
			this.start = Integer.parseInt(startEnd[0]);
			this.end = Integer.parseInt(startEnd[1]);
		}
		
		public String toString() {
			return "" + start + "-" + end + "";
		}
		
		public boolean contains(Range r) {
			return this.start <= r.start && this.end >= r.end;
		}
		
		public boolean overlaps(Range r) {
			return ( this.start <= r.start && r.start <= this.end ) || ( this.start <= r.end && r.end <= this.end )
					|| ( r.start <= this.start && this.start <= r.end ) || ( r.start <= this.end && this.end <= r.end );
		}
	}
	
	public static final void main(String[] args) throws IOException {
		System.out.println("December  4: Camp Cleanup");

		int partOneAnswer = 0;
		int partTwoAnswer = 0;

		BufferedReader reader = new BufferedReader(new FileReader("data/Data4"));
		String line = reader.readLine();

		while (line != null) {
			String[] ranges = line.split(",");
			Range firstRange = new Range(ranges[0]);
			Range secondRange = new Range(ranges[1]);

			if ( firstRange.contains(secondRange) || secondRange.contains(firstRange)) {
				partOneAnswer++;
			}
			
			if ( firstRange.overlaps(secondRange) ) {
				System.out.println( firstRange + " overlaps " + secondRange );
				partTwoAnswer++;
			} else {
				System.out.println( firstRange + " does not overlap " + secondRange );
			}
			
			line = reader.readLine();
		}

		reader.close();

		System.out.println("Part 1 answer is " + partOneAnswer);
		System.out.println("Part 2 answer is " + partTwoAnswer);
	}

}
