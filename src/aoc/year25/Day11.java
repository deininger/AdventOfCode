package aoc.year25;

import aoc.util.Node;
import aoc.util.PuzzleApp;

import java.util.*;

public class Day11 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 11: Reactor");
        PuzzleApp app = new Day11();
        app.run();
    }

    @Override
    public String filename() {
        return "data/year25/day11";
    }

    private final Map<String,Node<String>> nodes = new HashMap<>();

    private Node<String> getOrAddNode(String name) {
        if (nodes.containsKey(name)) return nodes.get(name);
        Node<String> n = new Node<>(name);
        nodes.put(name, n);
        return n;
    }

    @Override
    public void parseLine(String line) {
        String[] parts = line.split(" ");
        Node<String> device = getOrAddNode(parts[0].replace(":", ""));
        for (int i = 1; i < parts.length; i++) {
            Node<String> attachedDevice = getOrAddNode(parts[i]);
            device.connect(attachedDevice, 1);
        }
    }

    private long countPaths(String current, String end, Map<String,Long> cache) {
        if (current.equals(end)) return 1;
        if (cache.containsKey(current)) return cache.get(current);
        Node<String> node = nodes.get(current);
        long result = 0;
        if (node != null) {
            result = node.getNeighbors().stream().mapToLong(n -> countPaths(n.getValue(), end, cache)).sum();
            cache.put(current, result);
        }
        return result;
    }

    private long partOneResult = 0;

    public void process() {
        // Find all paths from "you" to "out" (assumes no cyclical paths)
        partOneResult = countPaths("you", "out", new HashMap<>());
    }

    @Override
    public void results() {
        // System.out.println(nodes);
        System.out.println("Part 1 result: " + partOneResult);
    }

    private long partTwoResult = 0;

    @Override
    public void processPartTwo() {
        // Find all paths from "svr" to "out" (assumes no cyclical paths)
        // by finding the counts for each segment svr-dac, dac-fft, fft-out and multiplying those together,
        // then also finding the counts for svr-fft, fft-dac, dac-out and multiplying those together,
        // and returning the sum of these two counts.

        // This only works because we know there are no cyclical paths, which means we don't have to worry about
        // encountering a path like svr-dac-fft-dac-out

        long svrDacPaths = countPaths("svr", "dac", new HashMap<>());
        System.out.println("SVR-DAC paths:" + svrDacPaths);
        long dacFftPaths = countPaths("dac", "fft", new HashMap<>());
        System.out.println("DAC-FFT paths:" + dacFftPaths);
        long fftOutPaths = countPaths("fft", "out", new HashMap<>());
        System.out.println("FFT-OUT paths:" + fftOutPaths);
        partTwoResult += svrDacPaths * dacFftPaths * fftOutPaths;

        long svrFftPaths = countPaths("svr", "fft", new HashMap<>());
        System.out.println("SVR-FFT paths:" + svrFftPaths);
        long fftDacPaths = countPaths("fft", "dac", new HashMap<>());
        System.out.println("FFT-DAC paths:" + fftDacPaths);
        long dacOutPaths = countPaths("dac", "out", new HashMap<>());
        System.out.println("DAC-OUT paths:" + dacOutPaths);
        partTwoResult += svrFftPaths * fftDacPaths * dacOutPaths;
    }

    @Override
    public void resultsPartTwo() {
        System.out.println("Part 2 result: " + partTwoResult);
    }
}
