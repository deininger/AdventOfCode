package aoc.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DijkstraGraph<T> {
    private Map<String,Node<T>> nodes = new HashMap<>();

    public void addNode(Node<T> node) {
        nodes.put(node.name(),node);
    }

    public Map<String,Node<T>> getNodes() {
        return nodes;
    }

    public Node<T> getNode(String name) {
        return nodes.get(name);
    }
    
    public static <T> DijkstraGraph<T> calculateShortestPathFromSource(DijkstraGraph<T> graph, Node<T> source) {
        source.setDistance(0);

        Set<Node<T>> settledNodes = new HashSet<>();
        Set<Node<T>> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node<T> currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Entry<Node<T>, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                Node<T> adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }

        return graph;
    }

    private static <T> Node<T> getLowestDistanceNode(Set<Node<T>> unsettledNodes) {
        Node<T> lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;

        for (Node<T> node : unsettledNodes) {
            int nodeDistance = node.distance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static <T> void calculateMinimumDistance(Node<T> evaluationNode, Integer edgeWeigh, Node<T> sourceNode) {
        Integer sourceDistance = sourceNode.distance();

        if (sourceDistance + edgeWeigh < evaluationNode.distance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node<T>> shortestPath = new LinkedList<>(sourceNode.shortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    public static class Node<T> {
        private String name;
        private T value;
        private List<Node<T>> shortestPath = new LinkedList<>();
        private Integer distance = Integer.MAX_VALUE;

        Map<Node<T>, Integer> adjacentNodes = new HashMap<>();

         public Node(String name, T value) {
            this.name = name;
            this.value = value;
        }

       public void addEdge(Node<T> destination, int distance) {
            adjacentNodes.put(destination, distance);
        }

        public String name() {
            return name;
        }

        public T value() {
            return value;
        }

        public List<Node<T>> shortestPath() {
            return shortestPath;
        }

        public Integer distance() {
            return distance;
        }

        public Map<Node<T>, Integer> getAdjacentNodes() {
            return adjacentNodes;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public void setShortestPath(List<Node<T>> shortestPath) {
            this.shortestPath = shortestPath;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public void setAdjacentNodes(Map<Node<T>, Integer> adjacentNodes) {
            this.adjacentNodes = adjacentNodes;
        }
    }
}
