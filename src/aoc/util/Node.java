package aoc.util;

import java.util.*;
import java.util.stream.Stream;

public class Node<T> implements Comparable<Object> {
    private final T value;
    private final Map<Node<T>,Integer> neighbors;

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

    public boolean isConnectedTo(Node<T> node) {
        return neighbors.containsKey(node);
    }

    public boolean isConnectedToAll(Collection<Node<T>> nodes) {
        return nodes.stream().allMatch(this::isConnectedTo);
    }

    public void findCliques(Collection<Node<T>> clique, int maxCliqueSize, Collection<Node<T>> largestClique) {
        for (Node<T> n : neighbors.keySet()) {
            if (!clique.contains(n) && n.isConnectedToAll(clique)) {
                Set<Node<T>> newClique = new HashSet<>(clique);
                newClique.add(n);

                if (newClique.size() > maxCliqueSize) {
                    maxCliqueSize = newClique.size();
                    largestClique.clear();
                    largestClique.addAll(newClique);
                }

                n.findCliques(newClique, maxCliqueSize, largestClique);
            }
        }
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

    public int compareTo(Object o) {
        Node<T> other = (Node<T>) o;
        if (this.value instanceof Comparable && other.value instanceof Comparable) {
            // Use compareTo if T implements Comparable and values are not null
            Comparable<T> thisValue = (Comparable<T>) this.value;
            Comparable<T> otherValue = (Comparable<T>) other.value;

            return thisValue.compareTo(other.value);
        } else {
            // Fallback comparison based on hashCode if T doesn't implement Comparable
            return Integer.compare(
                    this.value == null ? 0 : this.value.hashCode(),
                    other.value == null ? 0 : other.value.hashCode());
        }
    }
}
