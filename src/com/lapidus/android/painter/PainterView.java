package com.lapidus.android.painter;

import java.util.ArrayList;

import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;
import com.lapidus.android.reader.Approximizer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PainterView extends View {

	public PainterView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	public PainterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public PainterView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		init();
	}
	private void init() {
		paint = new Paint();
		this.setOnTouchListener(ontouchlistener);
		startX = startY = stopX = stopY = 0;
		points = new ArrayList<Point>();
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		image = Bitmap.createBitmap(480, 800, conf);
		canvas = new Canvas(image);
		shouldDrawBitmap = false;
		touched = false;
		a = new Point();
		b = new Point();
		pointCounter = 0;
		intersectingPoints = new ArrayList<Point>();
		segs = new ArrayList<Segment>();
		tempIntersectingPoints = new ArrayList<Point>();
		redoPoints = new ArrayList<Point>();
		redoSegs = new ArrayList<Segment>();
		approximizedPoints = new Point[0];
		lastAction = 0;
	}
	int pointCounter;
	Paint paint;
	Bitmap image;
	Canvas canvas;
	ArrayList<Point> points;
	ArrayList<Segment> segs; 
	ArrayList<Point> redoPoints; 
	ArrayList<Segment> redoSegs;
	ArrayList<Point> intersectingPoints; 
	ArrayList<Point> tempIntersectingPoints;
	Point tmp;
	Point a, b;	 
	public Point[] approximizedPoints;
	float startX, startY, stopX, stopY;
	boolean shouldDrawBitmap;
	boolean touched;
	int lastAction; 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.LTGRAY);
		canvas.drawPaint(paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		//canvas.drawLine(startX, startY, stopX, stopY, paint);
		if (image != null) {			
			canvas.drawBitmap(image, new Matrix(), paint);
		}
		
		refreshIntersectingPoints(intersectingPoints, segs);
		//drawPoints(canvas, paint);
		drawSegs(segs, canvas, paint);
		drawPoints(canvas, paint);
		drawApproximizedPoints(canvas, paint);
		drawIntersetingPoints(intersectingPoints, canvas, paint);
	}
	public void getExternalPoints(ArrayList<Point> arr) {
		points.clear();		
		points = (ArrayList<Point>) arr.clone();
		refreshSegs();
		refreshIntersectingPoints(intersectingPoints, segs);	
		smoothAll();
		invalidate();
	}
	public Bitmap getPreparedBitmap() {
		if (segs == null || segs.size() < 1) return null;
		Bitmap res;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		res = Bitmap.createBitmap(480, 800, conf);
		Canvas canv = new Canvas(res);
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.LTGRAY);
		canv.drawPaint(p);
		p.setStyle(Paint.Style.STROKE);		
		p.setColor(Color.GREEN);
		canv.drawCircle(segs.get(0).start.x, segs.get(0).start.y, 1, p);
		p.setColor(Color.RED);
		canv.drawCircle(segs.get(segs.size() - 1).stop.x, segs.get(segs.size() - 1).stop.y, 1, p);
		drawSegs(segs, canv, p);
		canvas.drawBitmap(res, new Matrix(), p);
		return res;
	}
	protected void drawSegs(ArrayList<Segment> segs, Canvas canvas, Paint paint) {
		paint.setColor(Color.BLACK);		
		for (Segment s : segs) {			
			canvas.drawLine(s.start.x, s.start.y, s.stop.x, s.stop.y, paint);			
		}				
		paint.setColor(Color.BLACK);
	}
	protected void drawPoints(ArrayList<Point> arr, Canvas canvas, Paint paint) {
		paint.setColor(Color.YELLOW);
		for (Point x : arr) {
			canvas.drawCircle(x.x, x.y, 10, paint);
		}
		paint.setColor(Color.BLACK);
	}
	protected void drawApproximizedPoints(Canvas canvas, Paint paint) {
		if (approximizedPoints == null || approximizedPoints.length < 2 ) return;
		paint.setColor(Color.RED);
		paint.setStrokeWidth(2f);		 
		for (int i = 0; i < approximizedPoints.length - 1; i ++) {
			paint.setColor(Color.GREEN);
			canvas.drawCircle(approximizedPoints[i].x, approximizedPoints[i].y, 2, paint);
			paint.setColor(Color.RED);
			canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
					approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
		}
		paint.reset();
		paint.setColor(Color.BLACK);
	}
	protected void drawIntersetingPoints(ArrayList<Point> arr, Canvas canvas, Paint paint) {
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.FILL_AND_STROKE);
		for (Point x : arr) {
			canvas.drawCircle(x.x, x.y, 5, paint);
		}
		for (Point x : tempIntersectingPoints) {
			canvas.drawCircle(x.x, x.y, 5, paint);
		}
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
	}
	protected void drawPoints(Canvas canvas, Paint paint) {
		if (points.size() == 0) return;
		paint.setColor(Color.YELLOW);
		if (points.size() == 1) {
			canvas.drawCircle(points.get(0).x, points.get(0).y, 17, paint);
			return;
		}
		for (int i = 0; i < points.size() - 1; i ++) {
			//if (i == 0) canvas.drawText(points.get(0).toString(), 40, 40, paint);
			paint.setColor(Color.BLACK);
			canvas.drawLine(points.get(i).x, points.get(i).y, 
					points.get(i+1).x, points.get(i+1).y, paint);	
			//paint.setColor(Color.YELLOW);
			//canvas.drawCircle(points.get(i).x, points.get(i).y, 17, paint);
		}
		
		/*paint.setColor(Color.GREEN);
		canvas.drawCircle(points.get(0).x, points.get(0).y, 1, paint);*/
		//paint.setColor(Color.YELLOW);
		//canvas.drawCircle(points.get(points.size() - 1).x, points.get(points.size() - 1).y, 17, paint);
		/*paint.setColor(Color.RED);		
		if (approximizedPoints != null && approximizedPoints.length != 0) {			
			for (int i = 0; i < approximizedPoints.length - 1; i ++) {
				canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
						approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
			}
		}*/
		paint.setColor(Color.BLACK);
	}
	Point pp;
	private OnTouchListener ontouchlistener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub	
			
			if((stopX-event.getX())*(stopX-event.getX()) > 100 
					&& (stopX-event.getX())*(stopX-event.getX()) > 100 ) {
				pp = new Point(event.getX(), event.getY(), 0f, points.size());
				/*int tmp = checkForIntersection(pp);
				if (tmp != -1) {
					pp.collides = true;
					pp.collisionIndex = tmp;
				}*/
				points.add(pp);				
				if (points.size() > 2) smoothAll();
			}
			if (MotionEvent.ACTION_UP == event.getAction()) {
				smoothAll();
				//points.add(hangingPoint);
			}
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				touched = true; 				
			}
			invalidate();
			return true;
		}
	};
	protected void smoothAll() {
		approximizedPoints = new Point[points.size()];
		Point[] temp = new Point[points.size()];
		int i = 0;
		for (Point x : points) {					
			temp[i] = x;
			i ++;
			Log.i("PP", x.toString() + " i = " + i);
		}
		approximizedPoints = Approximizer.approximize(2f, temp);	
		refreshSegs(approximizedPoints);
		touched = false;	
		refreshIntersectingPoints(intersectingPoints, segs);
		removeSmallLoops(intersectingPoints, segs);
		refreshPoints(segs, points);
		temp = new Point[points.size()];
		i = 0;
		for (Point x : points) {					
			temp[i] = x;
			i ++;
			Log.i("PP", x.toString() + " i = " + i);
		}
		approximizedPoints = Approximizer.approximize(2f, temp);
		refreshSegs(approximizedPoints);
		smoothAngles(segs, 2);
		removeShortSegments(segs, 2);
		refreshPoints(segs, points);
		temp = new Point[points.size()];
		i = 0;
		for (Point x : points) {					
			temp[i] = x;
			i ++;
			Log.i("PP", x.toString() + " i = " + i);
		}
		
		approximizedPoints = Approximizer.approximize(2f, temp);
		refreshSegs(approximizedPoints);
		
		refreshIntersectingPoints(intersectingPoints, segs);
	}
	boolean n;
	private OnTouchListener ontouchlistener2 = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				for (Point x : points) {
					if (Math.abs(x.x - event.getX()) < 17 && Math.abs(x.y - event.getY()) < 17) {
						touched = true;
						lastAction = 1;
						b.x = x.x;
						b.y = x.y;
						a = x;
						break;
					}
				}
				if (touched == false) {
					lastAction = 2;
					points.add(new Point(event.getX(), event.getY()));
				}
				redoPoints.clear();
				redoSegs.clear();
			}
			if (MotionEvent.ACTION_MOVE == event.getAction()) {
				if (touched == true) {
					a.x = event.getX();
					a.y = event.getY();					
				} else {
					points.get(points.size() - 1).x = event.getX();
					points.get(points.size() - 1).y = event.getY();
					Segment tmp = new Segment(points.get(points.size() - 1), points.get(points.size() - 2));
					tempIntersectingPoints = new ArrayList<Point>();
					for (int i = 0; i < segs.size() - 1; i ++) {
						n = false;
						n = tmp.checkForIntersection(segs.get(i));
						if (n == true) {
							tempIntersectingPoints.add(tmp.findIntersection(segs.get(i)));
						}
					}
				}				
			}
			if (MotionEvent.ACTION_UP == event.getAction()) {
				touched = false;
				tempIntersectingPoints = new ArrayList<Point>();
				if (points.size() > 1) {
					segs.add(new Segment(points.get(points.size() - 2), points.get(points.size() - 1)));
				}
				if (segs.size() > 2) {
					/*for (int i = 0; i < segs.size() - 2; i ++) {
						n = false;
						n = segs.get(segs.size() - 1).checkForIntersection(segs.get(i));
						if (n == true) { 
							intersectingPoints.add(segs.get(segs.size() - 1).findIntersection(segs.get(i)));
						}
					}*/
					intersectingPoints = new ArrayList<Point>();
					for (int i = 0; i < segs.size(); i ++) {
						for (int j = 0; j < segs.size(); j ++) {
							n = false;												
							if (j != (i - 1) && j != i && j != (i + 1)) {
								n = segs.get(i).checkForIntersection(segs.get(j));
								if (n == true) {
									intersectingPoints.add(segs.get(i).findIntersection(segs.get(j)));
								}
							}							
						}
					}
				}
			}
			invalidate();
			return true;
		}
	};
	public void refreshSegs() {
		segs.clear();
		for (int i = 0; i < points.size() - 1; i ++) {
			segs.add(new Segment(points.get(i), points.get(i+1)));
		}
	}
	public void refreshSegs(Point[] arr) {
		segs.clear();
		for (int i = 0; i < arr.length - 1; i ++) {			
			segs.add(new Segment(arr[i], arr[i+1]));
		}		
	}
	public void refreshPoints(ArrayList<Segment> segs, ArrayList<Point> points) {
		points.clear();
		points.add(segs.get(0).start);
		for (Segment s : segs) {
			points.add(s.stop);
		}
	}
	public void refreshIntersectingPoints(ArrayList<Point> intersectingPoints, ArrayList<Segment> segs) {
		if (segs.size() > 2) {					
			intersectingPoints.clear();
			for (int i = 0; i < segs.size(); i ++) {
				for (int j = 0; j < segs.size(); j ++) {
					n = false;												
					if (j != (i - 1) && j != i && j != (i + 1)) {
						n = segs.get(i).checkForIntersection(segs.get(j));
						if (n == true) {
							boolean pointExists = false;
							for (Point x : intersectingPoints) {
								if ((x.S1() == segs.get(j) && x.S2() == segs.get(i)) || (x.S1() == segs.get(j) && x.S2() == segs.get(i))) {
									pointExists = true;
								}
							}
							if (!pointExists) { 
								intersectingPoints.add(segs.get(i).findIntersection(segs.get(j)));
								intersectingPoints.get(intersectingPoints.size() - 1).addSegs(segs.get(i), segs.get(j));
								segs.get(i).collides = true;
								segs.get(j).collides = true;
							}														
						}
					}							
				}
			}
		} else intersectingPoints.clear();
	}
	public void removeSmallLoops(ArrayList<Point> intersectingPoints, ArrayList<Segment> segs) {
		Segment t1;
		Segment t2; 
		float loopLength = 0;
		int f1, f2;
		for (int i = 0; i < intersectingPoints.size(); i++) {
			f1 = segs.indexOf(intersectingPoints.get(i).S1());
			f2 = segs.indexOf(intersectingPoints.get(i).S2());
			if (f1 != -1 && f2 != -1) {
				loopLength = 0;
				t1 = new Segment(intersectingPoints.get(i), intersectingPoints.get(i).S1().stop);
				t2 = new Segment(intersectingPoints.get(i).S2().start, intersectingPoints.get(i));
				loopLength += t1.length();
				loopLength += t2.length();
				
				for (int j = f1 + 1; j < f2; j ++) {
					loopLength += segs.get(j).length();
				}
				if (loopLength < 88) {
					t1 = new Segment(intersectingPoints.get(i).S1().start, intersectingPoints.get(i));
					t2 = new Segment(intersectingPoints.get(i), intersectingPoints.get(i).S1().stop);
					for (int j = f2; j >= f1; j --) {
						segs.remove(j);
					}
					segs.add(f1, t1);
					segs.add(f1 + 1, t2);
					//refreshIntersectingPoints(intersectingPoints, segs);
				}
			}			
		}		
		refreshIntersectingPoints(intersectingPoints, segs);
	}
	public void removeShortSegments(ArrayList<Segment> segs, int depth) {
		if (depth == 0) return;
		float l = segs.get(0).length();
		Segment s;
		Segment s1, s2;
		ArrayList<Segment> removeInds = new ArrayList<Segment>();
		for (int i = 1; i < segs.size() - 1; i ++) {
			s = segs.get(i);
			s1 = segs.get(i - 1);
			s2 = segs.get(i + 1);
			if (s.length() < 10) {
				if (Math.abs(s.angle(s1)) < (2 * Math.PI / 3)) {
					s1.stop = s.stop;
					removeInds.add(s);
				} else if (Math.abs(s.angle(s2)) < (2 * Math.PI / 3)) {
					s2.start = s.start;
					removeInds.add(s);
				}
			}
		}
		for (Segment i : removeInds) {
			segs.remove(i);
		}
		removeShortSegments(segs, depth - 1);
	}
	public void smoothAngles(ArrayList<Segment> segs, int depth) {
		if (depth == 0) return;
		for (int i = 0; i < segs.size() - 1; i ++) {
			if (Math.abs(segs.get(i).angle(segs.get(i + 1))) < 2 * Math.PI / 3) {
				segs.get(i).start.smoothAngle(segs.get(i).stop, segs.get(i + 1).stop);
			}
		}
		smoothAngles(segs, depth - 1);
	}
	public void undo() {
		if (points.size() == 0) return;
		switch(lastAction) {
		case 1 : 
			a.x = b.x;
			a.y = b.y;
			break;
		case 2 : 
			redoPoints.add(points.get(points.size() - 1));
			points.remove(points.size() - 1);
			break;
		}
		refreshSegs();
		invalidate();
	}
	/*private class Point {
		float x, y;		
		Point (float x, float y) {
			this.x = x;
			this.y = y;
		}
	}*/
	/*private int checkForIntersection(Point p) {
		for (Point x : points ) {			
			if (p.x == x.x && p.y == x.y) return x.index; 
		}
		return -1;
	}*/
	
	public void setImage(String s) {
		image = BitmapFactory.decodeFile(s);	
		shouldDrawBitmap = true;
		invalidate();
	}
	
	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}*/

}
