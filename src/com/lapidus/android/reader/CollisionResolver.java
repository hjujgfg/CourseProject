package com.lapidus.android.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;

public class CollisionResolver extends Activity {
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
	}
	private void init() {
		
		view = new CollisionResolverView(this);
		setContentView(view);
		track = TrackHolder.getTrack();
		//view.c = track.collisions.get(TrackHolder.workingCollisionIndex);
		view.c = TrackHolder.c;
		Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		view.screenHeight = size.y;
		view.screenWidth = size.x;
		view.invalidate();
	}
	@Override
	protected void onResume(){
		super.onResume();
		init();
	}
	CollisionResolverView view;
	Track track; 
}
