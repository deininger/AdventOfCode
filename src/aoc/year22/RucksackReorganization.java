package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RucksackReorganization {
	private static final int GROUP_SIZE = 3;
	
	public static Integer priority(Integer charValue) {
		int newValue = 0;

		if (charValue >= 'a' && charValue <= 'z') {
			newValue = 1 + charValue - 'a';
		} else if (charValue >= 'A' && charValue <= 'Z') {
			newValue = 27 + charValue - 'A';
		}

		return newValue;
	}

	public static Set<Integer> toPrioritySet(String s) {
		return s.chars().boxed().map(RucksackReorganization::priority).collect(Collectors.toSet());
	}

	public static Set<Integer> findIntersection(String firstHalf, String secondHalf) {
		Set<Integer> firstCompartment = toPrioritySet(firstHalf);
		Set<Integer> secondCompartment = toPrioritySet(secondHalf);

		Set<Integer> intersection = new HashSet<>(firstCompartment);
		intersection.retainAll(secondCompartment);

		// System.out.println("Compartment intersection " + intersection);

		return intersection;
	}

	private static Set<Set<Integer>> rucksacksInGroup = new HashSet<>();

	public static Set<Integer> groupIntersection(String line) {
		rucksacksInGroup.add(toPrioritySet(line));
		Set<Integer> intersection = new HashSet<>();

		if (rucksacksInGroup.size() == GROUP_SIZE) {
			rucksacksInGroup.forEach(intersection::addAll);
			rucksacksInGroup.forEach(intersection::retainAll);
			rucksacksInGroup.clear();
		}
		
		// System.out.println("Group intersection " + intersection);
		return intersection;
	}

	public static final void main(String[] args) throws IOException {
		System.out.println("December  3: Rucksack Reorganization");

		int totalPriority = 0;
		int totalBadgePriority = 0;

		BufferedReader reader = new BufferedReader(new FileReader("data/Data3"));
		String line = reader.readLine();

		while (line != null) {
			String firstHalf = line.substring(0, line.length() / 2);
			String secondHalf = line.substring(line.length() / 2);
			totalPriority += findIntersection(firstHalf, secondHalf).stream().reduce(0, Integer::sum);
			totalBadgePriority += groupIntersection(line).stream().reduce(0, Integer::sum);

			line = reader.readLine();
		}

		reader.close();

		System.out.println("Part 1 answer is " + totalPriority);
		System.out.println("Part 2 answer is " + totalBadgePriority);
	}

}
