package aoc.util;

import java.util.Arrays;

public enum Card {
	JOKER(1,'1'), TWO(2,'2'), THREE(3,'3'), FOUR(4,'4'), FIVE(5,'5'),
	SIX(6,'6'), SEVEN(7,'7'), EIGHT(8,'8'), NINE(9,'9'),
	TEN(10,'T'), JACK(11,'J'), QUEEN(12,'Q'), KING(13,'K'),
	ACE(14,'A');
	
	public static Card getCard(char symbol) {
        return Arrays.stream(Card.values())
            .filter(card -> card.symbol() == symbol)
            .findFirst()
            .orElseThrow();
    }
	
	private int value;
	private char symbol;
	
	private Card(int value, char symbol) {
		this.value = value;
		this.symbol = symbol;
	}
	
	public int value() {
		return value;
	}
	
	public char symbol() {
		return symbol;
	}
	
	public String toString() {
		return Character.toString(symbol);
	}
}

