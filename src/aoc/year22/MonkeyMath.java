package aoc.year22;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aoc.util.PuzzleApp;

public class MonkeyMath extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 21: Monkey Math");
		PuzzleApp app = new MonkeyMath();
		app.run();
	}
	
	public String filename() {
		return "data/data21";
	}
	
	private static final String REGEX1 = "^(\\w+): (\\d+)";
	private static final String REGEX2 = "(\\w+): (?:(\\w+) ([+\\-*/]) (\\w+))";
	private static final Pattern PATTERN1 = Pattern.compile(REGEX1);
	private static final Pattern PATTERN2 = Pattern.compile(REGEX2);

	public void parseLine(String line) {
		Matcher matcher1 = PATTERN1.matcher(line);
		Matcher matcher2 = PATTERN2.matcher(line);

		if (matcher1.matches()) {
			new Monkey(matcher1.group(1), Long.parseLong(matcher1.group(2)));
		} else if (matcher2.matches()) {
			new Monkey(matcher2.group(1), matcher2.group(2), matcher2.group(3), matcher2.group(4));
		} else {
			throw new IllegalArgumentException( "Could not match pattern with line '" + line + "'");
		}
	}
	
	public void process() {
		Monkey.monkeys.values().forEach(Monkey::findConnections);
	}
	
	public void results() {
		Monkey root = Monkey.find("root");
		System.out.println("Part 1: Root evaluation: " + root.evaluate());
		
		Monkey humn = Monkey.find("humn");
		Monkey left = root.leftMonkey;
		Monkey right = root.rightMonkey;
		Monkey search = null;
		Monkey expression = null;
		
		if (left.contains(humn)) {
			// System.out.println("Left-hand side contains human!");
			// System.out.println("Right-hand evaluation is " + right.evaluate());
			search = left;
			expression = new Monkey("expression", right.evaluate());
		}

		if (right.contains(humn)) {
			// System.out.println("Right-hand side contains human!");
			// System.out.println("Left-hand evaluation is " + left.evaluate());
			search = right;
			expression = new Monkey("expression", left.evaluate());
		}
		
		// "search" contains HUMN somewhere within it, the goal is to peel away operations
		// (moving them onto the "expression" side to keep the equation balanced)
		// until we just have HUMN on the "search" side.
		
		while (!search.name().equals("humn")) {
			// System.out.println("Searching " + search + " for human");
			// System.out.println("Expression is now " + expression);

			left = search.leftMonkey;
			right = search.rightMonkey;

			if (left.contains(humn)) {
				// The easy path, we just have to apply the inverse operation:
				// System.out.println("  Left " + left.name() + " contains human");
				// System.out.println("  Evaluating " + right.name() + " = " + right.evaluate());
								
				switch (search.operator) {
				case "+": // h + c = x --> h = x - c
					expression.applyOperation("-", right.evaluate(), false);
					break;
				case "*": // h * c = x --> h = x / c
					expression.applyOperation("/", right.evaluate(), false);
					break;
				case "-": // h - c = x --> -h = x + c
					expression.applyOperation("+", right.evaluate(), false);
					break;
				case "/": // h / c = x --> h = x * c
					expression.applyOperation("*", right.evaluate(), false);
					break;
				default:
					throw new IllegalArgumentException("Unknown operator '" + search.operator + "'");
				}

				search = left;
			} else if (right.contains(humn)) {
				// The hard path! "+" and "*" are easy since they're commutative.
				// But "inverting" "-" and "/" is a bit trickier
				
				// System.out.println("  Right " + right.name() + " contains human");
				// System.out.println("  Evaluating " + left.name() + " = " + left.evaluate());
				
				// System.out.println("  Updating expression: " + left.evaluate() + " " 
				// + search.operator + " " + expression.name());
				
				switch (search.operator) {
				case "+": // c + h = x --> h = x - c
					expression.applyOperation("-", left.evaluate(), false);
					break;
				case "*": // c * h = x --> h = x / c
					expression.applyOperation("/", left.evaluate(), false);
					break;
				case "-": // c - h = x --> -h = x - c --> h = -(h + c) = (c - h)
					expression.applyOperation("-", left.evaluate(), true);
					break;
				case "/": // c / h = x --> 1/h = x / c --> h = c / x
					expression.applyOperation("/", left.evaluate(), true);
					break;
				default:
					throw new IllegalArgumentException("Unknown operator '" + search.operator + "'");
				}

				search = right;
			} else {
				throw new IllegalArgumentException("Can't find human???");
			}
		}
		
		// 3949235418274 is too high
		// 309 is too low
		// 3099532692128 is too high
		// 3099532691300 is the correct answer!
		
		System.out.println("Part 2: HUMN = " + expression.value());
	}
		
	class Monkey {
		public static Map<String,Monkey> monkeys = new HashMap<>();

		public static Monkey find(String name) {
			return monkeys.get(name);
		}
		
		private String name;
		private long value;
		private String left;
		private String operator;
		private String right;
		
		private Monkey leftMonkey;
		private Monkey rightMonkey;
		
		public Monkey(String name, long value) {
			this.name = name;
			this.value = value;
			monkeys.put(name, this);
		}
		
		public Monkey(String name, String left, String operator, String right) {
			this.name = name;
			this.left = left;
			this.operator = operator;
			this.right = right;
			monkeys.put(name, this);
		}
				
		public String name() {
			return name;
		}
		
		public long value() {
			return value;
		}
		
		public void findConnections() {
			this.leftMonkey = find(left);
			this.rightMonkey = find(right);
		}
		
		public long evaluate() {
			if (operator == null) {
				return value;
			} else {
				switch (operator) {
					case "+":
						return leftMonkey.evaluate() + rightMonkey.evaluate();
					case "-":
						return leftMonkey.evaluate() - rightMonkey.evaluate();
					case "*":
						return leftMonkey.evaluate() * rightMonkey.evaluate();
					case "/":
						return leftMonkey.evaluate() / rightMonkey.evaluate();
					default:
						throw new IllegalArgumentException("Unknown operator '" + operator + "'");
				}
			}
		}
		
		public void applyOperation(String operation, long value, boolean swapOperands) {
			if (operator != null) {
				throw new IllegalArgumentException("Can't perform an operation on a non-valued Monkey");
			} else if (operation != null) {
				switch (operation) {
					case "+":
						this.value = this.value + value;
						break;
					case "-":
						if (swapOperands) {
							this.value = value - this.value;
						} else {
							this.value = this.value - value;
						}
						break;
					case "*":
						this.value = this.value * value;
						break;
					case "/":
						if (swapOperands) {
							this.value = value / this.value;
						} else {
							this.value = this.value / value;
						}
						break;
					default:
						throw new IllegalArgumentException("Unknown operator '" + operation + "'");
				}
			} else {
				throw new IllegalArgumentException("Cannot apply null operation");
			}
		}
		
		public void negate() {
			if (operator != null) {
				throw new IllegalArgumentException("Can't perform an operation on a non-valued Monkey");
			} else {
				this.value = -this.value;
			}
		}
		
		public boolean contains(Monkey m) {
			if (this.name.equals(m.name)) {
				return true;
			} else if (this.operator == null) {
				return false;
			} else {
				return leftMonkey.contains(m) || rightMonkey.contains(m);
			}
		}
		
		public String toString() {
			if (operator == null) {
				return name + ": " + value;
			} else {
				return name + ": " + left + " " + operator + " " + right;
			}
		}
	}
}
