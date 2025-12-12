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
     * Use this when you want your connections to be bi-directional and un-weighted.
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

    public Collection<Node<T>> maximalClique() {
        Collection<Node<T>> potentialCliques = new HashSet<>(Set.of(this));
        Collection<Node<T>> candidates = new HashSet<>(neighbors.keySet());
        Collection<Node<T>> visited = new HashSet<>();
        Collection<Collection<Node<T>>> cliques = new HashSet<>();

        findCliquesRecursively(potentialCliques, candidates, visited, cliques);
        return cliques.stream().max(Comparator.comparingInt(Collection::size)).orElse(Collections.emptySet());
    }

    private void findCliquesRecursively(Collection<Node<T>> potentialCliques, Collection<Node<T>> candidates, Collection<Node<T>> visited, Collection<Collection<Node<T>>> cliques) {
        List<Node<T>> candidates_array = new ArrayList<>(candidates);
        boolean end = visited.stream().anyMatch(found -> found.isConnectedToAll(candidates));
        if (!end) {
            // for each candidate_node in candidates do
            for (Node<T> candidate : candidates_array) {
                Set<Node<T>> new_candidates = new HashSet<>();
                Set<Node<T>> new_already_found = new HashSet<>();

                // move candidate node to potential_clique
                potentialCliques.add(candidate);
                candidates.remove(candidate);

                // create new_candidates by removing nodes in candidates not
                // connected to candidate node
                for (Node<T> new_candidate : candidates) {
                    if (candidate.isConnectedTo(new_candidate)) {
                        new_candidates.add(new_candidate);
                    }
                }

                // create new_already_found by removing nodes in already_found
                // not connected to candidate node
                for (Node<T> new_found : visited) {
                    if (candidate.isConnectedTo(new_found)) {
                        new_already_found.add(new_found);
                    }
                }

                // if new_candidates and new_already_found are empty
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    // potential_clique is maximal_clique
                    cliques.add(new HashSet<>(potentialCliques));
                }
                else {
                    // recursive call
                    findCliquesRecursively(
                            potentialCliques,
                            new_candidates,
                            new_already_found,
                            cliques);
                }

                // move candidate_node from potential_clique to already_found;
                visited.add(candidate);
                potentialCliques.remove(candidate);
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
