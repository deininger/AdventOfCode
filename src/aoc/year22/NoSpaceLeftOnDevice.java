package aoc.year22;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Find all of the directories with a total size of at most 100000. What is the
 * sum of the total sizes of those directories?
 * 
 * @author david
 *
 */
public class NoSpaceLeftOnDevice {
	private static class Node {
		private String name;
		private int size;
		private List<Node> items = new ArrayList<>();
		private Node parent;

		public Node(String name) {
			// directory constructor
			this.name = name;
		}

		public Node(String name, int size) {
			// file node constructor
			this.name = name;
			this.size = size;
		}

		public void addItem(Node item) {
			this.items.add(item);
			item.setParent(this);
		}

		public Node getItem(String name) {
			return items.stream().filter(n -> n.name.equals(name)).findFirst().get();
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public Node getParent() {
			return parent;
		}

		public int size() {
			return size + items.stream().map(Node::size).collect(Collectors.summingInt(Integer::intValue));
		}

		public String toString() {
			return this.name + " (" + this.size() + ")\n  "
					+ items.stream().map(Node::toString).collect(Collectors.joining("\n  "));
		}

		public Stream<Node> allDirectories() {
			if (this.items.isEmpty()) {
				return Stream.empty(); // we are not a directory!
			} else {
				return Stream.concat(Stream.of(this), items.stream().flatMap(Node::allDirectories));
			}
		}

		public String nameAndSize() {
			return this.name + " (" + this.size() + ")";
		}
	}

	private static Node root = new Node("/");
	private static Node cwd = root;

	public static final void main(String[] args) throws IOException {
		System.out.println("December  7: No Space Left On Device");

		BufferedReader reader = new BufferedReader(new FileReader("data/Data7"));
		String line = reader.readLine();

		while (line != null) {
			analyze(line);
			line = reader.readLine();
		}

		reader.close();

		// System.out.println(root.toString());

		int totalSize = root.allDirectories().filter(n -> n.size() <= 100000).map(Node::size).collect(Collectors.summingInt(Integer::intValue));

		System.out.println("Total size of all directories <= 100000 is " + totalSize);
		
		int neededFreeSpace = 30000000;
		int currentFreeSpace = 70000000 - root.size();
		int spaceToFree = neededFreeSpace - currentFreeSpace;
		
		System.out.println("Need to free up additional " + spaceToFree);

		// root.allDirectories()
		//		.sorted((Node n1, Node n2) -> n1.size() - n2.size())
		//		.forEach(n -> System.out.println(n.nameAndSize()));
		
		Node directoryToDelete = root.allDirectories()
				.sorted((Node n1, Node n2) -> n1.size() - n2.size())
				.filter(n -> n.size() >= spaceToFree)
				.findFirst().get();
		
		System.out.println("The directory to delete is " + directoryToDelete.nameAndSize());
	}

	private static void analyze(String line) {
		// System.out.println("Analyzing '" + line + "', cwd: " + cwd);

		if (line.startsWith("$ cd ")) {
			String dir = line.substring(5);

			if ("/".equals(dir)) {
				cwd = root; // go to root dir
			} else if ("..".equals(dir)) {
				cwd = cwd.getParent(); // go to parent dir
			} else {
				cwd = cwd.getItem(dir); // go into dir
			}
		} else if (line.startsWith("$ ls")) {
			// nothing to do here...
		} else {
			String[] parts = line.split(" ");
			if ("dir".equals(parts[0])) {
				cwd.addItem(new Node(parts[1])); // add a dir
			} else {
				cwd.addItem(new Node(parts[1], Integer.parseInt(parts[0]))); // add a file
			}
		}
	}
}
