package com.lapidus.android.reader;

public class TrackHolder {
static Track t;
static int workingCollisionIndex;
static Collision c;
	public static void addTrack(Track track) {
		t = track;
	}
	public static Track getTrack() {
		return t;
	}
}
