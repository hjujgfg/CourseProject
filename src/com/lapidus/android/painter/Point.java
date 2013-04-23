package com.lapidus.android.painter;

import java.util.Comparator;

public class Point implements Cloneable {
	public float x,y,z;
	public int index;
	public boolean collides;
	public int collisionIndex;
	public boolean chkd;
	public static Comparator<Point> indexComp = new indexComparator();
	public static Comparator<Point> xComp = new xComparator();
	public Point (float x, float y, float z, int index) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
		collides = false;
		collisionIndex = -1;
		chkd = false;
	}
	public Point (float x, float y) {
		this.x = x;
		this.y = y;
		z = 0;
		index = -1;
		chkd = false;
		collides = false;
		collisionIndex = -1;
	}
	public String toString()
	{
	    return this.x + ":" + this.y;
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
	
	///Comparators
	
	private static class xComparator implements Comparator<Point> {

		public int compare(Point lhs, Point rhs) {
			// TODO Auto-generated method stub
			if (lhs.x > rhs.x) return 1; 
			if (lhs.x < rhs.x) return -1; 
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
 