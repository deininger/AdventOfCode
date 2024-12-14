package aoc.util;

import java.util.*;
import java.util.stream.Collectors;

public class CharacterGrid {
    private final List<String> rows = new ArrayList<>();

    public CharacterGrid() {
    }

    public CharacterGrid(CharacterGrid other) {
        this.rows.addAll(other.rows);
    }

    public CharacterGrid(int height, int width, char fill) {
        for (int r = 0; r < height; r++) {
            rows.add(String.valueOf(fill).repeat( width));
        }
    }

    public void addRow(String row) {
        rows.add(row);
    }

    public int height() {
        return rows.size();
    }

    public int width() {
        return rows.getFirst().length();
    }

    public boolean contains(int r, int c) {
        return r >= 0 && r < height() && c >= 0 && c < width();
    }

    public boolean contains(Loc loc) {
        return contains(loc.y(), loc.x());
    }

    public char at(int r, int c) {
        return rows.get(r).charAt(c);
    }

    public char at(Loc loc) {
        return at(loc.y(), loc.x());
    }

    public char safeAt(int r, int c) {
        if (contains(r, c)) return at(r, c);
        else return '.';
    }

    public char wrapAt(Loc loc) {
        int r = loc.y() % height();
        if (r < 0) r += height();
        int c = loc.x() % width();
        if (c < 0) c += width();
        return at(r, c);
    }

    public Loc locate(char x) {
        for (int r = 0; r < rows.size(); r++) {
            for (int c = 0; c < rows.getFirst().length(); c++) {
                if (at(r, c) == x) return new Loc(c, r);
            }
        }
        return null;
    }

    public Set<Loc> locateAll(char x) {
        Set<Loc> matches = new HashSet<>();
        for (int r = 0; r < rows.size(); r++) {
            for (int c = 0; c < rows.getFirst().length(); c++) {
                if (at(r, c) == x) matches.add(new Loc(c, r));
            }
        }
        return matches;
    }

    public void set(int r, int c, char x) {
        String line = rows.remove(r);
        line = line.substring(0, c) + x + line.substring(c + 1);
        rows.add(r, line);
    }

    public void set(Loc loc, char c) {
        set(loc.y(), loc.x(), c);
    }

    public void swap(int startR, int startC, int destR, int destC) {
        char x = at(destR, destC);
        set(destR, destC, at(startR, startC));
        set(startR, startC, x);
    }

    public CharacterGrid overlayPath(Collection<Loc> path, char x) {
        CharacterGrid newGrid = new CharacterGrid(this);

        path.forEach(l -> newGrid.set(l, x));

        return newGrid;
    }

    public CharacterGrid overlayCount(Collection<Loc> locs) {
        CharacterGrid newGrid = new CharacterGrid(this);
        Map<Loc,Long> counts = locs.stream().collect(Collectors.groupingBy(l -> l, Collectors.counting()));
        counts.forEach((k,v) -> newGrid.set(k,(char)('0'+v)));
        return newGrid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        rows.forEach(l -> sb.append(l).append('\n'));
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hash(rows);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CharacterGrid other = (CharacterGrid) obj;
        return Objects.equals(rows, other.rows);
    }
}
