/*
**************************************************************************
**
**    Class  SliderTableRenderer
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
 
/**
 * @version 1.0 11/09/98
 */

class SliderTableRenderer extends JSlider implements TableCellRenderer {
 
	public SliderTableRenderer() {
		slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
		slider.setValue(((Integer) value).intValue());
		return slider;
	}
    private JSlider slider;
}
