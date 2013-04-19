package com.lapidus.android.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.renderscript.Mesh.Primitive;
import android.renderscript.Mesh.TriangleMeshBuilder;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.Toast;

import com.lapidus.android.R;
import com.threed.jpct.Camera;
import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.lapidus.android.painter.Point;
/**
 * A simple demo. This shows more how to use jPCT-AE than it shows how to write
 * a proper application for Android. It includes basic activity management to
 * handle pause and resume...
 * 
 * @author EgonOlsen
 * 
 */
@TargetApi(13)
public class HelloWorld extends Activity {

	// Used to handle pause and resume...
	private static HelloWorld master = null;

	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(50, 50, 100);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;
	
	private float cameraRotationAngle;
	private Object3D cube = null;
	private Object3D nobj = null;
	private Object3D t = null;
	private Object3D loadedCar = null;
	private SimpleVector carDirection = null; 
	private int fps = 0;

	private Light sun = null;
	
	private Camera cam = null; 
	private float speed;
	
	private Matrix transformMatrix = new Matrix();
	private Matrix cameraTransformMatrix = new Matrix();
	
	private int screenWidth, screenHeight;	
	
	private String mytag = "MyTag";
	private SimpleVector ellipsoid = new SimpleVector(2, 2, 2);
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

		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
		Display d = getWindowManager().getDefaultDisplay();
		android.graphics.Point p = new android.graphics.Point();
		d.getSize(p);
		screenHeight = p.y;
		screenWidth = p.x;
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
	public static ArrayList<Point> path;
	/*public static void copyPoints(ArrayList<Point> a) {
		path = new ArrayList<Point>();
		
	}*/
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
	StringBuilder sb = new StringBuilder();
	  int upPI = 0;
	  int downPI = 0;
	  boolean inTouch = false;
	  String result = "";
	  int pointerIndex = 0;
	public boolean onTouchEvent(MotionEvent me) {
		
		/*int index = me.getActionIndex();
		int pointerID = me.getPointerId(index); 
		if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			Log.i("TE", "index : " + index);
			return true;
		}

		
		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			Log.i("TE", "happend");
			return true;
		}
		
		if (me.getActionMasked() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / -100f;
			touchTurnUp = yd / -100f;
			return true;
		}
		

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);*/
		
		
		// событие
	    int actionMask = me.getActionMasked();
	    // индекс касания
	    //int pointerIndex = me.getActionIndex();
	    pointerIndex = me.getActionIndex();
	    // число касаний
	    int pointerCount = me.getPointerCount();

	    switch (actionMask) {
	    case MotionEvent.ACTION_DOWN: // первое касание
		    inTouch = true;
		    xpos = me.getX(pointerIndex);
		    ypos = me.getY(pointerIndex);
		    Log.i("TE", "down " + pointerIndex);
		    break;
	    case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
	    	if (pointerIndex != 0 ) {
	    		xpos = me.getX(pointerIndex);
		    	ypos = me.getY(pointerIndex);
	    	}
	    	else {
		    	xpos = me.getX(pointerIndex);
			    ypos = me.getY(pointerIndex);
		    }
	    	Log.i("TE", "down pointer " + pointerIndex);
	    	downPI = pointerIndex;
	    	break;

	    case MotionEvent.ACTION_UP: // прерывание последнего касания
		    inTouch = false;
		    xpos = -1;
		    ypos = -1;
		    sb.setLength(0);
		    Log.i("TE", "up " + pointerIndex);
	    case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
	    	//xpos = -1;
			//ypos = -1;
		    upPI = pointerIndex;
		    Log.i("TE", "up pointer " + pointerIndex);
		    break;

	    case MotionEvent.ACTION_MOVE: // движение
	      sb.setLength(0);

	      for (int i = 0; i < 10; i++) {
	        sb.append("Index = " + i);
	        if (i < pointerCount) {
	          sb.append(", ID = " + me.getPointerId(i));
	          sb.append(", X = " + me.getX(i));
	          sb.append(", Y = " + me.getY(i));
	        } else {
	          sb.append(", ID = ");
	          sb.append(", X = ");
	          sb.append(", Y = ");
	        }
	        sb.append("\r\n");
	      }
	      break;
	    }
	    result = "down: " + downPI + "\n" + "up: " + upPI + "\n";

	    if (inTouch) {
	      result += "pointerCount = " + pointerCount + "\n" + sb.toString();
	    }
	    //tv.setText(result);
	   // Log.i("TE", result);
	    return true;
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}
			fb = new FrameBuffer(gl, w, h);

			if (master == null) {

				world = new World();
				world.setAmbientLight(20, 20, 20);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);
				cameraRotationAngle = 0;
				speed = 2;
				// Create a texture out of the icon...:-)
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
				Texture car3 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.police_car3)), 64, 64));
				Texture car4 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.police_car_ref)), 64, 64));
				Texture car5 = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.police_car_lit)), 64, 64));
				Texture asphalt = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.asphalt)), 64, 64));
				TextureManager.getInstance().addTexture("texture", texture);
				TextureManager.getInstance().addTexture("police_car3.tga", car3);
				TextureManager.getInstance().addTexture("police_car.tga", car4);
				TextureManager.getInstance().addTexture("police_car_lit.tga", car5);		
				TextureManager.getInstance().addTexture("asphalt", asphalt);
				
				nobj = Primitives.getSphere(10);
				nobj.calcTextureWrapSpherical();
				nobj.setTexture("texture");
				nobj.strip();
				nobj.build();
				nobj.translate(10, 10, 10);
				cube = Primitives.getCube(90);
				cube.calcTextureWrap();
				//cube.setTexture("texture");
				cube.strip();
				cube.build();
				Object3D newods = new Object3D(path.size() * 3);
				//newods.addTriangle(new SimpleVector(-50, 0, -50), new SimpleVector(50, 0, -50), new SimpleVector(-50, 0, 0));
				InputStream fis = null;
				fis = getResources().openRawResource(R.raw.policecar);				
				Object3D[] loadedCars = Loader.loadOBJ(fis, null, 1);				
				loadedCar = Object3D.mergeAll(loadedCars);	
				//loadedCar.scale(0.05f);	
				Log.i("CO", loadedCar.getCenter().toString());
				loadedCar.setScale(0.01f);
				Log.i("CO", loadedCar.getCenter().toString());
				//loadedCar.translate(-loadedCar.getCenter().x+8, -loadedCar.getCenter().y + 100, -loadedCar.getCenter().z);
				//loadedCar.translate(loadedCar.getCenter().z, loadedCar.getCenter().x, loadedCar.getCenter().y);
				loadedCar.strip();
				loadedCar.build();	
				Object3D flore = new Object3D(4);
				flore.addTriangle(new SimpleVector(-70, 100, -70), new SimpleVector(70, 100, -70), new SimpleVector(0, 100, 200));
				t = Primitives.getPlane(5, 10);
				t.calcTextureWrapSpherical();
				t.setTexture("texture");
				t.strip();
				t.build();
				t.rotateX(90);
				SimpleVector t1 = new SimpleVector(0, 0, 0);
				SimpleVector t2 = new SimpleVector(1, 0, 1);
				SimpleVector t3 = new SimpleVector(-1, 0, 1);
				Log.i("CO", " 1- 2 " + t1.calcAngleFast(t2));
				Log.i("CO" , "1 -3 " + t1.calcAngleFast(t3));
				t3.makeEqualLength(t1);
				Log.i("CO" , "1 -3e " + t1.calcAngleFast(t3));
				//Object3D[] suround = new Object3D[path.size()];			
				/*SimpleVector s1,s2,s3,s4;
				s1 = new SimpleVector(0,0,0);
				s2 = new SimpleVector(0,0,0);
				s3 = new SimpleVector(0,0,0);
				s4 = new SimpleVector(0,0,0);
				Log.i("RR", "fwe " + path.size());
				// работает для просто набора точек - пиксели
				for (int i = 0; i < path.size(); i ++) {
					
					s1.x = path.get(i).x - path.get(0).x - 10;
					s1.y = 100;
					s1.z = path.get(i).y - path.get(0).y - 10;
					s2.x = path.get(i).x - path.get(0).x + 10;
					s2.y = 100;
					s2.z = path.get(i).y - path.get(0).y - 10;
					s3.x = path.get(i).x - path.get(0).x - 10;
					s3.y = 100;
					s3.z = path.get(i).y - path.get(0).y + 10;
					s4.x = path.get(i).x - path.get(0).x + 10;
					s4.y = 100;
					s4.z = path.get(i).y - path.get(0).y + 10;
					newods.addTriangle(s1, s2, s3);
					newods.addTriangle(s3, s2, s4);									
				}		*/
				normalizePath();
				SimpleVector[] svv = new SimpleVector[4];
				Log.i("CO", loadedCar.getTransformedCenter().toString() + ": " + loadedCar.getCenter());
				for (int i = 0; i < path.size() - 1; i ++) {
					svv = generateRect(path.get(i), path.get(i + 1), svv);
					if (path.get(i).y < path.get(i + 1).y) {
						newods.addTriangle(svv[0], svv[1], svv[2]);					
						newods.addTriangle(svv[2], svv[1], svv[3]);
					} else {
						newods.addTriangle(svv[2], svv[1], svv[0]);					
						newods.addTriangle(svv[3], svv[1], svv[2]);
					}
					for (int k = 0; k < 4; k ++) {
						Log.i("CO", svv[k].toString() + ": " + i);
					}					
				}
				newods.scale(2f);
				Log.i("CO", " newods " + newods.getCenter().toString() + " : " +newods.getTransformedCenter().toString());
				newods.setTexture("asphalt");
				newods.strip();
				newods.build();
				//loadedCar.translate(path.get(0).x, path.get(0).y, path.get(0).z);
				newods.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				loadedCar.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
				flore.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				cube.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				/*loadedCar.addCollisionListener(new CollisionListener() {
					
					public boolean requiresPolygonIDs() {
						// TODO Auto-generated method stub
						return false;
					}
					
					public void collision(CollisionEvent arg0) {
						// TODO Auto-generated method stub
						Toast t = Toast.makeText(getApplicationContext(), "shit", Toast.LENGTH_LONG);
						t.show();
						Log.i("MT", "Collision");
					}
				});*/
				//world.addObjects(suround);
				//world.addObject(track);
				world.addObject(flore);
				world.addObject(t);
				world.addObject(cube);
				world.addObject(nobj);
				world.addObject(newods);				
				world.addObject(loadedCar);
				world.compileAllObjects();
				cam = world.getCamera();
				loadedCar.setOrientation(new SimpleVector(0, 0, -1), new SimpleVector(0, -1, 0));				
				carDirection = loadedCar.getZAxis();
				Log.i("CO", "centr " + loadedCar.getTransformedCenter().toString());				
				Log.i(mytag, "car o ^ " + loadedCar.getXAxis().toString());
				SimpleVector temp = loadedCar.getTransformedCenter();
				temp.y -= 10;
				temp.z -= 10;
				Log.i("CO", "cam pos " + cam.getPosition().toString());
				cam.setPosition(temp);
				Log.i("CO", "cam pos " + cam.getPosition().toString());
				//cam.moveCamera(temp, 1f);
				//cam.transform(temp);
				cam.lookAt(loadedCar.getTransformedCenter());
				
				Log.i("MyTag", " " + cam.getPosition().x + " " + cam.getPosition().y + " " + cam.getPosition().z);
				SimpleVector sv = new SimpleVector();
				sv.set(cube.getTransformedCenter());
				transformMatrix.translate(1, 1, 1);
				sv.y -= 100;
				sv.z -= 100;
				sv.x -= 100;
				sun.setPosition(sv);
				MemoryHelper.compact();
					
				if (master == null) {
					Logger.log("Saving master Activity!");
					master = HelloWorld.this;
				}
			}
		}
		private SimpleVector[] generateRect(Point a, Point b, SimpleVector[] t) {
			float x, y = 100, z; 
			x = a.x - 2;
			z = a.y + 2;
			t[0] = new SimpleVector(x, y, z);
			x = a.x + 2;
			z = a.y + 2;
			t[1] = new SimpleVector(x, y, z);
			x = b.x - 2;
			z = b.y + 2;
			t[2] = new SimpleVector(x, y, z);
			x = b.x + 2;
			z = b.y + 2;
			t[3] = new SimpleVector(x, y, z);			
			return t;
		}
		private void normalizePath () {
			float xx = path.get(0).x;
			float yy = path.get(0).y;
			for (Point x : path) {
				x.x -= xx;
				x.y -= yy;
			}
		}
		
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}
		
		public void onDrawFrame(GL10 gl) {
			
			/*if (touchTurn != 0) {
				cube.rotateY(touchTurn);
				nobj.rotateY(touchTurn);
				t.rotateY(touchTurn);							
				touchTurn = 0;
			}*/

			if (touchTurnUp != 0) {
				cube.rotateX(touchTurnUp);
				nobj.rotateX(touchTurnUp);
				t.rotateX(touchTurnUp);				
				cameraTransformMatrix.translate(new SimpleVector(0, -touchTurnUp * 30, 0));								
				touchTurnUp = 0;				
			}
			SimpleVector s = loadedCar.getTransformedCenter();
			s.x -= 50; 
			cube.translate(-1, 0 , 1);
			if (speed < 0.3 && (ypos < 2 * screenHeight / 3 || ypos < 0))  {
				speed += 0.2;
			}
			if (xpos > screenWidth / 2 && ypos < 2 * screenHeight / 3 && ypos >= 0) {
				cameraRotationAngle = (float)Math.PI / 90;				
			} else if (xpos < 200 && xpos >= 0 && ypos < 2 * screenHeight / 3 && ypos >= 0) {
				cameraRotationAngle = -(float)Math.PI / 90;				
			} 
			if (ypos > 2 * screenHeight / 3  && ypos >= 0 && speed > 0 ) {
				//cameraRotationAngle = (float)Math.PI / 180;
				speed -= 0.1;
			}
			//loadedCar.setCenter(loadedCar.getTransformedCenter());
			loadedCar.rotateY(cameraRotationAngle);
			/*if (loadedCar.checkForCollision(new SimpleVector(0, -1, 0), 10f) != Object3D.NO_OBJECT) {
				Log.i("collision", "v " + loadedCar.checkForCollision(new SimpleVector(0, -1, 0), 10f));
				Toast t = Toast.makeText(getApplicationContext(), "shit", Toast.LENGTH_SHORT);
				t.show();
			}*/
			//loadedCar.translate(-loadedCar.getZAxis().x * speed, 0, -loadedCar.getZAxis().z * speed);
			SimpleVector t = new SimpleVector(-loadedCar.getZAxis().x * speed, 0, -loadedCar.getZAxis().z * speed);			
			//t.scalarMul(speed);
			t = loadedCar.checkForCollisionEllipsoid(t, ellipsoid, 10);
			loadedCar.translate(t);
			//GRAVITY
			//t = loadedCar.checkForCollisionEllipsoid(new SimpleVector(0, 1, 0), ellipsoid, 1);
			//loadedCar.translate(t);
			//loadedCar.setCenter(loadedCar.getTransformedCenter());
			Log.i(mytag, loadedCar.getTransformedCenter().toString() + " " + loadedCar.getCenter().toString());
			SimpleVector backVect = loadedCar.getTransformedCenter();
			backVect.scalarMul(-1.0f);					
			cameraTransformMatrix.translate(backVect);
			cameraTransformMatrix.rotateY(cameraRotationAngle / 2);
			cameraTransformMatrix.translate(loadedCar.getTransformedCenter());
			cameraRotationAngle = 0;
			SimpleVector temp;
			temp = cam.getPosition();
			temp.matMul(cameraTransformMatrix);
			cam.setPosition(temp);			
			cam.moveCamera(new SimpleVector(cam.getDirection().x, 0, cam.getDirection().z), speed * 1.414f);			
			cam.lookAt(loadedCar.getTransformedCenter());
			sun.setPosition(cam.getPosition());
			cameraTransformMatrix.setIdentity();
			fb.clear(back);					
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
	}
}
