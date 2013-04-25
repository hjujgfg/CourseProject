package com.lapidus.android.painter;


import java.util.ArrayList;
import java.util.Collections;

import com.lapidus.android.R;
import com.lapidus.android.engine.HelloWorld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
public class Reader extends Activity {
	TextView tw;
	PainterView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new ReaderView(getApplicationContext());			
		setContentView(view);
		//Bitmap image = BitmapFactory.decodeFile("/Painter/res/drawable-hdpi/test.bmp");
		
		ArrayList<Point> arr = new ArrayList<Point>();
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
		ArrayList<Point> processedPoints = processPointsFromBitmap(arr);
		view.approximizedPoints = Approximizer.approximize(2f, toArray(processedPoints));
		view.points = processedPoints;			
		System.out.print(sb);
	}
	private Point[] toArray(ArrayList<Point> a) {
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
	private static ArrayList<Point> processPointsFromBitmap(ArrayList<Point> arr) {
		Collections.sort(arr, Point.indexComp);		
		boolean bb = true; 
		Point[] circum;
		Point tmp; 
		ArrayList<Point> result = new ArrayList<Point>();
		result.add(arr.get(0));
		tmp = arr.get(0);
		int counter; 
		while (bb) {
			circum = findNeighbours(tmp.x, tmp.y, arr);
			tmp.chkd = true;
			result.add(tmp);
			tmp = chooseSmoothest(tmp, circum, result.get(result.size() - 2));
			/*counter = 0; 
			for (int i = 0; i < 8; i++) {
				if (circum[i] != null && circum[i].chkd == false) {
					counter ++;
					tmp = circum[i];
				}				
			}
			if (counter > 2) {
				result.get(result.size() - 1).collides = true;				
			}*/
			
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
		for (int i = 0; i < sur.length; i ++) {
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
	private static Point[] findNeighbours(float x, float y, ArrayList<Point> arr) {
		Point[] res = new Point[8];
			res[0] = findPoint(x-1, y-1, arr);
			res[1] = findPoint(x, y-1, arr);
			res[2] = findPoint(x+1, y-1, arr);
			res[3] = findPoint(x+1, y, arr);
			res[4] = findPoint(x+1, y+1, arr);
			res[5] = findPoint(x, y+1, arr);
			res[6] = findPoint(x-1, y+1, arr);
			res[7] = findPoint(x-1, y, arr);
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
