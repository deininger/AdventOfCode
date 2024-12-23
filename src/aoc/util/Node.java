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



    /*
    private void findCliques(List<List<Integer>> graph, List<Integer> clique, int v, int maxCliqueSize, List<Integer> largestClique) {
        for (int u : graph.get(v)) {
            if (!clique.contains(u) && isConnectedToAll(graph, u, clique)) {
                List<Integer> newClique = new ArrayList<>(clique);
                newClique.add(u);

                if (newClique.size() > maxCliqueSize) {
                    maxCliqueSize = newClique.size();
                    largestClique.clear();
                    largestClique.addAll(newClique);
                }

                findCliques(graph, newClique, u, maxCliqueSize, largestClique);
            }
        }
    }
    */
        /*
    public Set<Node<T>> findLargestFullyConnectedGraph() {
        Set<Node<T>> largestGraph = new HashSet<>();

        if (this.neighbors.isEmpty()) {
            return largestGraph;
        }

        Set<Node<T>> graph = new HashSet<>();
        Queue<Node<T>> q = new ArrayDeque<>();
        q.add(this);

        while (!q.isEmpty()) {
            Node<T> n = q.remove();
            if (n.isFullyConnected(graph)) graph.add(n);
            q.addAll(n.neighbors.keySet());

        }

        // Iterate through all subsets of neighbors, starting with the largest
        for (int i = this.neighbors.size(); i >= 1; i--) {
            for (Set<Node<T>> subset : combinations(this.neighbors.keySet(), i)) {
                Set<Node<T>> potentialGraph = new HashSet<>(subset);
                potentialGraph.add(this); // include the starting node.

                if (isFullyConnected(potentialGraph)) {
                    // No need to look for smaller subsets once we have found a full graph.
                    return potentialGraph;
                }
            }
        }

        return largestGraph; // Return empty set if no fully connected graph is found.
    }

    private boolean isFullyConnected(Set<Node<T>> nodes) {
        return nodes.stream().allMatch(node -> node.isConnectedToAll(nodes));
    }

    private static <T> Set<Set<T>> combinations(Set<T> set, int k) {
        Set<Set<T>> result = new HashSet<>();
        if (k == 0) {
            result.add(new HashSet<>());
            return result;
        }
        if (k > set.size()) {
            return result;
        }

        List<T> list = new ArrayList<>(set);
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            Set<T> rest = new HashSet<>(list.subList(i + 1, list.size()));
            for (Set<T> subset : combinations(rest, k - 1)) {
                subset.add(element);
                result.add(subset);
            }
        }
        return result;
    }
*/

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
