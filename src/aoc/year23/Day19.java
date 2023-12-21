package aoc.year23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aoc.util.PuzzleApp;

public class Day19 extends PuzzleApp {

	public static final void main(String[] args) {
		System.out.println("December 19: Aplenty");
		PuzzleApp app = new Day19();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day19-part1";
	}

	private static final String WORKFLOW_REGEX = "^(\\w+)\\{(.+)\\}$";
	private static final String PART_REGEX = "^\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}$";
	private static final Pattern WORKFLOW_PATTERN = Pattern.compile(WORKFLOW_REGEX);
	private static final Pattern PART_PATTERN = Pattern.compile(PART_REGEX);

	private List<Workflow> workflows = new ArrayList<>();
	private Map<String,Workflow>  workflowMap = new HashMap<>();
	private List<Part> parts = new ArrayList<>();

	public void parseLine(String line) {
		if (line.isEmpty()) return;

		Matcher workflowMatcher = WORKFLOW_PATTERN.matcher(line);
		Matcher partMatcher = PART_PATTERN.matcher(line);

		if (workflowMatcher.matches()) {
			Workflow w = new Workflow(workflowMatcher.group(1));
			workflows.add(w);
			workflowMap.put(w.name(), w);

			String[] ruleStrings = workflowMatcher.group(2).split(",");
			
			for (String ruleString : ruleStrings) {
				String[] ruleAndDestination = ruleString.split(":");

				switch (ruleAndDestination.length) {
					case 0:	System.err.println("Unable to parse rule " + ruleString);
							break;
					case 1:	// No ":" so it's just the name of a destination
							Rule r1 = new Rule(ruleAndDestination[0]);
							w.addRule(r1);
							break;
					case 2: // This should be of the form: [var] < or > [value]
							Rule r2 = new Rule(Var.withSymbol(ruleAndDestination[0].substring(0,1)),
							Op.withSymbol(ruleAndDestination[0].substring(1,2)),
							Integer.parseInt(ruleAndDestination[0].substring(2)),
							ruleAndDestination[1]);
							w.addRule(r2);
							break;
					default: System.err.println("Unable to parse rule " + ruleString);
							break;
				}
			}
		} else if (partMatcher.matches()) {
			Part p = new Part(Integer.valueOf(partMatcher.group(1)),
				Integer.valueOf(partMatcher.group(2)),
				Integer.valueOf(partMatcher.group(3)),
				Integer.valueOf(partMatcher.group(4)));
			parts.add(p);
		} else {
			throw new IllegalArgumentException("Unable to parse line '" + line + "'");
		}
	}

	private List<PartRange> acceptedRanges = new ArrayList<PartRange>();
	private List<PartRange> rejectedRanges = new ArrayList<PartRange>();

	public long countCombinations(PartRange pr, String workflowName) {
		System.out.println("Processing workflow " + workflowName + " with Part Range " + pr);

		if (workflowName.equals("A")) {
			System.out.println("++++ Accepting " + pr + " with count " + pr.combinations());
			acceptedRanges.add(pr);
			return pr.combinations();
		}
		
		if (workflowName.equals("R")) {
			System.out.println("++++ Rejecting " + pr + " with count " + pr.combinations());
			rejectedRanges.add(pr);
			return 0;
		}

		long count = 0;
		Workflow w = workflowMap.get(workflowName);

		PartRange currentRange = new PartRange(pr);

		for (Rule r : w.rules()) {
			System.out.println("  processing rule " + r + " in workflow " + workflowName + " with current range " + currentRange);

			PartRange matchingRange = new PartRange(currentRange);

			switch (r.op) {
				case NONE:	count += countCombinations(currentRange, r.destination);
							break;
				case GT:
					switch(r.var) {
						case X:	matchingRange.x[0] = Math.max(matchingRange.x[0], r.value + 1);
								currentRange.x[1] = Math.min(currentRange.x[1], r.value);
								break;
						case M:	matchingRange.m[0] = Math.max(matchingRange.m[0], r.value + 1);
								currentRange.m[1] = Math.min(currentRange.m[1], r.value);
								break;
						case A:	matchingRange.a[0] = Math.max(matchingRange.a[0], r.value + 1);
								currentRange.a[1] = Math.min(currentRange.a[1], r.value);
								break;
						case S:	matchingRange.s[0] = Math.max(matchingRange.s[0], r.value + 1);
								currentRange.s[1] = Math.min(currentRange.s[1], r.value);
								break;
						case NONE: throw new IllegalArgumentException("Unexpected NONE in Rule " + r);
					}
					System.out.println("  matching range " + matchingRange);
					count += countCombinations(matchingRange, r.destination);
					break;
				case LT:
					switch(r.var) {
						case X:	matchingRange.x[1] = Math.min(matchingRange.x[1], r.value - 1);
								currentRange.x[0] = Math.max(currentRange.x[0], r.value);
								break;
						case M:	matchingRange.m[1] = Math.min(matchingRange.m[1], r.value - 1);
								currentRange.m[0] = Math.max(currentRange.m[0], r.value);
								break;
						case A:	matchingRange.a[1] = Math.min(matchingRange.a[1], r.value - 1);
								currentRange.a[0] = Math.max(currentRange.a[0], r.value);
								break;
						case S:	matchingRange.s[1] = Math.min(matchingRange.s[1], r.value - 1);
								currentRange.s[0] = Math.max(currentRange.s[0], r.value);
								break;
						case NONE: throw new IllegalArgumentException("Unexpected NONE in Rule " + r);
					}
					System.out.println("  matching range " + matchingRange);
					count += countCombinations(matchingRange, r.destination);
					break;
				}

			System.out.println("  after rule " + r + " in workflow " + workflowName + " current range is " + currentRange);
		}

		System.out.println("Completed recursive workflow " + workflowName);
		System.out.println();

		return count;
	}

	public void process() {
		System.out.println("Workflows: " + workflows);
		System.out.println("Parts: " + parts);

		for (Part p : parts) {
			System.out.print(p + " --> ");
			while (!"R".equals(p.nextRule()) && !"A".equals(p.nextRule())) {
				System.out.print(p.nextRule() + " --> ");
				Workflow w = workflowMap.get(p.nextRule());
				w.process(p);
			}
			System.out.println(p.nextRule());
		}

		int totalRating = 0;
		for (Part p : parts) {
			if ("A".equals(p.nextRule())) {
				totalRating += p.rating();
			}
		}

		System.out.println("Part 1: Total Rating = " + totalRating); // 434147

		// Part 2:

		long result = countCombinations(new PartRange(), "in");

		System.out.println("Part 2: Total number of combinations = " + result);

		long acceptedResult = 0;

		for (PartRange pr : acceptedRanges) {
			acceptedResult += pr.combinations();
		}

		System.out.println("Part 2: accepted result = " + acceptedResult);

		long rejecteResult = 0;

		for (PartRange pr : rejectedRanges) {
			rejecteResult += pr.combinations();
		}

		System.out.println("Part 2: rejected result = " + rejecteResult);

		System.out.println("    accepted + rejected = " + (acceptedResult + rejecteResult));
		System.out.println("    should equal 4000^4 = " + (long)Math.pow(4000,4));

	}

	public void results() {
	}

	class Workflow {
		private String name;
		private List<Rule> rules = new ArrayList<>();

		public Workflow(String name) {
			this.name = name;
		}

		public String name() {
			return name;
		}

		public void addRule(Rule rule) {
			rules.add(rule);
		}

		public List<Rule> rules() {
			return rules;
		}

		public void process(Part p) {
			for (Rule r: rules) {
				if (r.matches(p)) {
					p.setNextRule(r.destination);
					break;
				}
			}
		}

		public String toString() {
			return name + " --> " + rules;
		}
	}

	class Rule {
		private Var var;
		private Op op;
		private int value;
		private String destination; // The name of the next rule, or R or A.

		public Rule(String destination) {
			var = Var.NONE;
			op = Op.NONE;
			this.destination = destination;
		}

		public Rule(Var v, Op o, int val, String dest) {
			this.var = v;
			this.op = o;
			this.value = val;
			this.destination = dest;
		}

		public Var var() {
			return var;
		}

		public Op op() {
			return op;
		}

		public int value() {
			return value;
		}

		public boolean matches(Part p) {
			int partVal = 0;

			switch (var) {
				case X: partVal = p.x; break;
				case M: partVal = p.m; break;
				case A: partVal = p.a; break;
				case S: partVal = p.s; break;
				case NONE: return true; // This rule always matches everything
			}

			switch (op) {
				case GT: return partVal > this.value;
				case LT: return partVal < this.value;
				case NONE: 
			}

			throw new IllegalArgumentException("Unable to match, part = " + p + ", rule = " + this);
		}

		public String toString() {
			if (var == Var.NONE) {
				return "" + destination;
			} else {
				return "" + var + op + value + ":" + destination;
			}
		}
	}

	class Part {
		public int x;
		public int m;
		public int a;
		public int s;
		private String nextRule;

		public Part(int x, int m, int a, int s) {
			this.x = x;
			this.m = m;
			this.a = a;
			this.s = s;
			this.nextRule = "in";
		}

		public int rating() {
			return x + m + a + s;
		}

		public void setNextRule(String nextRule) {
			this.nextRule = nextRule;
		}

		public String nextRule() {
			return nextRule;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("{x=").append(x)
				.append(",m=").append(m)
				.append(",a=").append(a)
				.append(",s=").append(s).append("}");

			return sb.toString();
		}
	}

	class PartRange {
		public long[] x = new long[2];
		public long[] m = new long[2];
		public long[] a = new long[2];
		public long[] s = new long[2];

		public PartRange() {
			this.x[0] = 1; this.x[1] = 4000;
			this.m[0] = 1; this.m[1] = 4000;
			this.a[0] = 1; this.a[1] = 4000;
			this.s[0] = 1; this.s[1] = 4000;
		}

		public PartRange(PartRange other) {
			this.x[0] = other.x[0]; this.x[1] = other.x[1];
			this.m[0] = other.m[0]; this.m[1] = other.m[1];
			this.a[0] = other.a[0]; this.a[1] = other.a[1];
			this.s[0] = other.s[0]; this.s[1] = other.s[1];
		}

		public long combinations() {
			return (x[1] - x[0] + 1) * (m[1] - m[0] + 1) * (a[1] - a[0] + 1) * (s[1] - s[0] + 1);
		}

		public boolean overlaps(PartRange other) {
			return (this.x[0] >= other.x[1] || this.x[1] <= other.x[0]) &&
			(this.m[0] >= other.m[1] || this.m[1] <= other.m[0]) &&
			(this.a[0] >= other.a[1] || this.a[1] <= other.a[0]) &&
			(this.s[0] >= other.s[1] || this.s[1] <= other.s[0]);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append("{x=").append(x[0]).append("-").append(x[1])
				.append(",m=").append(m[0]).append("-").append(m[1])
				.append(",a=").append(a[0]).append("-").append(a[1])
				.append(",s=").append(s[0]).append("-").append(s[1])
				.append("}");

			return sb.toString();
		}
	}

	enum Var { 
		X("x"), M("m"), A("a"), S("s"), NONE("");

		public static Var withSymbol(String symbol) {
			for (Var v : Var.values()) {
				if (v.symbol.equals(symbol)) return v;
			}
			return null;
		}

		private final String symbol;

		private Var(String symbol) {
			this.symbol = symbol;
		}

		public String toString() {
			return symbol;
		}
	}

	enum Op { 
		GT(">"), LT("<"), NONE(""); 

		public static Op withSymbol(String symbol) {
			for (Op op : Op.values()) {
				if (op.symbol.equals(symbol)) return op;
			}
			return null;
		}

		private final String symbol;

		private Op(String symbol) {
			this.symbol = symbol;
		}

		public String toString() {
			return symbol;
		}
	}
}