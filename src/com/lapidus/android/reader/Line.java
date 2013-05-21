package com.lapidus.android.reader;

import java.util.ArrayList;

import com.lapidus.android.primitives.Point;
/**
 * класс хранит точки одной кривой линии 
 * @author Егор
 *
 */
public class Line implements Cloneable{
	ArrayList<Point> points; 
	/**конструктор*/
	public Line() {
		points = new ArrayList<Point>();
	}
	/**
	 * наследуемый метод интерфейса Clonable
	 */
	@Override
	public Line clone() throws CloneNotSupportedException {
		Line res = (Line) super.clone();
		res.points = new ArrayList<Point>();
		for (Point x : points) {
			res.addNextPoint(x.clone());
		}
		return res;		
	}
	/**
	 * добавить точку 
	 * @param p точка
	 */
	public void addNextPoint(Point p) {
		points.add(p);
	}
	/**
	 * получить первую точку линии
	 * @return первая точка линии
	 */
	public Point getFirst(){
		if (points.size() == 0) return null; 
		return points.get(0);		
	}
	/**
	 * 
	 * @return последняя точка линии
	 */
	public Point getLast() {
		if (points.size() == 0) return null; 
		
		return points.get(points.size() - 1);
	}
	/**
	 * получить точки
	 * @return точки линии
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}
	/**
	 * удалить последнюю точку 
	 */
	public void removeLast(){
		if (points.size() < 1) return;
		points.remove(points.size() - 1);
	}
	/**
	 * объединить линии
	 * @param l
	 */
	public void merge(Line l) {
		points.addAll(l.getPoints());
	}
}
