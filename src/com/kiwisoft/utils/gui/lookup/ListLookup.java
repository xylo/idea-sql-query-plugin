/*
 * Copyright (C) 2002-2006 Stefan Stiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kiwisoft.utils.gui.lookup;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;

import com.kiwisoft.utils.StringComparator;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 18:04:41 $
 */
public abstract class ListLookup implements Lookup
{
//	private LookupField field;

    protected ListLookup()
    {
    }

	public void open(LookupField field, Collection values)
	{
		LookupWindow window = new LookupWindow(field, values);
		Point point=field.getLocationOnScreen();
		window.setLocation(point.x, point.y+field.getSize().height);
		window.setSize(300, 200);
		window.setVisible(true);
		window.toFront();
		window.setFocusableWindowState(true);
		window.requestFocus();
		window.list.requestFocus();
		window.list.setSelectedValue(field.getValue(), true);
		window.addWindowFocusListener(window);
	}

	public class CancelAction extends AbstractAction
	{
		private LookupField field;

		public CancelAction(LookupField field)
		{
			this.field=field;
		}

		public void actionPerformed(ActionEvent e)
		{
			field.requestFocus();
		}
	}

	public class SelectionListener extends AbstractAction implements MouseListener
	{
		private JList list;
		private LookupField field;

		public SelectionListener(JList list, LookupField field)
		{
			this.list=list;
			this.field=field;
		}

		public void actionPerformed(ActionEvent e)
		{
			Object value=list.getSelectedValue();
			if (value!=null)
			{
				field.setValue(value);
				field.requestFocus();
			}
		}

		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
			{
				Object value=list.getSelectedValue();
				if (value!=null)
				{
					field.setValue(value);
					field.requestFocus();
				}
				e.consume();
			}
		}

		public void mousePressed(MouseEvent e)
		{
		}

		public void mouseReleased(MouseEvent e)
		{
		}

		public void mouseEntered(MouseEvent e)
		{
		}

		public void mouseExited(MouseEvent e)
		{
		}
	}

	public class LookupWindow extends JWindow implements WindowFocusListener
	{
		private JList list;
		private LookupField field;

		public LookupWindow(LookupField field, Collection values)
		{
			super((Window)field.getTopLevelAncestor());
			this.field=field;
			setContentPane(createContentPanel(values));
		}

		private JPanel createContentPanel(Collection values)
		{
			JPanel panel=new JPanel(new BorderLayout());
			List valueList=Collections.EMPTY_LIST;
			if (values!=null)
			{
				valueList = new LinkedList(values);
				Collections.sort(valueList, new StringComparator());
			}
			DefaultListModel model=new DefaultListModel();
			Iterator it=valueList.iterator();
			while (it.hasNext()) model.addElement(it.next());
			list=new JList(model);
			SelectionListener selectionListener=new SelectionListener(list, field);
			list.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
			list.getActionMap().put("select", selectionListener);
			list.getInputMap(JList.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
			list.getActionMap().put("cancel", new CancelAction(field));
			list.addMouseListener(selectionListener);
			panel.add(new JScrollPane(list), BorderLayout.CENTER);
			return panel;
		}

		public void windowGainedFocus(WindowEvent e)
		{
		}

		public void windowLostFocus(WindowEvent e)
		{
			dispose();
		}

		public void dispose()
		{
			field=null;
			list=null;
			super.dispose();
		}
	}
}
