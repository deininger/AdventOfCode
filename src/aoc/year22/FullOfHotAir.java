package aoc.year22;

import java.util.ArrayList;
import java.util.List;

import aoc.util.PuzzleApp;

public class FullOfHotAir extends PuzzleApp {
	public static final void main(String[] args) {
		System.out.println("December 25: Full of Hot Air");
		PuzzleApp app = new FullOfHotAir();
		app.run();
	}

	public String filename() {
		return "data/data25";
	}

	List<Snafu> snafus = new ArrayList<>();
	
	public void parseLine(String line) {
		Snafu snafu = new Snafu(line);
		snafus.add(snafu);
	}
	
	public void process() {
		long total = snafus.stream().mapToLong(Snafu::decimalValue).sum();
		System.out.println("Total decimal value: " + total + " --> " + Snafu.convertFromDecimal(total));
	}
	
	public void results() {
		// for (Snafu snafu : snafus) {
		// 	System.out.println( snafu + " --> " + snafu.decimalValue() + " --> " + Snafu.convertFromDecimal(snafu.decimalValue()));
		// }
		
//		System.out.println("        0 --> " + Snafu.convertFromDecimal(0));
//		System.out.println("        1 --> " + Snafu.convertFromDecimal(1));
//		System.out.println("        2 --> " + Snafu.convertFromDecimal(2));
//		System.out.println("        3 --> " + Snafu.convertFromDecimal(3));
//		System.out.println("        4 --> " + Snafu.convertFromDecimal(4));
//		System.out.println("        5 --> " + Snafu.convertFromDecimal(5));
//		System.out.println("        6 --> " + Snafu.convertFromDecimal(6));
//		System.out.println("        7 --> " + Snafu.convertFromDecimal(7));
//		System.out.println("        8 --> " + Snafu.convertFromDecimal(8));
//		System.out.println("        9 --> " + Snafu.convertFromDecimal(9));
//		System.out.println("       10 --> " + Snafu.convertFromDecimal(10));
//		System.out.println("       15 --> " + Snafu.convertFromDecimal(15));
//		System.out.println("       20 --> " + Snafu.convertFromDecimal(20));
//		System.out.println("     2022 --> " + Snafu.convertFromDecimal(2022));
//		System.out.println("    12345 --> " + Snafu.convertFromDecimal(12345));
//		System.out.println("314159265 --> " + Snafu.convertFromDecimal(314159265));
	}

	class Snafu {
		private static final int BASE = 5;
				
		private static int charValue(char c) {
			return switch (c) {
			case '2' -> 2;
			case '1' -> 1;
			case '0' -> 0;
			case '-' -> -1;
			case '=' -> -2;
			default -> throw new IllegalArgumentException("Unknown Snafu digit '" + c + "'");
			};
		}
		
		public static char snafuDigit(int digit) {
			return switch(digit) {
			case 2 -> '2';
			case 1 -> '1';
			case 0 -> '0';
			case -1 -> '-';
			case -2 -> '=';
			default -> throw new IllegalArgumentException("Can't convert '" + digit + "' to snafu digit");
			};
		}
		
		public static String convertFromDecimal(long decimalValue) {
			// System.out.println("Converting " + decimalValue);
			
			List<Integer> baseFiveDigits = new ArrayList<>();
			
			// First convert to "normal" base 5:
			
			int place = 0;
			
			while (decimalValue > Math.pow(BASE, place + 1)) {
				place++;
			}
			
			while (place >= 0) {
				int dividend = (int)(decimalValue / Math.pow(BASE, place));
				baseFiveDigits.add(dividend);
				decimalValue -= dividend * Math.pow(BASE, place);
				place--;
			}
			
			// StringBuffer sb = new StringBuffer();
			// for (int i = 0; i < baseFiveDigits.size(); i++) {
			// 	sb.append(baseFiveDigits.get(i));
			// }
			// System.out.println("Normal base 5: " + sb.toString());
			
			StringBuilder snafuStringBuilder = new StringBuilder();

			int carry = 0;
			for (int i = baseFiveDigits.size() - 1; i >= 0 ; i--) {
				int digit = baseFiveDigits.get(i) + carry;
				if (digit > 2) { digit = digit - 5; carry = 1; }
				else { carry = 0; }
				snafuStringBuilder.append(snafuDigit(digit));
			}
			
			if (carry > 0) {
				snafuStringBuilder.append(snafuDigit(carry));
			}
			
			return snafuStringBuilder.reverse().toString();
		}
		
		private String value;
		
		public Snafu(String value) {
			this.value = value;
		}
		
		public long decimalValue() {
			long decimalValue = 0;
			long place = 1;
			
			for (int i = value.length() - 1; i >= 0; i--) {
				decimalValue += charValue(value.charAt(i)) * place;
				place *= BASE;
			}
			
			return decimalValue;
		}
		
		public String toString() {
			return value;
		}
	}
}
