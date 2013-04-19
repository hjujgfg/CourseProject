package com.lapidus.android.painter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.Vector;
public class Reader extends Activity {
	TextView tw;
	PainterView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new PainterView(getApplicationContext());
		setContentView(view);
		tw = (TextView) findViewById(R.id.textView1);		
		//Bitmap image = BitmapFactory.decodeFile("/Painter/res/drawable-hdpi/test.bmp");
		
		ArrayList<Point> arr = new ArrayList<Point>();
		Bitmap image = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/newimage.png" );
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < image.getWidth(); i ++) {
			for (int j = 0; j < image.getHeight(); j ++) {
				if (Color.BLACK == image.getPixel(i, j)) {
					//sb.append("<" + i + " " + j + image.getPixel(i, j) + ">");
					//sb.append ("<item>" + i + "</item>\n");
					//sb2.append("<item>" + j + "</item>\n");
					arr.add(new Point(i, j, 0, arr.size()));
				}
				if (Color.GREEN == image.getPixel(i, j)) {
					arr.add(new Point(i, j, 0, 0));
				}
				if (Color.RED == image.getPixel(i, j)) {
					arr.add(new Point(i, j, 0, arr.size()));
				}
			}
			//sb.append("\n");		
		}
		HelloWorld.path = (ArrayList<Point>) arr.clone(); 
		HelloWorld.bb = false;
		Intent k = new Intent(this, HelloWorld.class);
		startActivity(k);
		/*ArrayList<Point> ret;
		try {
			ret = processPointsFromBitmap(arr);
			HelloWorld.path = ret;
			view.points = ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*ArrayList<Point> sortedPoints = processPointsFromBitmap(arr);
		Point[] tmp = new Point[arr.size()];
		int i = 0;
		for (Point x : sortedPoints) {
			tmp[i] = x;
			i ++;
		}
		tmp = Approximizer.approximize(2f, tmp.clone());
		
		HelloWorld.path = new ArrayList<Point>();
		for (i = 0; i < tmp.length; i++) {
			HelloWorld.path.add(tmp[i]);
		}*/
		//Intent j = new Intent(this, HelloWorld.class);
		//startActivity(j);
		/*try {
			File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj" + "/i.txt");
			FileWriter fw = new FileWriter(f);
			fw.write(sb.toString());
			fw.close();
			f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj" + "/j.txt");
			fw = new FileWriter(f);
			fw.write(sb2.toString());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		System.out.print(sb);
	}
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_reader, menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "Process");
		return true;
	}
	private static ArrayList<Point> processPointsFromBitmap(ArrayList<Point> arr) throws IOException {
		Collections.sort(arr, Point.indexComp);
		ArrayList<Point> result = new ArrayList<Point>();
		Point tmp;
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/coordslog.txt" );
		FileWriter fw = new FileWriter(f);
		for (Point x : arr) {
			tmp = findNeighbour(x.x, x.y, arr);
			if (tmp != null) {
				result.add(tmp);
			} else break;
			fw.write(x.toString() + " ^ " + tmp.toString() + "\n");
		}		
		fw.close();
		return result;
	}
	private static boolean findPoint(float x, float y, ArrayList<Point> arr) {
		for (Point p : arr) {
			if (p.x == x && p.y == y) return true;
		}
		return false;
	}
	private static Point findNeighbour(float x, float y, ArrayList<Point> arr) {
		Point res = null;
			if (findPoint(x-1, y-1, arr)) return new Point(x-1, y-1);
			if (findPoint(x, y-1, arr)) return new Point(x, y-1);
			if (findPoint(x+1, y-1, arr)) return new Point(x+1, y-1);
			if (findPoint(x+1, y, arr)) return new Point(x+1, y);
			if (findPoint(x+1, y+1, arr)) return new Point(x+1, y+1);
			if (findPoint(x, y+1, arr)) return new Point(x, y+1);
			if (findPoint(x-1, y+1, arr)) return new Point(x-1, y+1);
			if (findPoint(x-1, y, arr)) return new Point(x-1, y);
		return res;
	}	
	
	public static ArrayList<Point> processPointsPrepared(ArrayList<Point> arr) {
		
		Collections.sort(arr, Point.indexComp);
		
		return null;
	}
	private static final int LEFT = 0;
	private static final int LEFT_UP = 1;
	private static final int UP = 2;
	private static final int RIGHT_UP = 3;
	private static final int RIGHT = 4;
	private static final int RIGHT_DOWN = 5;
	private static final int DOWN = 6;
	private static final int LEFT_DOWN = 7;
	private static final int NIN = -1;
	private class Intersection {
		
	}	
}
