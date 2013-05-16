package com.lapidus.android.engine;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.lapidus.android.R;
import com.threed.jpct.Camera;
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
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;
import com.threed.jpct.util.SkyBox;
import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;

@TargetApi(13)
public class Engine extends Activity {

	// Used to handle pause and resume...
	//сохраненный мир
	private static Engine master = null;
	//объекты вида
	private GLSurfaceView mGLView;
	//Рендерер
	public MyRenderer renderer = null;
	//буфер кадра 
	private FrameBuffer fb = null;
	//Объект мира
	private World world = null;
	//ОБъект цвета
	private RGBColor back = new RGBColor(50, 50, 100);

	private float touchTurn = 0;
	private float touchTurnUp = 0;
	//глобальная х-координата точки касания экрана
	private float xpos = -1;
	//глобальная у-координата точки касания экрана
	private float ypos = -1;
	//координаты положения конечной точки трассы
	private SimpleVector endCollisionHolder;
	//угол вращения камеры
	private float cameraRotationAngle;
	//ОБъект основной модели
	private Object3D loadedCar = null;
	//вектор напрвления
	private SimpleVector carDirection = null;
	//объект трассы
	private Object3D newods = null;
	//левая граница трассы
	private Object3D leftBorder = null;
	//правая граница трассы
	private Object3D rightBorder = null;
	//объект финиша
	private Object3D end;
	//объект старта
	private Object3D start;
	
	private SimpleVector sv1 = null;
	private SimpleVector sv2 = null; 
	//счетчик кадров в секунду
	private int fps = 0;
	//Скайбокс
	private SkyBox skyBox;
	//освещение
	private Light sun = null;
	//камера
	private Camera cam = null; 
	//скорость движения
	private float speed;
	//матрица трансформаций
	private Matrix transformMatrix = new Matrix();
	private Matrix cameraTransformMatrix = new Matrix();
	//хранитель данной активности
	private Activity ctx; 
	//размеры экрана
	private int screenWidth, screenHeight;
	//Обработчик для вызовов из неосновного треда
	private Handler handler;	
	private String mytag = "MyTag";
	//вектор коллизий 
	private SimpleVector ellipsoid = new SimpleVector(1, 1, 1);
	
	public static boolean bb; 
	//конструктор
	public Engine() {
		renderer = new MyRenderer();
	}
	/**
	 * Наследуемый метод, вызывается при создании экземпляра класса
	 * инициализирует необходимые поля. 
	 */
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
		renderer = new MyRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
		Display d = getWindowManager().getDefaultDisplay();
		android.graphics.Point p = new android.graphics.Point();
		d.getSize(p);
		screenHeight = p.y;
		screenWidth = p.x;	
		handler = new Handler();
		ctx = this;
	}
	/**
	 * Наследуемый метод, вызывается при паузе активности
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}
	/**
	 * наследуемый метод, вызывается при воостановлении активности
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();		
	}
	/**
	 * 
	 * наследуемый метод, вызывается при окончательном завершении активности
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}
	// Список точек трека
	public static ArrayList<Point> path;
	/*public static void copyPoints(ArrayList<Point> a) {
		path = new ArrayList<Point>();
		
	}*/
	/**
	 * Копирует поля объекта в данный объект. Необходим для корректной обработки паузы.
	 * @param src - сохраненный объект мира 
	 */	
	
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
	  /// Обработчик касаний экрана
	public boolean onTouchEvent(MotionEvent me) {
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
	/*
	 * класс - рендерер трехмерной графики
	 */
	public class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();
		//конструктор без параметров. Создает пустой объект
		public MyRenderer() {
		}
		/**
		 * наследуемый метод. вызывается при изменении экрана 
		 * @param  gl - объект интерфейса GL10
		 * @param w - ширина экрана
		 * @param h - высота экрана
		 */
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
				speed = 1f;
				// Create a texture out of the icon...:-)
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
				
				//Texture asphalt = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.asphalt)), 64, 64));
				Texture green = new Texture(2, 2, RGBColor.GREEN);		
				Texture red = new Texture(2, 2, RGBColor.RED);
				if (!TextureManager.getInstance().containsTexture("green"))	{
					TextureManager.getInstance().addTexture("green", green);
				}
				if (!TextureManager.getInstance().containsTexture("red")) { 
					TextureManager.getInstance().addTexture("red", red);
				}
				Texture yellowc = new Texture(2, 2, new RGBColor(255, 255, 1));
				if (!TextureManager.getInstance().containsTexture("yellowc")) {
					TextureManager.getInstance().addTexture("yellowc", yellowc);
				}
				if (!TextureManager.getInstance().containsTexture("texture")) {
					TextureManager.getInstance().addTexture("texture", texture);
				}
				Texture backgr = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.bluetexture)), 64, 64));
								
				addTexture("front", backgr);
				addTexture("right", backgr);
				addTexture("back", backgr);
				addTexture("left", backgr);
				addTexture("up", backgr);
				addTexture("down", backgr);
				skyBox = new SkyBox("left", "front", "right", "back", "up", "down", 700f);
				skyBox.setCenter(new SimpleVector(0, 100, 0));
				Object3D skybox3DObj = skyBox.getWorld().getObjects().nextElement();
				skyBox.setCenter(new SimpleVector(40, 20, 0));
				VehicleViewer.tileTexture(skybox3DObj, 1.5f);
				/*TextureManager.getInstance().addTexture("police_car3.tga", car3);
				TextureManager.getInstance().addTexture("police_car.tga", car4);
				TextureManager.getInstance().addTexture("police_car_lit.tga", car5);*/		
				//TextureManager.getInstance().addTexture("asphalt", asphalt);
				
				newods = new Object3D(path.size() * 5);
				leftBorder = new Object3D(path.size() * 5);
				rightBorder = new Object3D(path.size() * 5);
				//newods.addTriangle(new SimpleVector(-50, 0, -50), new SimpleVector(50, 0, -50), new SimpleVector(-50, 0, 0));
				InputStream fis = null;
				fis = getResources().openRawResource(R.raw.rotatcar);				
				Object3D[] loadedCars = Loader.loadOBJ(fis, null, 1);				
				loadedCar = Object3D.mergeAll(loadedCars);	
				loadedCar.setTexture("yellowc");
				loadedCar.calcTextureWrapSpherical();
				loadedCar.rotateX((float)Math.PI / 2);
				loadedCar.rotateY((float)Math.PI);
				//loadedCar.scale(0.05f);	
				Log.i("CO", loadedCar.getCenter().toString());
				loadedCar.setScale(0.001f);
				Log.i("CO", loadedCar.getCenter().toString());
				//loadedCar.translate(new SimpleVector(0, -loadedCar.getTransformedCenter().y, 0));
				//loadedCar.translate(-loadedCar.getCenter().x+8, -loadedCar.getCenter().y + 100, -loadedCar.getCenter().z);
				//loadedCar.translate(loadedCar.getCenter().z, loadedCar.getCenter().x, loadedCar.getCenter().y);
				
				Object3D flore = new Object3D(4);
				flore.addTriangle(new SimpleVector(-130, 102, -130), new SimpleVector(130, 102, -130), new SimpleVector(0, 102, 280));
						
							
				generateTrack();
								

				Log.i("CO", " newods " + newods.getCenter().toString() + " : " +newods.getTransformedCenter().toString());
				flore.setTexture("green");
				rightBorder.setTexture("green");
				leftBorder.setTexture("green");
				rightBorder.setEnvmapped(true);
				leftBorder.calcTextureWrapSpherical();
				rightBorder.strip();
				rightBorder.build();
				leftBorder.strip();
				leftBorder.build();
				start.setTexture("green");
				end.setTexture("red");
				newods.setTexture("red");
				//newods.setEnvmapped(true);
				newods.calcTextureWrapSpherical();
				newods.strip();
				newods.build();
				loadedCar.strip();
				loadedCar.build();	
				
				//loadedCar.translate(path.get(0).x, path.get(0).y, path.get(0).z);
				newods.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				leftBorder.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				rightBorder.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
				loadedCar.setCollisionMode(Object3D.COLLISION_CHECK_SELF | Object3D.COLLISION_CHECK_OTHERS);
				flore.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);				
				end.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
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
				newods.setCulling(false);
				leftBorder.setCulling(false);
				rightBorder.setCulling(false);
				skyBox.compile();
				world.addObject(start);
				world.addObject(end);
				world.addObject(flore);				
				world.addObject(newods);				
				world.addObject(loadedCar);
				world.addObject(leftBorder);
				world.addObject(rightBorder);
				world.compileAllObjects();
				cam = world.getCamera();
				/*loadedCar.rotateZ((float)Math.PI / 2);
				loadedCar.rotateY((float)Math.PI / 2);*/
				/*loadedCar.setOrientation(new SimpleVector(0, 1, 0) , new SimpleVector(0, -1, 0));
				
				loadedCar.rotateY((float)Math.PI);
				loadedCar.rotateX((float)Math.PI / 2);*/
				
				//default for cops car
				Log.i("bat", loadedCar.getZAxis().toString() + " = z axis");
				 
				loadedCar.setOrientation(new SimpleVector(0, 0, -1), new SimpleVector(0, -1, 0));
				float f = - carDirection.calcAngle(loadedCar.getZAxis());
				if (carDirection.x > 0) {
					loadedCar.rotateY((float) ((float)Math.PI + f));
				} else {
					loadedCar.rotateY((float) ((float)Math.PI - f));
				}
				
				loadedCar.translate(-loadedCar.getTransformedCenter().x, -loadedCar.getTransformedCenter().y - 90, -loadedCar.getTransformedCenter().z);
				Log.i("Car loc", "car dir " + carDirection.toString());
				Log.i("Car loc", "car z " + loadedCar.getZAxis().toString());
				Log.i("Car loc", "car centr " + loadedCar.getTransformedCenter().toString());
				Log.i("Car loc", "angle " + f);
				Log.i("CO", "centr " + loadedCar.getTransformedCenter().toString());				
				Log.i(mytag, "car o ^ " + loadedCar.getXAxis().toString());
				//SimpleVector temp = loadedCar.getTransformedCenter();
				SimpleVector temp = new SimpleVector(0, 20, 0);
				
				Log.i("CO", "cam pos " + cam.getPosition().toString());
				//world.getCamera().setPosition(start.getTransformedCenter().x, start.getTransformedCenter().y - 10, start.getTransformedCenter().z);
				cam.setOrientation(carDirection, new SimpleVector(0, -1, 0));
				//cam.setPosition(0, 0, 0);
				cam.lookAt(loadedCar.getTransformedCenter());
				cam.moveCamera(Camera.CAMERA_MOVEDOWN, 30);
				Log.i("CO", "cam pos " + cam.getPosition().toString());
				
				//cam.setPositionToCenter(loadedCar);
				//cam.moveCamera(temp, 5f);
				//cam.transform(temp);
				
				endCollisionHolder = end.checkForCollisionSpherical(new SimpleVector(0, 1, 0), 3);
				Log.i("finish", "deffault " + endCollisionHolder.toString());
				Log.i("MyTag", " " + cam.getPosition().x + " " + cam.getPosition().y + " " + cam.getPosition().z);
				SimpleVector sv = new SimpleVector(0,0,0);
				transformMatrix.translate(1, 1, 1);
				sv.y -= 100;
				sv.z -= 100;
				sv.x -= 100;
				sun.setPosition(sv);
				yprev = loadedCar.getTransformedCenter().y;
				MemoryHelper.compact();
					
				if (master == null) {
					Logger.log("Saving master Activity!");
					//master = Engine.this;
				}
			}
		}
		/**
		 * Метод добавления текстуры. проверяет наличие заданной текстуры в синглтоне TextureManager, при её
		 * отсутствии добавляет данную текстуру. 
		 * @param tex
		 * @param t
		 */
		private void addTexture(String tex, Texture t) {
			if (!TextureManager.getInstance().containsTexture(tex)) {
				TextureManager.getInstance().addTexture(tex, t);
			}
		}
		/**
		 * Метод генерации трека на основе координат поля path. Добавляет нобходимые треугольники 
		 * в объекты трассы - newods и объекты границ трассы - leftborder, rightborder
		 */
		private void generateTrack () {
			normalizePath();
			carDirection = new SimpleVector(path.get(1).y, 0, path.get(1).x);
			SimpleVector[] sv = new SimpleVector[4];
			SimpleVector t1;
			SimpleVector t2; 
			SimpleVector tb1;
			SimpleVector tb2;
			processPoints(path.get(1), path.get(0), new Point(-1, -1, -1), false, sv, false);
			t1 = sv[2];
			t2 = sv[3];	
			start = Primitives.getCube(3);
			Segment tmp = new Segment(path.get(0), path.get(1));
			float k = tmp.countK();
			
			start.translate(0, 100, 0);
			for (int i = 1; i < path.size() - 1; i ++) {
				processPoints(path.get(i-1), path.get(i), path.get(i + 1), true, sv, true);
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
			processPoints(path.get(path.size() - 2), path.get(path.size() - 1), new Point(-1, -1, -1), false, sv, true);
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
			end = Primitives.getCube(1f);
			end.translate(path.get(path.size() - 2).y, (98 - path.get(path.size() - 2).z), path.get(path.size() - 2).x);
			//rightBorder.invert();
			//newods.invert();
		}
		/**
		 * Метод расчета координат полигонов для дтрех последовательных точек 
		 * @param a - первая точка
		 * @param b - вторая точка
		 * @param c - третья точка
		 * @param needrec - переменная-индикатор необходимости рекурсивного запуска данного метода в обратном
		 * направлении: true для точек внутри трека, false - для конечных точек
		 * @param sv - массив для результирующих точек 
		 * @param needL - переменная-индикатор необходимости сдвига перпендикуляра. false - для конечных точек
		 */
		public void processPoints(Point a, Point b, Point c, boolean needrec, SimpleVector[] sv, boolean needL) {
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
			float l;
			Segment s1 = new Segment(a, b);
			Segment s2 = new Segment(b, c);
			if (needL == true){
				float angle = a.anglePoint(b, c);
				l = 9 / angle;		
				
				if (l > s1.length()) l = s1.length() / 2;
			} else {
				l = 0;
			}
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
					processPoints(c, b, a, false, sv, needL);
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
		
		/**
		 * Метод нормализации точек маршрута относительно первой точки
		 */
		private void normalizePath () {
			float xx = path.get(0).x;
			float yy = path.get(0).y;
			for (Point x : path) {
				x.x -= xx;
				x.y -= yy;
			}
		}
		/**
		 * Наследуемый метод, вызывается при создании нового экрана
		 */
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}
		boolean onGround = false;
		float delta, yprev = 2;
		int d = 3;
		boolean bb = true;
		boolean finished = false;
		/**
		 * наследуемый метод отрисовки кадра
		 */
		public void onDrawFrame(GL10 gl) {
			
			/*if (touchTurn != 0) {
				cube.rotateY(touchTurn);
				nobj.rotateY(touchTurn);
				t.rotateY(touchTurn);							
				touchTurn = 0;
			}*/

			/*if (touchTurnUp != 0) {
				cube.rotateX(touchTurnUp);
				nobj.rotateX(touchTurnUp);
				t.rotateX(touchTurnUp);				
				cameraTransformMatrix.translate(new SimpleVector(0, -touchTurnUp * 30, 0));								
				touchTurnUp = 0;				
			}*/
			
			SimpleVector s = loadedCar.getTransformedCenter();
			s.x -= 50; 
			end.rotateY((float)Math.PI / 72);
			end.rotateX((float)Math.PI / 72);
			start.rotateY((float)Math.PI / 72);
			start.rotateX((float)Math.PI / 72);
			if (xpos > screenWidth / 2 && speed >= 0 && onGround) {//&& ypos < 2 * screenHeight / 3 && ypos >= 0) {
				cameraRotationAngle = (float)Math.PI / 90;				
			} else if (xpos < 200 && xpos >= 0 && speed >= 0 && onGround) {// && ypos < 2 * screenHeight / 3 && ypos >= 0) {
				cameraRotationAngle = -(float)Math.PI / 90;				
			} 
			if (ypos > 2 * screenHeight / 3  && ypos >= 0 && speed >= 0) {
				//cameraRotationAngle = (float)Math.PI / 180;
				speed -= 0.02;
				//if (xpos > screenWidth / 2) cameraRotationAngle = -(float)Math.PI / 90;
				//if (xpos < screenWidth / 2) cameraRotationAngle = (float)Math.PI / 90;
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
			if (onGround) loadedCar.translate(t);
			//GRAVITY
			t = loadedCar.checkForCollisionEllipsoid(new SimpleVector(0, 1, 0), ellipsoid, 1);
			loadedCar.translate(t);
			Log.i("GR", "corrected"  + t.toString());
			if (!onGround && t.y != 1) onGround = true;
			if (onGround) {
				if (speed < 1 && (ypos < 2 * screenHeight / 3 || ypos < 0))  {
					speed += 0.05;
				}
			}
			/*if (t.y == 1 && !onGround) loadedCar.translate(t);
			else {
				onGround = true;				
			}
			if (t.y == 1 && onGround) {
				if (speed > 0.2) {
					//speed -= 0.01;
				}
			} else if (onGround) {
				if (speed < 1 && (ypos < 2 * screenHeight / 3 || ypos < 0))  {
					speed += 0.05;
				}
			}*/
			
			
			
			/*if (t.x != endCollisionHolder.x || t.y != endCollisionHolder.y || t.z != endCollisionHolder.z) {				
				Log.i("finish", "finished " + t.toString());
			}*/
			SimpleVector m = loadedCar.getTransformedCenter();
			if (Math.abs(m.x - end.getTransformedCenter().x) < 5 && Math.abs(m.y - end.getTransformedCenter().y) < 5 
					&& Math.abs(m.z - end.getTransformedCenter().z) < 5 && !finished){
				finished = true;
				Log.i("finish", "finished " + m.toString());
				
				handler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						
						AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
						builder.setTitle("Finish!");
						builder
						.setMessage("You have just finished!")
						.setCancelable(false)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								ctx.finish();
							}
						});
						AlertDialog dialog = builder.create();
						dialog.show();						
					}
				});
			}
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
			//cam.setPosition(temp);	
			delta = loadedCar.getTransformedCenter().y - yprev;
			yprev = loadedCar.getTransformedCenter().y;
			/*if (delta < 0) {
				cam.moveCamera(new SimpleVector(cam.getDirection().x, -0.05, cam.getDirection().z), speed * 1.414f);
			} else if (delta == 0) {
				cam.moveCamera(new SimpleVector(cam.getDirection().x, 0, cam.getDirection().z), speed * 1.414f);
			} else {
				cam.moveCamera(new SimpleVector(cam.getDirection().x, 0.05, cam.getDirection().z), speed * 1.414f);
			}*/
			float camYpos = cam.getPosition().y;
			if (camYpos > loadedCar.getTransformedCenter().y - 5.8 && onGround) {
				cam.moveCamera(new SimpleVector(cam.getDirection().x, -0.02f, cam.getDirection().z), speed * 1.414f);
			} else if (onGround) {
				cam.moveCamera(new SimpleVector(cam.getDirection().x, 0.02, cam.getDirection().z), speed * 1.414f);
			} else if (!onGround) {
				cam.moveCamera(Camera.CAMERA_MOVEDOWN, 0.5f);
			}
			cam.lookAt(loadedCar.getTransformedCenter());
			sun.setPosition(cam.getPosition());
			cameraTransformMatrix.setIdentity();
			fb.clear(back);	
			skyBox.render(world, fb);
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
