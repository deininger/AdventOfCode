package aoc.year23;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aoc.util.PuzzleApp;

public class Day09 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 09: Mirage Maintenance");
		PuzzleApp app = new Day09();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day09-part1";
	}
	
	public List<List<Integer>> readings = new ArrayList<>();
	
	public void parseLine(String line) {
		readings.add(Stream.of(line.split(" ")).map(Integer::valueOf).collect(Collectors.toList()));
	}	
	
	public static final Integer ZERO = Integer.valueOf(0);
	
	public boolean zero(List<Integer> list) {
		return list.isEmpty() || list.stream().allMatch(ZERO::equals);
	}
	
	public List<Integer> interpolate(List<Integer> list) {
		if (list.isEmpty() || list.size() == 1) {
			throw new UnsupportedOperationException("Cannot interpolate list of size " + list.size());
		}
		
		List<Integer> newList = new ArrayList<>();

		for (int i = 1; i < list.size(); i++) {
			newList.add(list.get(i) - list.get(i-1));
		}

		return newList;
	}
	
	public void printLists(List<List<Integer>> lists) {
		for (int i = 0; i < lists.size(); i++) {
			System.out.println( " " + lists.get(i));
		}
	}
	
	public List<Integer> extrapolatedReadings = new ArrayList<>();
	public List<Integer> backwardsExtrapolatedReadings = new ArrayList<>();

	public void process() {
		for (int i = 0; i < readings.size(); i++) {
			List<List<Integer>> intermediateResults = new ArrayList<>();
			intermediateResults.add(new ArrayList<>(readings.get(i)));
			while (!zero(intermediateResults.get(intermediateResults.size()-1))) {
				intermediateResults.add(interpolate(intermediateResults.get(intermediateResults.size()-1)));
			}
			
			// printLists(intermediateResults);
			
			Integer extrapolate = ZERO;
			
			for (int x = intermediateResults.size()-1; x >= 0; x--) {
				List<Integer> l = intermediateResults.get(x);
				l.add(l.get(l.size()-1) + extrapolate);
				extrapolate = l.get(l.size()-1);
			}

			// printLists(intermediateResults);

			System.out.println("--> extrapolation = " + extrapolate);
			extrapolatedReadings.add(extrapolate);
			
			// Now extrapolate backwards as well:
			extrapolate = ZERO;

			for (int x = intermediateResults.size()-1; x >= 0; x--) {
				List<Integer> l = intermediateResults.get(x);
				extrapolate = l.get(0) - extrapolate;
			}

			System.out.println("--> backwards extrapolation = " + extrapolate);
			backwardsExtrapolatedReadings.add(extrapolate);
		}
	}
	
	public void results() {
		System.out.println("Total extrapolated values = " + extrapolatedReadings.stream().mapToInt(i -> i).sum());
		System.out.println("Backward extrapolated values = " + backwardsExtrapolatedReadings.stream().mapToInt(i -> i).sum());
	}
}
