package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.primitives.Point;
public class Collision {
	ArrayList<Point> collidingPoints; 
	ArrayList<Point> exitPoints; 
	ArrayList<Line> resolvedLines;
	Point center; 
	int type;
	final public static int TYPE_START = 0;
	final public static int TYPE_STOP = 2;
	final public static int TYPE_GENERAL = 1;
	public Collision() {
		collidingPoints = new ArrayList<Point>();
		exitPoints = new ArrayList<Point>();
		center = new Point();
		type = -1;
		resolvedLines = new ArrayList<Line>();
	}
	public void setType(int i) {
		type = i;
	}
	public int Type(){
		return type;
	}
	public void addCollidingPoint(Point p) {
		collidingPoints.add(p);
		calcCenter();
	}
	public void addExitPoint(Point p) {
		exitPoints.add(p);
		calcCenter();
	}
	public void addResolvedLine(Line l) {
		resolvedLines.add(l);
	}
	public void clearResolvedLines() {
		resolvedLines.clear();
	}
	public void merge(Collision b) {
		/*this.collidingPoints.addAll(b.collidingPoints);
		this.exitPoints.addAll(b.exitPoints);	*/
		ArrayList<Point> tmp = new ArrayList<Point>();
		for (Point x : b.exitPoints) {
			for (Point y : this.exitPoints) {
				if (!x.equals(y)) tmp.add(y);
			}
		}
		this.exitPoints.addAll(tmp);
		tmp = new ArrayList<Point>();
		for (Point x : b.collidingPoints) {
			for (Point y : this.collidingPoints) {
				if (!x.equals(y)) tmp.add(y);
			}
		}
		this.collidingPoints.addAll(tmp);
		calcCenter();
	}
	public void calcCenter() {
		Point maxx, maxy, minx, miny;
		maxx = Collections.max(collidingPoints, Point.xComp);
		maxy = Collections.max(collidingPoints, Point.yComp);
		minx = Collections.min(collidingPoints, Point.xComp);
		miny = Collections.min(collidingPoints, Point.yComp);
		center = new Point(minx.x + ((maxx.x - minx.x) / 2), miny.y + ((maxy.y - miny.y) / 2));
		//center = collidingPoints.get(0);
	}
	public float maxX() {
		Point o = Collections.max(collidingPoints, Point.xComp);
		return o.x;
	}
	public float maxY() {
		Point o = Collections.max(collidingPoints, Point.yComp);
		return o.y;
	}
	public float minX() {
		Point o = Collections.min(collidingPoints, Point.xComp);
		return o.x;
	}
	public float minY() {
		Point o = Collections.min(collidingPoints, Point.yComp);
		return o.y;
	}
	public float x() {
		return center.x;
	}
	public float y() {
		return center.y;
	}
	public Point areAdjacent(Collision c) {
		return null;
	}
	public int getExitsQuantity() {
		return exitPoints.size();
	}
	public void removeExitPoint(Point p) {
		exitPoints.remove(p);
	}
}
