package com.lapidus.android.painter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class PainterView extends View {

	public PainterView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		this.setOnTouchListener(ontouchlistener);
		startX = startY = stopX = stopY = 0;
		points = new ArrayList<Point>();
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		image = Bitmap.createBitmap(480, 800, conf);
		canvas = new Canvas(image);
		shouldDrawBitmap = false;
		touched = false;
		hangingPoint = new Point();
	}
	Paint paint;
	Bitmap image;
	Canvas canvas;
	ArrayList<Point> points;
	Point hangingPoint; 
	public Point[] approximizedPoints;
	float startX, startY, stopX, stopY;
	boolean shouldDrawBitmap;
	boolean touched;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.CYAN);
		canvas.drawPaint(paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		//canvas.drawLine(startX, startY, stopX, stopY, paint);
		if (image != null) {			
			canvas.drawBitmap(image, new Matrix(), paint);
		}
		
		drawPoints(canvas, paint);
	}
	protected void drawPoints(Canvas canvas, Paint paint) {
		if (points.size() == 0) return;
		if (points.size() == 1) {
			canvas.drawCircle(points.get(0).x, points.get(0).y, 5, paint);
			return;
		}
		for (int i = 0; i < points.size() - 1; i ++) {
			//if (i == 0) canvas.drawText(points.get(0).toString(), 40, 40, paint);
			canvas.drawLine(points.get(i).x, points.get(i).y, 
					points.get(i+1).x, points.get(i+1).y, paint);						
		}
		paint.setColor(Color.GREEN);
		canvas.drawCircle(points.get(0).x, points.get(0).y, 1, paint);
		paint.setColor(Color.RED);
		canvas.drawCircle(points.get(points.size() - 1).x, points.get(points.size() - 1).y, 1, paint);
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
			hangingPoint.x = event.getX();
			hangingPoint.y = event.getY();
			if((stopX-event.getX())*(stopX-event.getX()) > 100 
					&& (stopX-event.getX())*(stopX-event.getX()) > 100 ) {
				pp = new Point(event.getX(), event.getY(), 0f, points.size());
				/*int tmp = checkForIntersection(pp);
				if (tmp != -1) {
					pp.collides = true;
					pp.collisionIndex = tmp;
				}*/
				points.add(pp);				
			}
			if (MotionEvent.ACTION_UP == event.getAction()) {
				approximizedPoints = new Point[points.size()];
				Point[] temp = new Point[points.size()];
				int i = 0;
				for (Point x : points) {					
					temp[i] = x;
					i ++;
					Log.i("PP", x.toString() + " i = " + i);
				}
				approximizedPoints = Approximizer.approximize(2f, temp);	
				touched = false;				
				//points.add(hangingPoint);
			}
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				touched = true; 				
			}
			invalidate();
			return true;
		}
	};
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
