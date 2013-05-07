package com.lapidus.android.reader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class CollisionResolver extends Activity {
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		view = new CollisionResolverView(this);
		setContentView(view);
		track = TrackHolder.getTrack();
		view.c = track.collisions.get(TrackHolder.workingCollisionIndex);
	}
	CollisionResolverView view;
	Track track; 
}
