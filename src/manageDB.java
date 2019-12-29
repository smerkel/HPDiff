/*
**************************************************************************
**
**    Class  manageDB
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
**    This class is used for maintaining the JCPDS cards database
**    it can be run from the command line.
**
*************************************************************************/

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class manageDB extends JFrame implements ActionListener {
    phaseDBase database;
    JList list;
    DefaultListModel listModel  = new DefaultListModel();
    static defaults defaults = new defaults();


    manageDB() {
	super ("Phase database management");
	database = new phaseDBase();

	File dir = new File(System.getProperty("user.dir"));
	defaults.setDirectory(dir);
	
	listModel  = new DefaultListModel();
	list = new JList(listModel);
	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	//list.setLayoutOrientation(JList.VERTICAL );
	list.setVisibleRowCount(-1);
	JScrollPane listScroller = new JScrollPane(list);
	listScroller.setPreferredSize(new Dimension(250, 300));
	super.getContentPane().add(listScroller, BorderLayout.CENTER);
	
	JPanel buttons = new JPanel();
	buttons.setLayout(new GridLayout(0,1));
	JButton addFile = new JButton ("Add JCPDS");
	addFile.addActionListener(this);
	addFile.setActionCommand("jcpds");
	JButton readDB = new JButton ("Append database");
	readDB.addActionListener(this);
	readDB.setActionCommand("read");
	JButton writeDB = new JButton ("Save database");
	writeDB.addActionListener(this);
	writeDB.setActionCommand("write");
	JButton edit = new JButton ("Edit phase");
	edit.addActionListener(this);
	edit.setActionCommand("edit");
	JButton remove = new JButton ("Remove phase");
	remove.addActionListener(this);
	remove.setActionCommand("remove");
	JButton exit = new JButton ("Exit");
	exit.addActionListener(this);
	exit.setActionCommand("exit");
	buttons.add(readDB);
	buttons.add(writeDB);
	buttons.add(addFile);
	buttons.add(edit);
	buttons.add(remove);
	buttons.add(exit);
	super.getContentPane().add(buttons, BorderLayout.EAST);
	
	this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        this.setSize(300,300);
        this.setVisible(true);
    }

    public static void main (String[] args) {
	Locale.setDefault(Locale.US);
	manageDB goAhead = new  manageDB();
    }

//        public interface ActionListener extends java.util.EventListener {
//	public void actionPerformed(java.awt.event.ActionEvent e);
//    }
    
    public void actionPerformed(java.awt.event.ActionEvent e){
	if (e.getActionCommand().equals("jcpds")) {
	    boolean read = newPhase();
	} else if (e.getActionCommand().equals("edit")) {
	    int index = list.getSelectedIndex(); 
	    if (index != -1) { 
		editJCPDS editDialog;
		editDialog = new editJCPDS(this,database.get(index));
		if (editDialog.wasOk()) {
		    database.replace(index, editDialog.getPhase());
		    listModel.removeElementAt(index);
		    listModel.insertElementAt((database.get(index)).getShortName(), index);
		}
	    }
	} else if (e.getActionCommand().equals("write")) {
	    boolean ok = writeObject();
	} else if (e.getActionCommand().equals("read")) { 
	    boolean ok = readObject();
	} else if (e.getActionCommand().equals("remove")) {
	    int index = list.getSelectedIndex(); 
	    if (index != -1) { 
		database.remove(index);
		listModel.removeElementAt(index);
	    }
	} else if (e.getActionCommand().equals("exit")) {
	    System.exit(0);
	}
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
	    database.addPhase(test);
	    listModel.addElement(test.getShortName()); 
	}
	return read;
    }

    
    private boolean writeObject () {
	final JFileChooser fc = new JFileChooser(defaults.getDirectory());
	int returnVal = fc.showSaveDialog(this);
	if (returnVal != JFileChooser.APPROVE_OPTION) {
	    return false;
	} else {
	    defaults.setDirectory(fc.getCurrentDirectory());
	    try {
		File fichier = fc.getSelectedFile();		
		if (fichier.exists()) {
		    int n = JOptionPane.showConfirmDialog (this, 
							   "File "+fichier+" exists.\nOverwrite?", 
							   "Warning...", 
							   JOptionPane.YES_NO_OPTION);
		    if (n==1) {
			return false;
		    }
		}
		FileOutputStream f = new FileOutputStream(fichier);
		ObjectOutput s = new ObjectOutputStream(f);
		s.writeObject(database);
		s.flush();
	    }  catch ( FileNotFoundException e ) {
		JOptionPane.showMessageDialog(this, "File Disappeared", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }  catch ( IOException e) {
		JOptionPane.showMessageDialog(this, "Something is wrong: " + e.getMessage(), 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }  
	    return true;
	}
    }
    
    private boolean readObject() {
	final JFileChooser fc = new JFileChooser(defaults.getDirectory());
	int returnVal = fc.showOpenDialog(this);
	phaseDBase newDB; 
	if (returnVal != JFileChooser.APPROVE_OPTION) {
	    return false;
	} else {
	    defaults.setDirectory(fc.getCurrentDirectory());
	    try {
		File fichier = fc.getSelectedFile();
		FileInputStream f = new FileInputStream(fichier);
		ObjectInputStream s = new ObjectInputStream(f);
		newDB = (phaseDBase)s.readObject();
	    } catch ( ClassNotFoundException e) {
		JOptionPane.showMessageDialog(this, "Error reading database", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    } catch ( FileNotFoundException e ) {
		JOptionPane.showMessageDialog(this, "File Disappeared", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }  catch ( Exception e) {
		String mess=e.getMessage();
		JOptionPane.showMessageDialog(this, "Something is wrong: " + e.getMessage(), 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    } 
	} 
	database.merge(newDB);
	int nPhases = newDB.size();
	for (int i=0; i<nPhases; i++) {
	    listModel.addElement((newDB.get(i)).getShortName());
	}
	return true;
    }
}
