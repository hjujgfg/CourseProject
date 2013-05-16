package com.lapidus.android.primitives;

public class Segment {
	//точки начала и конца
	public Point start, stop;
	//индикатор коллизии
	public boolean collides;
	/**
	 * констурктор 
	 */
	public Segment() {
		start = new Point();
		stop = new Point();
		collides = false;
	}
	/**
	 * коструктор
	 * @param a - точка начала 
	 */
	public Segment(Point a) {
		start = a;
		stop = new Point();
		collides = false;
	}
	/**
	 * констурктор
	 * @param a - точка начала
	 * @param b - точка конца
	 */
	public Segment(Point a, Point b) {
		start = a;
		stop = b; 
		collides = false;
	}
	/**
	 * 
	 * @param x1 х точки начала
	 * @param y1 у точки начала
	 * @param x2 х точки конца
	 * @param y2 у точки конца
	 */
	public Segment(float x1, float y1, float x2, float y2) {
		start = new Point(x1, y1);
		stop = new Point(x2, y2);
	}
	/**
	 * вычислить угол наклона
	 * @return угол наклона
	 */
	public float countK() {
		if (start.x == stop.x) return Float.MAX_VALUE;
		return (start.y - stop.y) / (start.x - stop.x);
	}
	/**
	 * вычислить коэффициент смещения
	 * @return коэффициент смещения
	 */
	public float countB() {		
		return stop.y - (countK() * stop.x);
	}
	/**
	 * длина отрезка
	 * @return длина отрезка
	 */
	public float length() {
		float xx = stop.x - start.x;
		float yy = stop.y - stop.y;
		return (float)Math.sqrt(xx * xx + yy * yy);
	}
	/**
	 * найти точку пересечения
	 * @param s второй отрезок
	 * @return точка пересечения
	 */
	public Point findIntersection(Segment s) {
		Point intersection = new Point();
		intersection.x = (s.countB() - this.countB()) / (this.countK() - s.countK());
		intersection.y = countK() * intersection.x + countB();
		this.start.z = 10;
		this.stop.z = 10;
		s.start.z = -10;
		s.stop.z = -10;
		return intersection;
	}
	/**
	 * угол между отрезками 
	 * @param s - второй отрезок
	 * @return - угол между отрезками 
	 */
	public float angle(Segment s) {
		return this.start.anglePoint(this.stop, s.stop);
	}
	/**
	 * напрвление поворота
	 * @param pi 
	 * @param pj
	 * @param pk
	 * @return направление поворота - < 0 => против часовой 
	 */
	float direction(Point pi, Point pj, Point pk) {
	    //(pk - pi) * (pj - pi)
	    return (((pk.x - pi.x) * (pj.y - pi.y)) - 
	            ((pj.x - pi.x) * (pk.y - pi.y)));
	   
    }   
	/**
	 * точка на сегменте
	 * @param pi
	 * @param pj
	 * @param pk
	 * @return true, если точка на сегменте 
	 */
    boolean onSegment(Point pi, Point pj, Point pk) {
        if (min(pi.x, pj.x) <= pk.x && pk.x <= max(pi.x, pj.x) 
                && min(pi.y,pj.y) <= pk.y && pk.y <= max(pi.y,pj.y)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * минимум из двух точек
     * @param a
     * @param b
     * @return минимум из двух точек
     */
    float min(float a, float b) {
        if (a < b) return a; 
        else return b;
    }
    /**
     * максимум из двух точек 
     * @param a
     * @param b
     * @return максимум из двух точек
     */
    float max(float a, float b) {
        if (a > b) return a; 
        else return b;
    }
    /**
     * наличиче пересечния
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return наличие пересечения
     */
    boolean segmentInt(Point p1, Point p2, Point p3, Point p4) {
        float d1 = direction(p3, p4, stop);
        float d2 = direction(p3, p4, p2);
        float d3 = direction(stop, p2, p3);
        float d4 = direction(stop, p2, p4);
        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) && 
                (((d3 > 0) && (d4 < 0)) || (d3 < 0 && d4 > 0))) {
            return true;
        } else if(d1 == 0 && onSegment(p3, p4, stop)) {
            return true;
        } else if(d2 == 0 && onSegment(p3, p4, p2)) {
            return true;
        } else if(d3 == 0 && onSegment(stop, p2, p3)) {
            return true;
        } else if(d4 == 0 && onSegment(stop, p2, p4)) {
            return true;
        } else return false;
    }
    /**
     * запуск проверки на наличие пересечения
     * @param p
     * @return true, если есть пересечение 
     */
    public boolean checkForIntersection(Segment p) {
        return segmentInt(this.stop, this.start, p.start, p.stop);
    }
}
