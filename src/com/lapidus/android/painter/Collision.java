package com.lapidus.android.painter;

import java.util.ArrayList;
import java.util.Collections;
public class Collision {
	ArrayList<Point> collidingPoints; 
	ArrayList<Point> exitPoints; 
	Point center; 
	public Collision() {
		collidingPoints = new ArrayList<Point>();
		exitPoints = new ArrayList<Point>();
		center = new Point();
	}
	public void addCollidingPoint(Point p) {
		collidingPoints.add(p);
		calcCenter();
	}
	public void addExitPoint(Point p) {
		exitPoints.add(p);
		calcCenter();
	}
	public void merge(Collision b) {
		this.collidingPoints.addAll(b.collidingPoints);
		this.exitPoints.addAll(b.exitPoints);	
		calcCenter();
	}
	public void calcCenter() {
		/*Point maxx, maxy, minx, miny;
		maxx = Collections.max(collidingPoints, Point.xComp);
		maxy = Collections.max(collidingPoints, Point.yComp);
		minx = Collections.min(collidingPoints, Point.xComp);
		miny = Collections.min(collidingPoints, Point.yComp);
		center = new Point((maxx.x - minx.x) / 2, (maxy.y - miny.y) / 2);*/
		center = collidingPoints.get(0);
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
}
