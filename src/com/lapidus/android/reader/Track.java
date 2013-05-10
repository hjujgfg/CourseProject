package com.lapidus.android.reader;

import java.util.ArrayList;

public class Track implements Cloneable {
	ArrayList<Line> lines;
	ArrayList<Collision> collisions; 
	public Track () {
		lines = new ArrayList<Line>();
		collisions = new ArrayList<Collision>();
	}
	@Override
	public Track clone() throws CloneNotSupportedException {
		Track t = (Track) super.clone();
		t.lines = new ArrayList<Line>();
		for (Line l : lines) {
			t.lines.add(l.clone());
		}
		t.collisions = new ArrayList<Collision>();
		for (Collision x : collisions) {
			t.collisions.add(x.clone());
		}
		return t;
	}
	public void addLine(Line l) {
		lines.add(l);
	}
	public void addCollision(Collision c) {
		collisions.add(c);
	}
	public ArrayList<Line> getLines() {
		return lines;
	}
	public ArrayList<Collision> getCollisions() {
		return collisions; 
	}
}
