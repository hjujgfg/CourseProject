package com.lapidus.android.painter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;


import com.lapidus.android.R;
import com.lapidus.android.engine.Engine;
import com.lapidus.android.engine.ObjectViewer;
import com.lapidus.android.engine.VehicleViewer;
import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;
import com.lapidus.android.reader.Reader;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Painter extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new PainterView(getApplicationContext());
		setContentView(R.layout.activity_painter);
		ImageView button = (ImageView)findViewById(R.id.button);
		view = (PainterView) findViewById(R.id.view);
		final Context ctx = this;
		button.setOnClickListener(new OnClickListener() {
		
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.options_layout);
				dialog.setTitle("Options");
				TextView tw1 = (TextView)dialog.findViewById(R.id.painter_start_but);
				tw1.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						view.invalidate();
						if (view.segs.size() <= 3) {
							Toast t = Toast.makeText(ctx, "Short track. Please lengthen the track", Toast.LENGTH_LONG);
							t.show();
							return;
						}
						
						Engine.path = new ArrayList<Point>();
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(view.segs.get(0).start);
						Segment x;
						for (int i = 1; i < view.segs.size() - 1; i ++) {
							x = view.segs.get(i);
							tmp.add(x.stop);
							if (x.collides == true) {
								x.start.z += 2;
								x.stop.z = x.start.z + 3;
								view.segs.get(i - 1).start.z = x.start.z + 1;
							} else {
								x.stop.z = x.start.z;
							}
						}
						tmp.get(0).z = 0;
						
						for (Point a : tmp) {
							Engine.path.add(new Point(a.x, a.y, a.z));
						}
						//Engine.path = (ArrayList<Point>) view.points.clone();
						Engine.bb = true;
						Intent i = new Intent(ctx, Engine.class);
						startActivity(i);
					}
				});
				TextView tw2 = (TextView)dialog.findViewById(R.id.painter_import_but);
				tw2.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						loadFileList();
						onCreateDialog(1000);
						Reader.path = mChosenFile;
						if (mChosenFile == "") {
							Toast t = Toast.makeText(ctx, "No file chosen", Toast.LENGTH_LONG);
							t.show();
							return;
						}																		
					}
				});
				TextView tw3 = (TextView)dialog.findViewById(R.id.painter_export_but);
				tw3.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
											
						Bitmap image = view.getPreparedBitmap();
						if (image == null) {
							Toast t = Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_SHORT);
							t.show();
							return;
						}
						try {
							File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj");//+"/newimage.png" );
							f.mkdirs();
							f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj" + "/newimage.png" );
							f.createNewFile();
							FileOutputStream fos = new FileOutputStream(f);
							image.compress(Bitmap.CompressFormat.PNG, 90, fos);
							fos.close();
							Toast t = Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT);
							t.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				TextView tw4 = (TextView)dialog.findViewById(R.id.painter_preview_but);
				tw4.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						view.invalidate();
						view.invalidate();
						if (view.segs.size() <= 3) {
							Toast t = Toast.makeText(ctx, "Short track. Please lengthen the track", Toast.LENGTH_LONG);
							t.show();
							return;
						}
						ObjectViewer.path = new ArrayList<Point>();
						ArrayList<Point> tmp = new ArrayList<Point>();
						tmp.add(view.segs.get(0).start);
						Segment x;
						for (int i = 1; i < view.segs.size() - 1; i ++) {
							x = view.segs.get(i);
							tmp.add(x.stop);
							if (x.collides == true) {
								x.start.z += 2;
								x.stop.z = x.start.z + 3;
								view.segs.get(i - 1).start.z = x.start.z + 1;
							} else {
								x.stop.z = x.start.z;
							}
						}
						tmp.get(0).z = 0;
						
						for (Point a : tmp) {
							ObjectViewer.path.add(new Point(a.x, a.y, a.z));
						}
						//Engine.path = (ArrayList<Point>) view.points.clone();
						
						Intent i = new Intent(ctx, ObjectViewer.class);
						startActivity(i);
					}
				});
				dialog.show();
			}
		});
		if (externalPoints != null) {
			view.getExternalPoints(externalPoints);
			view.invalidate();
		}
	}
	final Context context = this;
	PainterView view;
	Button b;
	public static ArrayList<Point> externalPoints;
	private static final int DIALOG_LOAD_FILE = 1000;
	private String[] mFileList;
	private String mChosenFile;
	private static final String FTYPE = ".png";
	private File mPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj");
	
	
	
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
                    Reader.path = mChosenFile;
                    Intent i = new Intent(context, Reader.class);
					startActivity(i);
                }
            });
            break;
		}
		dialog = builder.show();
		return dialog;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (view.points.size() == 0) {
				view.invalidate();
				return super.onKeyDown(keyCode, event);
			}
			if (view.points.size() > 0)	{
				view.undo();
			}			
			view.invalidate();			
			return true;
		} else {
			view.invalidate();
			return super.onKeyDown(keyCode, event);
		}*/		
		return super.onKeyDown(keyCode, event);
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
