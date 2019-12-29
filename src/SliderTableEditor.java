 /*
**************************************************************************
**
**    Class  SliderTableEditor
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
**    Class to have a slider in a JTable (Swing component)
**    Found online at http://forum.java.sun.com/thread.jspa?threadID=497121&messageID=2345432
**
*************************************************************************/

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class SliderTableEditor extends AbstractCellEditor implements TableCellEditor {
	private boolean firstTime=false;

	public SliderTableEditor() {
		slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		slider.addMouseListener(new MouseAdapter() {
        	public void mouseReleased(MouseEvent event) {
          		stopCellEditing();
        	}
      	});
    }

	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
		if (firstTime) {
			firstTime = false;
			slider.setBounds(table.getCellRect(row, column, false));
			slider.updateUI();
		}
		slider.setValue(((Integer) value).intValue());
		return slider;
	}

	public Object getCellEditorValue() {
		return new Integer(slider.getValue());
	}
    private JSlider slider;
}
