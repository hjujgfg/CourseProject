package com.lapidus.android.painter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	}
	ArrayList<Point> points;
	ArrayList<Collision> cols; 
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
		drawPoints(canvas, paint);
		drawCollisions(canvas, paint);
	}
	protected void drawPoints(Canvas canvas, Paint paint) {
		paint.setColor(Color.BLACK);
		for (Point x : points) {
			canvas.drawCircle(x.x, x.y, 1, paint);
		}
		paint.setColor(Color.RED);		
		/*if (approximizedPoints != null && approximizedPoints.length != 0) {			
			for (int i = 0; i < approximizedPoints.length - 1; i ++) {
				canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
						approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
			}
		}*/
		paint.setColor(Color.BLACK);
	}
	protected void drawCollisions(Canvas canvas, Paint paint) {
		paint.setColor(Color.YELLOW);		
		for (Collision c : cols) {
			canvas.drawCircle(c.x(), c.y(), 15, paint);
		}
		paint.setColor(Color.BLACK);
	}
	private OnTouchListener ontouchlistener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	};

}
