package aoc.util;

import java.util.*;

/*
 * I adapted this code from https://github.com/liziliao/Bron-Kerbosch/blob/master/Bron-Kerbosch.java
 * to use a Map of Nodes instead of a Graph. The Map provides a convenient lookup of Node by its name (V),
 * and each Node holds a Map of its neighbors, which is used to determine adjacency.
 *
 * The purpose of this algorithm is to efficiently find the maximal cliques of the graph (set of Nodes).
 * A "clique" is a set of Nodes which are all neighbors of each other.
 */
public class BronKerbosch<V> {

    private final Map<V, Node<V>> nodes;
    private Collection<Set<V>> cliques;

    public BronKerbosch(Map<V, Node<V>> nodes) {
        this.nodes = nodes;
    }

    private boolean areNeighbors(V v1, V v2) {
        return nodes.get(v1).neighbors().containsKey(nodes.get(v2));
    }

    /**
     * Finds all maximal cliques of the graph. A clique is maximal if it is
     * impossible to enlarge it by adding another vertex from the graph. Note
     * that a maximal clique is not necessarily the biggest clique in the graph.
     *
     * @return Collection of cliques (each of which is represented as a Set of
     * vertices)
     */
    public Collection<Set<V>> getAllMaximalCliques() {
        // TODO:  assert that graph is simple

        cliques = new ArrayList<>();
        List<V> potential_clique = new ArrayList<>();
        List<V> candidates = new ArrayList<>();
        List<V> already_found = new ArrayList<>();
        candidates.addAll(nodes.keySet());
        findCliques(potential_clique, candidates, already_found);
        return cliques;
    }

    /**
     * Finds the biggest maximal cliques of the graph.
     *
     * @return Collection of cliques (each of which is represented as a Set of
     * vertices)
     */
    public Collection<Set<V>> getBiggestMaximalCliques() {
        // first, find all cliques
        getAllMaximalCliques();

        int maximum = 0;
        Collection<Set<V>> biggest_cliques = new ArrayList<>();
        for (Set<V> clique : cliques) {
            if (maximum < clique.size()) {
                maximum = clique.size();
            }
        }
        for (Set<V> clique : cliques) {
            if (maximum == clique.size()) {
                biggest_cliques.add(clique);
            }
        }
        return biggest_cliques;
    }

    private void findCliques(
            List<V> potential_clique,
            List<V> candidates,
            List<V> already_found) {
        List<V> candidates_array = new ArrayList<>(candidates);
        if (!end(candidates, already_found)) {
            // for each candidate_node in candidates do
            for (V candidate : candidates_array) {
                List<V> new_candidates = new ArrayList<>();
                List<V> new_already_found = new ArrayList<>();

                // move candidate node to potential_clique
                potential_clique.add(candidate);
                candidates.remove(candidate);

                // create new_candidates by removing nodes in candidates not
                // connected to candidate node
                for (V new_candidate : candidates) {
                    if (areNeighbors(candidate, new_candidate)) {
                        new_candidates.add(new_candidate);
                    }
                }

                // create new_already_found by removing nodes in already_found
                // not connected to candidate node
                for (V new_found : already_found) {
                    if (areNeighbors(candidate, new_found)) {
                        new_already_found.add(new_found);
                    }
                }

                // if new_candidates and new_already_found are empty
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    // potential_clique is maximal_clique
                    cliques.add(new HashSet<>(potential_clique));
                }
                else {
                    // recursive call
                    findCliques(
                            potential_clique,
                            new_candidates,
                            new_already_found);
                }

                // move candidate_node from potential_clique to already_found;
                already_found.add(candidate);
                potential_clique.remove(candidate);
            }
        }
    }

    private boolean end(List<V> candidates, List<V> already_found) {
        // if a node in already_found is connected to all nodes in candidates
        boolean end = false;
        int edgecounter;
        for (V found : already_found) {
            edgecounter = 0;
            for (V candidate : candidates) {
                if (areNeighbors(found, candidate)) {
                    edgecounter++;
                }
            }
            if (edgecounter == candidates.size()) {
                end = true;
            }
        }
        return end;
    }
}