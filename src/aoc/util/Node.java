package aoc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Node<T> {
    private T value;
    private Map<Node<T>,Integer> neighbors;

    public Node(T value) {
        this.value = value;
        this.neighbors = new HashMap<>();
    }

    public T getValue() {
        return value;
    }

    public Map<Node<T>,Integer> neighbors() {
        return neighbors;
    }
    
    public Set<Node<T>> getNeighbors() {
        return Collections.unmodifiableSet(neighbors.keySet());
    }

    public Stream<Map.Entry<Node<T>,Integer>> edges() {
        return neighbors.entrySet().stream();
    }

    /*
     * Use this when you want your connections to be directed and weighted.
     */
    public void connect(Node<T> destination, int weight) {
        if (this == destination) throw new IllegalArgumentException("Can't connect node to itself");
        this.neighbors.putIfAbsent(destination, weight);
    }

    /*
     * Use this when you want your connections to be bi-directional an un-weighted.
     */
    public void connect(Node<T> node) {
        if (this == node) throw new IllegalArgumentException("Can't connect node to itself");
        this.neighbors.putIfAbsent(node, 0);
        node.neighbors.putIfAbsent(this, 0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("N[").append(value).append("] --> ");
        neighbors.forEach((k,v) -> {
            sb.append(k.getValue());
            if (v > 0) sb.append("{").append(v).append("}");
            sb.append(" ");
        });
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node<T> other = (Node<T>) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
}