package aoc.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class Graph<T> {
    private Map<T,Node<T>> nodes = new HashMap<>();
	private Node<T> root;
	
	public Graph(Node<T> root) {
		this.root = root;
        nodes.put(root.getValue(), root);
	}
	
    public Node<T> root() {
        return root;
    }
    
    public void add(Node<T> node) {
        nodes.put(node.getValue(), node);
    }

    public Node<T> get(T value) {
        return nodes.get(value);
    }

    public int size() {
        return nodes.size();
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
