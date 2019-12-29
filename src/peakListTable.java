/*
**************************************************************************
**
**    Class  peakListTable
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
**    This class creates a table with a list of diffraction lines
**
*************************************************************************/

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;

class peakListTable  extends JTable {
    int nLines;
    DefaultTableModel dm;

peakListTable(int nLines) {
	super(nLines,6);
	this.nLines = nLines;
	String[] columnNames = {"Phase", "h", "k", "l", "d", "2 theta"};
	Object[][] data = new Object [nLines][6];
	for (int i=0; i<nLines; i++) {
	    data[i][0] = new String("");
	    data[i][1] = new Integer(0);
	    data[i][2] = new Integer(0);
	    data[i][3] = new Integer(0);
	    data[i][4] = new Double(0);
	    data[i][5] = new Double(0);
	} 
	dm = new DefaultTableModel();
	dm.setDataVector(data, columnNames);
	this.setModel(dm);
	//	this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	TableColumn col = this.getColumnModel().getColumn(0);
	int width = 50;
	col.setPreferredWidth(width); 
	col = this.getColumnModel().getColumn(1);
	width = 30;
	col.setPreferredWidth(width);
	col = this.getColumnModel().getColumn(2);
	width = 30;
	col.setPreferredWidth(width);
	col = this.getColumnModel().getColumn(3);
	width = 30;
	col.setPreferredWidth(width);
	
}

void fill(LinkedList peakList) {
	int nPeaks = peakList.size();
	int max = nLines;
	if (nPeaks<max) max = nPeaks;
	peak tmppeak;
	DecimalFormat fmt = new DecimalFormat( "0.0000;-0.0000" );
	for (int i=0; i<max; i++) {
	    tmppeak = (peak)peakList.get(i);
	    this.setValueAt(tmppeak.mat(),i,0);
	    this.setValueAt(new Integer(tmppeak.h()),i,1);
	    this.setValueAt(new Integer(tmppeak.k()),i,2);
	    this.setValueAt(new Integer(tmppeak.l()),i,3);
	    this.setValueAt(fmt.format(tmppeak.d()),i,4);
	    this.setValueAt(fmt.format(tmppeak.theta()),i,5);
	}
	for (int i=max; i<nLines; i++) {
	    this.setValueAt(" ",i,0);
	    this.setValueAt(" ",i,1);
	    this.setValueAt(" ",i,2);
	    this.setValueAt(" ",i,3);
	    this.setValueAt(" ",i,4);
	    this.setValueAt(" ",i,5);
	}

}
}
