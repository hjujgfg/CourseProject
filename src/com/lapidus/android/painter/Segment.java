package com.lapidus.android.painter;

public class Segment {
	Point start, stop;
	public Segment() {
		start = new Point();
		stop = new Point();
	}
	public Segment(Point a) {
		start = a;
		stop = new Point();
	}
	public Segment(Point a, Point b) {
		start = a;
		stop = b; 
	}
	public Segment(float x1, float y1, float x2, float y2) {
		start = new Point(x1, y1);
		stop = new Point(x2, y2);
	}
	private float countK() {
		return (start.y - stop.y) / (start.x - stop.x);
	}
	private float countB() {
		return stop.y - (countK() * stop.x);
	}
	public Point findIntersection(Segment s) {
		Point intersection = new Point();
		intersection.x = (s.countB() - this.countB()) / (this.countK() - s.countK());
		intersection.y = countK() * intersection.x + countB();
		return intersection;
	}
	float direction(Point pi, Point pj, Point pk) {
	    //(pk - pi) * (pj - pi)
	    return (((pk.x - pi.x) * (pj.y - pi.y)) - 
	            ((pj.x - pi.x) * (pk.y - pi.y)));
	   
	    }   
	    boolean onSegment(Point pi, Point pj, Point pk) {
	        if (min(pi.x, pj.x) < pk.x && pk.x < max(pi.x, pj.x) 
	                && min(pi.y,pj.y) < pk.y && pk.y < max(pi.y,pj.y)) {
	            return true;
	        } else {
	            return false;
	        }
	    }

	    float min(float a, float b) {
	        if (a < b) return a; 
	        else return b;
	    }

	    float max(float a, float b) {
	        if (a > b) return a; 
	        else return b;
	    }
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
	    public boolean checkForIntersection(Segment p) {
	        return segmentInt(this.stop, this.start, p.start, p.stop);
	    }
}
