package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalorieCounting {
	public static final void main(String[] args) throws IOException {
		System.out.println("December  1: Calorie Counting");
		
		BufferedReader reader = new BufferedReader(new FileReader("data/Data1"));
		
		String line = reader.readLine();
		int elfCounter = 0;
		Map<Integer,Integer> calorieCounter = new HashMap<>();
		
		while (line != null) {
			if (line.isEmpty()) {
				elfCounter++;
			} else {
				int calories = Integer.valueOf(line);
				calorieCounter.put(elfCounter, calorieCounter.getOrDefault(elfCounter, 0) + calories );
			}
			
			line = reader.readLine();
		}
		
		reader.close();

		int max = Collections.max(calorieCounter.values());
		
		System.out.println("Part 1 answer is " + max);
		
		List<Integer> sortedCalories = new ArrayList<>(calorieCounter.values());
		sortedCalories.sort(Comparator.naturalOrder());
		
		int topThreeTotal = sortedCalories.get(sortedCalories.size() - 1) + sortedCalories.get(sortedCalories.size() - 2) + sortedCalories.get(sortedCalories.size() - 3);
		
		System.out.println("Part 2 answer is " + topThreeTotal);
		
	}
}
