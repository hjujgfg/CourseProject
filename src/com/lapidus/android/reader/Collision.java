package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.primitives.Point;
public class Collision implements Cloneable {
	//список точек коллизии
	ArrayList<Point> collidingPoints;
	// список точек выходов
	ArrayList<Point> exitPoints;
	// список линий разрешенной коллизии
	ArrayList<Line> resolvedLines;
	//центр коллизии
	Point center; 
	//тип коллизии
	int type;
	// константа типа колиизии - начало 
	final public static int TYPE_START = 0;
	// константа типа коллизии - конец
	final public static int TYPE_STOP = 2;
	// константа типа коллизии - общая
	final public static int TYPE_GENERAL = 1;
	/**
	 * конструктор 
	 */
	public Collision() {
		collidingPoints = new ArrayList<Point>();
		exitPoints = new ArrayList<Point>();
		center = new Point();
		type = -1;
		resolvedLines = new ArrayList<Line>();
	}
	/**
	 * наследуемый метод интерфейса Clonable
	 */
	@Override
	public Collision clone() throws CloneNotSupportedException {
		Collision res = (Collision)super.clone();
		res.collidingPoints = new ArrayList<Point>();
		for (Point x : collidingPoints) {
			res.collidingPoints.add(x.clone());			
		}
		res.exitPoints = new ArrayList<Point>();
		for (Point x : exitPoints) {
			res.exitPoints.add(x.clone());
		}
		res.resolvedLines = new ArrayList<Line>();
		for (Line x : resolvedLines) {
			res.resolvedLines.add(x.clone());
		}
		res.center = center.clone();
		return res;
	}
	/**
	 * установить тип коллизии
	 * @param i - тип коллизии
	 */
	public void setType(int i) {
		type = i;
	}
	/**
	 * получить тип коллизии
	 * @return тип коллизии
	 */
	public int Type(){
		return type;
	}
	/**
	 * добавтиь точку колиизии
	 * @param p точка коллизии
	 */
	public void addCollidingPoint(Point p) {
		collidingPoints.add(p);
		calcCenter();
	}
	/**
	 * добавить точку выхода 
	 * @param p - точка выхода 
	 */
	public void addExitPoint(Point p) {
		exitPoints.add(p);
		calcCenter();
	}
	/**
	 * добавить линию азрешенной коллизии
	 * @param l
	 */
	public void addResolvedLine(Line l) {
		resolvedLines.add(l);
	}
	/**
	 * очистить список разрешенных линий 
	 */
	public void clearResolvedLines() {
		resolvedLines.clear();
	}
	/**
	 * объединить коллизии
	 * @param b
	 */
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
	/**
	 * расчитать центр коллизии
	 */
	public void calcCenter() {
		Point maxx, maxy, minx, miny;
		maxx = Collections.max(collidingPoints, Point.xComp);
		maxy = Collections.max(collidingPoints, Point.yComp);
		minx = Collections.min(collidingPoints, Point.xComp);
		miny = Collections.min(collidingPoints, Point.yComp);
		center = new Point(minx.x + ((maxx.x - minx.x) / 2), miny.y + ((maxy.y - miny.y) / 2));
		//center = collidingPoints.get(0);
	}
	/**
	 * 
	 * @return точка с максимульной х координатой
	 */
	public float maxX() {
		Point o = Collections.max(collidingPoints, Point.xComp);
		return o.x;
	}
	/**
	 * 
	 * @return точка с максимульной н координатой
	 */
	public float maxY() {
		Point o = Collections.max(collidingPoints, Point.yComp);
		return o.y;
	}
	/**
	 * 
	 * @return точка с минимальной х координатой
	 */
	public float minX() {
		Point o = Collections.min(collidingPoints, Point.xComp);
		return o.x;
	}
/**
 * 
 * @return точка с минимальной у координатой
 */
	public float minY() {
		Point o = Collections.min(collidingPoints, Point.yComp);
		return o.y;
	}
	/**
	 * 
	 * @return х центра коллизии
	 */
	public float x() {
		return center.x;
	}
	/**
	 * 
	 * @return у центра коллизии
	 */
	public float y() {
		return center.y;
	}
	
	public Point areAdjacent(Collision c) {
		return null;
	}
	/**
	 * 
	 * @return количество точек выхода
	 */
	public int getExitsQuantity() {
		return exitPoints.size();
	}
	/**
	 * удалить точку выхода
	 * @param p точка выхода для удаления
	 */
	public void removeExitPoint(Point p) {
		exitPoints.remove(p);
	}
}
