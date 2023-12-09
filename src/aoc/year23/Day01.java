package aoc.year23;

import java.util.List;
import java.util.stream.Collectors;

import aoc.util.PuzzleApp;

public class Day01 extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 01: Trebuchet?!");
		PuzzleApp app = new Day01();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day01-part1";
	}

	int sum = 0;
	
	public void parseLine(String line) {		
		List<Integer> intList = line.chars()
					.filter(c -> c >= '0' && c <= '9')
					.map(i -> i - '0')
					.boxed().collect(Collectors.toList());
		
		if (intList.size() > 0) {
			sum += (intList.get(0) * 10 + intList.get(intList.size()-1));
		}
	}
	
	public void results() {
		System.out.println(sum);
	}
}
