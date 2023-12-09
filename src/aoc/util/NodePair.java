package aoc.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class NodePair {
	private static Map<String,NodePair> nodePairMap = new HashMap<>();
	
	public static Map<String,NodePair> nodePairMap() {
		return nodePairMap;
	}
	
	public static NodePair addNodePair(String name, String left, String right) {
		NodePair nodePair = nodePairMap.computeIfAbsent(name, k -> new NodePair(name));
		NodePair leftNodePair = nodePairMap.computeIfAbsent(left, k -> new NodePair(left));
		NodePair rightNodePair = nodePairMap.computeIfAbsent(right, k -> new NodePair(right));
		nodePair.setElements(leftNodePair, rightNodePair);
		return nodePair;
	}
	
	private String name;
	private Pair<NodePair,NodePair> elements;
	
	public NodePair(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
	public void setElements(NodePair leftNodePair, NodePair rightNodePair) {
		this.elements = Pair.of(leftNodePair, rightNodePair);
	}
	
	public NodePair left() {
		return elements.getLeft();
	}
	
	public NodePair right() {
		return elements.getRight();
	}
	
	public String toString() {
		return name(); // + " (" + elements.getLeft().name() + "," + elements.getRight().name() + ")";
	}
}

