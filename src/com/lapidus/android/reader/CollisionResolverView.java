package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * класс визуализирует процесс импорта из png
 * @author Егор
 *
 */
public class CollisionResolverView extends View {
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @see android.view.View#View(Context)
	 */
	public CollisionResolverView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @param attrs - аттрибуты xml тега
	 * @see android.view.View#View(Context, AttributeSet))
	 */
	public CollisionResolverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @param attrs - аттрибуты xml тега
	 * @param i - стиль по умолчанию
	 * @see android.view.View#View(Context, AttributeSet, int)))
	 */
	public CollisionResolverView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		init(context);
	}
	/**
	 * установить размеры экрана
	 * @param w - ширина 
	 * @param h - высота 
	 */
	public void setScreenDimensions(int w, int h) {
		screenHeight = h;
		screenWidth = w;
	}
	/**
	 * инициализация полей
	 * @param context - контекст активности 
	 */
	private void init(Context context) {
		paint = new Paint();
		canvas = new Canvas();
		tmp = new ArrayList<Point>();
		c = TrackHolder.c;
		counter = 0;
		vac = new boolean[c.exitPoints.size()];
		for (int i = 0; i < vac.length; i ++) {
			vac[i] = false;
		}
		/*DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;*/
		this.setOnTouchListener(touchListener);
		
		invalidate();
		needredrawcollision = true;
		thisView = this;
		connections = new ArrayList<Segment>();
	}
	// временнные точки 
	Point a, b;
	// список соединенных линий 
	ArrayList<Segment> connections; 
	/**
	 * обработчик касаний экрана
	 */
	OnTouchListener touchListener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				for (Point x : tmp) {
					if (event.getX() > x.x * avgX && event.getX() < x.x * avgX + avgX 
							&& event.getY() > x.y * avgY && event.getY() < x.y * avgY + avgY) {
						if (x.z > 0) {
							Iterator<Segment> iterator = connections.iterator();
							while (iterator.hasNext()) {
								Segment s = iterator.next();
								if (s.start.connection.equals(x) || s.stop.connection.equals(x)) iterator.remove();
							}
							if (a == null) {
								a = new Point(event.getX(), event.getY());
								a.connection = x;							
								b = new Point(event.getX(), event.getY());							
							}							
						}							
					}					
				}
			}
			if (MotionEvent.ACTION_MOVE == event.getAction()) {
				if (b != null) {
					b.x = event.getX();
					b.y = event.getY();
				} 
			}
			if (MotionEvent.ACTION_UP == event.getAction()) {
				for (Point x : tmp) {
					if (event.getX() > x.x * avgX && event.getX() < x.x * avgX + avgX 
							&& event.getY() > x.y * avgY && event.getY() < x.y * avgY + avgY) {
						if (x.z > 0 && !x.equals(a.connection)) {
							Iterator<Segment> iterator = connections.iterator();
							while (iterator.hasNext()) {
								Segment s = iterator.next();
								if (s.start.connection.equals(x) || s.stop.connection.equals(x)) iterator.remove();
							}
							b = new Point(event.getX(), event.getY());
							b.connection = x;							
							connections.add(new Segment(a, b));		
							TrackHolder.updateNewLines(connections);
						}							
					}					
				}
				a = null;
				b = null;
			}
			invalidate();
			return true;
		}
	};	
	/** счетчик соединений*/
	int counter;
	/**перо*/
	Paint paint;
	/**холст*/
	Canvas canvas;
	/**коллизия*/
	Collision c;
	/** размеры экрана*/
	int screenHeight, screenWidth;
	/**размеры клетки*/
	float avgX, avgY;
	/**касание */
	boolean touched;
	/**временная точка*/
	Point hanging;
	/**индикатор необходимости перерисовки коллизии*/
	boolean needredrawcollision;
	
	boolean[] vac;
	/**временный список точек*/
	ArrayList<Point> tmp;
	/**вид*/
	View thisView;
	/**диалог */
	Dialog d;
	/**
	 * наследуемы метод отрисовки 
	 */
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
		drawConnections(canvas, paint);
	}
	/**
	 * отрисовать коллизию
	 * @param canvas - холст 
	 * @param paint - перо
	 */
	private void drawCollision(Canvas canvas, Paint paint) {
		needredrawcollision = false;
		tmp = new ArrayList<Point>();
		for (Point x : c.collidingPoints) {
			tmp.add(new Point(x.x, x.y, -1));
		}
		for (Point x : c.exitPoints) {
			tmp.add(new Point(x.x, x.y, 1));
			tmp.get(tmp.size() - 1).index = c.exitPoints.indexOf(x);
			tmp.get(tmp.size() - 1).connection = x;
		}		
		
		
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
	/**
	 * отрисовать сетку
	 * @param canvas - холст
	 * @param paint - перо
	 */
	private void drawGrid(Canvas canvas, Paint paint) {
		Point t1 = Collections.max(tmp, Point.xComp);
		Point t2 = Collections.min(tmp, Point.xComp);
		Point f1 = Collections.max(tmp, Point.yComp);
		Point f2 = Collections.min(tmp, Point.yComp);
		for (int i = 0; i < (int)(t1.x - t2.x)+1; i ++) {
			canvas.drawLine(avgX * i, 0, avgX * i, screenHeight, paint);			
		}
		for (int i = 0; i < (int)(f1.y - f2.y) + 1; i ++) {
			canvas.drawLine(0, avgY * i, screenWidth, avgY * i, paint);		
		}		
	}
	/**
	 * отрисовать клетки 
	 * @param canvas - холст
	 * @param paint - перо
	 */
	private void drawCells(Canvas canvas, Paint paint) {
		for (Point x : tmp) {
			drawCell(x, false, avgX, avgY, paint, canvas);
		}
	}
	/**
	 * нормализация списка точек
	 * @param arr - список точек
	 * @param minX - минимальный х
	 * @param minY - минимальный у
	 */
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
	/**
	 * отрисовать клетку
	 * @param p - точка клетки 
	 * @param exit - индикатор выхода
	 * @param avgX - размер ячейки 
	 * @param avgY - размер ячейки
	 * @param paint - перо
	 * @param canvas - холст
	 */
	private void drawCell(Point p, boolean exit, float avgX, float avgY, Paint paint, Canvas canvas) {
		if (p.chkd) {
			paint.setColor(Color.BLUE);
		} else {
			if (p.z > 0) paint.setColor(Color.GREEN);
			else paint.setColor(Color.LTGRAY);
			if (p.collisionIndex > 0) {
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
	/**
	 * отрисовать соединение 
	 * @param canvas - холст
	 * @param paint - перо
	 */
	private void drawConnections(Canvas canvas, Paint paint) {
		for (Segment s : connections) {
			paint.setColor(Color.YELLOW);
			canvas.drawRect(s.start.x - 10, s.start.y - 10, s.start.x + 10, s.start.y + 10, paint);
			canvas.drawCircle(s.start.x, s.start.y, 2f, paint);
			canvas.drawRect(s.stop.x - 10, s.stop.y - 10, s.stop.x + 10, s.stop.y + 10, paint);
			canvas.drawCircle(s.stop.x, s.stop.y, 2f, paint);			
			canvas.drawLine(s.start.x, s.start.y, s.stop.x, s.stop.y, paint);			
		}
		if (a != null && b != null) {
			paint.setColor(Color.DKGRAY);
			canvas.drawLine(a.x, a.y, b.x, b.y, paint);
			canvas.drawCircle(a.x, a.y, 10, paint);
			canvas.drawCircle(b.x, b.y, 10, paint);
		}
	}
}
