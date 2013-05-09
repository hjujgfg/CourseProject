package com.lapidus.android.reader;

import java.util.ArrayList;

import android.util.Log;

import com.lapidus.android.primitives.Segment;

public class TrackHolder {
static Track t;
static int workingCollisionIndex;
static Collision c;
static ArrayList<Line> newLines = new ArrayList<Line>();
	public static void updateNewLines(ArrayList<Segment> segs) {
		newLines = new ArrayList<Line>();
		for (Segment s : segs) {
			Line l = new Line();
			Log.i("add line", s.start.connection.toString());
			Log.i("add line", s.stop.connection.toString());
			l.addNextPoint(s.start.connection.connection);
			l.addNextPoint(s.stop.connection.connection);
			newLines.add(l);
		}
	}
	public static void addTrack(Track track) {
		t = track;
	}
	public static Track getTrack() {
		return t;
	}
}
 