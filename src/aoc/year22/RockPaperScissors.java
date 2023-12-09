package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RockPaperScissors {
	private static final String A_ROCK = "A";
	private static final String B_PAPER = "B";
	private static final String C_SCISSORS = "C";
	
	private static final String X_ROCK = "X";
	private static final String Y_PAPER = "Y";
	private static final String Z_SCISSORS = "Z";

	private static final String X_LOSE = "X";
	private static final String Y_DRAW = "Y";
	private static final String Z_WIN = "Z";

	// Scoring: 1 point if our answer is Rock (X), 2 if our answer is Paper (Y),
	//          3 points if our answer is Scissors (Z), plus 0 points if we lost
	//          the round, 3 points for a draw, 6 points for a win.

	public static final void main(String[] args) throws IOException {
		System.out.println("December  2: Rock Paper Scissors");

		BufferedReader reader = new BufferedReader(new FileReader("data/Data2"));

		String line = reader.readLine();
		int score = 0;
		int alternateScore = 0;
		
		while (line != null) {
			String[] choices = line.split(" ");
			score += analyze(choices[0], choices[1]);
			alternateScore += analyze2(choices[0], choices[1]);
			line = reader.readLine();
		}

		reader.close();

		System.out.println("Part 1 answer is " + score);
		System.out.println("Part 2 answer is " + alternateScore);
	}

	public static String winningChoice(String opponentChoice) {
		String ourChoice = "";
		
		switch (opponentChoice) {
		case A_ROCK:
			ourChoice = Y_PAPER;
			break;
		case B_PAPER:
			ourChoice = Z_SCISSORS;
			break;
		case C_SCISSORS:
			ourChoice = X_ROCK;
			break;
		}
	
		return ourChoice;
	}
	
	public static String losingChoice(String opponentChoice) {
		String ourChoice = "";
		
		switch (opponentChoice) {
		case A_ROCK:
			ourChoice = Z_SCISSORS;
			break;
		case B_PAPER:
			ourChoice = X_ROCK;
			break;
		case C_SCISSORS:
			ourChoice = Y_PAPER;
			break;
		}
	
		return ourChoice;
	}

	public static String drawingChoice(String opponentChoice) {
		String ourChoice = "";
		
		switch (opponentChoice) {
		case A_ROCK:
			ourChoice = X_ROCK;
			break;
		case B_PAPER:
			ourChoice = Y_PAPER;
			break;
		case C_SCISSORS:
			ourChoice = Z_SCISSORS;
			break;
		}
	
		return ourChoice;
	}

	public static int analyze2(String opponentChoice, String outcome) {
		String ourChoice = "";
		
		switch (outcome) {
		case X_LOSE:
			ourChoice = losingChoice(opponentChoice);
			break;
		case Y_DRAW:
			ourChoice = drawingChoice(opponentChoice);
			break;
		case Z_WIN:
			ourChoice = winningChoice(opponentChoice);
			break;
		}

		return analyze(opponentChoice, ourChoice);
	}
	
	public static int analyze(String opponentChoice, String ourChoice) {
		int score = 0;

		switch (ourChoice) {
		case X_ROCK:
			score += 1;
			break;
		case Y_PAPER:
			score += 2;
			break;
		case Z_SCISSORS:
			score += 3;
			break;
		}

		// The draws:

		if (A_ROCK.equals(opponentChoice) && X_ROCK.equals(ourChoice))
			score += 3;
		if (B_PAPER.equals(opponentChoice) && Y_PAPER.equals(ourChoice))
			score += 3;
		if (C_SCISSORS.equals(opponentChoice) && Z_SCISSORS.equals(ourChoice))
			score += 3;

		// The wins:

		if (A_ROCK.equals(opponentChoice) && Y_PAPER.equals(ourChoice))
			score += 6;
		if (B_PAPER.equals(opponentChoice) && Z_SCISSORS.equals(ourChoice))
			score += 6;
		if (C_SCISSORS.equals(opponentChoice) && X_ROCK.equals(ourChoice))
			score += 6;

		// System.out.println("The score for " + opponentChoice + " " + ourChoice + " is " + score);
		return score;
	}
}
