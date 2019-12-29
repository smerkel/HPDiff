/*
**************************************************************************
**
**    Class  phase
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
**    This class holds information from a JCPDS card, performs EOS calculations, 
**    will provide a peak list at a given pressure
**    and reads information from txt JCPDS
**
*************************************************************************/


import java.util.*;
import java.io.*;
import java.text.DateFormat;

class phase implements Cloneable, Serializable {
Calendar created;
boolean set;
String name, header1, nameShort, eosReference, peaksReference, creator;
int npeak, symmetry;
int[] h, k, l;
double[] d0;
double[] I0;
double K0, dK0,dK0dT,dK0dTdP, alphaT, dAlphaT;
double a0, b0, c0, alpha0, beta0, gamma0, v0; 
static final long serialVersionUID = 1;

phase() {
	set = false;
	npeak = 0;
	K0 = 0;
	dK0 = 4.0;
	dK0dT = 0.0;
	dK0dTdP = 0.0;
	a0 = 0.0;
	b0 = 0.0;
	c0 = 0.0;
	alpha0 = 90.;
	beta0 = 90.; 
	gamma0 = 90; 
	v0 = 0; 
}

public Object clone() {
	try {
	    phase copy = (phase)super.clone();
	    copy.set = set;
	    copy.name = name;
	    copy.nameShort = nameShort;
	    copy.header1 = header1;
	    copy.eosReference = eosReference;
	    copy.peaksReference = peaksReference;
	    copy.creator = creator;
	    copy.npeak = npeak;
	    copy.symmetry = symmetry;
	    copy.K0 = K0;
	    copy.dK0 = dK0;
	    copy.dK0dT = dK0dT;
	    copy.dK0dTdP = dK0dTdP;
	    copy.a0 = a0;
	    copy.b0 = b0;
	    copy.c0 = c0;
	    copy.alpha0 = alpha0;
	    copy.beta0 = beta0;
	    copy.gamma0 = gamma0;
	    copy.v0 = v0;
	    if (set) {
			copy.h = (int[])(h.clone());
			copy.k = (int[])(k.clone());
			copy.l = (int[])(l.clone());
			copy.d0 = (double[])(d0.clone());
			copy.I0 = (double[])(I0.clone());
			copy.created = created;
	    }
	    return copy;
	} catch (CloneNotSupportedException e) {
	    throw new Error ("Ca chie dans le clonage");
	}
}
    
public boolean readDataJCPDS (BufferedReader zyva) throws IOException, NumberFormatException {
	int nn;
	String line;

	if (set) throw new IOException("This object is already set");
	// Line 1: version
	line = zyva.readLine();
	StringTokenizer st = new StringTokenizer( line );
	nn = st.countTokens();
	//System.out.println("Read " + line + " and nn is " + nn);
	if (nn == 1) {
		int version = Integer.valueOf(st.nextToken()).intValue();
		if (version != 3) { 
			throw new IOException("Wrong format at line 1"); 
		}
		return readDataJCPDS_V3 (zyva);
	} else if (nn == 2) {
		line = st.nextToken();
		line = st.nextToken();
		int version = Integer.valueOf(line).intValue();
		if (version != 4) { 
			throw new IOException("Wrong format at line 1"); 
		}
		return readDataJCPDS_V4 (zyva);
	}
	throw new IOException("Can only read JCPDS format 3 or 4!");
	}
	
private boolean readDataJCPDS_V3 (BufferedReader zyva) 
	throws IOException, NumberFormatException {
	int nn;
	String line;
	StringTokenizer st; 
	// Line 2: header
	header1 = zyva.readLine();
	name = header1;
	nameShort = name;
	// Line 3:  symmetry, and K0, K0p
	line = zyva.readLine();
	st = new StringTokenizer( line );
	nn = st.countTokens();
	if (nn != 3) throw new IOException("Wrong format at line 3"); 
	symmetry = Integer.valueOf(st.nextToken()).intValue();
	K0 = Double.valueOf(st.nextToken()).doubleValue();
	dK0 = Double.valueOf(st.nextToken()).doubleValue();
	// Line 4: unit-cell parameters
	// read cell data
	// 1 cubic, 2 hexagonal, 3 tetragonal, 4 orthorhombic, 5 monoclinic, 6 triclinic
	line = zyva.readLine();
	st = new StringTokenizer( line );
	nn = st.countTokens();
	switch (symmetry) {
	case 1:  // Cubic
	    if (nn != 1) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = a0;
	    c0 = a0;
	    alpha0 = 90.;
	    beta0 = 90.;
	    gamma0 = 90.;
	    break;
	case 2: // Hexagonal
	    if (nn != 2) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = a0;
	    c0 = Double.valueOf(st.nextToken()).doubleValue();
	    alpha0 = 90.;
	    beta0 = 90.;
	    gamma0 = 120.;
	    break;
	case 3: // Tetragonal
	    if (nn != 2) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = a0;
	    c0 = Double.valueOf(st.nextToken()).doubleValue();
	    alpha0 = 90.;
	    beta0 = 90.;
	    gamma0 = 90.;
	    break;
	case 4: // Orthorhombic
	    if (nn != 3) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = Double.valueOf(st.nextToken()).doubleValue();
	    c0 = Double.valueOf(st.nextToken()).doubleValue();
	    alpha0 = 90.;
	    beta0 = 90.;
	    gamma0 = 90.;
	    break;
	case 5: // Monoclinic
	    if (nn != 4) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = Double.valueOf(st.nextToken()).doubleValue();
	    c0 = Double.valueOf(st.nextToken()).doubleValue();
	    alpha0 = 90.;
	    beta0 = Double.valueOf(st.nextToken()).doubleValue();
	    gamma0 = 90.;
	    break;
	case 6: // Triclinic
	    if (nn != 6) throw new IOException("Wrong format at line 4"); 
	    a0 = Double.valueOf(st.nextToken()).doubleValue();
	    b0 = Double.valueOf(st.nextToken()).doubleValue();
	    c0 = Double.valueOf(st.nextToken()).doubleValue();
	    alpha0 = Double.valueOf(st.nextToken()).doubleValue();
	    beta0 = Double.valueOf(st.nextToken()).doubleValue();
	    gamma0 = Double.valueOf(st.nextToken()).doubleValue();
	    break;
	default:  throw new IOException("Illegal symmetry code"); 
	}
	v0 = a0 * b0* c0;
	if (symmetry == 2) v0 = v0*Math.sqrt(3.)/2.;
	if (symmetry == 5) v0 = v0*Math.sin(TORAD(beta0));
	if (symmetry == 6) v0 = v0*Math.sqrt(1. - Math.pow(Math.cos(TORAD(alpha0)),2)
				 - Math.pow(Math.cos(TORAD(beta0)),2) 
				 - Math.pow(Math.cos(TORAD(gamma0)),2) + 
				 2.*Math.cos(TORAD(alpha0))*Math.cos(TORAD(beta0))*Math.cos(TORAD(gamma0)));
	// 2 dummy lines
	line = zyva.readLine();
	line = zyva.readLine();
	// D-spacing, intensity, h, k, l
	// read from file and store in a string
	String s2 = new String();
	int ndata = 0;
	while((line = zyva.readLine())!= null) {
	    s2 += line + "\n";
	    ndata += 1;
	}
	// re-read the string
	npeak = ndata;
        h = new int[npeak];
	k = new int[npeak];
	l = new int[npeak];
	d0 = new double[npeak];
	I0 = new double[npeak];
	LineNumberReader  in2 = new LineNumberReader(new StringReader(s2));
	for (int i=0; i<npeak; i++) {
	    line = in2.readLine();
	    st = new StringTokenizer( line );
	    nn = st.countTokens();
	    if (nn != 5) throw new IOException("Problem reading data for peak " + (i+1));
	    d0[i] = Double.valueOf(st.nextToken()).doubleValue();
	    I0[i] = Double.valueOf(st.nextToken()).doubleValue();
	    h[i] = Integer.valueOf(st.nextToken()).intValue();
	    k[i] = Integer.valueOf(st.nextToken()).intValue();
	    l[i] = Integer.valueOf(st.nextToken()).intValue();
	}
	set = true;
	created = Calendar.getInstance();
	return set;
    }
	
	
private boolean readDataJCPDS_V4 (BufferedReader zyva) throws IOException, NumberFormatException {
	int nn;
	int nline = 1;
	int npeaksread = 0;
	String line, label, label2;
	StringTokenizer st; 
	Vector hread, kread, lread, dread, iread;
	hread = new Vector();
	kread = new Vector();
	lread = new Vector();
	dread = new Vector();
	iread = new Vector();
	while((line = zyva.readLine())!= null) {
		// System.out.println("Looking at " + line);
		nline += 1;
		st = new StringTokenizer( line );
		nn = st.countTokens();
		if (nn>0) {
			label = (st.nextToken()).toUpperCase();
			if (label.equals("COMMENT:")) {
				header1 = "";
				for (int i=0; i<nn-1; i ++) {
					header1 = header1 + " " + st.nextToken();
				}
				name = header1;
				nameShort = name;
			} else if (label.equals("K0:")) {
				K0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("K0P:")) {
				dK0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("DK0DT:")) {
				dK0dT = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("DK0PDT:")) {
				dK0dTdP = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("SYMMETRY:")) {
				label2 = (st.nextToken()).toUpperCase();
				if (label2.equals("CUBIC")) {
					symmetry = 1;
				} else if (label2.equals("TETRAGONAL")) {
					symmetry = 3;
				} else if (label2.equals("HEXAGONAL")) {
					symmetry = 2;
				} else if (label2.equals("ORTHORHOMBIC")) {
					symmetry = 4;
				} else if (label2.equals("MONOCLINIC")) {
					symmetry = 5;
				} else if (label2.equals("TRICLINIC")) {
					symmetry = 6;
				} else if (label2.equals("RHOMBOHEDRAL")) {
					symmetry = 7;
				} else {
					throw new IOException("Symmetry " + label2 + " is not supported"); 
				}
			} else if (label.equals("A:")) {
				a0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("B:")) {
				b0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("C:")) {
				c0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("ALPHA:")) {
				alpha0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("BETA:")) {
				beta0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("GAMMA:")) {
				gamma0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("VOLUME:")) {
				v0 = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("ALPHAT:")) {
				alphaT = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("DALPHAT:")) {
				dAlphaT = Double.valueOf(st.nextToken()).doubleValue();
			} else if (label.equals("DIHKL:")) {
				npeaksread += 1;
				dread.add(st.nextToken());
				iread.add(st.nextToken());
				hread.add(st.nextToken());
				kread.add(st.nextToken());
				lread.add(st.nextToken());
			} else {
				System.out.println("Don't know what to do with " + label);
			}
			// System.out.println("Done with " + line);
		}
	}
	// Fixing up symmetries
	switch (symmetry) {
		case 1:  // Cubic
		    b0 = a0;
	    	c0 = a0;
	    	alpha0 = 90.;
	    	beta0 = 90.;
	    	gamma0 = 90.;
	    	break;
		case 2: // Hexagonal
			b0 = a0;
			alpha0 = 90.;
			beta0 = 90.;
			gamma0 = 120.;
			break;
		case 3: // Tetragonal
			b0 = a0;
			alpha0 = 90.;
			beta0 = 90.;
			gamma0 = 90.;
			break;
		case 4: // Orthorhombic
			alpha0 = 90.;
			beta0 = 90.;
			gamma0 = 90.;
			break;
		case 5: // Monoclinic
			alpha0 = 90.;
			gamma0 = 90.;
			break;
		case 6: // Triclinic
			break;
		case 7: // Rhombohedral
			b0 = a0;
			c0 = a0;
			break;
		default:  throw new IOException("Illegal symmetry code"); 
	}
	v0 = a0 * b0* c0;
	if (symmetry == 2) v0 = v0*Math.sqrt(3.)/2.;
	if (symmetry == 5) v0 = v0*Math.sin(TORAD(beta0));
	if ((symmetry == 6) || (symmetry == 7)) v0 = v0*Math.sqrt(1. - Math.pow(Math.cos(TORAD(alpha0)),2)
				 - Math.pow(Math.cos(TORAD(beta0)),2) 
				 - Math.pow(Math.cos(TORAD(gamma0)),2) + 
				 2.*Math.cos(TORAD(alpha0))*Math.cos(TORAD(beta0))*Math.cos(TORAD(gamma0)));
	
	// fixing peak list
	// System.out.println("Fixing peak list");
	npeak = npeaksread;
    h = new int[npeak];
	k = new int[npeak];
	l = new int[npeak];
	d0 = new double[npeak];
	I0 = new double[npeak];
	for (int i=0; i<npeak; i++) {
		// System.out.println("Working on peak " + i + ": getting d");
	    d0[i] = Double.valueOf((String)dread.get(i)).doubleValue();
		// System.out.println("Working on peak " + i + ": getting i");
	    I0[i] = Double.valueOf((String)iread.get(i)).doubleValue();
		// System.out.println("Working on peak " + i + ": getting h");
	    h[i] = Double.valueOf((String)hread.get(i)).intValue();
		// System.out.println("Working on peak " + i + ": getting k");
	    k[i] = Double.valueOf((String)kread.get(i)).intValue();
		// System.out.println("Working on peak " + i + ": getting l");
	    l[i] = Double.valueOf((String)lread.get(i)).intValue();
	}
	// System.out.println("Done fixing peak list");
	set = true;
	// System.out.println("Get a date");
	created = Calendar.getInstance();
	// System.out.println("Finished reading this file ");
	return set;
}
	

LinkedList peaksAtP(double P, double intFactor, double width, double wavelength) {
	double v = v0*vBirch3(P);
	double d;
	double f = Math.pow(v,1./3.);
	double a, b, c;
	LinkedList peaks = new LinkedList();
	switch (symmetry) {
	case 1:  // cubic
            a = f;
	    for (int i=0; i<npeak; i++) {
		d = 1./Math.sqrt((h[i]*h[i]+k[i]*k[i]+l[i]*l[i])/(a*a));
		peaks.add(new peak(nameShort, h[i], k[i], l[i], d, I0[i], intFactor, width, wavelength));
	    }
	    break;
	case 2:  // hexagonal
            a = Math.pow(2.0*v/(Math.sqrt(3.0)*c0/a0) , 1.0/3.0);
            c = a*c0/a0;
	    for (int i=0; i<npeak; i++) {
		d = 1./Math.sqrt( (4.0/3.0)*(h[i]*h[i] + h[i]*k[i] + k[i]*k[i])/(a*a) 
				     + l[i]*l[i]/(c*c) );
		peaks.add(new peak(nameShort, h[i], k[i], l[i], d, I0[i], intFactor, width, wavelength));
	    }
	    break;
	case 3:  // tetragonal
            a = Math.pow(v/(c0/a0) , 1./3.);
	    c = c0/a0 * a;
            b = a; 
	    for (int i=0; i<npeak; i++) {
		d = 1./ Math.sqrt( ((h[i]*h[i])+(k[i]*k[i]))/(a*a) + (l[i]*l[i])/(c*c));
		peaks.add(new peak(nameShort, h[i], k[i], l[i], d, I0[i], intFactor, width, wavelength));
	    }
            break;
	case 4:  // orthorombic
            a = Math.pow(v/(b0/a0*c0/a0), 1./3.);
            c = c0/a0 * a;
            b = b0/a0 * a;
	    for (int i=0; i<npeak; i++) {
		d = 1./ Math.sqrt((h[i]*h[i])/(a*a)+(k[i]*k[i])/(b*b)+(l[i]*l[i])/(c*c));
		peaks.add(new peak(nameShort, h[i], k[i], l[i], d, I0[i], intFactor, width, wavelength));
	    }
            break;
        case 5: // monoclinic (assume that angle doesn't change with pressure)
            a = Math.pow(v/(b0/a0*c0/a0*Math.sin(TORAD(beta0))) , 1./3.);
            c = c0/a0 * a;
            b = b0/a0 * a;
	    for (int i=0; i<npeak; i++) {
		d = 1./ Math.sqrt ( 1./(Math.pow(Math.sin(TORAD(beta0)),2))*
				  ( h[i]*h[i]/a/a +k[i]*k[i]*
				    (Math.pow(Math.sin(TORAD(beta0)),2))/b/b + l[i]*l[i]/c/c 
				    - 2.*h[i]*l[i]*Math.cos(TORAD(beta0))/a/c) );
		peaks.add(new peak(nameShort, h[i], k[i], l[i], d, I0[i], intFactor, width, wavelength));
	    }
	    break;
	default:
	    break;
	}
	return peaks;
}

LinkedList peaksDef() {
	LinkedList peaks = new LinkedList();
	for (int i=0; i<npeak; i++) {
	    peaks.add(new peak(nameShort, h[i], k[i], l[i], d0[i], I0[i], 1.0, 1.0, 1.0));
	}
	return peaks;
}

String getName() {
	return name;
}

String getEosReference() {
	return eosReference;
}

String getPeaksReference() {
	return peaksReference;
}

String getCreator() {
	return creator;
}

String getShortName() {
	return nameShort;
}

String getLastModified() {  
	String myString = (DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT)).format(created.getTime());
	return myString;
}

int getSymmetry() {
	return symmetry;
}

String getSymName() {
	switch (symmetry) {
	case 1:  
	    return "cubic";
	case 2:  
	    return "hexagonal";
	case 3:  
	    return "tetragonal";
	case 4:  
	    return "orthorombic";
        case 5: 
	    return "monoclinic (angle doesn't change with pressure)";
	case 6: 
	    return "triclinic (not supported!)";
	case 7: 
	    return "rhombohedral (not supported!)";
	default:
	    return "unknown (trouble ahead!)";
	}
}

double getK0() {return K0;}
double getDK0() {return dK0;}
double getA0() {return a0;}
double getB0() {return b0;}
double getC0() {return c0;}
double getV0() {return v0;}
double getAlpha0() {return alpha0;}
double getBeta0() {return beta0;}
double getGamma0() {return gamma0;}

void setName(String newname) {
	name = newname;
	header1 = newname;
}
 
void setShortName(String newname) {
	nameShort = newname;
}

void setEosReference(String newname) {
	eosReference = newname;
}

void setPeaksReference(String newname) {
	peaksReference = newname;
}

void setCreator(String newname) {
	creator = newname;
}

void setTimeStamp() {
	created = Calendar.getInstance();
}

void setK0(double newVal) {K0 = newVal;}
void setDK0(double newVal) {dK0 = newVal;}
void setA0(double newVal) {a0 = newVal;}
void setB0(double newVal) {b0 = newVal;}
void setC0(double newVal) {c0 = newVal;}
void setAlpha0(double newVal) {alpha0 = newVal;}
void setBeta0(double newVal) {beta0 = newVal;}
void setGamma0(double newVal) {gamma0 = newVal;}
void setV0(double newVal) {v0 = newVal;}
  
void setPeaks(LinkedList newpeaks) {
	// int npeak, symmetry;
	// int[] h, k, l;
	// double[] d0;
	// double[] I0;
	npeak = newpeaks.size();
	h = new int[npeak];
	k = new int[npeak];
	l = new int[npeak];
	d0 = new double[npeak];
	I0 = new double[npeak];
	for (int i=0; i<npeak; i++) {
	    peak thisone = (peak)newpeaks.get(i);
	    d0[i] = thisone.d();
	    I0[i] = thisone.intensity();
	    h[i] = thisone.h();
	    k[i] = thisone.k();
	    l[i] =thisone.l();
	}
}
  
/*
	Finds V/Vo for a pressure and the  3d order Birch-Murngham EOS
	@param p pressure
	@return V/Vo
	*/  
private double vBirch3 (double P) {
	double vmin = .1;
	double vmax = 1.;
	double prec = 0.001;
	double v,pp;
	v = (vmin+vmax)/2.;
	while ((vmax-vmin)>prec) {
	    v = (vmin+vmax)/2.;
	    pp = birch3(v);
	    if (pp>P) {
		vmin = v;
	    } else {
		vmax = v;
	    }
	}
	return v;
}

/*
	Calculates the pressure for a given V/Vo  using the 3d order Birch-Murnagham EOS
	@param v V/Vo
	@return the pressure P
	*/
private double birch3(double v) {
	double f = .5*(java.lang.Math.pow(v,-2./3.)-1.);
	double p0 = K0;
	double p1 = 1.5*K0*(dK0-4.);
	double F = p0+p1*f;
	double p = F*3.*f*java.lang.Math.pow(1.+2.*f,2.5);
	return p;
}   

private double TORAD(double angle) {
	return angle*3.1415927/180.;
}

private void writeObject(ObjectOutputStream s) throws IOException {
	   // version number for file formatting
	   s.writeInt(2);
	   // Data
	   s.writeBoolean(set);
	   s.writeObject(created);
	   s.writeObject(name);
	   s.writeObject(nameShort);
	   s.writeObject(header1);
	   s.writeObject(eosReference);
	   s.writeObject(peaksReference);
	   s.writeObject(creator);
	   s.writeInt(npeak);
	   s.writeInt(symmetry);
	   s.writeDouble(K0);
	   s.writeDouble(dK0);
	   s.writeDouble(dK0dT);
	   s.writeDouble(dK0dTdP);
	   s.writeDouble(alphaT);
	   s.writeDouble(dAlphaT);
	   s.writeDouble(a0);
	   s.writeDouble(b0);
	   s.writeDouble(c0);
	   s.writeDouble(alpha0);
	   s.writeDouble(beta0);
	   s.writeDouble(gamma0);
	   s.writeDouble(v0);
	   if (set) {
	       for (int i=0;i<npeak;i++) {
		   s.writeInt(h[i]);
		   s.writeInt(k[i]);
		   s.writeInt(l[i]);
		   s.writeDouble(d0[i]);
		   s.writeDouble(I0[i]);
	       }
	   }
}
    
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
	//System.out.println("Reading phase 1 ");
	int version = s.readInt();
	if (version == 1) {
		readObjectVersion1(s);
	} else if (version == 2) {
		readObjectVersion2(s);
	} else throw new IOException("File format not supported");
}

private void readObjectVersion1(ObjectInputStream s) throws IOException, ClassNotFoundException  {
	set = s.readBoolean();
	created = (Calendar)s.readObject();
	name = (String)s.readObject();
	nameShort = (String)s.readObject();
	header1 = (String)s.readObject();	
	npeak = s.readInt();
	symmetry = s.readInt();
	K0 = s.readDouble();
	dK0 = s.readDouble();
	dK0dT = 0.0;
	dK0dTdP = 0.0;
	alphaT = 0.0;
	dAlphaT = 0.0;
	a0 = s.readDouble();
	b0 = s.readDouble();
	c0 = s.readDouble();
	alpha0 = s.readDouble();
	beta0 = s.readDouble();
	gamma0 = s.readDouble();
	v0  = s.readDouble();
	//System.out.println("Reading phase 3 ");
	if (set) {
	    h = new int[npeak];
	    k = new int[npeak];
	    l = new int[npeak];
	    d0 = new double[npeak];
	    I0 =  new double[npeak];
	    for (int i=0;i<npeak;i++) {
		h[i] = s.readInt();
		k[i] = s.readInt();
		l[i] = s.readInt();
		d0[i] = s.readDouble();
		I0[i] = s.readDouble();
	    }
	}
}
	
private void readObjectVersion2(ObjectInputStream s) throws IOException, ClassNotFoundException  {
	set = s.readBoolean();
	created = (Calendar)s.readObject();
	name = (String)s.readObject();
	nameShort = (String)s.readObject();
	header1 = (String)s.readObject();	
	eosReference = (String)s.readObject();
	peaksReference = (String)s.readObject();
	creator = (String)s.readObject();
	npeak = s.readInt();
	symmetry = s.readInt();
	K0 = s.readDouble();
	dK0 = s.readDouble();
	dK0dT = s.readDouble();
	dK0dTdP = s.readDouble();
	alphaT = s.readDouble();
	dAlphaT = s.readDouble();
	a0 = s.readDouble();
	b0 = s.readDouble();
	c0 = s.readDouble();
	alpha0 = s.readDouble();
	beta0 = s.readDouble();
	gamma0 = s.readDouble();
	v0  = s.readDouble();
	//System.out.println("Reading phase 3 ");
	if (set) {
	    h = new int[npeak];
	    k = new int[npeak];
	    l = new int[npeak];
	    d0 = new double[npeak];
	    I0 =  new double[npeak];
	    for (int i=0;i<npeak;i++) {
		h[i] = s.readInt();
		k[i] = s.readInt();
		l[i] = s.readInt();
		d0[i] = s.readDouble();
		I0[i] = s.readDouble();
	    }
	}
}

}
