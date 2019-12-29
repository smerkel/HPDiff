/*
**************************************************************************
**
**    Class  peak
**
**************************************************************************
**
**    Copyright (C) 2005-now S. Merkel, Univ. Lille, France, 
**    http://merkel.texture.rocks
**
**    This is part of the HPDiff application
**    http://merkel.texture.rocks/HPDiff/
**
**    This program is free software; you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation; either version 2 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program; if not, write to the Free Software
**    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
**
**************************************************************************
**
**    This class holds information on a diffraction line and can return 
**    information for plotting
**
*************************************************************************/

import java.util.*;

public class peak implements Comparable {
private String  material;
private int h, k, l;
private double d, i, theta, ifactor, width;

public peak(String mat, int hh, int kk, int ll, double dd, double ii, 
		double ifac, double wi, double wavelength) {
	material = mat;
	h = hh;
	k = kk;
	l = ll;
	d = dd;
	i = ii;
	ifactor = ifac;
	width = wi;
	theta =  2.*180.*Math.asin(wavelength/(2.*d))/3.14159265;
}

public int h() {return h;}
public int k() {return k;}
public int l() {return l;}
public String mat() {return material;}
public double d() {return d;}
public double theta() {return theta;}
public double intensity() {return i;}

public boolean equals(Object o) {
	if (!(o instanceof peak))
		return false;
	peak n = (peak)o;
	return (n.material.equals(material)) && 
	(n.h == h) && (n.k == k) && (n.l == l);
}
    
public int compareTo(Object o) {
        peak n = (peak)o;
	if (n.d > d) {return 1;}
	if (n.d < d) {return -1;}
	return 0;
}

public double [] addGauss(double start[], double plotMin, double plotMax, double dTheta) {
	double xmin = theta-width*5.;
	if (xmin<plotMin) xmin = plotMin;
	double xmax = theta+width*5.;
	if (xmax>plotMax) xmax = plotMax;
	int nPoints = (int)((xmax-xmin)/dTheta);
	int indexOffset = (int)((xmin-plotMin)/dTheta);
	double x, y;
	// System.out.println("Peak at " + theta + " with half-width " + width);
	for (int j=0; j<nPoints;j++) {
	    x = xmin+dTheta*j;
	    y = ifactor*i*Math.exp(-Math.pow((theta-x)/width,2)/2.);
	    start[j+indexOffset] += y;
	}
	return start;
    }    
	
public double [] addSpike(double start[], double plotMin, double plotMax, double dTheta) {
	double x = theta;
	if ((x<plotMin)||(x>plotMax)) return start;
	int indexOffset = (int)((x-plotMin)/dTheta);
	double y;
	// System.out.println("Peak at " + theta + " with half-width " + width);
	y = ifactor*i;
	start[indexOffset] += y;
	return start;
    }
}
