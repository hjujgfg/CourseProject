package com.lapidus.android.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import android.view.Window;


import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;

import com.threed.jpct.Logger;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;
import com.lapidus.android.R;
import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;

public class ObjectViewer extends Activity {
	private static ObjectViewer master = null;

	private GLSurfaceView mGLView;
	public ViewerRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);
	private Light sun = null;	
	private Camera cam = null; 
	public static ArrayList<Point> path;
	private Object3D newods = null;
	private Object3D leftBorder = null;
	private Object3D rightBorder = null;
	private Object3D end;
	private Object3D start;
	private int screenWidth, screenHeight;	
	private int fps = 0;
	private SkyBox sb;
	public ObjectViewer() {
		renderer = new ViewerRenderer();
	}
	protected void onCreate(Bundle savedInstanceState) {

		Logger.log("onCreate");

		if (master != null) {
			copy(master);
		}

		super.onCreate(savedInstanceState);
		mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		renderer = new ViewerRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
		Display d = getWindowManager().getDefaultDisplay();
		android.graphics.Point p = new android.graphics.Point();
		d.getSize(p);
		screenHeight = p.y;
		screenWidth = p.x;		
	}
	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	protected boolean isFullscreenOpaque() {
		return true;
	}
	private class ViewerRenderer implements GLSurfaceView.Renderer{
		private long time = System.currentTimeMillis();

		public ViewerRenderer() {
		}

		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			newods.rotateY((float)Math.PI/72);
			/*leftBorder.rotateY((float)Math.PI/72);
			rightBorder.rotateY((float)Math.PI/72);*/
			fb.clear(back);			
			sb.render(world, fb);
			world.renderScene(fb);
			world.draw(fb);
			fb.display();
			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(fps + "fps");
				fps = 0;
				time = System.currentTimeMillis();
			}
			fps++;
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			if (fb != null) {
				fb.dispose();
			}
			fb = new FrameBuffer(gl, width, height);

			if (master == null) {

				world = new World();
				world.setAmbientLight(20, 20, 20);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);
				Texture greenn = new Texture(2, 2, RGBColor.GREEN);		
				Texture redd = new Texture(2, 2, RGBColor.RED);
				addTexture("greenn", greenn);
				addTexture("redd", redd);
				Texture backgr = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.backgroundcolor)), 64, 64));
				addTexture("front", backgr);
				addTexture("right", backgr);
				addTexture("back", backgr);
				addTexture("left", backgr);
				addTexture("up", backgr);
				addTexture("down", backgr);
				sb = new SkyBox("left", "front", "right", "back", "up", "down", 700f);
				newods = new Object3D(path.size() * 5);
				leftBorder = new Object3D(path.size() * 5);
				rightBorder = new Object3D(path.size() * 5);
				generateTrack();
				rightBorder.setTexture("greenn");
				leftBorder.setTexture("greenn");
				rightBorder.setEnvmapped(true);
				leftBorder.calcTextureWrapSpherical();
				newods.setTexture("redd");
				//newods.setEnvmapped(true);
				newods.calcTextureWrapSpherical();
				newods = Object3D.mergeObjects(newods, leftBorder);
				newods = Object3D.mergeObjects(newods, rightBorder);
				/*rightBorder.strip();
				rightBorder.build();
				leftBorder.strip();
				leftBorder.build();*/
				start.setTexture("greenn");
				end.setTexture("redd");
				
				newods.strip();
				newods.build();
				newods.setCulling(false);
				leftBorder.setCulling(false);
				rightBorder.setCulling(false);
				sb.compile();
				world.addObject(start);
				world.addObject(end);
				world.addObject(newods);						
				/*world.addObject(leftBorder);
				world.addObject(rightBorder);*/
				world.compileAllObjects();
				cam = world.getCamera();
				cam.lookAt(newods.getTransformedCenter());
				cam.setPosition(0, 0, 0);
				MemoryHelper.compact();
				
				if (master == null) {
					Logger.log("Saving master Activity!");
					//master = ObjectViewer.this;
				}
			}
		}
		private void addTexture(String tex, Texture t) {
			if (!TextureManager.getInstance().containsTexture(tex)) {
				TextureManager.getInstance().addTexture(tex, t);
			}
		}
		private void normalizePath () {
			float xx = path.get(0).x;
			float yy = path.get(0).y;
			for (Point x : path) {
				x.x -= xx;
				x.y -= yy;
			}
		}
		private void generateTrack () {
			normalizePath();			
			SimpleVector[] sv = new SimpleVector[4];
			SimpleVector t1;
			SimpleVector t2; 
			SimpleVector tb1;
			SimpleVector tb2;
			processPoints(path.get(1), path.get(0), new Point(-1, -1, -1), false, sv);
			t1 = sv[2];
			t2 = sv[3];	
			start = Primitives.getCube(3);
			//start.translate(-start.getCenter().x, -start.getCenter().y, -start.getCenter().z);
			start.translate(0, 100, 0);
			for (int i = 1; i < path.size() - 1; i ++) {
				processPoints(path.get(i-1), path.get(i), path.get(i + 1), true, sv);
				newods.addTriangle(t2, sv[1], sv[0]);
				newods.addTriangle(t2, t1, sv[1]);
				newods.addTriangle(sv[0], sv[1], sv[2]);
				newods.addTriangle(sv[2], sv[3], sv[0]);
				
				tb1 = new SimpleVector(t1.x, t1.y - 3, t1.z);
				leftBorder.addTriangle(t1, sv[1], tb1);
				tb2 = new SimpleVector(sv[1].x, sv[1].y - 3, sv[1].z);
				leftBorder.addTriangle(sv[1], tb2, tb1);
				leftBorder.addTriangle(sv[1], sv[2], tb2);
				tb1 = new SimpleVector(sv[2].x, sv[2].y - 3, sv[2].z);
				leftBorder.addTriangle(sv[2], tb1, tb2);
				
				tb1 = new SimpleVector(t2.x, t2.y - 3, t2.z);
				rightBorder.addTriangle(t2, sv[0], tb1);
				tb2 = new SimpleVector(sv[0].x, sv[0].y - 3, sv[0].z);
				rightBorder.addTriangle(sv[0], tb2, tb1);
				rightBorder.addTriangle(sv[0], sv[3], tb2);
				tb1 = new SimpleVector(sv[3].x, sv[3].y - 3, sv[3].z);
				rightBorder.addTriangle(sv[3], tb1, tb2);
				t2 = sv[3];
				t1 = sv[2];
			}
			processPoints(path.get(path.size() - 2), path.get(path.size() - 1), new Point(-1, -1, -1), false, sv);
			newods.addTriangle(t2, sv[1], sv[0]);
			newods.addTriangle(t2, t1, sv[1]);
			tb1 = new SimpleVector(t1.x, t1.y - 3, t1.z);
			
			leftBorder.addTriangle(t1, sv[1], tb1);
			tb2 = new SimpleVector(sv[1].x, sv[1].y - 3, sv[1].z);
			leftBorder.addTriangle(sv[1], tb2, tb1);
			tb1 = new SimpleVector(t2.x, t2.y - 3, t2.z);
			
			rightBorder.addTriangle(t2, sv[0], tb1);
			tb2 = new SimpleVector(sv[0].x, sv[0].y - 3, sv[0].z);
			rightBorder.addTriangle(sv[0], tb2, tb1);
			leftBorder.invert();
			end = Primitives.getCube(3);
			end.translate(-path.get(path.size() - 1).y, - (100 - path.get(path.size() - 1).z), -path.get(path.size() - 1).x);
			//rightBorder.invert();
			//newods.invert();
		}
		public void processPoints(Point a, Point b, Point c, boolean needrec, SimpleVector[] sv) {
			int q;
			int d = 3;
			if (needrec == true) {
				for (int i = 0; i < 4; i ++) {
					sv[i] = null;
				}
				q = 0;
			} else {
				q = 2;
			}
			
			Point[] arr = new Point[4];
			Point t1, t2;
			float angle = a.anglePoint(b, c);
			float l = 9 / angle;			
			Segment s1 = new Segment(a, b);
			Segment s2 = new Segment(b, c);
			if (l > s1.length()) l = s1.length() / 2;
			float k = s1.countK();			
			float bb = s1.countB();
			
			if (b.x > a.x && k != 0 && k != Float.NaN) {
				float xxl = b.x - (l / android.util.FloatMath.sqrt(k * k + 1));
				Point xl = new Point(xxl, k * xxl + bb);
				float kl = 0 - (1 / k);
				float bl = xl.y - kl * xl.x;
				
				float tmp = xl.x - d / android.util.FloatMath.sqrt(kl * kl + 1) ;
				float tmpp = kl * tmp + bl;
				t1 = new Point(tmp, tmpp, b.z);
				tmp = xl.x + d / android.util.FloatMath.sqrt(kl * kl + 1) ;
				tmpp = kl * tmp + bl;
				t2 = new Point(tmp, tmpp, b.z);									
				if (b.y < a.y) {
					arr[q] = t1;
					arr[q + 1] = t2;					
				} else {
					arr[q] = t2;
					arr[q + 1] = t1;
				}
				t1 = null;
				t2 = null;
			} else if (b.x < a.x && k != 0 && k != Float.NaN) {
				float xxl = b.x + (l / android.util.FloatMath.sqrt(k * k + 1));
				Point xl = new Point(xxl, k * xxl + bb);
				float kl = 0 - (1 / k);
				float bl = xl.y - kl * xl.x;
				
				float tmp = xl.x - d / android.util.FloatMath.sqrt(kl * kl + 1);
				float tmpp = kl * tmp + bl;
				t1 = new Point(tmp, tmpp, b.z);
				tmp = xl.x + d / android.util.FloatMath.sqrt(kl * kl + 1);
				tmpp = kl * tmp + bl;
				t2 = new Point(tmp, tmpp, b.z);									
				if (b.y < a.y) {
					arr[q] = t1;
					arr[q+1] = t2;					
				} else {
					arr[q] = t2;
					arr[q+1] = t1;
				}
				t1 = null;
				t2 = null;
			} 
			if (k == Float.MAX_VALUE) {
				
				if (b.y < a.y) {
					t1 = new Point(b.x - d, b.y - l, b.z);
					t2 = new Point(b.x + d, b.y - l, b.z);
					arr[q] = t1;
					arr[q+1] = t2;
				} else if (b.y >= a.y){
					t1 = new Point(b.x - d, b.y + l, b.z);
					t2 = new Point(b.x + d, b.y + l, b.z);
					arr[q] = t2;
					arr[q+1] = t1;
				}
				t1 = null;
				t2 = null;
			}
			if (k == 0) {
				
				if (b.x > a.x) {
					t1 = new Point(b.x - l, b.y - d, b.z);
					t2 = new Point(b.x - l, b.y + d, b.z);
					arr[q] = t1;
					arr[q+1] = t2;
				} else if (b.x <= a.x){
					t1 = new Point(b.x + l, b.y + d, b.z);
					t2 = new Point(b.x + l, b.y - d, b.z);
					arr[q] = t1;
					arr[q+1] = t2;
				}
				t1 = null;
				t2 = null;
			}
			try {
				if (needrec == true) {
					processPoints(c, b, a, false, sv);
					sv[0] = new SimpleVector(arr[0].y, 100 - arr[0].z, arr[0].x);
					sv[1] = new SimpleVector(arr[1].y, 100 - arr[1].z, arr[1].x);
				} else {				
					sv[2] = new SimpleVector(arr[2].y, 100 - arr[2].z, arr[2].x);
					sv[3] = new SimpleVector(arr[3].y, 100 - arr[3].z, arr[3].x);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				for (int i = 0; i < 4; i ++) {
					Log.i("EN", "a " + a.toString() + " b " + b.toString() + " c " + c.toString());
					Log.i("EN", "arr^ " + arr[i].toString());
					Log.i("EN", "sv^ " + sv[i].toString());
				}
			}
					
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
