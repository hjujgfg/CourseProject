package com.lapidus.android.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CollisionResolverView extends View {
	
	public CollisionResolverView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	Paint paint;
	Canvas canvas; 
	Collision c;
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.CYAN);
		canvas.drawPaint(paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawCollision(canvas, paint);
	}
	private void drawCollision(Canvas canvas, Paint paint) {
		float avgX = 480 / (c.maxX() - c.minX());
		float avgY = 800 / (c.maxY() - c.minY());
		for (int i = 0; i < (int)(c.maxX() - c.minX()); i ++) {
			canvas.drawLine(avgX * i, 0, avgX * i, 800, paint);
		}
	}
	
}
