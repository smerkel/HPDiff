/*
**************************************************************************
**
**    Class  phaseSelect
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
**    This class creates a GUI to show the content of a JCPDS card
**
*************************************************************************/

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.text.DecimalFormat;

public class showJCPDS extends JDialog  implements ActionListener {
    phase phase;
    int symmetry;
    JTextField shortName, fullName, eosReference, peaksReference, creator, timestamp, K0, dK0, a0, b0, c0, alpha0, beta0, gamma0, V0;
    JTable tableHKL;
    ThisTableModel modelHKL;
    boolean saveChg;
    
showJCPDS(JFrame parent, phase toedit) {
	super (parent,"Information on JCPDS Card",true);
	phase = (phase)toedit.clone();
	symmetry = phase.getSymmetry();
	saveChg = false;
	// Names, refs...
	JPanel titles = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints cons = new GridBagConstraints();
	titles.setLayout(gridbag);
	cons.fill = GridBagConstraints.HORIZONTAL; 
	JLabel shName = new JLabel("Short name");
	shortName = new  JTextField(phase.getShortName(), 30);
	cons.gridy = 0;
	cons.gridx = 0;
	gridbag.setConstraints(shName, cons);
	titles.add(shName);
	cons.gridx = 1;
	gridbag.setConstraints(shortName, cons);
	titles.add(shortName);
	JLabel fName = new JLabel("Card information");
	fullName = new  JTextField(phase.getName(), 30);
	cons.gridy = 1;
	cons.gridx = 0;
	gridbag.setConstraints(fName, cons);
	titles.add(fName);
	cons.gridx = 1;
	gridbag.setConstraints(fullName, cons);
	titles.add(fullName);
	JLabel pName = new JLabel("Reference for peaks");
	peaksReference = new  JTextField(phase.getPeaksReference(), 40);
	cons.gridy = 2;
	cons.gridx = 0;
	gridbag.setConstraints(pName, cons);
	titles.add(pName);
	cons.gridx = 1;
	gridbag.setConstraints(peaksReference, cons);
	titles.add(peaksReference);
	JLabel eName = new JLabel("Reference for EOS");
	eosReference = new  JTextField(phase.getEosReference(), 40);
	cons.gridy = 3;
	cons.gridx = 0;
	gridbag.setConstraints(eName, cons);
	titles.add(eName);
	cons.gridx = 1;
	gridbag.setConstraints(eosReference, cons);
	titles.add(eosReference);
	JLabel cName = new JLabel("Creator");
	creator = new  JTextField(phase.getCreator(), 40);
	cons.gridy = 4;
	cons.gridx = 0;
	gridbag.setConstraints(cName, cons);
	titles.add(cName);
	cons.gridx = 1;
	gridbag.setConstraints(creator, cons);
	titles.add(creator);
	JLabel tName = new JLabel("Last Modified");
	timestamp = new  JTextField(phase.getLastModified(), 40);
	cons.gridy = 5;
	cons.gridx = 0;
	gridbag.setConstraints(tName, cons);
	titles.add(tName);
	cons.gridx = 1;
	gridbag.setConstraints(timestamp, cons);
	titles.add(timestamp);
	JLabel sym = new JLabel("Symmetry");
	JLabel thesym = new JLabel(phase.getSymName());
	cons.gridy = 6;
	cons.gridx = 0;
	gridbag.setConstraints(sym, cons);
	titles.add(sym);
	cons.gridx = 1;
	gridbag.setConstraints(thesym, cons);
	titles.add(thesym);
	// Physical properties...
	JPanel physics = new JPanel();
	physics.setLayout(new GridLayout(0,4));
	JLabel lK0 = new JLabel("K0");
	JLabel ldK0 = new JLabel("K'0");
	JLabel la0 = new JLabel("a0");
	JLabel lb0 = new JLabel("b0");
	JLabel lc0 = new JLabel("c0");
	JLabel lalpha0 = new JLabel("alpha0");
	JLabel lbeta0 = new JLabel("beta0");
	JLabel lgamma0 = new JLabel("gamma0");
	JLabel lV0 = new JLabel("V0");
	K0 = new JTextField(""+ phase.getK0(), 6);
	dK0 = new JTextField(""+ phase.getDK0(), 6);
	a0 = new JTextField(""+ phase.getA0(), 6);
	b0 = new JTextField(""+ phase.getB0(), 6);
	c0 = new JTextField(""+ phase.getC0(), 6);
	alpha0 = new JTextField(""+ phase.getAlpha0(), 6);
	beta0 = new JTextField(""+ phase.getBeta0(), 6);
	gamma0 = new JTextField(""+ phase.getGamma0(), 6);
	DecimalFormat formatter = new DecimalFormat ( "0.00" ) ;
	V0 = new JTextField(formatter.format(phase.getV0()), 6);
	physics.add(lK0);
	physics.add(K0);
	physics.add(ldK0);
	physics.add(dK0);
	physics.add(la0);
	physics.add(a0);
	physics.add(lb0);
	physics.add(b0);
	physics.add(lc0);
	physics.add(c0);
	physics.add(lalpha0);
	physics.add(alpha0);
	physics.add(lbeta0);
	physics.add(beta0);
	physics.add(lgamma0);
	physics.add(gamma0);
	physics.add(lV0);
	physics.add(V0);
	shortName.setEditable(false);
	fullName.setEditable(false);
	eosReference.setEditable(false);
	peaksReference.setEditable(false);
	creator.setEditable(false);
	timestamp.setEditable(false);
	K0.setEditable(false);
	dK0.setEditable(false);
	a0.setEditable(false);
	b0.setEditable(false);
	c0.setEditable(false);
	alpha0.setEditable(false);
	beta0.setEditable(false);
	gamma0.setEditable(false);
	V0.setEditable(false);
	// HKL list...
	JPanel hklList = new JPanel();
	LinkedList peakList = new LinkedList();
	peakList.addAll(phase.peaksDef());
	int nPeaks = peakList.size();
	String[] columnNames = {"h", "k", "l", "d0", "I0"};
	Object[][] data = new Object [nPeaks][5];
	for (int i=0; i<nPeaks; i++) {
	    data[i][0] = new Integer(((peak)peakList.get(i)).h());
	    data[i][1] = new Integer(((peak)peakList.get(i)).k());
	    data[i][2] = new Integer(((peak)peakList.get(i)).l());
	    data[i][3] = new Double(((peak)peakList.get(i)).d());
	    data[i][4] = new Double(((peak)peakList.get(i)).intensity());
	}
	modelHKL = new ThisTableModel(data, columnNames);
	tableHKL = new JTable(modelHKL);
	JScrollPane scrollPane = new JScrollPane(tableHKL);
	tableHKL.setPreferredScrollableViewportSize(new Dimension(400, 200));
	//table.setPreferredScrollableViewportSize(new Dimension(500, 70));
	hklList.add(scrollPane);
	TableColumn column = null;
	for (int i = 0; i < 5; i++) {
	    column = tableHKL.getColumnModel().getColumn(i);
	    if (i < 3) {
		column.setPreferredWidth(20); //sport column is bigger
	    } else if (i==3) {
		column.setPreferredWidth(100);
		column.setCellRenderer(new dspacsRenderer(5));
	    } else {
		column.setPreferredWidth(100);
		column.setCellRenderer(new dspacsRenderer(1));
	    }
	}
	// Buttons
	JPanel buttons = new JPanel();
	JButton ok = new JButton("Ok");
	ok.addActionListener(this);
	ok.setActionCommand("ok");
	buttons.add(ok);
	// Finishingup
	JPanel props = new JPanel();
	GridBagLayout gridbag2 = new GridBagLayout();
	GridBagConstraints cons2 = new GridBagConstraints();
	props.setLayout(gridbag2);
	cons2.gridy = 0;
	cons2.gridx = 0;
	gridbag2.setConstraints(titles, cons2);
	props.add(titles);
	cons2.gridy = 1;
	cons2.gridx = 0;
	gridbag2.setConstraints(physics, cons2);
	props.add(physics);
	cons2.gridy = 2;
	cons2.gridx = 0;
	gridbag2.setConstraints(hklList, cons2);
	props.add(hklList);
	this.getContentPane().add(props,  BorderLayout.CENTER);
	this.getContentPane().add(buttons,  BorderLayout.SOUTH);
	this.pack();
	this.setVisible(true);
}

//public interface ActionListener extends java.util.EventListener {
//		public void actionPerformed(java.awt.event.ActionEvent e);
//}
    
public void actionPerformed(java.awt.event.ActionEvent e){
	if (e.getActionCommand().equals("ok")) { 
		this.dispose();
	}
}

class ThisTableModel extends AbstractTableModel {
	private String[] columnNames;
	Vector rows = new Vector();
	public ThisTableModel(Object[][] data,  String[] columnNames) {
	    this.columnNames = columnNames;
	    for (int i=0; i<data.length; i++) {
		Vector row = new Vector();
		for (int j=0; j<5; j++) {
		    row.add(data[i][j]);
		}
		addRow(row);
	    }
	}
	public int getColumnCount() { return columnNames.length; }
	public int getRowCount() { return rows.size(); }
	public String getColumnName(int col) { return columnNames[col]; }
	public Object getValueAt(int row, int col) {   
	    Vector tmprow = (Vector)rows.elementAt(row);
	    return tmprow.elementAt(col);       
	}
	public Class getColumnClass(int c) {
	    return getValueAt(0, c).getClass();
	}
	public boolean isCellEditable(int row, int col) {
	    return true;
	}
	public void addRow(Vector row) {
	    rows.addElement(row);
	    fireTableStructureChanged();
	}
	public void insertRow(int index, Object data[]) {
	    Vector row = new Vector();
	    for (int j=0; j<5; j++) {
		row.add(data[j]);
	    }
	    rows.insertElementAt(row,index);
	    fireTableStructureChanged();
	}
	public void removeRow(int index) {
	    rows.removeElementAt(index);
	    fireTableStructureChanged();
	}
	public void removeRow(int index[]) {
	    for (int i=index.length;i>=0;i--) {
		rows.removeElementAt(i);
	    }
	    fireTableStructureChanged();
	}
	public void setValueAt(Object value, int row, int col) {
	    Vector tmprow = (Vector)rows.elementAt(row);
	    tmprow.setElementAt(value, col);
	    fireTableCellUpdated(row, col);
	}
    }

    static class dspacsRenderer extends DefaultTableCellRenderer {
	DecimalFormat decimalFormat; 
	public dspacsRenderer(int nDec) { 
	    super();  
	    String format = "0.";
	    for (int i=0;i<nDec;i++) { format += "0";}
	    decimalFormat = new DecimalFormat (format); 
	}
	
	public void setValue(Object value) {
	    setHorizontalAlignment(RIGHT);
	    setText((value == null) ? "" : decimalFormat.format(value));
	}
    }
}
