/*
**************************************************************************
**
**    Class  diffData
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
**    This class holds diffraction data and knows how to read it from a chi file
**
*************************************************************************/

import java.util.*;
import java.io.*;

class diffData implements Cloneable {
    boolean set;
    String header;
    int nData;
    Vector theta;
    Vector intensity;

	diffData() {
		set = false;
		theta = new Vector();
		intensity = new Vector();
		nData = 0;
    }
	
	public void clear() {
		set = false;
		nData = 0;
		intensity.clear();
		theta.clear();
	}

    public Object clone() {
	try {
	    diffData copy = (diffData)super.clone();
	    copy.set = set;
	    copy.header = header;
	    copy.nData = nData;
		copy.theta = (Vector)(theta.clone());
		copy.intensity = (Vector)(intensity.clone());
	    return copy;
	} catch (CloneNotSupportedException e) {
	    throw new Error ("Ca chie dans le clonage");
	}
    }
    
    public boolean readDataChi (BufferedReader zyva) 
	throws IOException, NumberFormatException {
	int nn;
	String line;

	if (set) throw new IOException("This object is already set");
	// Line 1: header
	header = zyva.readLine();
	// Line 2, 3 nothing
	line = zyva.readLine();
	line = zyva.readLine();
	// Line 4: nData
	line = zyva.readLine();
	StringTokenizer st = new StringTokenizer( line );
	nn = st.countTokens();
	if (nn != 1) {  throw new IOException("Wrong format at line 4"); }
	nData = Integer.valueOf(st.nextToken()).intValue();
	// Data
	for (int i=0; i<nData; i++) {
		line = zyva.readLine();
	    st = new StringTokenizer( line );
	    nn = st.countTokens();
	    if (nn != 2) throw new IOException("Problem reading data at line" + (i+5));
	    theta.add(Double.valueOf(st.nextToken()));
	    intensity.add(Double.valueOf(st.nextToken()));
	}
	set = true;
	return set;
    }
    
    String getName() {
	return header;
    }
	
	boolean isSet() {return set;}
	
	int getNData() {return nData;}
	
	double get2Theta(int i) {return ((Double)theta.get(i)).doubleValue();}

	double getIntensity(int i) {return ((Double)intensity.get(i)).doubleValue();}
	
	double maxIntensity() {
		double max = 0.0;
		double y;
		for (int i=0; i<nData; i++) {
			y = ((Double)intensity.get(i)).doubleValue();
			if (y>max) { max=y;}
		}
		return max;
	}
}
