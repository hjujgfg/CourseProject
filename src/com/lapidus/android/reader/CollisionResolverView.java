package com.lapidus.android.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class CollisionResolverView extends View {
	
	public CollisionResolverView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	Paint paint;
	Canvas canvas; 
	Track track; 
	int collisionIndex;
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
	}
	
	public void updateIndex(int index) {
		collisionIndex = index;
	}
}
