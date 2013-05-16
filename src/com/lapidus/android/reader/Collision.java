package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.primitives.Point;
public class Collision implements Cloneable {
	//������ ����� ��������
	ArrayList<Point> collidingPoints;
	// ������ ����� �������
	ArrayList<Point> exitPoints;
	// ������ ����� ����������� ��������
	ArrayList<Line> resolvedLines;
	//����� ��������
	Point center; 
	//��� ��������
	int type;
	// ��������� ���� �������� - ������ 
	final public static int TYPE_START = 0;
	// ��������� ���� �������� - �����
	final public static int TYPE_STOP = 2;
	// ��������� ���� �������� - �����
	final public static int TYPE_GENERAL = 1;
	/**
	 * ����������� 
	 */
	public Collision() {
		collidingPoints = new ArrayList<Point>();
		exitPoints = new ArrayList<Point>();
		center = new Point();
		type = -1;
		resolvedLines = new ArrayList<Line>();
	}
	/**
	 * ����������� ����� ���������� Clonable
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
	 * ���������� ��� ��������
	 * @param i - ��� ��������
	 */
	public void setType(int i) {
		type = i;
	}
	/**
	 * �������� ��� ��������
	 * @return ��� ��������
	 */
	public int Type(){
		return type;
	}
	/**
	 * �������� ����� ��������
	 * @param p ����� ��������
	 */
	public void addCollidingPoint(Point p) {
		collidingPoints.add(p);
		calcCenter();
	}
	/**
	 * �������� ����� ������ 
	 * @param p - ����� ������ 
	 */
	public void addExitPoint(Point p) {
		exitPoints.add(p);
		calcCenter();
	}
	/**
	 * �������� ����� ���������� ��������
	 * @param l
	 */
	public void addResolvedLine(Line l) {
		resolvedLines.add(l);
	}
	/**
	 * �������� ������ ����������� ����� 
	 */
	public void clearResolvedLines() {
		resolvedLines.clear();
	}
	/**
	 * ���������� ��������
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
	 * ��������� ����� ��������
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
	 * @return ����� � ������������ � �����������
	 */
	public float maxX() {
		Point o = Collections.max(collidingPoints, Point.xComp);
		return o.x;
	}
	/**
	 * 
	 * @return ����� � ������������ � �����������
	 */
	public float maxY() {
		Point o = Collections.max(collidingPoints, Point.yComp);
		return o.y;
	}
	/**
	 * 
	 * @return ����� � ����������� � �����������
	 */
	public float minX() {
		Point o = Collections.min(collidingPoints, Point.xComp);
		return o.x;
	}
/**
 * 
 * @return ����� � ����������� � �����������
 */
	public float minY() {
		Point o = Collections.min(collidingPoints, Point.yComp);
		return o.y;
	}
	/**
	 * 
	 * @return � ������ ��������
	 */
	public float x() {
		return center.x;
	}
	/**
	 * 
	 * @return � ������ ��������
	 */
	public float y() {
		return center.y;
	}
	
	public Point areAdjacent(Collision c) {
		return null;
	}
	/**
	 * 
	 * @return ���������� ����� ������
	 */
	public int getExitsQuantity() {
		return exitPoints.size();
	}
	/**
	 * ������� ����� ������
	 * @param p ����� ������ ��� ��������
	 */
	public void removeExitPoint(Point p) {
		exitPoints.remove(p);
	}
}
