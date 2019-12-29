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
**    This class creates a dialog to pick a phase from the database
**
*************************************************************************/

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class phaseSelect extends JDialog implements ActionListener {
    JList list;
    DefaultListModel listModel  = new DefaultListModel();
    boolean isOk;
    int selectedIndex;
 
    phaseSelect(Frame  parent, phaseDBase database) {
	super (parent,"Select phase", true);
	isOk = false;

	listModel  = new DefaultListModel();
	list = new JList(listModel);
	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	//list.setLayoutOrientation(JList.VERTICAL );
	list.setVisibleRowCount(-1);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	JScrollPane listScroller = new JScrollPane(list);
	listScroller.setPreferredSize(new Dimension(250, 300));
	super.getContentPane().add(listScroller, BorderLayout.CENTER);
	
	int nPhases = database.size();
	for (int i=0; i<nPhases; i++) {
	    listModel.addElement((database.get(i)).getShortName());
	}

	JPanel buttons = new JPanel();
	JButton ok = new JButton ("Ok");
	ok.addActionListener(this);
	ok.setActionCommand("ok");
	JButton exit = new JButton ("Cancel");
	exit.addActionListener(this);
	exit.setActionCommand("cancel");
	buttons.add(ok);
	buttons.add(exit);
	super.getContentPane().add(buttons, BorderLayout.SOUTH);
	//this.setLocationRelativeTo(null);//centers on screen
        this.setSize(250,200); 
        this.setVisible(true);
    }
    

//    public interface ActionListener extends java.util.EventListener {
//	public void actionPerformed(java.awt.event.ActionEvent e);
//    }
    
    public void actionPerformed(java.awt.event.ActionEvent e){
	if (e.getActionCommand().equals("cancel")) {
	    this.dispose();
	} else if (e.getActionCommand().equals("ok")) {
	    selectedIndex = list.getSelectedIndex(); 
	    if (selectedIndex  != -1) { 
		isOk = true;
	    }
	    this.dispose();
	}
    }
    
    public boolean wasOk() {return isOk;}

    public int selectedItem() { return  selectedIndex;}
  
}
