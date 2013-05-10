package com.lapidus.android.reader;

import java.util.ArrayList;

import com.lapidus.android.primitives.Point;

public class Line implements Cloneable{
	ArrayList<Point> points; 
	public Line() {
		points = new ArrayList<Point>();
	}
	@Override
	public Line clone() throws CloneNotSupportedException {
		Line res = (Line) super.clone();
		res.points = new ArrayList<Point>();
		for (Point x : points) {
			res.addNextPoint(x.clone());
		}
		return res;		
	}
	public void addNextPoint(Point p) {
		points.add(p);
	}
	public Point getFirst(){
		if (points.size() == 0) return null; 
		return points.get(0);		
	}
	public Point getLast() {
		if (points.size() == 0) return null; 
		
		return points.get(points.size() - 1);
	}
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void removeLast(){
		if (points.size() < 1) return;
		points.remove(points.size() - 1);
	}
	public void merge(Line l) {
		points.addAll(l.getPoints());
	}
}
