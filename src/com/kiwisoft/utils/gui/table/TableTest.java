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
package com.kiwisoft.utils.gui.table;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Set;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.table.*;

import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.StrikeThroughDecorator;
import com.kiwisoft.utils.Tristate;
import com.kiwisoft.utils.format.ObjectFormat;
import com.kiwisoft.utils.format.ByteArrayFormat;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:04:07 $
 */
public class TableTest
{
	private TableTest()
	{
	}

	public static void main(String[] args)
	{
		RendererFactory.getInstance().setRenderer(byte[].class, new ByteArrayFormat(ObjectFormat.DEFAULT));


		TestModel model=new TestModel();
		model.addRow(new TestRow("String", "abcde"));
		model.addRow(new TestRow("Integer", new Integer(12345)));
		model.addRow(new TestRow("Double", new Double(123.45)));
		model.addRow(new TestRow("Boolean", Boolean.FALSE));
		model.addRow(new TestRow("Boolean", Boolean.TRUE));
		model.addRow(new TestRow("Tristate", Tristate.FALSE));
		model.addRow(new TestRow("Tristate", Tristate.TRUE));
		model.addRow(new TestRow("Tristate", Tristate.UNDEFINED));
		model.addRow(new TestRow("Date", new Date()));
		model.addRow(new TestRow("byte[]", new byte[]{1, 2, 3, 4}));

		SortableTable table=new SortableTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.putClientProperty(SortableTable.ALTERNATE_ROW_BACKGROUND, Color.ORANGE);
		table.putClientProperty(SortableTable.NO_QUICK_EDITING, Boolean.TRUE);
		table.getTableHeader().addMouseListener(new TableHeaderMouseListener());
		TableUtils.sizeColumnsToFit(table, true, true);

		JFrame frame=new JFrame();
		frame.setContentPane(new JScrollPane(table));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static class TestModel extends SortableTableModel
	{
		private ObjectStyle[] styles;
		private RowNumberRenderer rowNumberRenderer;

		public TestModel()
		{
			rowNumberRenderer=new RowNumberRenderer();
			styles=new ObjectStyle[]
			{
				null,
				null,
				new ObjectStyle(null, Color.GREEN),
				new ObjectStyle(Color.RED, null),
				new ObjectStyle(UIManager.getFont("Label.font").deriveFont(Font.BOLD)),
				new ObjectStyle(new StrikeThroughDecorator(Color.RED)),
				new ObjectStyle(Color.BLUE, Color.LIGHT_GRAY, new StrikeThroughDecorator())
			};
		}

		public int getColumnCount()
		{
			return styles.length+1;
		}

		public TableCellRenderer getCellRenderer(int row, int col)
		{
			if (col==0) return rowNumberRenderer;
			return super.getCellRenderer(row, col);
		}

		public ObjectStyle getCellStyle(int row, int col)
		{
			if (col>0) return styles[col-1];
			return null;
		}

		public Class getCellClass(int row, int col)
		{
			if (col==0) return Integer.class;
			return super.getCellClass(row, col);
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex==0) return new Integer(rowIndex+1);
			return super.getValueAt(rowIndex, columnIndex);
		}
	}

	private static class TestRow extends SortableTableRow
	{
		private String name;
		private Class objectClass;

		public TestRow(String name, Object userObject)
		{
			super(userObject);
			objectClass=userObject.getClass();
			this.name=name;
		}

		public Class getCellClass(int col)
		{
			return objectClass;
		}

		public Object getDisplayValue(int column)
		{
			if (column==1) return name;
			return getUserObject();
		}

		public boolean isEditable(int column)
		{
			return column>1;
		}

		public int setValue(Object value, int col)
		{
			if (col>1)
			{
				setUserObject(value);
				return SortableTableModel.ROW_UPDATE;
			}
			return SortableTableModel.NO_UPDATE;
		}
	}

	private static class TableHeaderMouseListener extends MouseAdapter
	{
		public void mouseClicked(final MouseEvent mouseEvent)
		{
			if (mouseEvent.getButton()==MouseEvent.BUTTON3)
			{
				JTableHeader tableHeader=(JTableHeader)mouseEvent.getSource();
				final SortableTable table=(SortableTable)tableHeader.getTable();
				JPopupMenu menu=new JPopupMenu();
				menu.add(new AbstractAction("Hide Column")
				{
					public void actionPerformed(ActionEvent e)
					{
						int column=table.columnAtPoint(mouseEvent.getPoint());
						TableColumnModel columnModel=table.getColumnModel();
						table.hideColumn(columnModel.getColumn(column));
					}
				});
				JMenu showColumnMenu=new JMenu("Show Column");
				Set hiddenColumns=table.getHiddenColumns();
				for (Iterator it=hiddenColumns.iterator(); it.hasNext();)
				{
					final TableColumn column=(TableColumn)it.next();
					showColumnMenu.add(new AbstractAction(String.valueOf(column.getHeaderValue()))
					{
						public void actionPerformed(ActionEvent e)
						{
							table.showColumn(column);
						}
					});
				}
				menu.add(showColumnMenu);
				menu.show((Component)mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
			}
		}
	}

	private static class RowNumberRenderer extends JButton implements TableCellRenderer
	{
		public RowNumberRenderer()
		{
			setMargin(new Insets(0, 0, 0, 0));
			setHorizontalAlignment(SwingConstants.TRAILING);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			setFont(table.getFont());
			setText(String.valueOf(value));
			return this;
		}
	}
}
