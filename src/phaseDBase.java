/*
**************************************************************************
**
**    Class  phaseDBase
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
**    This class meant to manage the database of JCPDS cards
**
*************************************************************************/

import java.util.*;
import java.io.*;

class phaseDBase implements  Serializable {
    LinkedList phaseList;
    static final long serialVersionUID = 1;

    phaseDBase() {
	phaseList = new LinkedList();
    }

    boolean addPhase(phase newphase) {
	phaseList.add(newphase.clone());
	return true;
    }

    void merge(phaseDBase newDB) {
	int nPhases = newDB.size();
	for (int i=0; i<nPhases; i++) {
	    this.addPhase(newDB.get(i));
	}
    }

    int size() {
	return phaseList.size();
    }

    phase get(int i) {
	return  (phase)phaseList.get(i);
    }

    void remove(int i) {
	phaseList.remove(i);
    }

    void replace(int i, phase newphase) {
	 phaseList.remove(i);
	 phaseList.add(i, newphase.clone());
    }

    private void writeObject(ObjectOutputStream s)
	throws IOException {
	// version number for file formatting
	s.writeInt(1);
	// Data
	int nPhases = phaseList.size();
	s.writeInt(nPhases);
	for (int i=0;i<nPhases;i++) {
	    s.writeObject(phaseList.get(i));
	}
    }
    
    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException  {
	//System.out.println("Reading database 1 ");
	int version = 1;
	if (version != s.readInt()) throw new IOException("File format not supported");	
	//System.out.println("Reading database 2 ");
	int nPhases = s.readInt();
	phaseList = new LinkedList();
	//System.out.println("Reading database 3 ");
	for (int i=0;i<nPhases;i++) {
	    //System.out.println("Reading database: phase " + i);
	    phaseList.add((phase)s.readObject());
	    //System.out.println("Reading database: phase " + i + " END");
	}
	//System.out.println("Reading database END ");
    }
}
