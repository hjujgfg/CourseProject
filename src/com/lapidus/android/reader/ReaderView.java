package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.R;
import com.lapidus.android.primitives.Point;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ReaderView extends View {
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @see android.view.View#View(Context)
	 */
	public ReaderView(Context context) {
		super(context);		
		// TODO Auto-generated constructor stub
		init();
	}
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @param attrs - аттрибуты xml тега
	 * @see android.view.View#View(Context, AttributeSet))
	 */
	public ReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	/**
	 * конструктор 
	 * @param context - контекст родительской активности 
	 * @param attrs - аттрибуты xml тега
	 * @param i - стиль по умолчанию
	 * @see android.view.View#View(Context, AttributeSet, int)))
	 */
	public ReaderView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		init();
	}
	/**
	 * метод инициализации
	 */
	private void init() {
		paint = new Paint();
		this.setOnTouchListener(ontouchlistener);		
		points = new ArrayList<Point>();				
		canvas = new Canvas();
		cols = new ArrayList<Collision>();
		pointsAreProcessed = false;
		track = new Track();
		thisView = this;
		finalPoints = new ArrayList<Point>();
	}
	/**этот вид*/
	View thisView;
	/**список точек*/
	ArrayList<Point> points;
	/**список точек после обработки */
	ArrayList<Point> finalPoints;
	/**список коллизий*/
	ArrayList<Collision> cols;
	/**линия старта*/
	Line startLine;
	/**конечная линия*/
	Line endLine;
	/**трек*/
	Track track;	
	/**массив аппроксимированных точек */
	Point[] approximizedPoints;
	boolean pointsAreProcessed;
	/**перо*/
	Paint paint;
	/**холст*/
	Canvas canvas;
	/**контекст активности */
	Context context;
	/**
	 * наследуемый метод отрисовки 
	 */
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
		drawPoints(finalPoints, canvas, paint);
	}
	/**
	 * отрисовка точек
	 * @param canvas - холст
	 * @param paint - перо
	 */
	protected void drawPoints(Canvas canvas, Paint paint) {
		paint.setColor(Color.BLACK);
		for (Point x : points) {
			canvas.drawCircle(x.x, x.y, 1, paint);
		}		
		paint.setColor(Color.BLACK);
	}
	/**
	 * перегрузка метода отрисовки точек 
	 * @param arr - массив точек
	 * @param canvas - холст
	 * @param paint - перо
	 */
	protected void drawPoints(ArrayList<Point> arr, Canvas canvas, Paint paint) {
		if (arr.size() < 2 ) return;
		paint.setColor(Color.RED);
		for (int i = 0; i < arr.size() - 1; i ++) {
			canvas.drawLine(arr.get(i).x, arr.get(i).y, arr.get(i + 1).x, arr.get(i + 1).y, paint);
		}
		paint.setColor(Color.BLACK);
	}
	/**
	 * отрисовка обработанных точек
	 * @param points - список точек
	 * @param canvas - холст
	 * @param paint - перо
	 */
	protected void drawProcessedPoints (ArrayList<Point> points, Canvas canvas, Paint paint) {
		paint.setColor(Color.DKGRAY);
		for (int i = 0; i < points.size() - 1; i ++) {
			Log.i("PO", points.get(i).toString());
			canvas.drawLine(points.get(i).x, points.get(i).y, 
					points.get(i + 1).x, points.get(i + 1).y, paint);
		}
		paint.setColor(Color.BLACK);
	}
	/**
	 * отрисовка аппроксимированных точек
	 * @param canvas - холст
	 * @param paint - перо
	 */
	protected void drawApproximizedPoints(Canvas canvas, Paint paint) {
		paint.setColor(Color.RED);
		if (approximizedPoints == null || approximizedPoints.length == 0) return; 
		for (int i = 0; i < approximizedPoints.length - 1; i ++) {
			canvas.drawLine(approximizedPoints[i].x, approximizedPoints[i].y, 
					approximizedPoints[i + 1].x, approximizedPoints[i + 1].y, paint);						
		}
		paint.setColor(Color.BLACK);
	}
	/**
	 * отрисовка коллизии
	 * @param canvas - холст
	 * @param paint - перо
	 * @param cols - список коллизий
	 */
	protected void drawCollisions(Canvas canvas, Paint paint, ArrayList<Collision> cols) {
		paint.setColor(Color.YELLOW);
		if (cols == null) return;
		/*for (Collision c : cols) {
			canvas.drawCircle(c.x(), c.y(), 15, paint);
		}*/
		for (int i = 0; i < cols.size(); i ++) {
			canvas.drawCircle(cols.get(i).x(), cols.get(i).y(), 15, paint);
			canvas.drawText(cols.get(i).center.toString(), cols.get(i).x() + 10, cols.get(i).y(), paint);
		}
		paint.setColor(Color.BLACK);
	}
	/**
	 * отрисовка трека
	 * @param track - трек
	 * @param canvas - холст
	 * @param paint - перо
	 */
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
	}
	/**
	 * отрисовка линии
	 * @param l - линия
	 * @param canvas - холст
	 * @param paint - перо
	 */
	protected void drawLine(Line l, Canvas canvas, Paint paint) {
		drawProcessedPoints(l.getPoints(), canvas, paint);
	}
	/**
	 * обновить точки после обработки 
	 */
	private void updateFinalPoints() {
		if (startLine == null) return;
		finalPoints.clear();
		Track t = new Track();
		try {
			t = track.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finalPoints.clear();
		finalPoints.addAll(t.lines.get(0).points);						
		Point tmp = t.lines.get(0).getLast();
		t.lines.remove(0);
		boolean bb = true;
		while (bb) {
			bb = false;
			for (Line l : t.lines) {
				if (l.getFirst().x == tmp.x && l.getFirst().y == tmp.y) {
					l.points.remove(tmp);
					finalPoints.addAll(l.points);
					tmp = l.getLast();
					bb = true;
					t.lines.remove(l);
					break;									
				} else if (l.getLast().x == tmp.x && l.getLast().y == tmp.y) {
					l.points.remove(tmp);
					Log.i("qw e", l.getLast().toString() + " 1^" + tmp.toString());
					Collections.reverse(l.points);
					Log.i("qw e", l.getLast().toString() + " 2^" + tmp.toString());
					finalPoints.addAll(l.points);
					tmp = l.getLast();
					bb = true;
					t.lines.remove(l);
					break;
				} 
			}
		}
		Toast to = Toast.makeText(getContext(), finalPoints.size() + " ", Toast.LENGTH_LONG);
		to.show();
	}
	/**
	 * обработчик касаний экрана
	 */
	private OnTouchListener ontouchlistener = new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			ArrayList<Collision> tol = track.getCollisions();
			TrackHolder.addTrack(track);
			float xx, yy;
			for (Collision x : tol) {
				xx = event.getX() - x.center.x;
				yy = event.getY() - x.center.y;
				if (xx * xx < 256 && yy * yy < 256) {
					if (x.exitPoints.size() == 1) {
						
						final Collision tr = x;
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder
						.setTitle("Choose start point")
						.setMessage("Set this point as start?")
						
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if (startLine != null) {
									track.lines.remove(startLine);
								}
								startLine = new Line();
								startLine.addNextPoint(tr.collidingPoints.get(0));
								startLine.addNextPoint(tr.exitPoints.get(0));
								track.lines.add(0, startLine);
								updateFinalPoints();
								thisView.invalidate();
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						AlertDialog dialog = builder.create();
						dialog.show();						
						
						return false;
					}
					TrackHolder.c = x;					
					final Dialog dialog = new Dialog(thisView.getContext());
					dialog.setContentView(R.layout.collision_resolver_layout);
					CollisionResolverView resView = (CollisionResolverView) dialog.findViewById(R.id.collisionResolverView1);
					dialog.setTitle("Collision resolver");
					ImageView exit = (ImageView) dialog.findViewById(R.id.resolver_cross);
					final Collision collisionForBut = x;
					exit.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (collisionForBut.resolvedLines.size() != 0) {
								for (Line t : collisionForBut.resolvedLines) {
									track.getLines().remove(t);
								}
								collisionForBut.clearResolvedLines();
							}
							for (Line l : TrackHolder.newLines) {
								collisionForBut.addResolvedLine(l);
								track.addLine(l);								
							}
							updateFinalPoints();
							
								
							thisView.invalidate();
							dialog.dismiss();
							if (startLine == null) {
								Toast t = Toast.makeText(getContext(), "Touch one of the endpoints to set start", Toast.LENGTH_LONG);
								t.show();
							}
						}
					});					
					resView.d = dialog;
					dialog.show();
					
					return false;					
				}
			}
			return false;
		}
	};
}
