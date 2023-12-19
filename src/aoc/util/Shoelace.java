package aoc.util;

import java.math.BigDecimal;
import java.util.List;

public class Shoelace {
    /*
     * Provide a list of (x,y) locations.
     */
    public static long calculateArea(List<Loc> points) {
        if (!points.get(0).equals( points.get(points.size()-1))) {
            points.add(points.get(0)); // Ensure the first and last point are identical
        }

        long n = points.size();
        long area = 0L;

        // add product of x coordinate of ith point with y coordinate of (i + 1)th point
        for (int i = 0; i < n - 1; i++) {
            long x = points.get(i).x();
            long y = points.get(i+1).y();
            area += x * y;
        }

        // subtract product of y coordinate of ith point with x coordinate of (i + 1)th point
        for (int i = 0; i < n - 1; i++) {
            long y = points.get(i).y();
            long x = points.get(i+1).x();
            area -= y * x;
        }
 
        // find absolute value and divide by 2
        return Math.abs(area) / 2;
    }
}
