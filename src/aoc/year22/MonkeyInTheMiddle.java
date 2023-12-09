package aoc.year22;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aoc.util.PuzzleApp;

public class MonkeyInTheMiddle extends PuzzleApp {
	class Monkey {
		int monkeyNumber;
		Deque<Integer> items = new ArrayDeque<>();
		String operator;
		String operand;
		int testDivisor;
		int throwToWhenTrue;
		int throwToWhenFalse;
		int itemInspectionCount;
		
		Monkey(int monkeyNumber) {
			this.monkeyNumber = monkeyNumber;
		}

		public void addItem(int item) {
			this.items.add(item);
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public void setOperand(String operand) {
			this.operand = operand;
		}
		
		public void setTestDivisor(int testDivisor) {
			this.testDivisor = testDivisor;
		}
		
		public void throwToWhenTrue(int monkeyNumber) {
			this.throwToWhenTrue = monkeyNumber;
		}

		public void throwToWhenFalse(int monkeyNumber) {
			this.throwToWhenFalse = monkeyNumber;
		}
		
		public void inspect() {
			while(!items.isEmpty()) {
				int item = items.pop();
				itemInspectionCount++;
				// System.out.println("Monkey " + monkeyNumber + " Inspecting item " + item + " (" + itemInspectionCount + ")");
				long worryLevel = item;
				
				switch (operator) {
				case "+": 
						if (operand.equals("old")) {
							worryLevel = worryLevel + worryLevel;
						} else {
							worryLevel = worryLevel + Integer.parseInt(operand);
						}
						break;
				case "*":
					if (operand.equals("old")) {
						worryLevel = worryLevel * worryLevel;
					} else {
						worryLevel = worryLevel * Integer.parseInt(operand);
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown operator '" + operator + "'");
				}
				
				// worryLevel /= 3;
				worryLevel %= monkeyGCM;

				// System.out.println("  Worry level changed from " + item + " to " + worryLevel);
				item = (int)worryLevel;

				if (worryLevel % testDivisor == 0) {
					// System.out.println("  Throwing item " + item + " to monkey " + throwToWhenTrue);
					monkeys.get(throwToWhenTrue).addItem(item);
				} else {
					// System.out.println("  Throwing item " + item + " to monkey " + throwToWhenFalse);
					monkeys.get(throwToWhenFalse).addItem(item);
				}
			}
		}
		
		public int itemInspectionCount() {
			return itemInspectionCount;
		}
		
		public String toString() {
			return "Monkey " + monkeyNumber + ": " + items + " (" + itemInspectionCount + ")";
		}
	}
	
	private static final int ROUNDS = 10000;
	
	public static final void main(String[] args) {
		System.out.println("December 11: Monkey In The Middle");
		PuzzleApp app = new MonkeyInTheMiddle();
		app.run();
	}
		
	private Map<Integer,Monkey> monkeys = new HashMap<>();
	private Monkey currentMonkey;
	private int monkeyGCM = 1;
	
	public String filename() {
		return "data/data11";
	}
	
	public void setup() {
	}
	
	public Monkey createMonkey(int monkeyNumber) {
		Monkey m = new Monkey(monkeyNumber);
		monkeys.put(monkeyNumber, m);
		return m;
	}
	
	void processLine(String line) {
		String[] parts = line.trim().split(" ");
		String s = null;
		
		// System.out.println( Arrays.asList(parts) );

		if (!parts[0].isEmpty()) {
			switch( parts[0].replace(":", "")) {
			case "Monkey":
					s = parts[1].replace(":","");
					currentMonkey = createMonkey(Integer.parseInt(s)); 
					break;
			case "Starting":
					for (int i = 2; i < parts.length; i++) {
						s = parts[i].replace(",", "");
						currentMonkey.addItem(Integer.parseInt(s));
					}
					break;
			case "Operation":
				currentMonkey.setOperator(parts[4]);
				currentMonkey.setOperand(parts[5]);
				break;
			case "Test":
				currentMonkey.setTestDivisor(Integer.parseInt(parts[3]));
				break;
			case "If":
				if (parts[1].equals("true:")) {
					currentMonkey.throwToWhenTrue(Integer.parseInt(parts[5]));
				} else if (parts[1].equals("false:")) {
					currentMonkey.throwToWhenFalse(Integer.parseInt(parts[5]));
				} else {
					throw new IllegalArgumentException("Unknown Test Result '" + parts[1] + "'");
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown Comand '" + parts[0] + "'");
			}
		}
	}
	
	public void results() {
		System.out.println("Start: " + monkeys + "\n");
		
		monkeys.values().forEach(m -> { monkeyGCM *= m.testDivisor; });
		// System.out.println("Monkey GCM is " + monkeyGCM);
		
		for (int i = 0; i < ROUNDS; i++) {
			// System.out.println("\nROUND " + i);
			monkeys.forEach((k,monkey) -> monkey.inspect());
			if (i % 1000 == 0) System.out.println("Round " + i + ": " + monkeys + "\n");
		}
		
		List<Monkey> sortedMonkeys = monkeys.values().stream().sorted(Comparator.comparingInt(Monkey::itemInspectionCount).reversed()).collect(Collectors.toList());
		
		long total = (long) sortedMonkeys.get(0).itemInspectionCount() * sortedMonkeys.get(1).itemInspectionCount();

		System.out.println("\nTOTAL = " + total);
	}
}
