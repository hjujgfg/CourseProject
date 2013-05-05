package com.lapidus.android.primitives;

import java.util.Comparator;

public class Point implements Cloneable {
	public float x,y,z;
	public int index;
	public boolean collides;
	public int collisionIndex;
	Segment s1, s2; 
	public boolean chkd;
	public static Comparator<Point> indexComp = new indexComparator();
	public static Comparator<Point> xComp = new xComparator();
	public static Comparator<Point> yComp = new yComparator();
	public Point (float x, float y, float z, int index) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
		collides = false;
		collisionIndex = -1;
		chkd = false;
		s1 = null;
		s2 = null;
	}
	public Point (float x, float y) {
		this.x = x;
		this.y = y;
		z = 0;
		index = -1;
		chkd = false;
		collides = false;
		collisionIndex = -1;
		s1 = null;
		s2 = null;
	}
	public Point (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		index = -1;
		chkd = false;
		collides = false;
		collisionIndex = -1;
		s1 = null;
		s2 = null;
	}
	public Point() {
		// TODO Auto-generated constructor stub
		x = 0;
		y = 0;
		z = 0;
		index = -1;
		collides = false;
		collisionIndex = -1;
		chkd = false;
		s1 = null;
		s2 = null;
	}
	public String toString()
	{
	    return this.x + ":" + this.y + ":" + this.z;
	}
	public void smoothAngle(Point b, Point c) {
		b.x = ((this.x + c.x + b.x) / 3);
		b.y = ((this.y + c.y + b.y) / 3);
	}
	public float distanceSquared(Point other)
	{
	    return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
	}
	public float length()
	{
	    return (float)Math.sqrt(this.x * this.x + this.y * this.y);
	}
	public Point minus(Point a)
	{
	    return new Point(this.x - a.x, this.y - a.y);
	}
	public float dot(Point a)
	{
	    return this.x * a.x + this.y * a.y;
	}
	public Point times(float a) {
	    return new Point(this.x * a, this.y * a);
	}
	public float anglePoint (Point b, Point c)
	{
	   double x1 = this.x - b.x, x2 = c.x - b.x;
	   double y1 = this.y - b.y, y2 = c.y - b.y;
	   double d1 = Math.sqrt (x1 * x1 + y1 * y1);
	   double d2 = Math.sqrt (x2 * x2 + y2 * y2);
	   return (float) Math.acos ((x1 * x2 + y1 * y2) / (d1 * d2));
	}
	public float vectorMult(Point p1, Point p2) {
        return ((p1.x - x)*(p2.y - y) - (p2.x - x) * (p1.y - y));
    }
	public void addSegs(Segment a, Segment b) {
		s1 = a; 
		s2 = b;
	}
	public Segment S1() {
		return s1;
	}
	public Segment S2() {
		return s2;
	}
	///Comparators
	
	private static class xComparator implements Comparator<Point> {
		public int compare(Point lhs, Point rhs) {
			// TODO Auto-generated method stub
			if (lhs.x > rhs.x) return 1; 
			if (lhs.x < rhs.x) return -1;			
			return 0;
		}		
	}
	private static class yComparator implements Comparator<Point> {
		public int compare(Point lhs, Point rhs) {
			// TODO Auto-generated method stub
			if (lhs.y > rhs.y) return 1;
			if (lhs.y < rhs.y) return -1;  
			return 0;
		}		
	}
	private static class indexComparator implements Comparator<Point> {

		public int compare(Point arg0, Point arg1) {
			// TODO Auto-generated method stub
			if (arg0.index > arg1.index) return 1;
			if (arg0.index < arg1.index) return -1;
			return 0;
		}		
	}	
}
 