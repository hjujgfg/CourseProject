package com.lapidus.android.reader;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.R;
import com.lapidus.android.engine.HelloWorld;
import com.lapidus.android.painter.Point;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.NeighboringCellInfo;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
public class Reader extends Activity {
	TextView tw;
	ReaderView view;
	ArrayList<Collision> collisions;
	ArrayList<Point> arr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new ReaderView(getApplicationContext());			
		setContentView(view);
		//Bitmap image = BitmapFactory.decodeFile("/Painter/res/drawable-hdpi/test.bmp");
		
		arr = new ArrayList<Point>();
		Bitmap image = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/newimage.png" );
		StringBuilder sb = new StringBuilder();		
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
			//sb.append("\n");		
		}
		//HelloWorld.path = (ArrayList<Point>) arr.clone(); 
		//HelloWorld.bb = false;
		/*view.pointsAreProcessed = true;
		ArrayList<Point> processedPoints = processPointsFromBitmap(arr);
		view.approximizedPoints = Approximizer.approximize(2f, toArray(processedPoints));
		view.points = processedPoints;*/
		//ArrayList<Point> proc = processPointsFromBitmap(arr);
		Collections.sort(arr, Point.indexComp);
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/arrcoords.txt" );
		try {
			FileWriter fw = new FileWriter(f);
			for (Point x : arr) {
				fw.write(x.toString() + "\n");
				Log.i("AR", x.toString());
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast t = Toast.makeText(getApplicationContext(), "shitshitshit", Toast.LENGTH_SHORT);
			t.show();
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
		
		
		
		/*view.points = arr;
		collisions = view.cols;
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub				
				findCollisions(arr, collisions, view);
				view.postInvalidate();
			}
		});
		t.start();	*/	
		
	}
	public static Point[] toArray(ArrayList<Point> a) {
		int i = 0;
		Point[] res = new Point[a.size()];
		for (Point x : a) {
			res[i] = x;
			i ++;
		}
		return res;
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_reader, menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "Process");
		return true;
	}
	private static void collisionStarter (Point p, Point prev, ArrayList<Point> arr, Track track, ReaderView v) {
		if (p.collides == true) return;
		Collision col = new Collision();
		processCollision(arr, p, col, prev);
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
	private static void findCollisions (ArrayList<Point> arr, ArrayList<Collision> cols, ReaderView v) {
		//ArrayList<Collision> cols = vi;
		int o = 0;
		Point[] sur; 
		Collision tmp; 
		for (Point x : arr) {
			if (x.collides == false) {
				o = 0;
				sur = findNeighbors(x.x, x.y, arr);
				o = sur[8].collisionIndex;
				if (o > 2) {
					//tmp = processCollision(arr, x, sur);
					//if (tmp.getExitsQuantity() > 2) {
						
					//}
					cols.add(processCollision(arr, x, sur));
					v.postInvalidate();
				}
			}
		}			
		//return cols;
	}
	public static void doLine(Point start, ArrayList<Point> arr, Track track, ReaderView v) {
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
			track.addLine(line);
			v.postInvalidate();
		}		
	}
	private static void generateTrack(ArrayList<Point> arr, Track track, ReaderView v) {
		Collections.sort(arr, Point.indexComp);	
		doLine(arr.get(0), arr, track, v);		
	}
	public static void processCollision(ArrayList<Point> arr, Point p, Collision c, Point prev) {
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
	public static Collision processCollision(ArrayList<Point> arr, Point p, Point[] neighbors) {
		Collision col = new Collision();
		int o = -1;											
		Point[] nextNeighbors;
		col.addCollidingPoint(p);
		p.collides = true;									
		for (int i = 0; i < 8; i ++) {
			if (neighbors[i] != null) {
				o = -1;
				nextNeighbors = findNeighbors(neighbors[i].x, neighbors[i].y, arr);
				o = nextNeighbors[8].collisionIndex;
				if (o <= 2) col.addExitPoint(neighbors[i]);
				if (o > 2) {
					if (neighbors[i].collides == false) {
						col.merge(processCollision(arr, neighbors[i], nextNeighbors));
					}					
				}							
			}
		}		
		return col; 
	}
	private static ArrayList<Point> processPointsFromBitmap(ArrayList<Point> arr) {
		Collections.sort(arr, Point.indexComp);		
		boolean bb = true; 
		Point[] circum;
		Point tmp; 
		Integer quantity = 0;
		ArrayList<Point> result = new ArrayList<Point>();
		result.add(arr.get(0));
		tmp = arr.get(0);
		int counter; 
		while (bb) {
			circum = findNeighbors(tmp.x, tmp.y, arr);
			tmp.chkd = true;
			result.add(tmp);
			tmp.index = result.size() - 1;
			tmp = chooseSmoothest(tmp, circum, result.get(result.size() - 2));			
			
			if (tmp == null) {
				bb = false; 
			}
		}
		return result;
	}
	private static Point chooseSmoothest(Point a, Point[] sur, Point prev) {
		float mul = Integer.MAX_VALUE; 
		int index = -1;
		float tmp; 
		boolean hasPoints = false; 
		for (int i = 0; i < 8; i ++) {
			if (sur[i] != null) hasPoints = true; 
			if (sur[i] != null && sur[i].chkd == false) {
				tmp = vectorMult(prev, a, sur[i]);
				if (Math.abs(tmp) < Math.abs(mul)) { 
					mul = tmp;
					index = i;
				}
			}
		}
		if (mul != Integer.MAX_VALUE) {
			return sur[index];
		}
		if (!hasPoints) return null; 
		
		return null;
	}
	private static float vectorMult(Point p1, Point p2, Point p3) {
		return (p2.x - p1.x)*(p3.y - p1.y) - (p2.y - p1.y)*(p3.x - p1.x);
	}	
	private static Point findPoint(float x, float y, ArrayList<Point> arr) {
		for (Point p : arr) {
			if (p.x == x && p.y == y) return p;
		}
		return null;
	}
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
	
	public static ArrayList<Point> processPointsPrepared(ArrayList<Point> arr) {		
		Collections.sort(arr, Point.indexComp);		
		return null;
	}
	private static final int LEFT = 7;
	private static final int LEFT_UP = 0;
	private static final int UP = 1;
	private static final int RIGHT_UP = 2;
	private static final int RIGHT = 3;
	private static final int RIGHT_DOWN = 4;
	private static final int DOWN = 5;
	private static final int LEFT_DOWN = 6;
	private static final int NIN = -1;
	private class Intersection {
		
	}	
	
}
