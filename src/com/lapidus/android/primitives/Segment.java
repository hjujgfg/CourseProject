package com.lapidus.android.primitives;

public class Segment {
	//����� ������ � �����
	public Point start, stop;
	//��������� ��������
	public boolean collides;
	/**
	 * ����������� 
	 */
	public Segment() {
		start = new Point();
		stop = new Point();
		collides = false;
	}
	/**
	 * ����������
	 * @param a - ����� ������ 
	 */
	public Segment(Point a) {
		start = a;
		stop = new Point();
		collides = false;
	}
	/**
	 * �����������
	 * @param a - ����� ������
	 * @param b - ����� �����
	 */
	public Segment(Point a, Point b) {
		start = a;
		stop = b; 
		collides = false;
	}
	/**
	 * 
	 * @param x1 � ����� ������
	 * @param y1 � ����� ������
	 * @param x2 � ����� �����
	 * @param y2 � ����� �����
	 */
	public Segment(float x1, float y1, float x2, float y2) {
		start = new Point(x1, y1);
		stop = new Point(x2, y2);
	}
	/**
	 * ��������� ���� �������
	 * @return ���� �������
	 */
	public float countK() {
		if (start.x == stop.x) return Float.MAX_VALUE;
		return (start.y - stop.y) / (start.x - stop.x);
	}
	/**
	 * ��������� ����������� ��������
	 * @return ����������� ��������
	 */
	public float countB() {		
		return stop.y - (countK() * stop.x);
	}
	/**
	 * ����� �������
	 * @return ����� �������
	 */
	public float length() {
		float xx = stop.x - start.x;
		float yy = stop.y - stop.y;
		return (float)Math.sqrt(xx * xx + yy * yy);
	}
	/**
	 * ����� ����� �����������
	 * @param s ������ �������
	 * @return ����� �����������
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
	 * ���� ����� ��������� 
	 * @param s - ������ �������
	 * @return - ���� ����� ��������� 
	 */
	public float angle(Segment s) {
		return this.start.anglePoint(this.stop, s.stop);
	}
	/**
	 * ���������� ��������
	 * @param pi 
	 * @param pj
	 * @param pk
	 * @return ����������� �������� - < 0 => ������ ������� 
	 */
	float direction(Point pi, Point pj, Point pk) {
	    //(pk - pi) * (pj - pi)
	    return (((pk.x - pi.x) * (pj.y - pi.y)) - 
	            ((pj.x - pi.x) * (pk.y - pi.y)));
	   
    }   
	/**
	 * ����� �� ��������
	 * @param pi
	 * @param pj
	 * @param pk
	 * @return true, ���� ����� �� �������� 
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
     * ������� �� ���� �����
     * @param a
     * @param b
     * @return ������� �� ���� �����
     */
    float min(float a, float b) {
        if (a < b) return a; 
        else return b;
    }
    /**
     * �������� �� ���� ����� 
     * @param a
     * @param b
     * @return �������� �� ���� �����
     */
    float max(float a, float b) {
        if (a > b) return a; 
        else return b;
    }
    /**
     * �������� ����������
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return ������� �����������
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
     * ������ �������� �� ������� �����������
     * @param p
     * @return true, ���� ���� ����������� 
     */
    public boolean checkForIntersection(Segment p) {
        return segmentInt(this.stop, this.start, p.start, p.stop);
    }
}
