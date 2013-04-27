package com.lapidus.android.painter;

import java.util.ArrayList;

public class Track {
	ArrayList<Line> lines;
	ArrayList<Collision> collisions; 
	public Track () {
		lines = new ArrayList<Line>();
		collisions = new ArrayList<Collision>();
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
