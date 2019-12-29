/*
**************************************************************************
**
**    Class  Base
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
**    This class is the base of the diffHP applet, it builds the user
**    interface and calls the various subroutines
**
*************************************************************************/


import java.util.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;   
import java.applet.Applet;
import java.io.*;
import java.text.*;
import graph.*;

public class base extends Applet implements ActionListener {
phaseListTable phaseListTable;
diffData diffData;
JTextField pVal, wVal, eVal, dataFileVal, bgVal;
JPopupMenu popup;
G2Dint  plot;
Axis  xaxis, yaxis;
peakListTable peakListTable;
JCheckBox coordinates, enveloppe, plotchi, plotgauss;
static JFrame frame;
static boolean applet;
static defaults defaults = new defaults();
boolean rescale, firstset;

/*** Constructor *****/
public base() {
}

/**
* Init routine if run as an applet
**/
public void init(){
	Locale.setDefault(Locale.US);
	applet = true;
	buildUI(this, false);
}

/**
* Init routine if run as an application
**/
public static void main (String[] args) {
	Locale.setDefault(Locale.US);
	applet = false;
	frame = new JFrame("Simulation of High Pressure Diffraction Pattern");
	frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {System.exit(0);}
	});
	base base = new base();
	base.buildUI(frame.getContentPane(), true);
	frame.setSize(750,600);
	frame.setVisible(true);
}

/*** BuildUI *****/
private void buildUI(Container container, boolean appli) {

	JPanel entry = new JPanel();
	JPanel buttons = new JPanel();
	
	JLabel pLabel = new JLabel("Pressure (GPa)");
	pVal = new JTextField("" + 0, 4);
	pVal.addActionListener(this);
	pVal.setActionCommand("newP");
	JLabel wLabel = new JLabel("Wavelength (A)");
	wVal = new JTextField("" + 0.4, 4);
	wVal.addActionListener(this);
	wVal.setActionCommand("newW");
	JLabel eLabel = new JLabel("Energy (keV)");
	eVal = new JTextField("" + 30., 4);
	eVal.addActionListener(this);
	eVal.setActionCommand("newE");
	entry.setLayout(new GridLayout(0,2));
	entry.add(pLabel);
	entry.add(pVal);
	entry.add(wLabel);
	entry.add(wVal);
	entry.add(eLabel);
	entry.add(eVal);
	
	if (applet) {
	Object parent=this.getParent();
	while (!(parent instanceof Frame)) parent=((Component)parent).getParent();
	Frame frame=(Frame)parent; 
	}
	phaseListTable = new phaseListTable(frame);
	JScrollPane phaseListScroll = new JScrollPane(phaseListTable);
	phaseListTable.setPreferredScrollableViewportSize(new Dimension(400, 50)); 
  	phaseListTable.addMouseListener(new MouseAdapter() { 
		public void mouseClicked(MouseEvent e)  {
			phaseListTable.mouseClicked(e);
			if (phaseListTable.needRedraw()) {
				plotit();
			}
     	}
		public void mouseReleased(MouseEvent e)  {
			phaseListTable.mouseReleased(e);
			if (phaseListTable.needRedraw()) {
				plotit();
			}
     	}
	});
  	phaseListTable.addMouseMotionListener(new MouseMotionAdapter() { 
		public void mouseDragged(MouseEvent e)  {
			phaseListTable.mouseDragged(e);
			if (phaseListTable.needRedraw()) {
				plotit();
			}
     	}
	});
	phaseListTable.addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_ENTER) {
				plotit();
			}
 			
 		}
	});
	
	JPanel addNew = new JPanel();
	JButton addPhase = new JButton("Add phase");
	addNew.setLayout(new GridLayout(0,1));
	//JButton addDatabase = new JButton ("Database");
	//addDatabase.addActionListener(this);
	//addDatabase.setActionCommand("phaseDatabase");
	//JButton addLocalDatabase = new JButton ("Local database");
	//addLocalDatabase.addActionListener(this);
	//addLocalDatabase.setActionCommand("phaseLocalDatabase");
	//JButton addFile = new JButton ("File");
	//addFile.addActionListener(this);
	//addFile.setActionCommand("newPhase");
	addNew.add(addPhase);
	//addNew.add(addDatabase);
	//addNew.add(addLocalDatabase);
	//addNew.add(addFile);
	//
	popup = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("From database");
    menuItem.addActionListener(this);
	menuItem.setActionCommand("phaseDatabase");
    popup.add(menuItem);
    JMenuItem menuItem2 = new JMenuItem("JCPDS Card");
    menuItem2.addActionListener(this);
	menuItem2.setActionCommand("newPhase");
    popup.add(menuItem2);
	MouseAdapter fAdapter = new MouseAdapter () {
		Component fSelectedComponent;
		// On some platforms, mouseReleased sets PopupTrigger.
		public void mouseReleased (MouseEvent e) {
			if (e.isPopupTrigger ()) {
				showPopupMenu (e);
			}
 		}
		// And on other platforms, mousePressed sets PopupTrigger.
		public void mousePressed (MouseEvent e) {
			//if (e.isPopupTrigger ()) {
				showPopupMenu (e);
			//}
		}
		public void showPopupMenu (MouseEvent e) {
			fSelectedComponent = e.getComponent ();
			popup.show (fSelectedComponent, e.getX (), e.getY ());
 		}
	};
	addPhase.addMouseListener (fAdapter);
	
	diffData = new diffData();
	JPanel dataFilePanel = new JPanel(new FlowLayout());
	JLabel dataFileLabel = new JLabel("Data file (.chi)");
	dataFileVal = new JTextField("none", 40);
	dataFileVal.setEditable(false);
	JButton dataFileChange = new JButton ("Change chi file");
	dataFileChange.addActionListener(this);
	dataFileChange.setActionCommand("changeDataFile");
	dataFilePanel.add(dataFileLabel);
	dataFilePanel.add(dataFileVal);
	dataFilePanel.add(dataFileChange);
	Border lineBdr = BorderFactory.createLineBorder(Color.black);
	dataFilePanel.setBorder(lineBdr);
	
	JPanel top = new JPanel();
	top.setLayout(new BorderLayout());
	top.add(entry, BorderLayout.WEST);
	top.add(phaseListScroll, BorderLayout.CENTER);
	top.add(addNew, BorderLayout.EAST);
	top.add(dataFilePanel, BorderLayout.SOUTH);

	plot = new G2Dint();
	plot.zerocolor = new Color(0,0,0);
	plot.borderTop    = 20;
	plot.borderBottom = 20;
	plot.borderRight = 20;
	plot.borderLeft = 20;
	plot.drawgrid = true;
	plot.gridcolor = new Color(200,200,200);
	plot.setDataBackground(Color.white);
	xaxis = plot.createXAxis(); // THIS IS IMPORTANT FOR SCALING!
	xaxis.setTitleText("2 theta");
	xaxis.setTitleFont(new Font("Helvetica",Font.BOLD,12));
	xaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,12));
	yaxis = plot.createYAxis(); // THIS IS IMPORTANT FOR SCALING!
	yaxis.setTitleText("Intensity");
	yaxis.setTitleFont(new Font("Helvetica",Font.BOLD,12));
	yaxis.setLabelFont(new Font("Helvetica",Font.PLAIN,12));
	
	int nRef = 60;
	peakListTable = new peakListTable (nRef);
	JScrollPane peakListScroll = new JScrollPane(peakListTable);
	peakListTable.setPreferredScrollableViewportSize(new Dimension(250, 400));
	
	JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, plot, peakListScroll);
	center.setOneTouchExpandable(true);
	center.setContinuousLayout(false); 
	center.setDividerLocation(500);
	center.setDividerSize(5);

	
	JButton dorefresh = new JButton ("Refresh");
	dorefresh.addActionListener(this);
	dorefresh.setActionCommand("refresh");
	buttons.add(dorefresh);
	JButton doplot = new JButton ("Rescale");
	doplot.addActionListener(this);
	doplot.setActionCommand("plot");
	buttons.add(doplot);
	if (appli) {
	JButton exit = new JButton ("Exit");
	exit.addActionListener(this);
	exit.setActionCommand("exit");
	buttons.add(exit);
	}

	JPanel sign = new JPanel();
	JLabel nom = new JLabel("Version 0.8.9 -- Mar 29, 2011");
	JLabel nom2 = new JLabel("Copyright S. Merkel, 2005-2011");
	JLabel web = new JLabel("http://merkel.ZoneO.net/HPDiff/");
	sign.setLayout(new GridLayout(3,1));
	sign.add(nom);
	sign.add(nom2);
	sign.add(web);

	JPanel options = new JPanel();
	options.setLayout(new GridLayout(1,0));
	JPanel optionsTable = new  JPanel();
	optionsTable.setLayout(new GridLayout(0,1));
	plotgauss = new JCheckBox("Gaussian peaks");    
	plotgauss.setMnemonic(KeyEvent.VK_G); 
	plotgauss.setSelected(false);
	plotgauss.addActionListener(this);
	plotgauss.setActionCommand("plotgauss");
	enveloppe = new JCheckBox("Draw full spectrum");    
	enveloppe.setMnemonic(KeyEvent.VK_E); 
	enveloppe.setSelected(false);
	enveloppe.addActionListener(this);
	enveloppe.setActionCommand("enveloppe");
	coordinates = new JCheckBox("Pointer coordinates");    
	coordinates.setMnemonic(KeyEvent.VK_C); 
	coordinates.setSelected(false);
	coordinates.addActionListener(this);
	coordinates.setActionCommand("coordinates");
	plotchi = new JCheckBox("Plot chi data");    
	plotchi.setMnemonic(KeyEvent.VK_E); 
	plotchi.setSelected(false);
	plotchi.addActionListener(this);
	plotchi.setActionCommand("plotchi");
	optionsTable.add(plotgauss);
	optionsTable.add(enveloppe);
	optionsTable.add(coordinates);
	optionsTable.add(plotchi);
	JPanel bgPanel = new JPanel();
	JLabel bgLabel = new JLabel("Background");
	bgVal = new JTextField("" + 0.0, 4);
	bgVal.addActionListener(this);
	bgVal.setActionCommand("newBg");
	bgPanel.add(bgLabel);
	bgPanel.add(bgVal);
	options.add(optionsTable);
	options.add(bgPanel);


	//	JButton about = new JButton ("About");
	//about.addActionListener(this);
	//about.setActionCommand("about");
	//aboutP.add(about);

	JPanel bottom = new JPanel();
	bottom.setLayout(new GridLayout(1,3));
	bottom.add(sign);
	bottom.add(buttons);
	bottom.add(options);
	

	container.setLayout(new BorderLayout() );
	container.add(center, BorderLayout.CENTER);
	container.add(bottom, BorderLayout.SOUTH);
	container.add(top, BorderLayout.NORTH); 
	setWavelengthFromE();
	rescale = true;
	firstset = true;
}


//public interface ActionListener extends java.util.EventListener {
//	public void actionPerformed(java.awt.event.ActionEvent e);
//}

public void actionPerformed(java.awt.event.ActionEvent e){
	if (e.getActionCommand().equals("newPhase")) {
	boolean read = newPhase();
	if (read) plotit();
	} else if (e.getActionCommand().equals("phaseDatabase")) {
	boolean read = newPhaseFromDatabase();
	if (read) plotit();
	} else if (e.getActionCommand().equals("phaseLocalDatabase")) {
	boolean read = newPhaseFromLocalDatabase();
	if (read) plotit();
	}  else if (e.getActionCommand().equals("plot")) {
	rescale = true;
	plotit();
	}  else if (e.getActionCommand().equals("newP")) {
	rescale = false;
	plotit();
	}  else if (e.getActionCommand().equals("refresh")) {
	rescale = false;
	plotit();
	} else if (e.getActionCommand().equals("newW")) { 
	setEnergyFromW();
	} else if (e.getActionCommand().equals("newE")) {
	setWavelengthFromE();
	} else if (e.getActionCommand().equals("changeDataFile")) {
	changeDataFile();
	} else if (e.getActionCommand().equals("removeDataFile")) {
	diffData.clear();
	dataFileVal.setText("none") ;
	plotit();
	}  else if (e.getActionCommand().equals("enveloppe")) {
	plotit();
	}  else if (e.getActionCommand().equals("plotgauss")) {
	plotit();
	}  else if (e.getActionCommand().equals("newBg")) {
	plotit();
	} else if (e.getActionCommand().equals("plotchi")) {
	plotit();
	} else if (e.getActionCommand().equals("coordinates")) {
	if (coordinates.isSelected()) {
		plot.triggerKeyEvent('c');
	} else {
		plot.triggerKeyEvent('C');
	}
	} else if (e.getActionCommand().equals("exit")) {
	System.exit(0);
	}
}

private void plotit() {
	double pressure = 0.0;
	try {
	pressure = Double.valueOf(pVal.getText()).doubleValue();
	} catch (NumberFormatException ee) {
	JOptionPane.showMessageDialog(this, "Pressure is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	} 
	if (pressure < 0) {
	JOptionPane.showMessageDialog(this, "Pressure is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	}
	double wavelength = 0.0;
	try {
	wavelength = Double.valueOf(wVal.getText()).doubleValue();
	} catch (NumberFormatException ee) {
	JOptionPane.showMessageDialog(this, "Wavelength is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	} 
	setEnergyFromW(false); 
	double bg = 0.0;
	try {
	bg = Double.valueOf(bgVal.getText()).doubleValue();
	} catch (NumberFormatException ee) {
	JOptionPane.showMessageDialog(this, "Background is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	} 
	double x1 = 0.; 
	double x2 = 0.; 
	double y1 = 0.; 
	double y2 = 0.;
	if ((!rescale) && (!firstset)) {
		x1 = xaxis.getMin();
		x2 = xaxis.getMax();
		y1 = yaxis.getMin();
		y2 = yaxis.getMax();
	}
	plot.detachDataSets();
	xaxis.detachAll();
	yaxis.detachAll();
	phaseListTable.fillPlot(plot, xaxis, yaxis, peakListTable, 
				pressure, wavelength, enveloppe.isSelected(),diffData, bg, plotchi.isSelected(), plotgauss.isSelected());
	if ((!rescale) && (!firstset)) {
		xaxis.setMin(x1);
		xaxis.setMax(x2);
		yaxis.setMin(y1);
		yaxis.setMax(y2);
	}
	rescale = false;
	firstset = false;
	plot.repaint();
}

private void setEnergyFromW() {
	setEnergyFromW(true);
}

private void setEnergyFromW(boolean doplot) {
	double wavelength = 0.0;
	try {
	wavelength = Double.valueOf(wVal.getText()).doubleValue();
	} catch (NumberFormatException ee) {
	JOptionPane.showMessageDialog(this, "Wavelength is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	} 	
	if (wavelength <= 0) {
	JOptionPane.showMessageDialog(this, "Wavelength is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	}
	double h = 6.62606876 * Math.pow(10.,-34);
	double c = 299792458;
	double eV = 1.602176462 * Math.pow(10.,-19);
	double angstrom = Math.pow(10.,-10);
	double e = h*c/(wavelength*angstrom*1000.*eV); 
	DecimalFormat fmt = (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);
	fmt.applyPattern("0.00000;-0.00000");
	String b = fmt.format( e );
	eVal.setText(b);
	if (doplot) {
		rescale = false;
		plotit();
	}
}

private void setWavelengthFromE() {
	double energy = 0.0;
	try {
	energy= Double.valueOf(eVal.getText()).doubleValue();
	} catch (NumberFormatException ee) {
	JOptionPane.showMessageDialog(this, "Energy is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	} 	
	if (energy <= 0) {
	JOptionPane.showMessageDialog(this, "Energy is incorrect",
					"Error",
					JOptionPane.ERROR_MESSAGE);
	return;
	}
	double h = 6.62606876 * Math.pow(10.,-34);
	double c = 299792458;
	double eV = 1.602176462 * Math.pow(10.,-19);
	double angstrom = Math.pow(10.,-10);
	double w = h*c/(energy*angstrom*1000.*eV);
	DecimalFormat fmt = (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);
	fmt.applyPattern("0.00000;-0.00000");
	String b = fmt.format( w );
	wVal.setText(b);
	rescale = false;
	plotit();
}

private boolean newPhase() {
	boolean read=false;
	phase test = new phase();
	final JFileChooser fc = new JFileChooser(defaults.getDirectory());
	int returnVal = fc.showOpenDialog(this);
	if (returnVal != JFileChooser.APPROVE_OPTION) {return false;}
	defaults.setDirectory(fc.getCurrentDirectory());
	File fichier = fc.getSelectedFile();
	try {
	FileReader jelis = new FileReader( fichier );
	BufferedReader zyva = new BufferedReader( jelis );
	read = test.readDataJCPDS(zyva);
	} catch (FileNotFoundException e3) {
	JOptionPane.showMessageDialog(this, e3.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	} catch (IOException e2) {
	JOptionPane.showMessageDialog(this, e2.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}  catch (Exception e4) {
	JOptionPane.showMessageDialog(this, e4.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}
	if (read) {
	read = phaseListTable.addPhase(test);
	}
	return read;
}


private boolean newPhaseFromDatabase() {
	boolean read=false;
	phaseDBase newDB = new phaseDBase();
	//final JFileChooser fc = new JFileChooser();
	//int returnVal = fc.showOpenDialog(this);
	//if (returnVal != JFileChooser.APPROVE_OPTION) {return false;}
	//File fichier = fc.getSelectedFile();
	URL fichier = this.getClass().getResource("phaseDatabase.dat");
	//System.out.println("Trying to read stuff out of " + fichier);
	try {
	InputStream f = fichier.openStream();
	ObjectInputStream s = new ObjectInputStream(f);
	newDB = (phaseDBase)s.readObject();
	}  catch (Exception e4) {
	
	JOptionPane.showMessageDialog(this, "Error: " + e4.getMessage(),
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}
	// We select a phase out of the database
	phaseSelect select;
	if (applet) {
	Object parent=this.getParent();
	while (!(parent instanceof Frame)) parent=((Component)parent).getParent();
	Frame frame=(Frame)parent;
	select = new phaseSelect(frame,newDB);
	} else {
	select = new phaseSelect(frame, newDB);
	}
	if (select.wasOk()) {
	read = phaseListTable.addPhase(newDB.get(select.selectedItem()));
	}
	return read; 
}

private boolean newPhaseFromLocalDatabase() {
	boolean read=false;
	phaseDBase newDB = new phaseDBase();
	final JFileChooser fc = new JFileChooser(defaults.getDirectory());
	int returnVal = fc.showOpenDialog(this);
	if (returnVal != JFileChooser.APPROVE_OPTION) {return false;}
	defaults.setDirectory(fc.getCurrentDirectory());
	File fichier = fc.getSelectedFile();
	try {
	FileInputStream f = new  FileInputStream(fichier); 
	ObjectInputStream s = new ObjectInputStream(f);
	newDB = (phaseDBase)s.readObject();
	}  catch (Exception e4) {
	
	JOptionPane.showMessageDialog(this, "Error: " + e4.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}
	// We select a phase out of the database
	phaseSelect select;
	if (applet) {
	Object parent=this.getParent();
	while (!(parent instanceof Frame)) parent=((Component)parent).getParent();
	Frame frame=(Frame)parent; 
	select = new phaseSelect(frame,newDB);
	} else {
	select = new phaseSelect(frame, newDB);
	}
	if (select.wasOk()) {
	read = phaseListTable.addPhase(newDB.get(select.selectedItem()));
	}
	return read; 
}

private boolean changeDataFile() {
	boolean read=false;
	diffData test = new diffData();
	final JFileChooser fc = new JFileChooser(defaults.getDirectory());
	int returnVal = fc.showOpenDialog(this);
	if (returnVal != JFileChooser.APPROVE_OPTION) {return false;}
	defaults.setDirectory(fc.getCurrentDirectory());
	File fichier = fc.getSelectedFile();
	try {
	FileReader jelis = new FileReader( fichier );
	BufferedReader zyva = new BufferedReader( jelis );
	read = test.readDataChi(zyva);
	} catch (FileNotFoundException e3) {
	JOptionPane.showMessageDialog(this, e3.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	} catch (IOException e2) {
	JOptionPane.showMessageDialog(this, e2.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}  catch (Exception e4) {
	JOptionPane.showMessageDialog(this, e4.getMessage(), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
	return false;
	}
	if (read) {
	diffData = test;
	dataFileVal.setText(fichier.getName()) ;
	plotchi.setSelected(true);
	plotit();
	}
	return read;
}

}


