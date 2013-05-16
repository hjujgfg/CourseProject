package com.lapidus.android.primitives;

import java.io.Serializable;
import java.util.Comparator;

import com.lapidus.android.reader.Line;

public class Point implements Cloneable, Serializable {
	
	//константа сериализации
	private static final long serialVersionUID = 12221L;
	//координаты 
	public float x,y,z;
	//индекс
	public int index;
	//индикатор коллизий
	public boolean collides;
	//индекс коллизий
	public int collisionIndex;
	//смежные сегменты
	Segment s1, s2;
	//индикатор проверки 	
	public boolean chkd;
	//Линия, которой данная точка пренадлежит 
	public Line line;
	//связанная точка
	public Point connection;
	//Сравнение по индексу
	public static Comparator<Point> indexComp = new indexComparator();
	//сравнение по х-координате
	public static Comparator<Point> xComp = new xComparator();
	//сравнение по у-координате
	public static Comparator<Point> yComp = new yComparator();
	//конструктор 
	public Point (float x, float y, float z, int index) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.index = index;
		collides = false;
		collisionIndex = -1;
		chkd = false;
		s1 = null;
		s2 = null;
	}
	public Point (float x, float y) {
		this.x = x;
		this.y = y;
		z = 0;
		index = -1;
		chkd = false;
		collides = false;
		collisionIndex = -1;
		s1 = null;
		s2 = null;
	}
	public Point (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		index = -1;
		chkd = false;
		collides = false;
		collisionIndex = -1;
		s1 = null;
		s2 = null;
	}
	public Point() {
		// TODO Auto-generated constructor stub
		x = 0;
		y = 0;
		z = 0;
		index = -1;
		collides = false;
		collisionIndex = -1;
		chkd = false;
		s1 = null;
		s2 = null;
	}
	/**
	 * наследуемы метод интерфейса Comparable
	 */
	@Override
	public Point clone() throws CloneNotSupportedException {
		Point res = (Point)super.clone();
		res.x = this.x;
		res.y = this.y;
		res.z = this.z;
		res.index = this.index;
		res.collides = this.collides;
		res.collisionIndex = this.collisionIndex;
		return res;		
	}
	//строковый вид
	public String toString()
	{
	    return this.x + ":" + this.y + ":" + this.z;
	}
	/**
	 * сгладить угол данная точка - b - c
	 * @param b сторая точка
	 * @param c третья точка
	 */
	public void smoothAngle(Point b, Point c) {
		b.x = ((this.x + c.x + b.x) / 3);
		b.y = ((this.y + c.y + b.y) / 3);
	}
	/**
	 * квадрат расстояния
	 * @param other - вторая точка
	 * @return квадрат расстояния между точками
	 */
	public float distanceSquared(Point other)
	{
	    return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
	}
	/**
	 * длина данного ветора
	 * @return длину вектора
	 */
	public float length()
	{
	    return (float)Math.sqrt(this.x * this.x + this.y * this.y);
	}
	/**
	 * вычитание веторов
	 * @param a - второй вектор 
	 * @return разность
	 */
	public Point minus(Point a)
	{
	    return new Point(this.x - a.x, this.y - a.y);
	}
	/**
	 * точечное произведение
	 * @param a вторая точка
	 * @return точечное произведение
	 */
	public float dot(Point a)
	{
	    return this.x * a.x + this.y * a.y;
	}
	/**
	 * скалярное произведение 
	 * @param a - скаляр
	 * @return скалярное произведение
	 */
	public Point times(float a) {
	    return new Point(this.x * a, this.y * a);
	}
	/**
	 * вычисление угла
	 * @param b - вторая точка
	 * @param c - третья точка
	 * @return угол abc
	 */
	public float anglePoint (Point b, Point c)
	{
	   double x1 = this.x - b.x, x2 = c.x - b.x;
	   double y1 = this.y - b.y, y2 = c.y - b.y;
	   double d1 = Math.sqrt (x1 * x1 + y1 * y1);
	   double d2 = Math.sqrt (x2 * x2 + y2 * y2);
	   return (float) Math.acos ((x1 * x2 + y1 * y2) / (d1 * d2));
	}
	/**
	 * векторное произведение 
	 * @param p1 - вторая точка
	 * @param p2 - тертья точка
	 * @return векторное произведение
	 */
	public float vectorMult(Point p1, Point p2) {
        return ((p1.x - x)*(p2.y - y) - (p2.x - x) * (p1.y - y));
    }
	/**
	 * установить смежные отрезки
	 * @param a
	 * @param b
	 */
	public void addSegs(Segment a, Segment b) {
		s1 = a; 
		s2 = b;
	}
	/**
	 * 
	 * @return первый смежный отрезок
	 */
	public Segment S1() {
		return s1;
	}
	/**
	 * 
	 * @return второй смежный отрезок
	 */
	public Segment S2() {
		return s2;
	}
	/**
	 * устоновть линию
	 * @param l 
	 */
	public void setLine(Line l) {
		this.line = l;
	}
	/**
	 * 
	 * @return линию
	 */
	public Line getLine() {
		return line;
	}
	///Comparators
	/**
	 * сравние по х
	 * @author Егор
	 *
	 */
	private static class xComparator implements Comparator<Point> {
		public int compare(Point lhs, Point rhs) {
			// TODO Auto-generated method stub
			if (lhs.x > rhs.x) return 1; 
			if (lhs.x < rhs.x) return -1;			
			return 0;
		}		
	}
	/**
	 * сравнение по у
	 * @author Егор
	 *
	 */
	private static class yComparator implements Comparator<Point> {
		public int compare(Point lhs, Point rhs) {
			// TODO Auto-generated method stub
			if (lhs.y > rhs.y) return 1;
			if (lhs.y < rhs.y) return -1;  
			return 0;
		}		
	}
	/**
	 * сравнение по индексам
	 * @author Егор
	 *
	 */
	private static class indexComparator implements Comparator<Point> {

		public int compare(Point arg0, Point arg1) {
			// TODO Auto-generated method stub
			if (arg0.index > arg1.index) return 1;
			if (arg0.index < arg1.index) return -1;
			return 0;
		}		
	}	
}
 