package com.lapidus.android.painter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import com.lapidus.android.R;
import com.lapidus.android.engine.Engine;
import com.lapidus.android.engine.ObjectViewer;
import com.lapidus.android.engine.VehicleViewer;
import com.lapidus.android.net.ServerThreadSer;
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
	/**
	 * наследуемый метод, вызывается при создании активности 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new PainterView(getApplicationContext());
		setContentView(R.layout.activity_painter);
		ImageView button = (ImageView)findViewById(R.id.button);
		view = (PainterView) findViewById(R.id.view);
		final Context ctx = this;
		mPath = new File(getExternalFilesDir(null).getAbsolutePath());
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
								x.start.z -= 2;
								x.stop.z = x.start.z - 3;
								view.segs.get(i - 1).start.z = x.start.z - 1;
							} else {
								x.stop.z = x.start.z;
							}
						}
						tmp.get(0).z = 0;
						
						for (Point a : tmp) {
							Engine.path.add(new Point(a.x, a.y, a.z));
						}
						if (isMulti == false) {
							//Engine.path = (ArrayList<Point>) view.points.clone();
							Engine.bb = true;
							Intent i = new Intent(ctx, Engine.class);
							startActivity(i);
						} else {
							ServerThreadSer.arr = new ArrayList<Point>();
							for (Point xx : tmp) {
								try {
									ServerThreadSer.arr.add(xx.clone());
								} catch (CloneNotSupportedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							ServerThreadSer.context = context;
							Thread t = new Thread(new ServerThreadSer());
							t.start();
						}
						dialog.dismiss();
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
							File f = new File(getExternalFilesDir(null).getAbsolutePath());//+"/newimage.png" );
							f.mkdirs();
							f = new File(getExternalFilesDir(null).getAbsolutePath() + "/newimage.png" );
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
				TextView tw5 = (TextView)dialog.findViewById(R.id.painter_save_but);
				tw5.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						view.invalidate();
						//File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cprj");
						File f = new File(getExternalFilesDir(null).getAbsolutePath());
						f.mkdirs();
						f = new File(getExternalFilesDir(null).getAbsolutePath() + "/track.ser" );
						Log.i("saving", getExternalFilesDir(null).getAbsolutePath());
						try {
							f.createNewFile();
							FileOutputStream fos = new FileOutputStream(f);
							ObjectOutputStream oos = new ObjectOutputStream(fos);
							oos.writeObject(view.points);
							oos.flush();
							oos.close();
							fos.close();
							Toast t = Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT);
							t.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Toast t = Toast.makeText(getApplicationContext(), "not saved - exception", Toast.LENGTH_SHORT);
							t.show();
							e.printStackTrace();
						}
					}
				});
				TextView tw6 = (TextView)dialog.findViewById(R.id.painter_open_but);
				tw6.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						view.invalidate();
						File f = new File(getExternalFilesDir(null).getAbsolutePath());						
						f = new File(getExternalFilesDir(null).getAbsolutePath() + "/track.ser" );
						try {							
							FileInputStream fis = new FileInputStream(f);
							ObjectInputStream oos = new ObjectInputStream(fis);							
							view.points = (ArrayList<Point>) oos.readObject();													
							oos.close();
							fis.close();
							view.smoothAll();
							view.invalidate();
							Toast t = Toast.makeText(getApplicationContext(), "opened", Toast.LENGTH_SHORT);
							t.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Toast t = Toast.makeText(getApplicationContext(), "no file track.ser?", Toast.LENGTH_SHORT);
							t.show();
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
				TextView tw7 = (TextView)dialog.findViewById(R.id.painter_clear_but);
				tw7.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						view.points.clear();
						view.segs.clear();
						view.approximizedPoints = null;
						view.intersectingPoints.clear();
						view.invalidate();
						Toast t = Toast.makeText(getApplicationContext(), "Cleared", Toast.LENGTH_SHORT);
						t.show();
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
	// хранитель контекста
	final Context context = this;
	//вид рисования
	PainterView view;
	
	Button b;
	//список точек для загрузки из файла
	public static ArrayList<Point> externalPoints;
	
	private static final int DIALOG_LOAD_FILE = 1000;
	//список файлов в папке
	private String[] mFileList;
	//выбранный файл
	private String mChosenFile;
	//расширение файла
	private static final String FTYPE = ".png";
	//путь к папке
	private File mPath; //= new File(getExternalFilesDir(null).getAbsolutePath());
	//индикатор много/одно - пользовательской игры
	public static boolean isMulti;
	
	/**
	 * статический метод создания изображения на основе рисунка на виде рисования
	 * @param view - вид рисования
	 * @return созданное изображение 
	 */
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
	/**
	 * Диалог выбора файла
	 */
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
	/**
	 * Обработчик нажатия кнопок
	 */
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
	/**
	 * метод загрузки списка файлов 
	 */
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
