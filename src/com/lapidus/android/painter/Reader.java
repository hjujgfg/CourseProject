package com.lapidus.android.painter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.lapidus.android.R;
import com.lapidus.android.engine.HelloWorld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

public class Reader extends Activity {
	TextView tw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_layout);
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
					arr.add(new Point(i, j));
				}							
			}
			//sb.append("\n");
		}
		HelloWorld.path = (ArrayList<Point>) arr.clone();
		Intent i = new Intent(this, HelloWorld.class);
		startActivity(i);
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
}
