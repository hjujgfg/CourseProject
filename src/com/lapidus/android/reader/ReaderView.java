package com.lapidus.android.reader;

import java.util.ArrayList;

import com.lapidus.android.primitives.Point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ReaderView extends View {

	public ReaderView(Context context) {
		super(context);		
		// TODO Auto-generated constructor stub
		paint = new Paint();
		this.setOnTouchListener(ontouchlistener);		
		points = new ArrayList<Point>();				
		canvas = new Canvas();
		cols = new ArrayList<Collision>();
		pointsAreProcessed = false;
		track = new Track();
	}
	ArrayList<Point> points;
	ArrayList<Collision> cols;
	Track track; 
	Point[] approximizedPoints;
	boolean pointsAreProcessed; 
	Paint paint;
	Canvas canvas; 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.CYAN);
		canvas.drawPaint(paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawTrack(track, canvas, paint);
		/*if (pointsAreProcessed) drawProcessedPoints(points);
		else drawPoints(canvas, paint);
		drawCollisions(canvas, paint, cols);
		drawApproximizedPoints(canvas, paint);*/
		canvas.drawText(track.getLines().size() + " = points size" , 50, 50, paint);
		if (approximizedPoints != null) canvas.drawText(approximizedPoints.length + " = ap points size" , 50, 60, paint); 
	}
	protected void drawPoints(Canvas canvas, Paint paint) {
		paint.setColor(Color.BLACK);
		for (Point x : points) {
			canvas.drawCircle(x.x, x.y, 1, paint);
		}		
		paint.setColor(Color.BLACK);
	}
	protected void drawProcessedPoints (ArrayList<Point> points, Canvas canvas, Paint paint) {
		paint.setColor(Color.DKGRAY);
		for (int i = 0; i < points.size() - 1; i ++) {
			Log.i("PO", points.get(i).toString());
			canvas.drawLine(points.get(i).x, points.get(i).y, 
					points.get(i + 1).x, points.get(i + 1).y, paint);
		}
		paint.setColor(Color.BLACK);
	}
	protected void drawApproximizedPoints(Canvas canvas, Paint paint) {
		paint.setColor(Color.RED);
		if (approximizedPoints == null || approximizedPoints.length == 0) return; 
		for (int i = 0; i < approximizedPoints.length - 1; i ++) {
			canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
					approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
		}
		paint.setColor(Color.BLACK);
	}
	protected void drawCollisions(Canvas canvas, Paint paint, ArrayList<Collision> cols) {
		paint.setColor(Color.YELLOW);
		if (cols == null) return;
		/*for (Collision c : cols) {
			canvas.drawCircle(c.x(), c.y(), 15, paint);
		}*/
		for (int i = 0; i < cols.size(); i ++) {
			canvas.drawCircle(cols.get(i).x(), cols.get(i).y(), 15, paint);
		}
		paint.setColor(Color.BLACK);
	}
	protected void drawTrack(Track track, Canvas canvas, Paint paint) {
		if (track == null) return;
		//generates concurentmodificationexception when multithreading
		/*for (Line l : track.getLines()) {
			drawLine(l, canvas, paint);
		}*/
		for (int i = 0; i < track.getLines().size(); i ++) {
			drawLine(track.getLines().get(i), canvas, paint);
		}
		drawCollisions(canvas, paint, track.getCollisions());
		paint.setColor(Color.BLACK);
		canvas.drawCircle(0, 0, 30, paint);
	}
	protected void drawLine(Line l, Canvas canvas, Paint paint) {
		drawProcessedPoints(l.getPoints(), canvas, paint);
	}
	private OnTouchListener ontouchlistener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			
			return false;
		}
	};

}
