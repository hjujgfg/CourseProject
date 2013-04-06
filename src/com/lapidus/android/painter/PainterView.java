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
		points = new ArrayList<PainterView.Point>();
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		image = Bitmap.createBitmap(480, 800, conf);
		canvas = new Canvas(image);
		shouldDrawBitmap = false;
	}
	Paint paint;
	Bitmap image;
	Canvas canvas;
	ArrayList<Point> points;
	float startX, startY, stopX, stopY;
	boolean shouldDrawBitmap;
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
	private void drawPoints(Canvas canvas, Paint paint) {
		if (points.size() == 0) return;
		if (points.size() == 1) {
			canvas.drawCircle(points.get(0).x, points.get(0).y, 5, paint);
			return;
		}
		for (int i = 0; i < points.size() - 1; i ++) {
			canvas.drawLine(points.get(i).x, points.get(i).y, 
					points.get(i+1).x, points.get(i+1).y, paint);
		}
	}
	
	private OnTouchListener ontouchlistener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub	
			
			if((stopX-event.getX())*(stopX-event.getX()) > 100 
					&& (stopX-event.getX())*(stopX-event.getX()) > 100 ) {
				points.add(new Point(event.getX(), event.getY()));
			}
			invalidate();
			return true;
		}
	};
	private class Point {
		float x, y;		
		Point (float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
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
