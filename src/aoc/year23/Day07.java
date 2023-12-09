package aoc.year23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.PatternSyntaxException;

import aoc.util.Card;
import aoc.util.PuzzleApp;

public class Day07 extends PuzzleApp {
	public static final Boolean JOKERS = Boolean.TRUE;

	public static final void main(String[] args) {
		System.out.println("December 07: Camel Cards");
		PuzzleApp app = new Day07();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day07-part1";
	}
	
	public List<HandBid> hands = new ArrayList<>();
	
	/*
	 * Deal with the Jokers introduced in Part 2
	 */
	public String jokers(String cards) {
		if (JOKERS) {
			cards = cards.replace('J', '1');
		}
		return cards;
	}
	
	public void parseLine(String line) {
		try {
			String[] cardsAndBid = line.split(" ");
			String cards = jokers(cardsAndBid[0]);
			int bid = Double.valueOf(cardsAndBid[1]).intValue();
			HandBid hand = new HandBid(new Hand(cards), bid);
			// System.out.println("Parsed: " + hand);
			hands.add(hand);
		} catch (PatternSyntaxException e) {
			System.out.flush();
			System.err.println(e.getMessage()); 
			System.err.flush();
		}
	}	
		
	public void process() {
		// Sort the hands so we can rank them:
		
		AtomicInteger rank = new AtomicInteger(0);
		hands.stream().sorted().forEachOrdered(hand -> hand.setRank(rank.incrementAndGet()));
		
		System.out.println(hands);
	}
	
	public void results() {
		int total = hands.stream().mapToInt(hand -> hand.score()).sum();
		System.out.println("Part 1: Total winnings = " + total);
	}
	
	public class HandBid implements Comparable<HandBid> {
		private Hand hand;
		private int bid;
		private int rank;
		
		public HandBid(Hand hand, int bid) {
			this.hand = hand;
			this.bid = bid;
		}
		
		public Hand hand() {
			return hand;
		}
		
		public int bid() {
			return bid;
		}
		
		public void setRank(int rank) {
			this.rank = rank;
		}
		
		public int rank() {
			return rank;
		}
		
		public int score() {
			return bid * rank;
		}
		
		public int compareTo(HandBid other) {
			return this.hand().compareTo(other.hand());
		}
		
		public String toString() {
			return hand + " with bid " + bid + " and rank " + rank;
		}
	}
	
	public class Hand implements Comparable<Hand>{
		private List<Card> cards = new ArrayList<>();
		private HandType type = HandType.UNKNOWN;
		
		public Hand(String cardString) {
			cardString.chars().forEachOrdered(c -> cards.add(Card.getCard((char)c)));			
			
			if (JOKERS) {
				type = HandType.determineWithJokers(cardString);
			} else {
				type = HandType.determine(cardString);
			}
		}
		
		public Card card(int i) {
			return cards.get(i);
		}
		
		public int compareTo(Hand other) {
			int result = this.type.compareTo(other.type);
			
			if (result == 0) {
				for (int i = 0; i < cards.size(); i++) {
					result = this.card(i).compareTo(other.card(i));
					if (result != 0) break;
				}
			}
			
			return result;
		}
		
		public String toString() {
			return "Hand " + cards + " type " + type;
		}
	}
	
	enum HandType {
		/*
		 * Five of a kind, where all five cards have the same label: AAAAA
		 * Four of a kind, where four cards have the same label and one card has a different label: AA8AA
		 * Full house, where three cards have the same label, and the remaining two cards share a different label: 23332
		 * Three of a kind, where three cards have the same label, and the remaining two cards are each different from any other card in the hand: TTT98
		 * Two pair, where two cards share one label, two other cards share a second label, and the remaining card has a third label: 23432
		 * One pair, where two cards share one label, and the other three cards have a different label from the pair and each other: A23A4
		 * High card, where all cards' labels are distinct: 23456
		 */
		
		UNKNOWN, HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND;

		/*
		 * Check for each type, from highest to lowest
		 */
		static HandType determine(String cards) {
			char[] chars = cards.toCharArray();
	        Arrays.sort(chars);
	        String sortedCards = new String(chars);
	        
			if (sortedCards.matches("(.)\\1*")) {
				return FIVE_OF_A_KIND;
			}
			
			if (sortedCards.matches("(.)\\1{3}.") || sortedCards.matches(".(.)\\1{3}")) {
				return FOUR_OF_A_KIND;
			}

			if (sortedCards.matches("(.)\\1(.)\\2{2}") || sortedCards.matches("(.)\\1{2}(.)\\2")) {
				return FULL_HOUSE;
			}

			if (sortedCards.matches("(.)\\1{2}..") || sortedCards.matches(".(.)\\1{2}.") || sortedCards.matches("..(.)\\1{2}")) {
				return THREE_OF_A_KIND;
			}
			
			if (sortedCards.matches("(.)\\1(.)\\2.") || sortedCards.matches("(.)\\1.(.)\\2") || sortedCards.matches(".(.)\\1(.)\\2")) {
				return TWO_PAIR;
			}

			if (sortedCards.matches("(.)\\1...") || sortedCards.matches(".(.)\\1..") || sortedCards.matches("..(.)\\1.") || sortedCards.matches("...(.)\\1")) {
				return ONE_PAIR;
			}

			return HIGH_CARD;
		}
		
		static HandType determineWithJokers(String cards) {
			char[] chars = cards.toCharArray();
	        
	        Map<Card,Integer> cardCounts = new HashMap<>();
	        
	        for (int i = 0; i < chars.length; i++) {
	        	if (chars[i] == Card.JOKER.symbol()) continue; // Skip Jokers!

	        	cardCounts.compute(Card.getCard(chars[i]),
	        			(character, count) -> count == null ? 1 : ++count);
	        }
	        
	        if (cardCounts.size() > 0) {
	        	int maxCount = cardCounts.values()
	        		  .stream()
	        	      .mapToInt(Integer::intValue)
	        	      .max()
	        	      .getAsInt();

	        	Card mostFrequentNonJokerCard = null;
	        
	        	for(Entry<Card,Integer> entry: cardCounts.entrySet()) {
	        		if (entry.getValue() == maxCount) {
	        			if (mostFrequentNonJokerCard == null || mostFrequentNonJokerCard.compareTo(entry.getKey()) < 0) {
	        				mostFrequentNonJokerCard = entry.getKey();
	        			}
	        		}
	        	}

	        	// System.out.println("Replacing Jokers with " + mostFrequentBestCard);
	        
	        	for (int i = 0; i < chars.length; i++) {
	        		if (chars[i] == Card.JOKER.symbol()) {
	        			chars[i] = mostFrequentNonJokerCard.symbol();
	        		}
	        	}
	        }

	        Arrays.sort(chars);
	        String sortedCards = new String(chars);
	        
	        System.out.println(cards + " -> " + sortedCards);
	        
			if (sortedCards.matches("(.)\\1*")) {
				return FIVE_OF_A_KIND;
			}
			
			if (sortedCards.matches("(.)\\1{3}.") || sortedCards.matches(".(.)\\1{3}")) {
				return FOUR_OF_A_KIND;
			}

			if (sortedCards.matches("(.)\\1(.)\\2{2}") || sortedCards.matches("(.)\\1{2}(.)\\2")) {
				return FULL_HOUSE;
			}

			if (sortedCards.matches("(.)\\1{2}..") || sortedCards.matches(".(.)\\1{2}.") || sortedCards.matches("..(.)\\1{2}")) {
				return THREE_OF_A_KIND;
			}
			
			if (sortedCards.matches("(.)\\1(.)\\2.") || sortedCards.matches("(.)\\1.(.)\\2") || sortedCards.matches(".(.)\\1(.)\\2")) {
				return TWO_PAIR;
			}

			if (sortedCards.matches("(.)\\1...") || sortedCards.matches(".(.)\\1..") || sortedCards.matches("..(.)\\1.") || sortedCards.matches("...(.)\\1")) {
				return ONE_PAIR;
			}

			return HIGH_CARD;
		}
	}
}
