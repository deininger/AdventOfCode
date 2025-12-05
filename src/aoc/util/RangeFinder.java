package aoc.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RangeFinder {
    private final Set<Pair<Long, Long>> ranges = new HashSet<>();

    private boolean isOverlapping(Pair<Long, Long> r1, Pair<Long, Long> r2) {
        return r1.getLeft() <= r2.getRight() && r2.getLeft() <= r1.getRight();
    }

    private Pair<Long, Long> grow(Pair<Long, Long> r1, Pair<Long, Long> r2) {
        return Pair.of(Math.min(r1.getLeft(), r2.getLeft()), Math.max(r1.getRight(), r2.getRight()));
    }

    public void addRange(Pair<Long, Long> range) {
        for (Iterator<Pair<Long, Long>> it = ranges.iterator(); it.hasNext(); ) {
            Pair<Long, Long> r = it.next();
            if (isOverlapping(r, range)) {
                // System.out.println("Growing " + r + " and " + range);
                range = grow(r, range);
                it.remove();
            }
        }
        ranges.add(range);
    }

    public long count() {
        return ranges.stream().mapToLong(r -> r.getRight() - r.getLeft() + 1).sum();
    }

    public int size() {
        return ranges.size();
    }

    public String toString() {
        return ranges.toString();
    }
}
