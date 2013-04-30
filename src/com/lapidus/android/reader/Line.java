package com.lapidus.android.reader;

import java.util.ArrayList;

import com.lapidus.android.painter.Point;

public class Line {
	ArrayList<Point> points; 
	public Line() {
		points = new ArrayList<Point>();
	}
	public void addNextPoint(Point p) {
		points.add(p);
	}
	public Point getFirst(){
		return points.get(0);		
	}
	public Point getLast() {
		return points.get(points.size() - 1);
	}
	public ArrayList<Point> getPoints() {
		return points;
	}
}
