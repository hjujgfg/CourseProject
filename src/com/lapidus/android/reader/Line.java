package com.lapidus.android.reader;

import java.util.ArrayList;

import com.lapidus.android.primitives.Point;
/**
 * ����� ������ ����� ����� ������ ����� 
 * @author ����
 *
 */
public class Line implements Cloneable{
	ArrayList<Point> points; 
	/**�����������*/
	public Line() {
		points = new ArrayList<Point>();
	}
	/**
	 * ����������� ����� ���������� Clonable
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
	 * �������� ����� 
	 * @param p �����
	 */
	public void addNextPoint(Point p) {
		points.add(p);
	}
	/**
	 * �������� ������ ����� �����
	 * @return ������ ����� �����
	 */
	public Point getFirst(){
		if (points.size() == 0) return null; 
		return points.get(0);		
	}
	/**
	 * 
	 * @return ��������� ����� �����
	 */
	public Point getLast() {
		if (points.size() == 0) return null; 
		
		return points.get(points.size() - 1);
	}
	/**
	 * �������� �����
	 * @return ����� �����
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}
	/**
	 * ������� ��������� ����� 
	 */
	public void removeLast(){
		if (points.size() < 1) return;
		points.remove(points.size() - 1);
	}
	/**
	 * ���������� �����
	 * @param l
	 */
	public void merge(Line l) {
		points.addAll(l.getPoints());
	}
}
