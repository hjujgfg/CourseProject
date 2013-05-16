package com.lapidus.android.reader;

import java.util.ArrayList;

import android.util.Log;

import com.lapidus.android.primitives.Segment;

public class TrackHolder {
//сохраненный трек
static Track t;
//рабочий индекс коллизии
static int workingCollisionIndex;
//рабочая коллизия
static Collision c;
//разрешенные линии
static ArrayList<Line> newLines = new ArrayList<Line>();
	/**
	 * обновить линии разрешения коллизии по отрезкам
	 * @param segs - список отрезков
	 */
	public static void updateNewLines(ArrayList<Segment> segs) {
		newLines = new ArrayList<Line>();
		for (Segment s : segs) {
			Line l = new Line();
			Log.i("add line", s.start.connection.connection.toString());
			Log.i("add line", s.stop.connection.connection.toString());
			l.addNextPoint(s.start.connection.connection);
			l.addNextPoint(s.stop.connection.connection);
			newLines.add(l);
		}
	}
	/**
	 * установить рабочий трек
	 * @param track - трек 
	 */
	public static void addTrack(Track track) {
		t = track;
	}
	/**
	 * получить рабочий трек
	 * @return трек
	 */
	public static Track getTrack() {
		return t;
	}
}
 