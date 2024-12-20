package aoc.util;

import java.util.*;

public class MazeSolver {
    public static Node solveWithBreadthFirstSearch(Loc start, Loc end, LocationValidator validator) {
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(start, 0, null));

        // Keep track of visited locations
        Set<Loc> visited = new HashSet<>();

        while (!q.isEmpty()) {
            Node current = q.poll();

            if (current.loc().equals(end)) {
                return current;
            }

            if (visited.contains(current.loc())) continue;

            current.loc().adjacent().filter(validator::isValid)
                    .forEach(a -> q.add(new Node(a, current.totalDistance() + 1, current)));

            visited.add(current.loc());
        }

        return null;
    }

    /*
     * TODO: add a weight function to this algorithm (for calculating the distance
     *       from one node to the next), right now it has a 1 weight hardcoded in.
     *
     * TODO: Need a way to support "Loc+Direction" concept instead of just Loc,
     *       perhaps by parameterizing this class and the embeddeded Node class.
     */
    public static Node solveWithPriorityQueue(Loc start, Loc end, LocationValidator validator) {
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

        /*
         * Returns all the locs from start to this node, in order.
         */
        public List<Loc> path() {
            List<Loc> path = new ArrayList<>();
            path.add(loc);
            Node p = predecessor();
            while (p != null) {
                path.add(p.loc());
                p = p.predecessor();
            }
            return path.reversed();
        }

        public int compareTo(Node other) {
            return Integer.compare(this.totalDistance, other.totalDistance);
        }

        public String toString() {
            return "PL " + loc + " " + totalDistance;
        }
    }
}
