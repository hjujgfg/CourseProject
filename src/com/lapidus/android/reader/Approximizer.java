package com.lapidus.android.reader;

import java.util.Vector;

import com.lapidus.android.primitives.Point;
/**
 * класс аппроксимации
 * @author Егор
 *
 */
public class Approximizer {
	/**
	 * аппроксимация точек
	 * @param tol глубина рекурсии
	 * @param V - массив точек 
	 * @return массив аппроксимированных точек
	 */
	public static Point[] approximize (float tol, Point [] V) {
		int n = V.length;
        
        int i, k, m, pv;
        float tol2 = tol*tol;
        Point [] vt = new Point[n];
        int [] mk = new int[n];
        
        Vector sV = new Vector();
        
        for (int b = 0; b < n; b++){
            mk[b] = 0;
        }
        
        //STAGE 1 simple vertex reduction
        vt[0] = V[0];
        
        for (i=k=1, pv=0; i < n; i++){
            if (V[i].distanceSquared(V[pv]) < tol2)
                continue;
            vt[k++] = V[i];
            pv = i;
        }
        
        if (pv < n-1)
            vt[k++] = V[n-1];
        
        //STAGE 2 Douglas-Peucker polyline simplify
        //mark the first and last vertices
        mk[0] = mk[k-1] = 1;
        simplifyDP2D(tol, vt, 0, k-1, mk);
        
        //copy marked vertices to output
        for (i=m=0; i<k; i++) {
            if (mk[i] == 1)
                sV.add(vt[i]);
        }
        
        Point [] out = new Point[sV.size()];
        sV.copyInto(out);
        return out;
	}
	/**
	 * Упрощение линий
	 * @param tol глубина рекурсии 
	 * @param v массив точек 
	 * @param j индекс начала 
	 * @param k индекс конца
	 * @param mk массив отметок
	 */
	private static void simplifyDP2D(float tol, Point[] v, int j, int k, int [] mk){
        
        if (k <= j+1) return;  //nothing to simplify
        
        int maxi = j;
        float maxd2 = 0;
        float tol2 = tol*tol;
        //Seg S = new Seg(v[j], v[k]);
        
        Point u = v[k].minus(v[j]);
        float cu = u.dot(u);
        
        Point w;
        Point Pb;
        float b, cw, dv2;
        
        for (int i=j+1; i < k; i++ ){
            w = v[i].minus(v[j]);
            cw = w.dot(u);
            if (cw <= 0)
                dv2 = v[i].distanceSquared(v[j]);
            else if (cu <= cw)
                dv2 = v[i].distanceSquared(v[k]);
            else{
                b = cw/cu;
                Pb= v[j].minus(u.times(-b));
                dv2 = v[i].distanceSquared(Pb);
                
            }
            
            if (dv2 <= maxd2)
                continue;
            maxi = i;
            maxd2 = dv2;
        }
        if (maxd2 > tol2){
            mk[maxi] = 1;
            simplifyDP2D(tol,v,j,maxi,mk);
            simplifyDP2D(tol,v,maxi,k,mk);            
        }
        return;
        
    }
}
