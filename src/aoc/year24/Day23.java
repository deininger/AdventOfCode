package aoc.year24;

import aoc.util.Node;
import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class Day23 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 23: LAN Party");
        PuzzleApp app = new Day23();
        app.run();
    }

    public String filename() {
        return "data/year24/day23";
    }

    private final Map<String,Node<String>> computers = new HashMap<>();

    public void parseLine(String line) {
        String[] computerNames = line.split("-");
        Node<String> computerOne = computers.computeIfAbsent(computerNames[0], Node::new);
        Node<String> computerTwo = computers.computeIfAbsent(computerNames[1], Node::new);
        computerOne.connect(computerTwo); // This is a bi-directional connection
    }

    private final List<Triple<String,String,String>> triples = new ArrayList<>();

    public void process() {
        computers.values().stream().sorted().forEach( n1 -> {
            Set<Node<String>> firstConnections = n1.neighbors().keySet().stream()
                    .filter( n -> n.getValue().compareTo(n1.getValue()) > 0)
                    .collect(Collectors.toSet());

            firstConnections.stream().sorted().forEach( n2 -> {
                Set<Node<String>> secondConnections = n2.neighbors().keySet().stream()
                        .filter(n -> n.getValue().compareTo(n2.getValue()) > 0)
                        .collect(Collectors.toSet());

                secondConnections.stream().sorted()
                        .filter( n -> n.neighbors().containsKey(n1))
                        .forEach( n3 -> triples.add(Triple.of(n1.getValue(), n2.getValue(), n3.getValue())));
            });
        });
    }

    public void results() {
        System.out.println("There are " + computers.size() + " computers in our data set, with "
                + computers.values().stream().mapToInt(n -> n.neighbors().size()).sum() + " connections.");

        // triples.stream().filter(t -> t.getLeft().startsWith("t") || t.getMiddle().startsWith("t") || t.getRight().startsWith("t")).forEach(System.out::println);

        System.out.println("Day 23 part 1 results: " +
                triples.stream().filter(t -> t.getLeft().startsWith("t") || t.getMiddle().startsWith("t") || t.getRight().startsWith("t")).count());
    }

    private Collection<Node<String>> findLargestFullyConnectedNodes(Collection<Node<String>> nodes) {
        final ConcurrentMap<Node<String>,Collection<Node<String>>> results = new ConcurrentHashMap<>();

        nodes.parallelStream().forEach( n -> {
            // System.out.println("Examining " + n.getValue());
            Collection<Node<String>> clique = n.maximalClique();
            // System.out.println("After examining " + n.getValue() + ", largest clique is " + clique);
            results.put(n, clique);
        });

        return results.values().stream().max(Comparator.comparingInt(Collection::size)).orElse(Collections.emptySet());
    }

    public void processPartTwo() {
        // BronKerbosch<String> alorithm = new BronKerbosch<>(computers);
        // Collection<Set<String>> cliques = alorithm.getBiggestMaximalCliques();
    }

    public void resultsPartTwo() {
        Collection<Node<String>> clique = findLargestFullyConnectedNodes(computers.values());
        System.out.println("Maximal clique: " + clique.stream().map(Node::getValue).sorted().collect(Collectors.joining(",")));
    }
}
