package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.primitives.Point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CollisionResolverView extends View {
	
	public CollisionResolverView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CollisionResolverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public CollisionResolverView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		init(context);
	}
	public void setScreenDimensions(int w, int h) {
		screenHeight = h;
		screenWidth = w;
	}
	private void init(Context context) {
		paint = new Paint();
		canvas = new Canvas();
		tmp = new ArrayList<Point>();
		c = TrackHolder.c;
		counter = 0;
		/*DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;*/
		
		this.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					for (Point x : tmp) {
						if (event.getX() >= x.x * avgX && event.getX() < x.x * avgX + avgX 
								&& event.getY() >= x.y * avgY && event.getY() < x.y * avgY + avgY) {
							if (x.z > 0) {
								touched = true;
								x.chkd = true;	
								x.collisionIndex = counter;
								break;
							}							
						}
					}
				}
				if (MotionEvent.ACTION_MOVE == event.getAction()) {
					boolean needErasion = false;
					for (Point x : tmp) {
						if (event.getX() >= x.x * avgX && event.getX() < x.x * avgX + avgX 
								&& event.getY() >= x.y * avgY && event.getY() < x.y * avgY + avgY) {
							if (hanging != null) {
								if (areNeighbours(x, hanging)) {
									x.chkd = true;
									hanging = x;
								} else needErasion = true;
							} else {
								x.chkd = true;
								hanging = x;
							}														
						}
					}
					if (needErasion) {
						for (Point x : tmp) {
							x.chkd = false;
						}
					}
				}
				if (MotionEvent.ACTION_UP == event.getAction()) {
					hanging = null;
					boolean needErasion = false;
					for (Point x : tmp) {
						if (event.getX() >= x.x * avgX && event.getX() < x.x * avgX + avgX 
								&& event.getY() >= x.y * avgY && event.getY() < x.y * avgY + avgY) {
							if (x.z > 0 && !x.collides && touched) {
								x.collides = true;
								touched = false;
								needErasion = false;
								x.collisionIndex = counter; 
								counter ++;
								break;
							} else needErasion = true;
						} else needErasion = true;
					}
					if (needErasion) {
						for (Point x : tmp) x.chkd = false;
					}else {
						for (Point x : tmp) {
							if (x.chkd) {
								x.chkd = false;
								x.collides = true;
							}
						}
					}										
				}
				invalidate();
				return true;
			}
		});
		invalidate();
		needredrawcollision = true;
	}
	
	
	int counter;
	Paint paint;
	Canvas canvas; 
	Collision c;
	int screenHeight;
	int screenWidth;
	float avgX;
	float avgY;
	boolean touched;
	Point hanging;
	boolean needredrawcollision;
	ArrayList<Point> tmp;
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if (needredrawcollision) {
			screenHeight = this.getHeight();
			screenWidth = this.getWidth();
		}
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.CYAN);
		canvas.drawPaint(paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		if (needredrawcollision) drawCollision(canvas, paint);
		drawCells(canvas, paint);
		drawGrid(canvas, paint);
		canvas.drawText(c.exitPoints.size() + " ", 10, 10, paint);
		for (Point x : c.exitPoints) {
			//tmp.add(new Point(x.x, x.y, 1));			
		}
	}
	private void drawCollision(Canvas canvas, Paint paint) {
		needredrawcollision = false;
		int j = 0;
		tmp = new ArrayList<Point>();
		for (Point x : c.collidingPoints) {
			tmp.add(new Point(x.x, x.y, -1));
		}
		for (Point x : c.exitPoints) {
			tmp.add(new Point(x.x, x.y, 1));
			tmp.get(tmp.size() - 1).index = c.exitPoints.indexOf(x);
		}
		
		/*for (Point p : c.collidingPoints) {
			canvas.drawText(p.toString(), 10, 10 * j, paint);
			j ++;
		}
		paint.setColor(Color.BLUE);
		for (Point p : c.exitPoints) {
			canvas.drawText(p.toString(), 10, 10 * j, paint);
			j ++;
		}*/
		
		Point t1 = Collections.max(tmp, Point.xComp);
		Point t2 = Collections.min(tmp, Point.xComp);
		avgX = screenWidth / (Math.abs(t1.x -t2.x) + 1);
		Point f1 = Collections.max(tmp, Point.yComp);
		Point f2 = Collections.min(tmp, Point.yComp);
		avgY = screenHeight/ (Math.abs(f1.y - f2.y) + 1);
		normalize(tmp, t2, f2);
		boolean exit;
		for (Point x : tmp) {
			if (x.z > 0) exit = true;
			else exit = false;
			drawCell(x, exit, avgX, avgY, paint, canvas);
		}
		drawGrid(canvas, paint);
		
	}
	private void drawGrid(Canvas canvas, Paint paint) {
		Point t1 = Collections.max(tmp, Point.xComp);
		Point t2 = Collections.min(tmp, Point.xComp);
		Point f1 = Collections.max(tmp, Point.yComp);
		Point f2 = Collections.min(tmp, Point.yComp);
		for (int i = 0; i < (int)(t1.x - t2.x)+1; i ++) {
			canvas.drawLine(avgX * i, 0, avgX * i, screenHeight, paint);
			//canvas.drawText(c.collidingPoints.size() + " " + avgX + " " + avgY, 150, 10, paint);
		}
		for (int i = 0; i < (int)(f1.y - f2.y) + 1; i ++) {
			canvas.drawLine(0, avgY * i, screenWidth, avgY * i, paint);
			//canvas.drawText(c.exitPoints.size() + " " + avgX + " " + avgY, 150, 10, paint);
		}
		/*canvas.drawText(screenHeight + " ^ " + screenWidth, 150, 20, paint);
		Toast t = Toast.makeText(getContext(), "draw grid", Toast.LENGTH_LONG);
		t.show();*/
	}
	private void drawCells(Canvas canvas, Paint paint) {
		for (Point x : tmp) {
			drawCell(x, false, avgX, avgY, paint, canvas);
		}
	}
	private void normalize(ArrayList<Point> arr, Point minX, Point minY) {
		float f = minX.x;
		for (Point p : arr) {
			p.x -= f;
		}
		f = minY.y;
		for (Point p : arr) {
			p.y -= f;
		}
	}	
	
	private void drawCell(Point p, boolean exit, float avgX, float avgY, Paint paint, Canvas canvas) {
		if (p.chkd) {
			paint.setColor(Color.BLUE);
		} else {
			if (p.z > 0) paint.setColor(Color.GREEN);
			else paint.setColor(Color.LTGRAY);
			if (p.collides) {
				paint.setColor(Color.WHITE);
			}
		}
		paint.setStyle(Style.FILL_AND_STROKE);
		//canvas.drawRect(p.x , p.y, p.x + avgX, p.y + avgY, paint);
		/*canvas.drawRect(((int)(p.x / avgX)) * avgX, ((int)(p.y / avgY)) * avgY,
				((int)(p.x / avgX)) * avgX + avgX, ((int)(p.y / avgY)) * avgY + avgY, paint);*/
		canvas.drawRect(p.x * avgX, p.y * avgY, p.x * avgX + avgX, p.y * avgY + avgY, paint);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
	}
	private boolean areNeighbours(Point a, Point b) {
		if (Math.abs(a.x - b.x) > 1) return false;
		if (Math.abs(a.y - b.y) > 1) return false;
		return true;
	}
}
