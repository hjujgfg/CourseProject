package com.lapidus.android.painter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;


import com.lapidus.android.R;
import com.lapidus.android.engine.Engine;
import com.lapidus.android.engine.ObjectViewer;
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
				Toast t = Toast.makeText(getApplicationContext(), "shit", Toast.LENGTH_SHORT);
				t.show();
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.options_layout);
				dialog.setTitle("Options");
				TextView tw1 = (TextView)dialog.findViewById(R.id.textView1);
				tw1.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						view.invalidate();
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
				TextView tw2 = (TextView)dialog.findViewById(R.id.textView2);
				tw2.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						/*//loadFileList();
						//onCreateDialog(1000);
						Intent i = new Intent(context, Reader.class);
						startActivity(i);*/
						view.invalidate();
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
				TextView tw3 = (TextView)dialog.findViewById(R.id.textView3);
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
							File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/newimage.png" );
							//f.mkdirs();
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
				dialog.show();
			}
		});
		if (externalPoints != null) {
			view.getExternalPoints(externalPoints);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_painter, menu);
		menu.add(Menu.NONE, 1, Menu.NONE, "Read File");
		menu.add(Menu.NONE, 2, Menu.NONE, "Save Image");
		menu.add(Menu.NONE, 3, Menu.NONE, "Choose File");
		menu.add(Menu.NONE, 4, Menu.NONE, "Pass route");
		menu.add(Menu.NONE, 5, Menu.NONE, "Redo");
		return true;
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent i = new Intent(this, Reader.class);
			startActivity(i);
		}
		if (item.getItemId() == 2) {
			//Bitmap image = getBitmapFromView(view);
			Bitmap image = view.getPreparedBitmap();
			if (image == null) {
				Toast t = Toast.makeText(getApplicationContext(), "Nothing to save", Toast.LENGTH_SHORT);
				t.show();
				return true;
			}
			try {
				File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj"+"/newimage.png" );
				//f.mkdirs();
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
		if (item.getItemId() == 3) {
			//loadFileList();
			//this.onCreateDialog(1000);
			Intent i = new Intent(this, Options.class);
			startActivity(i);
		}
		if (item.getItemId() == 4) {
			view.invalidate();
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
			/*for (int i = 0; i < view.approximizedPoints.length; i++) {
				tmp.add(view.approximizedPoints[i]);
			}
			int ind;
			for (Point x : view.intersectingPoints) {
				ind = tmp.indexOf(x.S1().start);
				//tmp.get(ind - 1).z = 2;
				tmp.get(ind).z = 2;
				//tmp.get(ind - 1).z = 2;
				//tmp.get(ind + 2).z = 2;
				tmp.add(ind + 1, new Point(x.x, x.y, 4));
				ind = tmp.indexOf(x.S2().start);
				tmp.get(ind).z = 0;
				tmp.get(ind + 1).z = 0;
				//tmp.add(ind + 1, new Point(x.x, x.y, 0));
			}*/
			/*for (int i = 0; i < view.approximizedPoints.length; i++) {
				Engine.path.add(new Point(view.approximizedPoints[i].x, view.approximizedPoints[i].y));
			}*/
			for (Point a : tmp) {
				Engine.path.add(new Point(a.x, a.y, a.z));
			}
			//Engine.path = (ArrayList<Point>) view.points.clone();
			Engine.bb = true;
			Intent i = new Intent(this, Engine.class);
			startActivity(i);
		}
		if (item.getItemId() == 5) {
			if (view.redoPoints.size() > 0) {
				view.points.add(view.redoPoints.get(view.redoPoints.size() - 1));
				view.redoPoints.remove(view.redoPoints.get(view.redoPoints.size() - 1));
				view.refreshSegs();				
			}			
			view.invalidate();
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
		}					
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
