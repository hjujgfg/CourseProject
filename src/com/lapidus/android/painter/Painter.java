package com.lapidus.android.painter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

import com.lapidus.android.R;
import com.lapidus.android.engine.HelloWorld;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Painter extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new PainterView(getApplicationContext());
		setContentView(view);
	}
	PainterView view;
	private static final int DIALOG_LOAD_FILE = 1000;
	private String[] mFileList;
	private String mChosenFile;
	private static final String FTYPE = ".png";
	private File mPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj");
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_painter, menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "Read File");
		menu.add(Menu.NONE, 2, Menu.NONE, "Save Image");
		menu.add(Menu.NONE, 3, Menu.NONE, "Choose File");
		menu.add(Menu.NONE, 4, Menu.NONE, "Pass route");
		return true;
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent i = new Intent(this, Reader.class);
			startActivity(i);
		}
		if (item.getItemId() == 2) {
			Bitmap image = getBitmapFromView(view);
			try {
				File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/newimage.png" );
				//f.mkdirs();
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				image.compress(Bitmap.CompressFormat.PNG, 90, fos);
				Toast t = Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT);
				t.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (item.getItemId() == 3) {
			loadFileList();
			this.onCreateDialog(1000);
		}
		if (item.getItemId() == 4) {
			HelloWorld.path = new ArrayList<Point>();
			for (int i = 0; i < view.approximizedPoints.length; i++) {
				HelloWorld.path.add(new Point(view.approximizedPoints[i].x, view.approximizedPoints[i].y));
			}
			HelloWorld.bb = true;
			Intent i = new Intent(this, HelloWorld.class);
			startActivity(i);
		}
		return true;
	}
	
	public static Bitmap getBitmapFromView(View view) {
	    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(returnedBitmap);
	    Drawable bgDrawable =view.getBackground();
	    if (bgDrawable!=null) 
	        bgDrawable.draw(canvas);
	    else 
	        canvas.drawColor(Color.WHITE);
	    view.draw(canvas);
	    return returnedBitmap;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);
		
		switch(id) {
        case DIALOG_LOAD_FILE:
            builder.setTitle("Choose your file");
            if(mFileList == null) {
                Log.e("tag1", "Showing file picker before loading the file list");
                dialog = builder.create();
                return dialog;
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];
                    //you can do stuff with the file here too
                    view.setImage(mPath + "/" + mChosenFile);
                }
            });
            break;
		}
		dialog = builder.show();
		return dialog;
	}
	private void loadFileList() {
	    try {
	        mPath.mkdirs();
	    }
	    catch(SecurityException e) {
	        Log.e("tag2", "unable to write on the sd card " + e.toString());
	    }
	    if(mPath.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                return filename.contains(FTYPE) || sel.isDirectory();
	            }
	        };
	        mFileList = mPath.list(filter);
	    }
	    else {
	        mFileList= new String[0];
	    }
	}
}
