package aoc.util;

public class CubeSurface<T> {
	private int cubeSize;
	private Grid<T> topFace;
	private Grid<T> frontFace;
	private Grid<T> leftFace;
	private Grid<T> rightFace;
	private Grid<T> backFace;
	private Grid<T> bottomFace;

	public CubeSurface(int cubeSize) {
		this.cubeSize = cubeSize;
	}
	
	public void stitchTopFace(Grid<T> topFace) {
		this.topFace = topFace;
		
		if (frontFace != null) {
			for (int i = 0; i < cubeSize; i++) {
				topFace.get(cubeSize-1, i).addNeighbor(Direction.DOWN, frontFace.get(0, i));
				frontFace.get(0, i).addNeighbor(Direction.UP, topFace.get(0, i));
			}
		}
	}
	
	public void stitchFrontFace(Grid<T> frontFace) {
		this.frontFace = frontFace;
	}

	public void stitchLeftFace(Grid<T> leftFace) {
		this.leftFace = leftFace;
	}

	public void stitchRightFace(Grid<T> rightFace) {
		this.rightFace = rightFace;
	}

	public void stitchBackFace(Grid<T> backFace) {
		this.backFace = backFace;
	}

	public void stitchBottomFace(Grid<T> bottomFace) {
		this.bottomFace = bottomFace;
	}

}
