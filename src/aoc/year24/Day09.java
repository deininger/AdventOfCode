package aoc.year24;

import aoc.util.PuzzleApp;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day09 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 8: Resonant Collinearity");
        PuzzleApp app = new aoc.year24.Day09();
        app.run();
    }

    public String filename() {
        return "data/year24/day09-small";
    }

    int maxId = 0;
    int maxPosition = 0;
    Map<Integer,File> filesystem = new HashMap<>();
    File freespace = new File(-1);

    public void parseLine(String line) {
        int position = 0;
        for (int i = 0; i < line.length(); i++) {
            int length = line.charAt(i) - '0';
            if (length == 0) continue;
            if (i%2==1) {
                freespace.addBlock(position,length);
            } else {
                File f = filesystem.get(i);
                if (f == null) {
                    f = new File(i/2);
                    filesystem.put(i/2,f);
                }
                f.addBlock(position,length);
            }
            position += length;
        }
        maxId = line.length()/2;
        maxPosition = position;
    }

    public void process() {
        System.out.println("Max ID = " + maxId + ", Max Position = " + maxPosition);
        System.out.println("free space: "+ freespace);
        System.out.println(filesystem);
        System.out.println(printFilesystem());
    }

    /*
     * This is inefficient but we just want it for debugging.
     */
    public String printFilesystem() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxPosition; i++) {
            if (freespace.contains(i)) {
                sb.append('.');
            } else {
                for (File f: filesystem.values()) {
                    if (f.contains(i)) sb.append((char)('0' + f.id()));
                }
            }
        }
        return sb.toString();
    }

    static class File {
        int id;
        Set<Pair<Integer,Integer>> blocks = new HashSet<>();

        public File(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public void addBlock(int start, int length) {
            blocks.add(Pair.of(start, length));
        }

        public boolean contains(int position) {
            for (Pair<Integer,Integer> p: blocks) {
                if (p.getLeft() <= position && position < p.getLeft() + p.getRight()) {
                    return true;
                }
            }
            return false;
        }
        public String toString() {
            return id + ":" + blocks;
        }
    }
}
