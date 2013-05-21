package com.lapidus.android.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.lapidus.android.R;
import com.lapidus.android.painter.Painter;
import com.lapidus.android.primitives.Point;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
public class Reader extends Activity {
	
	/**вид*/
	ReaderView view;
	/**список соллизий*/
	ArrayList<Collision> collisions;
	/**список точек*/
	ArrayList<Point> arr;
	/**путь*/
	public static String path;
	/**контекст активности*/
	static Context context;	
	
	/**
	 * наследуемый метод создания активности
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.reader_layout);
		context = this;
		view = (ReaderView)findViewById(R.id.readerView);
		if (path == "" || path == null) {
			this.stopService(getIntent());
		}
		ImageView button = (ImageView)findViewById(R.id.reader_gear_button);
		button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, Painter.class);
				Painter.externalPoints = view.finalPoints;
				startActivity(i);
			}
		});
		arr = new ArrayList<Point>();
		Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(null)+ "/" + path);
		if (image == null) finish();		
		for (int i = 0; i < image.getWidth(); i ++) {
			for (int j = 0; j < image.getHeight(); j ++) {
				if (Color.BLACK == image.getPixel(i, j)) {
					//sb.append("<" + i + " " + j + image.getPixel(i, j) + ">");
					//sb.append ("<item>" + i + "</item>\n");
					//sb2.append("<item>" + j + "</item>\n");
					arr.add(new Point(i, j, 0, arr.size() - 1));
				}
				if (Color.GREEN == image.getPixel(i, j)) {
					arr.add(new Point(i, j, 0, -100));
				}
				if (Color.RED == image.getPixel(i, j)) {
					arr.add(new Point(i, j, 0, arr.size()));
				}
			}			
		}					
		view.track = new Track();
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				generateTrack(arr, view.track, view);
			}
		});
		t.start();
		view.invalidate();		
		
	}
	/**
	 * привести список к массиву
	 * @param a список точек 
	 * @return массив точек
	 */
	public static Point[] toArray(ArrayList<Point> a) {
		int i = 0;
		Point[] res = new Point[a.size()];
		for (Point x : a) {
			res[i] = x;
			i ++;
		}
		return res;
	}	
	/**
	 * Стартер обработки коллизии
	 * @param p - точка начала колизии
	 * @param prev - предыдущая точка
	 * @param arr - список всех точек 
	 * @param track - трек
	 * @param v - вид для отрисовки из другого потока
	 */
	private static void collisionStarter (Point p, Point prev, ArrayList<Point> arr, Track track, ReaderView v) {
		if (p.collides == true) return;
		Collision col = new Collision();
		processCollision(arr, p, col, prev);
		HashSet<Point> hs = new HashSet<Point>();
		hs.addAll(col.exitPoints);
		col.exitPoints.clear();
		col.exitPoints.addAll(hs);
		Point[] tr;
		if (col.getExitsQuantity() % 2 == 1) {
			for (Point x : col.exitPoints) {
				tr = findNeighbors(x.x, x.y, arr);
				Log.i("Read col", tr[9].collisionIndex + "^ " + col.center.toString());
				if (tr[9].collisionIndex == 0) {
					col.collidingPoints.add(x);
					col.exitPoints.remove(x);
					break;
				}
			}
		}		
		if (col.getExitsQuantity() == 2) {
			Line l = new Line();
			l.addNextPoint(col.exitPoints.get(0));
			l.addNextPoint(col.exitPoints.get(1));
			track.addLine(l);			
		}
		for (Point x : col.exitPoints) {
			doLine(x, arr, track, v);
		}
		if (col.getExitsQuantity() != 2) {
			track.addCollision(col);			
			v.postInvalidate();
		}		
	}	
	/**
	 * обработка линии
	 * @param start - начало линии
	 * @param arr - список всех точек
	 * @param track - трек
	 * @param v - вид для отрисовки из другого потока
	 */
	private static void doLine(Point start, ArrayList<Point> arr, Track track, ReaderView v) {
		if (start.chkd == true) return;
		Line line = new Line();
		line.addNextPoint(start);
		start.chkd = true;
		boolean bb = true;
		boolean bbb = false;
		Point[] sur;
		Point tmp = start; 
		while (bb) {
			sur = findNeighbors(tmp.x, tmp.y, arr);
			if (sur[9].collisionIndex == 2) {
				for (int i = 0; i < 8; i ++) {
					if (sur[i] != null && sur[i].chkd == false && sur[i].collides == false)  {
						tmp = sur[i];
						break;
					}
				}
				line.addNextPoint(tmp);
				tmp.chkd = true;
				tmp.setLine(line);
				Log.i("RE", "just added " + track.lines.size() + " Po:" + tmp.toString());
			}
			if (sur[9].collisionIndex > 2) {
				Log.i("RE", "started collision");
				collisionStarter(tmp, line.getLast(), arr, track, v);
				Log.i("RE", "exit collision");
				bb = false;
			} else if (sur[9].collisionIndex < 2) {
				if (bbb == false) {
					for (int i = 0; i < 8; i ++) {
						if (sur[i] != null && sur[i].chkd == false && sur[i].collides == false) {
							tmp = sur[i];
							break;
						}
					}
					line.addNextPoint(tmp);
					tmp.chkd = true;
					bbb = true;
				} else {
					line.addNextPoint(tmp);
					bb = false;
				}				
			}
		}
		if (!(line.getPoints().size() == 1 && line.getFirst().collides == true)) {
			if (line.getLast().equals(line.getPoints().get(line.points.size() - 1)) || line.getLast().collides == true) {
				line.removeLast();
			}
			track.addLine(line);
			v.postInvalidate();
		}		
	}
	/**
	 * начать генерацию трека
	 * @param arr - список всех точек
	 * @param track - трек 
	 * @param v - вид для отрисовки из другого потока
	 */
	private static void generateTrack(ArrayList<Point> arr, Track track, ReaderView v) {
		Collections.sort(arr, Point.indexComp);	
		doLine(arr.get(0), arr, track, v);
		
	}
	/**
	 * обработать коллизию 
	 * @param arr - список всех точек 
	 * @param p - начальная точка
	 * @param c - коллизия
	 * @param prev - предыдущаю точка
	 */
	private static void processCollision(ArrayList<Point> arr, Point p, Collision c, Point prev) {
		Point[] sur = findNeighbors(p.x, p.y, arr);	
		for (int i = 0; i < 8; i ++) {
			if (sur[i] != null) {
				for (Point x : c.exitPoints) {
					if (x.equals(sur[i])) return;
				}
			}
		}
		if (sur[8].collisionIndex > 2) {
			c.addCollidingPoint(p);
			p.collides = true;
			for (int i = 0; i < 8; i ++) {
				if (sur[i] != null && sur[i].collides == false && !sur[i].equals(prev)) {
					processCollision(arr, sur[i], c, p);
				}
			}
		} else if (sur[8].collisionIndex <= 2) {
			//avoid double exits from one colliding point 
			for (int i = 0; i < 8; i ++) {
				if (sur[i] != null) {
					for (Point x : c.exitPoints) {
						if (x.equals(sur[i])) return;
					}
				}
			}
			if (sur[8].collisionIndex != sur[9].collisionIndex) c.addExitPoint(p);
		}
	}				
	/**
	 * наити точку в массиве
	 * @param x х координата точки
	 * @param y у координата точки 
	 * @param arr - список точек
	 * @return - объект точки с заданными координатами
	 */
	private static Point findPoint(float x, float y, ArrayList<Point> arr) {
		for (Point p : arr) {
			if (p.x == x && p.y == y) return p;
		}
		return null;
	}
	/**
	 * найти соседей 
	 * @param x - х координата точки
	 * @param y - у координата точки
	 * @param arr - список точек
	 * @return - массив соседних точек
	 */
	private static Point[] findNeighbors(float x, float y, ArrayList<Point> arr) {
		Point[] res = new Point[10];
		int quantity;
		int nocolQuantity = 0;
			res[0] = findPoint(x-1, y-1, arr);
			res[1] = findPoint(x, y-1, arr);
			res[2] = findPoint(x+1, y-1, arr);
			res[3] = findPoint(x+1, y, arr);
			res[4] = findPoint(x+1, y+1, arr);
			res[5] = findPoint(x, y+1, arr);
			res[6] = findPoint(x-1, y+1, arr);
			res[7] = findPoint(x-1, y, arr);
		quantity = 0;
		for (int i = 0; i < 8; i ++) {
			if (res[i] != null) quantity ++;
			if (res[i] != null && res[i].collides == false) nocolQuantity ++;
		}
		res[8] = new Point();
		res[8].collisionIndex = quantity;
		res[9] = new Point();
		res[9].collisionIndex = nocolQuantity;
		return res;
	}		
}
