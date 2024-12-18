package aoc.util;

import java.util.*;

public class MazeSolver {
    public static Node solve(Loc start, Loc end, LocationValidator validator) {
        // Create a priority queue for nodes to visit
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(start, 0, null));

        // Keep track of best distances from the start node to each location
        Map<Loc, Integer> visited = new HashMap<>();
        visited.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // Check if we've reached the end
            if (current.loc().equals(end)) {
                return current; // Contains both the total distance and the path we used to get to the end
            }

            // Explore neighbors

            current.loc().adjacent().filter(validator::isValid)
                    .forEach(a -> {
                        int newDistance = visited.get(current.loc()) + 1;
                        if (newDistance < visited.getOrDefault(a, Integer.MAX_VALUE)) {
                            visited.put(a, newDistance); // replace any previous distance with this new better one
                            pq.add(new Node(a, newDistance, current));
                        }
                    });
        }

        // No path found
        return null;
    }

    public interface LocationValidator {
        boolean isValid(Loc l);
    }

    public static class Node implements Comparable<Node> {
        private final Loc loc;
        private final int totalDistance;
        private final Node predecessor;

        public Node(Loc loc, int totalDistance, Node predecessor) {
            this.loc = loc;
            this.totalDistance = totalDistance;
            this.predecessor = predecessor;
        }

        public Loc loc() {
            return loc;
        }

        public int totalDistance() {
            return totalDistance;
        }

        public Node predecessor() {
            return predecessor;
        }

        public Set<Loc> path() {
            Set<Loc> path;
            if (predecessor == null) {
                path = new HashSet<>();
            } else {
                path = predecessor.path();
            }
            path.add(loc);
            return path;
        }

        public int compareTo(Node other) {
            return Integer.compare(this.totalDistance, other.totalDistance);
        }

        public String toString() {
            return "PL " + loc + " " + totalDistance;
        }
    }
}
