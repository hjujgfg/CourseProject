package com.lapidus.android.painter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ReaderView extends PainterView {

	public ReaderView(Context context) {
		super(context);		
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void drawPoints(Canvas canvas, Paint paint) {
		super.drawPoints(canvas, paint);
		paint.setColor(Color.RED);		
		if (approximizedPoints != null && approximizedPoints.length != 0) {			
			for (int i = 0; i < approximizedPoints.length - 1; i ++) {
				canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
						approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
			}
		}
		paint.setColor(Color.BLACK);
	}

}
