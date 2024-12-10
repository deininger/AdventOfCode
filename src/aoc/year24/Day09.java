package aoc.year24;

import aoc.util.PuzzleApp;

import java.util.*;

public class Day09 extends PuzzleApp {
    public static void main(String[] args) {
        System.out.println("Day 8: Resonant Collinearity");
        PuzzleApp app = new aoc.year24.Day09();
        app.run();
    }

    private final static String symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String filename() {
        return "data/year24/day09";
    }

    SortedSet<FileBlock> filesystem = new TreeSet<>();
    SortedSet<FileBlock> freespace = new TreeSet<>();

    SortedSet<FileBlock> filesystemPartTwo = new TreeSet<>();
    SortedSet<FileBlock> freespacePartTwo = new TreeSet<>();

    public void parseLine(String line) {
        int position = 0;
        for (int i = 0; i < line.length(); i++) {
            int length = line.charAt(i) - '0';
            if (length == 0) continue;
            if (i%2==1) {
                freespace.add(new FileBlock(-1,'.', position, length));
                freespacePartTwo.add(new FileBlock(-1,'.', position, length));
            } else {
                char symbol = symbols.charAt((i/2)%symbols.length());
                filesystem.add(new FileBlock(i/2, symbol, position, length));
                filesystemPartTwo.add(new FileBlock(i/2, symbol, position, length));
            }
            position += length;
        }
    }

    public void process() {
        // System.out.println(printFilesystem(filesystem, freespace));

        while (!freespace.isEmpty() && freespace.first().start() < filesystem.last().start()) {
            FileBlock free = freespace.first();
            FileBlock file = filesystem.last();
            // System.out.println("Filling free " + free + " from " + file);

            if (free.length() >= file.length()) {
                // The file fits entirely into free space, just move it and decrease the free space
                int moved = file.length();
                filesystem.remove(file); // remove the block and add them at the end to get them re-sorted.
                freespace.remove(free);

                FileBlock newFree = new FileBlock(free.id(),free.symbol(),file.start()+file.length()-moved,moved);
                freespace.add(newFree);

                file.setStart(free.start());

                free.setStart(free.start()+moved);
                free.setLength(free.length()-moved);

                filesystem.add(file);
                if (free.length()>0) freespace.add(free);
            } else {
                // We can't move the whole thing, move as much as we can (split the file)
                int moved = free.length();
                filesystem.remove(file); // remove the block and add them at the end to get them re-sorted.
                freespace.remove(free);

                FileBlock newFile = new FileBlock(file.id(),file.symbol(),free.start(),moved);
                filesystem.add(newFile);

                file.setLength(file.length()-moved); // Shrink the remaining end file

                free.setStart(file.start()+file.length()); // Move the free space to the end

                filesystem.add(file);
                freespace.add(free);
            }
        }
    }

    public void results() {
        // System.out.println(printFilesystem(filesystem, freespace));
        long checksum = filesystem.stream().mapToLong(FileBlock::checksum).sum();
        System.out.println("Day 9 part 1 result: " + checksum);
    }

    public void processPartTwo() {
        // System.out.println(printFilesystem(filesystemPartTwo, freespacePartTwo));

        boolean makingProgress = true;
        while (makingProgress) {
            makingProgress = false;

            for (FileBlock free: freespacePartTwo) {
                Optional<FileBlock> oFile = filesystemPartTwo.reversed().stream().filter(f -> f.start() > free.start() && f.length() <= free.length()).findFirst();
                if (oFile.isPresent()) {
                    FileBlock file = oFile.get();
                    // We have found a file that fits, move it:

                    int moved = file.length();
                    filesystemPartTwo.remove(file); // remove the block and add them at the end to get them re-sorted.
                    freespacePartTwo.remove(free);

                    FileBlock newFree = new FileBlock(free.id(),free.symbol(),file.start()+file.length()-moved,moved);
                    freespacePartTwo.add(newFree);

                    file.setStart(free.start());

                    free.setStart(free.start()+moved);
                    free.setLength(free.length()-moved);


                    filesystemPartTwo.add(file);
                    if (free.length()>0) freespacePartTwo.add(free);

                    makingProgress = true;
                    break;
                }
            }
        }
    }

    public void resultsPartTwo() {
        // System.out.println(printFilesystem(filesystemPartTwo, freespacePartTwo));
        long checksum = filesystemPartTwo.stream().mapToLong(FileBlock::checksum).sum();
        System.out.println("Day 9 part 2 result: " + checksum);
    }

    /*
     * This is inefficient, but we just want it for debugging.
     */
    private String printFilesystem(SortedSet<FileBlock> fs1, SortedSet<FileBlock> fs2) {
        StringBuilder sb = new StringBuilder();
        SortedSet<FileBlock> s = new TreeSet<>(fs1);
        s.addAll(fs2);

        for (FileBlock fb: s) {
            for (int i = 0; i < fb.length(); i++) {
                sb.append(fb.symbol());
            }
        }
        return sb.toString();
    }

    static class FileBlock implements Comparable<FileBlock> {
        private final long id;
        private final char symbol;
        private int start;
        private int length;

        public FileBlock(long id, char symbol, int start, int length) {
            this.id = id;
            this.symbol = symbol;
            this.start = start;
            this.length = length;
        }

        public long id() {
            return id;
        }

        public char symbol() {
            return symbol;
        }

        public int start() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int length() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }


        public long checksum() {
            long checksum = 0;
            for (int i = start; i < start + length; i++) {
                checksum += id * i;
            }
            return checksum;
        }

        public boolean contains(int position) {
            return (start <= position && position < start + length);
        }

        public String toString() {
            return id + " [" + symbol + "]: (" + start + "-" + (start+length-1) + ")";
        }

        @Override
        public int compareTo(FileBlock o) {
            return Integer.compare(start, o.start);
        }

    }
}
