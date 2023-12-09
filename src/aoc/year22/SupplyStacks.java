package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupplyStacks {
	private static boolean moveCratesOneAtATime = false;
	
	private static int NUMBER_OF_STACKS = 10;
	private static List<LinkedList<Character>> stacks;

	static {
		stacks = new ArrayList<>(NUMBER_OF_STACKS);
		for (int i = 0; i < NUMBER_OF_STACKS; i++) {
			stacks.add(new LinkedList<>());
		}
	}
	
	public static final void main(String[] args) throws IOException {
		System.out.println("December  5: Supply Stacks");

		boolean moveInstructions = false;

		BufferedReader reader = new BufferedReader(new FileReader("data/Data5"));
		String line = reader.readLine();

		while (line != null) {
			if (line.isEmpty()) {
				moveInstructions = true;
				printStacks();
			} else if ( !moveInstructions ) {
				loadStartingStacks(line);
			} else {
				move(line);
			}
			
			line = reader.readLine();
		}

		reader.close();

		printStacks();
	}

	private static Pattern p = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");

	private static void move(String line) {		
		Matcher m = p.matcher(line);
		
		if (m.find()) {
			int moveCount = Integer.parseInt(m.group(1));
			int originStack = Integer.parseInt(m.group(2));
			int destinationStack = Integer.parseInt(m.group(3));
			
			// System.out.println(line + " -> " + moveCount + " " + originStack + " " + destinationStack);

			if (moveCratesOneAtATime) {
				for (int i = 0; i < moveCount; i++) {
					stacks.get(destinationStack).push(stacks.get(originStack).pop());
				}
			} else {
				Deque<Character> d = new LinkedList<>();
				
				for (int i = 0; i < moveCount; i++) {
					d.push(stacks.get(originStack).pop());
				}

				for (int i = 0; i < moveCount; i++) {
					stacks.get(destinationStack).push(d.pop());
				}
			}
			
			// printStacks();
		}
	}

	private static void loadStartingStacks(String line) {
		if (line.startsWith(" 1")) return; // Line just shows stack numbers
		
		int stackNumber = 1;
		int characterPosition = 1;
		
		while (characterPosition < line.length()) {
			addBoxToBottomOfStack(line.charAt(characterPosition), stackNumber);
			characterPosition += 4;
			stackNumber++;
		}
	}
	
	private static void addBoxToBottomOfStack(char box, int stackNumber) {
		if (box == ' ') return;
		
		// System.out.println("Adding box " + box + " to bottom of stack " + stackNumber);		

		stacks.get(stackNumber).addLast(box);
	}

	private static void printStacks() {
		int highestStack = stacks.stream().mapToInt(deque -> deque.size()).max().orElse(0);
		System.out.println("Highest stack is " + highestStack + " items");
		
		for (int i = 0; i <= highestStack; i++) {
			for (int j = 1; j < NUMBER_OF_STACKS; j++) {
				int pos = highestStack - i;
				if (stacks.get(j).size() > pos) {
					System.out.print("[" + stacks.get(j).get(stacks.get(j).size() - pos - 1) + "] ");
				} else {
					System.out.print("    ");
				}
			}
			
			System.out.println();
		}

		for (int j = 1; j < NUMBER_OF_STACKS; j++) {
			System.out.print(" " + j + "  ");
		}
		
		System.out.println();
		System.out.println();
	}
}
