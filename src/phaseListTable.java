/*
**************************************************************************
**
**    Class  phaseListTable
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
**    This class generates a table with a list of phases to be plotted and 
**    performs plotting operations
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
import graph.G2Dint;
import graph.DataSet;
import graph.Axis;

class phaseListTable  extends JTable implements TableModelListener {
LinkedList phaseList;
MyTableModel dm;
int nColor = 11, phaseNAdd;
JFrame thisparent;
boolean needNewPlot,even;

static protected Color[] colors = {
	new Color(0xff0000),   // red
	new Color(0x0000ff),   // blue
	new Color(0x00aaaa),   // cyan-ish
	new Color(0x000000),   // black
	new Color(0xffa500),   // orange
	new Color(0x53868b),   // cadetblue4
	new Color(0xff7f50),   // coral
	new Color(0x45ab1f),   // dark green-ish
	new Color(0x90422d),   // sienna-ish
	new Color(0xa0a0a0),   // grey-ish
	new Color(0x14ff14),   // green-ish
};
static protected  Color maincolor = new Color(0x404040);

phaseListTable(JFrame parent) {
	super(0,5);
	thisparent = parent;
	even = false;
	phaseNAdd = 0;
	dm = new MyTableModel();
	dm.addTableModelListener(this);
	this.setModel(dm); 
	this.getColumn("Info").setCellRenderer(new ButtonRenderer());
    this.getColumn("Info").setCellEditor(new ButtonEditor(new JCheckBox()));
	phaseList = new LinkedList();
}

class MyTableModel extends AbstractTableModel   {
	final String[] columnNames = {"Phase","Info", "Active", "P = 0", "Color", "Intensity", "Peak width"};
 	Vector rows = new Vector();
	public int getColumnCount() { return columnNames.length; }
	public int getRowCount() { return rows.size(); }
	public String getColumnName(int col) { return columnNames[col]; }
	public Object getValueAt(int row, int col) {   
		Vector tmprow = (Vector)rows.elementAt(row);
	    return tmprow.elementAt(col);       
	}
	public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
	public boolean isCellEditable(int row, int col) {
	    return true;
	}
	public void addRow(Vector row) {
	    rows.addElement(row);
	    fireTableStructureChanged();
	}
	public void setValueAt(Object value, int row, int col) {
	    Vector tmprow = (Vector)rows.elementAt(row);
	    tmprow.setElementAt(value, col);
	    fireTableCellUpdated(row, col);
	}
	public void tableChanged(TableModelEvent e) {
		fireTableChanged(e);
	}
}

// Those events are not processed properly...
public void tableChanged(TableModelEvent e) {
	needNewPlot = false;
	if (even == false) {
		if (e.getColumn() == 1) {
			showJCPDS showDialog = new showJCPDS(thisparent,(phase)phaseList.get(e.getLastRow()));
		} else if (e.getColumn() == 4) { // New Color
			needNewPlot = true;
		} else if (e.getColumn() == 5) { // Peak intensity
			needNewPlot = true;
		}
		even = true;
	} else {
		even = false;
	}
    super.tableChanged(e);
}

boolean needRedraw() {
	return needNewPlot;
}

public void mouseClicked(MouseEvent e) { 
 	TableColumnModel columnModel = this.getColumnModel();
 	int viewColumn = columnModel.getColumnIndexAtX(e.getX());
 	int column = this.convertColumnIndexToModel(viewColumn);
	if (column>1) needNewPlot = true;
}

public void mouseReleased(MouseEvent e) { 
 	TableColumnModel columnModel = this.getColumnModel();
 	int viewColumn = columnModel.getColumnIndexAtX(e.getX());
 	int column = this.convertColumnIndexToModel(viewColumn);
	if (column>4) needNewPlot = true; // Intensity and half-wdith need redraw after dragging...
}

public void mouseDragged(MouseEvent e) { 
 	TableColumnModel columnModel = this.getColumnModel();
 	int viewColumn = columnModel.getColumnIndexAtX(e.getX());
 	int column = this.convertColumnIndexToModel(viewColumn);
	needNewPlot = false;
	if (column > 4) needNewPlot = true; // Intensity and half-wdith
}

boolean addPhase(phase newphase) {
	int colornumber;
	phaseList.add(newphase.clone());
	String name = newphase.getShortName();
	//String[] ivalues = new String[]{"0.2", "0.4", "0.6","0.8", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0"};
	//String[] wvalues = new String[]{"0.01", "0.03", "0.05","0.07", "1.0", "1.3", "1.6", "2.0", "2.5", "3.0"};
	Vector row = new Vector();
	row.add(name);
	row.add("Info");
	row.add(new Boolean(true));
	row.add(new Boolean(false));
	colornumber = phaseNAdd % nColor;
	row.add(colors[colornumber]);
	row.add(new Integer(50));
	row.add(new Integer(20));
	dm.addRow(row);
	this.getColumn("Info").setCellRenderer(new ButtonRenderer());
    this.getColumn("Info").setCellEditor(new ButtonEditor(new JCheckBox()));
	this.getColumn("Color").setCellRenderer(new ColorRenderer(true));
    this.getColumn("Color").setCellEditor(new ColorEditor());    
	this.getColumn("Intensity").setCellRenderer(new SliderTableRenderer());
	this.getColumn("Intensity").setCellEditor(new SliderTableEditor());
	this.getColumn("Peak width").setCellRenderer(new SliderTableRenderer());
	this.getColumn("Peak width").setCellEditor(new SliderTableEditor());
	phaseNAdd += 1;
	return true;
}

void fillPlot(G2Dint plot,Axis  xaxis, Axis  yaxis,  peakListTable peakListTable, 
		  double pressure, double wavelength, boolean drawEnveloppe, 
		  diffData diffData, double background, boolean plotchi, boolean plotgauss) {
	int nPhases = dm.getRowCount();
	boolean[] doit = new boolean[nPhases];
	boolean[] p0 = new boolean[nPhases];
	double[] intFactor = new double[nPhases];
	double[] width = new double[nPhases];
	LinkedList peakList = new LinkedList();
	for (int i=0; i<nPhases; i++) {
	    doit[i] = ((Boolean)dm.getValueAt(i,2)).booleanValue();
	    p0[i] = ((Boolean)dm.getValueAt(i,3)).booleanValue();
	    intFactor[i] = 0.02*((Integer)dm.getValueAt(i,5)).doubleValue();
	    width[i] = 0.005*((Integer)dm.getValueAt(i,6)).doubleValue();
	    if (doit[i]) {
			if (p0[i]) {
				peakList.addAll(((phase)phaseList.get(i)).peaksAtP(0.0, intFactor[i], width[i], wavelength));
			} else {
				peakList.addAll(((phase)phaseList.get(i)).peaksAtP(pressure, intFactor[i], width[i], wavelength));
			}
	    }
	}
	Collections.sort(peakList);
	peakListTable.fill(peakList);
	int k,l;
	// Plotting experimental data
	if (diffData.isSet() && plotchi) {
		double dataExp[] = new double[2*diffData.getNData()];
		double scale = 100./diffData.maxIntensity();
	    for(k=l=0; k<diffData.getNData(); k++,l+=2) {
		dataExp[l] = diffData.get2Theta(k);
		dataExp[l+1] = diffData.getIntensity(k)*scale;
	    }
	    DataSet data = new DataSet();
	    try {
		data.append(dataExp,diffData.getNData());
	    } catch (Exception e) {}
	    data.linestyle = 1;
	    data.marker    = 0;
	    data.linecolor   = maincolor;
	    plot.attachDataSet(data);
	    xaxis.attachDataSet(data);
	    yaxis.attachDataSet(data);
	}
	// Plotting synthetic data for each phase and calculating total spectrum
	double plotMin = 0.;
	double plotMax = 25.;
	double thetainterval = 0.01;
	int nPoints = (int)((plotMax-plotMin)/thetainterval);
	double[] dataSet1 = new double[nPoints];
	double[] dataSetP = new double[nPoints];
	double[] total = new double[nPoints];
	for (int j=0; j< nPoints; j++) total[j]=0.0;
	double[] dataD = new double[2*nPoints];
	int npeaks;
	int nPlot = 0;
	int colornumber;
	for (int i=0; i<nPhases; i++) {    
	    for (int j=0; j< nPoints; j++) dataSet1[j]=0.0;
	    for (int j=0; j< nPoints; j++) dataSetP[j]=0.0;
	    if (doit[i]) {
			DataSet data = new DataSet();
			if (p0[i]) {
				peakList = ((phase)phaseList.get(i)).peaksAtP(0.0, intFactor[i], width[i], wavelength);
			} else {
				peakList = ((phase)phaseList.get(i)).peaksAtP(pressure, intFactor[i], width[i], wavelength);
			}
			npeaks = peakList.size();
			for (int j=0; j<npeaks;j++) {
				if (plotgauss) { 
			    	dataSet1 = ((peak)peakList.get(j)).addGauss(dataSet1, 
						  plotMin, plotMax, thetainterval);
					dataSetP = dataSet1;
				} else {
			    	dataSet1 = ((peak)peakList.get(j)).addSpike(dataSet1, 
						  plotMin, plotMax, thetainterval);
			    	dataSetP = ((peak)peakList.get(j)).addGauss(dataSetP, 
						  plotMin, plotMax, thetainterval);
				}
			}
			for(k=l=0; k<nPoints; k++,l+=2) {
			// If we are plotting gaussians, we can leave the data interval continuous.
			// If we want spikes, we need to make it smaller around the peaks!
				if (plotgauss) {
			    	dataD[l] = plotMin+thetainterval*k;
				} else {
					if ( (k>0) && (k<(nPoints-1)) && (dataSet1[k+1]>0)&&(dataSet1[k]<0.1) ) {
						dataD[l] = plotMin+thetainterval*(k+1)-0.000000001;
					} else if ( (k>0)&& (k<(nPoints-1))&&(dataSet1[k-1]>0)&&(dataSet1[k]<0.1) ) {
						dataD[l] = plotMin+thetainterval*(k-1)+0.000000001;
					} else {
						dataD[l] = plotMin+thetainterval*k;
					}
				}
		    	dataD[l+1] = background+dataSet1[k];
		    	total[k] +=  dataSetP[k];
			}
			try {
		    	data.append(dataD,nPoints);
			} catch (Exception e) {}
			data.linestyle = 1;
			data.marker    = 2;
			data.linecolor   = ((Color)dm.getValueAt(i,4));
			plot.attachDataSet(data);
			xaxis.attachDataSet(data);
			yaxis.attachDataSet(data);
			nPlot += 1;
	    }
	}
	if (drawEnveloppe) {
	    for(k=l=0; k<nPoints; k++,l+=2) {
			dataD[l] = plotMin+thetainterval*k;
			dataD[l+1] = background+total[k];
	    }
	    DataSet data = new DataSet();
	    try {
			data.append(dataD,nPoints);
	    } catch (Exception e) {}
	    data.linestyle = 1;
	    data.marker    = 0;
	    data.linecolor   = maincolor;
	    plot.attachDataSet(data);
	    xaxis.attachDataSet(data);
	    yaxis.attachDataSet(data);
	}
}

}
