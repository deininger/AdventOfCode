package aoc.util;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class Graph<T> {
	private Node<T> root;
	
	public Graph(Node<T> root) {
		this.root = root;
	}
	
    public static <T> Optional<Node<T>> search(T value, Node<T> start) {
        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.add(start);

        Node<T> currentNode;
        Set<Node<T>> alreadyVisited = new HashSet<>();

        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            // LOGGER.debug("Visited node with value: {}", currentNode.getValue());

            if (currentNode.getValue().equals(value)) {
                return Optional.of(currentNode);
            } else {
                alreadyVisited.add(currentNode);
                queue.addAll(currentNode.getNeighbors());
                queue.removeAll(alreadyVisited);
            }
        }

        return Optional.empty();
    }

}
