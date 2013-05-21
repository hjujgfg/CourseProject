package com.lapidus.android.reader;

import java.util.ArrayList;

public class Track implements Cloneable {
	/**список линий*/
	ArrayList<Line> lines;
	/**список коллизий*/
	ArrayList<Collision> collisions; 
	/**
	 * конструктор 
	 */
	public Track () {
		lines = new ArrayList<Line>();
		collisions = new ArrayList<Collision>();
	}
	/**
	 * наследуемый метод интерфейса Cloneable
	 */
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
	/**
	 * добавить линию
	 * @param l - линия
	 */
	public void addLine(Line l) {
		lines.add(l);
	}
	/**
	 * добавить коллизию
	 * @param c - коллизия
	 */
	public void addCollision(Collision c) {
		collisions.add(c);
	}
	/**
	 * вернуть линии
	 * @return список линий
	 */
	public ArrayList<Line> getLines() {
		return lines;
	}
	/**
	 * вернуть коллизии
	 * @return список коллизий
	 */
	public ArrayList<Collision> getCollisions() {
		return collisions; 
	}
}
