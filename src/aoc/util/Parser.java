package aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
	private String s;
	private int pos;
	
	public Parser(String s) {
		this.s = s;
		this.pos = 0;
	}
	
	public Token nextToken() {
		Token t = new Token();

		if (s.charAt(pos) == '[') {
			pos++; // consume '['

			// What follows is a comma-separated list of zero
			// or more Tokens:
			
			while (s.charAt(pos) != ']') {
				t.addSubtoken(nextToken());
				if (s.charAt(pos) == ',') {
					pos++; // consume ','
				}
			}
			
			if (s.charAt(pos) == ']') {
				pos++; // consume ']'
			} else {
				throw new IllegalArgumentException("Expected ] at pos " + pos);
			}	
		} else {
			// What follows is a number
			int number = 0;
			
			while (s.charAt(pos) >= '0' && s.charAt(pos) <= '9') {
				number = 10 * number + (s.charAt(pos) - '0');
				pos++;
			}

			t.setValue(number);
		}
		
		return t;
	}
	
	public class Token {
		private List<Token> subtokens = new ArrayList<>();
		private Integer value;
		
		public Token() {
		}
		
		public Token(Integer value) {
			this.value = value;
		}
		
		public void addSubtoken(Token subtoken) {
			subtokens.add(subtoken);
		}
		
		public void setValue(Integer value) {
			this.value = value;
		}
		
		public List<Token> subtokens() {
			if (hasValue()) {
				return List.of(new Token(value)); // special case
			}
			return subtokens;
		}
		
		public Integer value() {
			return value;
		}
		
		public boolean hasValue() {
			return value != null;
		}
		
		public String toString() {
			if (hasValue()) {
				return value.toString();
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("[");
				sb.append(subtokens.stream().map(Token::toString).collect(Collectors.joining(",")));
				sb.append("]");
				return sb.toString();
			}
		}
	}
}
