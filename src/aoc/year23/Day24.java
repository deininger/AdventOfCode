package aoc.year23;

import java.util.ArrayList;
import java.util.List;

import aoc.util.PuzzleApp;

public class Day24 extends PuzzleApp {
	private static long MIN_XY = 0; // 200000000000000l;
	private static long MAX_XY = 400000000000000l;

	public static final void main(String[] args) {
		System.out.println("December 24: Never Tell Me The Odds");
		PuzzleApp app = new Day24();
		app.run();
	}

	@Override
	public String filename() {
		return "data/year23/day24-part1";
	}

	private List<Hailstone> hailstones = new ArrayList<>();

	public void parseLine(String line) {
		String[] pv = line.split("@");
		String[] p = pv[0].split(",");
		String[] v = pv[1].split(",");
		Hailstone h = new Hailstone(Long.parseLong(p[0].trim()), Long.parseLong(p[1].trim()), Long.parseLong(p[2].trim()),
				Long.parseLong(v[0].trim()), Long.parseLong(v[1].trim()), Long.parseLong(v[2].trim()));
		hailstones.add(h);
	}

	private String xycoefficients(Hailstone h1, Hailstone h2) {
		// (H2vy - H1vy) * Rx + (H1vx - H2vx) * Ry + (H1y - H2y) * Rvx + (H2x- H1x) * Rvy + (H1x * H1vy - H1y * H1vx + H2y * H2vx - H2x * H2vy) = 0

		long c1 = h2.vy - h1.vy;
		long c2 = h1.vx - h2.vx;
		long c3 = h1.py - h2.py;
		long c4 = h2.px - h1.px;
		long c5 = h1.px * h1.vy - h1.py * h1.vx + h2.py * h2.vx - h2.px * h2.vy;

		return "coefficients: " + c1 + "a + " + c2 + "b + " + c3 + "c + " + c4 + "d + " + c5 + " = 0";	
	}

	private String xzcoefficients(Hailstone h1, Hailstone h2) {
		// (H2vz - H1vz) * Rx + (H1vx - H2vx) * Rz + (H1z - H2z) * Rvx + (H2x- H1x) * Rvz + (H1x * H1vz - H1z * H1vx + H2z * H2vx - H2x * H2vz) = 0

		long c1 = h2.vz - h1.vz;
		long c2 = h1.vx - h2.vx;
		long c3 = h1.pz - h2.pz;
		long c4 = h2.px - h1.px;
		long c5 = h1.px * h1.vz - h1.pz * h1.vx + h2.pz * h2.vx - h2.px * h2.vz;

		return "coefficients: " + c1 + "a + " + c2 + "b + " + c3 + "c + " + c4 + "d + " + c5 + " = 0";	
	}

	private int intersectionCount = 0;

	public void process() {
		for (int i = 0; i < hailstones.size(); i++) {
			for (int j = i+1; j < hailstones.size(); j++) {
				Double[] intersectionPoint = hailstones.get(i).intersection(hailstones.get(j));

				if (intersectionPoint[0] != null && intersectionPoint[1] != null 
					&& intersectionPoint[0] >= MIN_XY && intersectionPoint[1] >= MIN_XY
					&& intersectionPoint[0] <= MAX_XY && intersectionPoint[1] <= MAX_XY) {
						// System.out.println("Intersection of " + hailstones.get(i) + " and " + hailstones.get(j) 
						//		+ " is: " + intersectionPoint[0] + "," + intersectionPoint[1]);
						intersectionCount++;
					}
			}
		}

		
		for (int i = 0; i < hailstones.size(); i++) {
			System.out.println(i);
			for (int j = 0; j < hailstones.size(); j++) {
				if (i == j) continue;

				Hailstone hi = hailstones.get(i);
				Hailstone hj = hailstones.get(j);
				
			}
		}

		System.out.println(xycoefficients(hailstones.get(0), hailstones.get(1)));
		System.out.println(xycoefficients(hailstones.get(1), hailstones.get(2)));
		System.out.println(xycoefficients(hailstones.get(2), hailstones.get(3)));
		System.out.println(xycoefficients(hailstones.get(3), hailstones.get(4)));

		// coefficients: -2 , -1 , -6 , -1 , 44
		// coefficients: -1 , 1 , -6 , 2 , -9
		// coefficients: 0 , -1 , -6 , -8 , 3
		// coefficients: -3 , -2 , 12 , 8 , 126
		//
		// Wolfram Alpha says: a = 24 and b = 13 and c = -3 and d = 1
		// which translates to Rx = 24, Ry = 13, Rvx = -3, Rvy = 1
		//
		// Wolfram Alpha says: a = 334948624416533 and b = 371647004954419 and c = -86 and d = -143
		

		System.out.println(xzcoefficients(hailstones.get(0), hailstones.get(1)));
		System.out.println(xzcoefficients(hailstones.get(1), hailstones.get(2)));
		System.out.println(xzcoefficients(hailstones.get(2), hailstones.get(3)));
		System.out.println(xzcoefficients(hailstones.get(3), hailstones.get(4)));

		// coefficients: 0 , -1 , 8 , -1 , 36
		// coefficients: -2 , 1 , -12 , 2 , -2
		// coefficients: 3 , -1 , 6 , -8 , -28
		// coefficients: -2 , -2 , 13 , 8 , 91
		//
		// Wolfram Alpha says: a = 24 and b = 10 and c = -3 and d = 2
		// which translates to Rx = 24, Rz = 10, Rvx = -3, Rvz = 2
		//
		// Wolfram Alpha says: a = 334948624416533 and b = 142351957892081 and c = -86 and d = 289

		// So the final answer for the Rock is position (24, 13, 10) and velocity (-3, 1, 2);
		// For the "real" data, final answer is (334948624416533, 371647004954419, 142351957892081) @ (-86, -143, 289)

		System.out.println("Sum of X, Y, and Z coordinates of Rock is: " + (334948624416533l + 371647004954419l + 142351957892081l));


		// For Part 2, let's solve a system of equations:
		//
		// Given a Rock with initial coordinates (rpx, rpy, rpz) and initial velocity (rvx, rvy, rvz)
		//
		// At time t1, the Rock intersects with Hailstone h:
		//    rpx + rvx * t1 = hpx + hvx * t1  -->  t1 = (hpx - rpx) / (rvx - hvx)
		//    rpy + rvy * t1 = hpy + hvy * t1  -->  t1 = (hpy - rpy) / (rvy - hvy)
		//    rpz + rvz * t1 = hpz + hvz * t1  -->  t1 = (hpz - rpz) / (rvz - hvz)
		// Equating the t1 gives us:
		//    (hpx - rpx) * (rvy - hvy) = (hpy - rpy) * (rvx - hvx)
		//    (hpy - rpy) * (rvz - hvz) = (hpz - rpz) * (rvy - hvy)
		// These are the equations we need to solve, given our set of Hailstones (all the H variables)
		// We have 6 unknowns: the rpx, rpy, and rpz coordinates of the Rock, and the rvx, rvy, and rvz coordinates of the rock's velocity.
		// We could also find the time t at which the Rock intersects with each Hailstone, but we don't need it.
		//
		// Solving this should be doable by examining just 3 of the hailstones in the data set. 
		// Doing all the substitutions by hand is going to be a nightmare...
		//
		// Multiplying out...
		//
		// hpx * rvy - rpx * rvy - hpx * hvy + rpx * hvy = hpy * rvx - rpy * rvx - hpy * hvx + rpy * hvx
		//
		// rpy * rvx - rpx * rvy  = hpy * rvx - hpy * hvx + rpy * hvx - hpx * rvy + hpx * hvy - rpx * hvy
		//
		// Identically for y * z:
		//
		// rpy * rvz - rpz * rvy  = hpy * rvz - hpy * hvz + rpy * hvz - hpz * rvy + hpz * hvy - rpz * hvy

	}

	public void results() {
		// System.out.println(hailstones);
		System.out.println("Part 1: Intersection count = " + intersectionCount);
	}

	class Hailstone {
		private long px;
		private long py;
		private long pz;
		private long vx;
		private long vy;
		private long vz;

		public Hailstone(long px, long py, long pz, long vx, long vy, long vz) {
			this.px = px;
			this.py = py;
			this.pz = pz;
			this.vx = vx;
			this.vy = vy;
			this.vz = vz;
		}

		public long[] position(long t) {
			return new long[] {px + t * vx, py + t * vy, pz + t * vz};
		}

		/*
		 * We have a system of 4 equations in times t1 and t2:
		 *     this.px + this.vx * t1 = other.px + other.vx * t2
		 *     this.py + this.vy * t1 = other.py + other.vy * t2
		 * 
		 *     t1 = (other.px + other.vx * t2 - this.px) / this.vx = (other.py + other.vy * t2 - this.py) / this.vy
		 * 
		 *     Solving for t2:
		 *         (other.px + other.vx * t2 - this.px) * this.vy = (other.py + other.vy * t2 - this.py) * this.vx
		 *         this.vy * other.px + this.vy * other.vx * t2 - this.vy * this.px = this.vx * other.py + this.vx * other.vy * t2 - this.vx * this.py
		 *         this.vy * other.vx * t2 - this.vx * other.vy * t2 = this.vx * other.py - this.vx * this.py - this.vy * other.px + this.vy * this.px
		 *         t2 = (this.vx * other.py - this.vx * this.py - this.vy * other.px + this.vy * this.px) / (this.vy * other.vx - this.vx * other.vy)
		 */
		public Double[] intersection(Hailstone other) {
			Double[] intersectionPoint = new Double[] {null, null};

			long numerator = this.vx * other.py - this.vx * this.py - this.vy * other.px + this.vy * this.px;
			long denominator = this.vy * other.vx - this.vx * other.vy;

			if (denominator != 0) {
				double t2 = (double)numerator / (double)denominator;

				double xIntersection = other.px + other.vx * t2;
				double yIntersection = other.py + other.vy * t2;

				double t1 = (other.px + other.vx * t2 - this.px) / this.vx; // So we can check that both intersections have t >= 0

				if (t1 >= 0 && t2 >= 0) {
					// System.out.println("Intersection of " + this + " and " + other + " is: " + xIntersection + "," + yIntersection);
					intersectionPoint[0] = xIntersection;
					intersectionPoint[1] = yIntersection;
				} else {
					// System.out.println( "Intersection of " + this + " and " + other + " occurred in the past not the future.");
				} 
			} else {
				// Hailstone paths are parallel, they either always intersect or never do. We'll go with "never" for simplicity.
				// System.out.println("Hailstones " + this + " and " + other + " are parallel");
			}

			return intersectionPoint;
		}



		public String toString() {
			return "(" + px + "," + py + "," + pz + " @ " + vx + "," + vy + "," + vz + ")";
		}
	}
}