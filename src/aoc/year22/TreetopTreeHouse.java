package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TreetopTreeHouse {
	public static final void main(String[] args) throws IOException {
		System.out.println("December  8: Treetop Tree House");

		BufferedReader reader = new BufferedReader(new FileReader("data/Data8"));
		String line = reader.readLine();
		int size = line.length();
		int[][] trees = new int[size][size];

		for (int j = 0; line != null; j++) {
			for (int i = 0; i < size; i++) {
				trees[j][i] = line.charAt(i) - '0';
			}

			line = reader.readLine();
		}

		reader.close();

		printTrees(trees);

		int visibleTrees = analyze(trees);
		
		System.out.println( "Visible Trees: " + visibleTrees);

		int bestVisibility= analyze2(trees);
		
		System.out.println( "Best Visibility Score: " + bestVisibility);
	}
	
	private static void printTrees(int[][] trees) {
		int size = trees.length;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				System.out.print(trees[i][j]);
			}
			System.out.println();
		}
	}
	
	private static int analyze(int[][] trees) {
		int size = trees.length;
		int visibleCount = 0;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (isVisible(trees,i,j)) visibleCount++;
			}
		}
		
		return visibleCount;
	}
	
	private static boolean isVisible(int[][] trees, int i, int j) {
		int size = trees.length;
		int height = trees[i][j];
		
		// Check UP:
		
		boolean visible = true;
		for (int iUp = i-1; iUp >= 0; iUp--) {
			if (trees[iUp][j] >= height) {
				visible = false;
				break;
			}
		}
		
		if (visible) return true;
		
		// Check DOWN:
		
		visible = true;
		for (int iDown = i+1; iDown < size; iDown++) {
			if (trees[iDown][j] >= height) {
				visible = false;
				break;
			}
		}

		if (visible) return true;

		// Check LEFT:
		
		visible = true;
		for (int jLeft = j-1; jLeft >= 0; jLeft--) {
			if (trees[i][jLeft] >= height) {
				visible = false;
				break;
			}
		}
		
		if (visible) return true;

		// Check RIGHT:
		
		visible = true;
		for (int jRight = j+1; jRight < size; jRight++) {
			if (trees[i][jRight] >= height) {
				visible = false;
				break;
			}
		}
		
		if (visible) return true;

		return false;
	}

	private static int analyze2(int[][] trees) {
		int size = trees.length;
		int maximumScore = 0;
		
		int[][] scores = new int[size][size];
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int score = calculateVisibility(trees,i,j);
				scores[i][j] = score;
				maximumScore = Math.max(maximumScore, score);
			}
		}
		
		printTrees(scores);
		return maximumScore;
	}
	
	private static int calculateVisibility(int[][] trees, int i, int j) {
		int size = trees.length;
		int height = trees[i][j];
		
		// Check UP:
		
		int rangeUp = 0;
		for (int iUp = i-1; iUp >= 0; iUp--) {
			if (trees[iUp][j] < height) {
				rangeUp++;
			} else {
				rangeUp++;
				break;
			}
		}
				
		// Check DOWN:
		
		int rangeDown = 0;
		for (int iDown = i+1; iDown < size; iDown++) {
			if (trees[iDown][j] < height) {
				rangeDown++;
			} else {
				rangeDown++;
				break;
			}
		}

		// Check LEFT:
		
		int rangeLeft = 0;
		for (int jLeft = j-1; jLeft >= 0; jLeft--) {
			if (trees[i][jLeft] < height) {
				rangeLeft++;
			} else {
				rangeLeft++;
				break;
			}
		}
		
		// Check RIGHT:
		
		int rangeRight = 0;
		for (int jRight = j+1; jRight < size; jRight++) {
			if (trees[i][jRight] < height) {
				rangeRight++;
			} else {
				rangeRight++;
				break;
			}
		}
		
		return rangeUp * rangeDown * rangeLeft * rangeRight;
	}


}
