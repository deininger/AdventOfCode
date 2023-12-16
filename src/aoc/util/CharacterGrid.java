package aoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharacterGrid {
    private List<String> rows = new ArrayList<>();

    public CharacterGrid() {
    }

    public CharacterGrid(CharacterGrid other) {
        for (int i = 0; i < other.rows.size(); i++) {
            this.rows.add(other.rows.get(i));
        }
    }

    public void addRow(String row) {
        rows.add(row);
    }

    public int height() {
        return rows.size();
    }

    public int width() {
        return rows.get(0).length();
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
