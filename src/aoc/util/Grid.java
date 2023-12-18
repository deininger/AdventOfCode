package aoc.util;

public class Grid<T> {
	private int height;
	private int width;
	private Element<T>[][] elements;
	
	public Grid(int height, int width) {
		this.height = height;
		this.width = width;
		elements = new Element[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				elements[i][j] = new Element<>();
			}
		}
	}
	
	public Grid(int height, int width, Element<T>[][] elements) {
		this.height = height;
		this.width = width;
		this.elements = elements;
	}
	
	public int height() {
		return height;
	}
	
	public int width() {
		return width;
	}
	
	public Element<T> get(int row, int col) {
		return elements[row][col];
	}
	
	public void set(int row, int col, T value) {
		elements[row][col].setValue(value);
	}
	
	public Grid<T> subgrid(int row, int col, int size) {
		Element<T>[][] subelements = new Element[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				subelements[i][j] = elements[row+i][col+j];
			}
		}
		return new Grid(size, size, subelements);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				sb.append(elements[i][j]).append('\t');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}