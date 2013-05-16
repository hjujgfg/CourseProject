package com.lapidus.android.engine;

import android.app.Activity;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;


import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.TextView;


import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;

import com.threed.jpct.Logger;

import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
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
import com.lapidus.android.R;
import com.lapidus.android.painter.Painter;
import com.lapidus.android.primitives.Point;
import com.lapidus.android.primitives.Segment;
public class VehicleViewer extends Activity {
	//сохраненный мир
	private static VehicleViewer master = null;
	//вид
	private GLSurfaceView mGLView;
	//рендерер
	public ViewerRenderer renderer = null;
	//буфер кадра
	private FrameBuffer fb = null;
	//мир
	private World world = null;
	//цвет
	private RGBColor back = new RGBColor(50, 50, 100);
	//освещение
	private Light sun = null;
	//камера
	private Camera cam = null;
	//путь
	public static ArrayList<Point> path;
	//Объект основной модели 
	private Object3D car;
	private int screenWidth, screenHeight;
	//счетчик кадров в секунду
	private int fps = 0;
	//скайбокс
	private SkyBox sb;
	public VehicleViewer() {
		renderer = new ViewerRenderer();
	}
	/**
	 * наследуемый  метод создания активности
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
		renderer = new ViewerRenderer();
		mGLView.setRenderer(renderer);
		setContentView(mGLView);
		LayoutInflater inflater = getLayoutInflater();
		View tmpView;
		tmpView = inflater.inflate(R.layout.main_menu_layout, null);
		addContentView(tmpView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		TextView single = (TextView)findViewById(R.id.single_button);
		single.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent o = new Intent(getApplicationContext(), Painter.class);
				startActivity(o);
			}
		});
		single.setAlpha(50);
		Display d = getWindowManager().getDefaultDisplay();
		android.graphics.Point p = new android.graphics.Point();
		d.getSize(p);
		screenHeight = p.y;
		screenWidth = p.x;		
	}
	/**
	 * копирование полей сохраненного мира в текущий
	 * @param src - сохраненный мир
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
	/**
	 * зациклить текстуру 
	 * @param obj - объект на котором текстура находится
	 * @param tileFactor - коэффициент 
	 */
	public static void tileTexture(Object3D obj, float tileFactor) {
		PolygonManager pm = obj.getPolygonManager();

		int end = pm.getMaxPolygonID();
		for (int i = 0; i < end; i++) {
			SimpleVector uv0 = pm.getTextureUV(i, 0);
			SimpleVector uv1 = pm.getTextureUV(i, 1);
			SimpleVector uv2 = pm.getTextureUV(i, 2);

			uv0.scalarMul(1/tileFactor);
			uv1.scalarMul(1/tileFactor);
			uv2.scalarMul(1/tileFactor);

			int id = pm.getPolygonTexture(i);

			TextureInfo ti = new TextureInfo(id, uv0.x, uv0.y, uv1.x, uv1.y,
					uv2.x, uv2.y);
			pm.setPolygonTexture(i, ti);
		}
	}
	private class ViewerRenderer implements GLSurfaceView.Renderer{
		private long time = System.currentTimeMillis();
		//конструктор 
		public ViewerRenderer() {
		}
		/**
		 * наследуемы метод отрисовки кадра
		 */
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			car.rotateY((float)Math.PI / 72);
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
		/**
		 * наследуемый метод обработки изменения экрана
		 */
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
				
				Texture yellow = new Texture(2, 2, new RGBColor(255, 255, 0));
				Texture front = new Texture(2, 2, new RGBColor(150, 50, 0));
				Texture left = new Texture(2, 2, new RGBColor(50, 150, 0));
				Texture right = new Texture(2, 2, new RGBColor(50, 150, 0));
				Texture back = new Texture(2, 2, new RGBColor(50, 150, 0));
				Texture up = new Texture(2, 2, new RGBColor(50, 150, 0));
				Texture down = new Texture(2, 2, new RGBColor(50, 150, 0));
				Texture backgr = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.bluetexturecur)), 64, 64));
				backgr.setClamping(false);
				
				TextureManager.getInstance().addTexture("yellow", yellow);
				TextureManager.getInstance().addTexture("front", backgr);
				TextureManager.getInstance().addTexture("left", backgr);
				TextureManager.getInstance().addTexture("right", backgr);
				TextureManager.getInstance().addTexture("back", backgr);
				TextureManager.getInstance().addTexture("up", backgr);
				TextureManager.getInstance().addTexture("down", backgr);
				sb= new SkyBox("left", "front", "right", "back", "up", "down", 200f);
				Object3D skybox3DObj = sb.getWorld().getObjects().nextElement();
				sb.setCenter(new SimpleVector(40, 20, 0));
				tileTexture(skybox3DObj, 1f);
				InputStream fis = null;
				fis = getResources().openRawResource(R.raw.batwing);				
				Object3D[] loadedCars = Loader.loadOBJ(fis, null, 1);				
				car = Object3D.mergeAll(loadedCars);
				car.setTexture("yellow");
				car.rotateX(-(float)Math.PI/2);
				car.scale(0.1f);
				car.translate(-car.getTransformedCenter().x, -car.getTransformedCenter().y, -car.getTransformedCenter().z);
				sb.compile();
				world.addObject(car);
				
				world.compileAllObjects();
				cam = world.getCamera();
				
				cam.setOrientation(cam.getDirection(), new SimpleVector(0, -1, 0));
				cam.lookAt(car.getTransformedCenter());
				cam.setPosition(0, 0, 0);
				//cam.rotateCameraY((float)Math.PI / 2);
				MemoryHelper.compact();
				
				if (master == null) {
					Logger.log("Saving master Activity!");
					master = VehicleViewer.this;
				}
			}
		}
		
		public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
			// TODO Auto-generated method stub
			
		}
	}
}
