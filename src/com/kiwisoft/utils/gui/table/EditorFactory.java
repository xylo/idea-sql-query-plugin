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

import java.awt.Component;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.sql.Timestamp;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import com.kiwisoft.utils.DoubleKeyMap;
import com.kiwisoft.utils.Tristate;
import com.kiwisoft.utils.format.*;
import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.lookup.DialogLookup;
import com.kiwisoft.utils.gui.lookup.Lookup;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.8 $, $Date: 2006/03/24 18:58:21 $
 */
public class EditorFactory
{
	public static final String DEFAULT="Default";

	private static EditorFactory instance;

	public synchronized static EditorFactory getInstance()
	{
		if (instance==null) instance=new EditorFactory();
		return instance;
	}

	private Map defaultEditors=new HashMap();
	private DoubleKeyMap editors=new DoubleKeyMap();
	private DoubleKeyMap styleEditors=new DoubleKeyMap();

	private EditorFactory()
	{
		defaultEditors.put(String.class, new TableTextEditor(String.class, new DefaultStringFormat(ObjectFormat.DEFAULT)));

		DefaultNumberFormat numberFormat=new DefaultNumberFormat(ObjectFormat.DEFAULT);
		defaultEditors.put(Byte.class, new DefaultObjectEditor(Byte.class, numberFormat));
		defaultEditors.put(Short.class, new DefaultObjectEditor(Short.class, numberFormat));
		defaultEditors.put(Integer.class, new DefaultObjectEditor(Integer.class, numberFormat));
		defaultEditors.put(Long.class, new DefaultObjectEditor(Long.class, numberFormat));
		defaultEditors.put(BigInteger.class, new DefaultObjectEditor(BigInteger.class, numberFormat));
		defaultEditors.put(Float.class, new DefaultObjectEditor(Float.class, numberFormat));
		defaultEditors.put(Double.class, new DefaultObjectEditor(Double.class, numberFormat));
		defaultEditors.put(BigDecimal.class, new DefaultObjectEditor(BigDecimal.class, numberFormat));

		defaultEditors.put(Boolean.class, new BooleanObjectEditor());
		defaultEditors.put(Tristate.class, new TristateObjectEditor(false));

		defaultEditors.put(Date.class, new TableDateEditor(Date.class, new DefaultDateFormat(ObjectFormat.DEFAULT)));
		defaultEditors.put(Timestamp.class, new TableDateEditor(Timestamp.class, new DefaultDateFormat(ObjectFormat.DEFAULT)));
		defaultEditors.put(java.sql.Date.class, new TableDateEditor(java.sql.Date.class, new DefaultDateFormat(ObjectFormat.DEFAULT)));

		editors.put(Boolean.class, "Tristate", new TristateObjectEditor(true));
	}

	public ObjectEditor getEditor(Class aClass, String variant)
	{
		if (variant==null || variant.equals(ObjectFormat.DEFAULT))
		{
			ObjectEditor editor=null;
			if (defaultEditors.containsKey(aClass))
			{
				editor=(ObjectEditor)defaultEditors.get(aClass);
			}
			else
			{
				for (Iterator it=FormatManager.getInstance().getFormats().iterator(); it.hasNext();)
				{
					ObjectFormat format=(ObjectFormat)it.next();
					if ((format.getName()==null || ObjectFormat.DEFAULT.equals(format.getName())) && format.canParse(aClass))
					{
						if (format instanceof BooleanFormat)
							editor=new BooleanObjectEditor(aClass, (BooleanFormat)format);
						else
							editor=new DefaultObjectEditor(aClass, (TextFormat)format);
						break;
					}
				}
				defaultEditors.put(aClass, editor);
			}
			if (editor!=null) return editor.cloneEditor();
			return null;
		}
		else
		{
			ObjectEditor editor=null;
			if (editors.containsKey(aClass, variant))
			{
				editor=(ObjectEditor)editors.get(aClass, variant);
			}
			else
			{
				for (Iterator it=FormatManager.getInstance().getFormats().iterator(); it.hasNext();)
				{
					ObjectFormat format=(ObjectFormat)it.next();
					if (variant.equals(format.getName()) && format.canParse(aClass))
					{
						if (format instanceof BooleanFormat)
							editor=new BooleanObjectEditor(aClass, (BooleanFormat)format);
						else if (Date.class.isAssignableFrom(aClass))
							editor=new TableDateEditor(aClass, (TextFormat)format);
						else
							editor=new DefaultObjectEditor(aClass, (TextFormat)format);
						break;
					}
				}
				if (editor==null) editor=getEditor(aClass, null);
				editors.put(aClass, variant, editor);
			}
			if (editor!=null) return editor.cloneEditor();
			return null;
		}
	}

	public TableCellEditor getEditor(Class aClass, String variant, ObjectStyle style)
	{
		ObjectEditor editor=getEditor(aClass, variant);
		if (editor==null) return editor;
		return wrapEditor(editor, style, true);
	}

	public TableCellEditor wrapEditor(ObjectEditor editor, ObjectStyle style, boolean cache)
	{
		if (style==null) return editor;
		StyleEditor styleEditor=(StyleEditor)styleEditors.get(editor, style);
		if (styleEditor==null)
		{
			styleEditor=new StyleEditor(editor, style);
			if (cache) styleEditors.put(editor, style, styleEditor);
		}
		else styleEditor=styleEditor.cloneEditor();
		return styleEditor;
	}

	public void setDefaultEditor(Class aClass, ObjectEditor editor)
	{
		defaultEditors.put(aClass, editor);
	}

	public void setDefaultEditor(Class aClass, Lookup lookup)
	{
		defaultEditors.put(aClass, new TableLookupEditor(lookup));
	}

	public void setEditor(Class aClass, String variant, ObjectEditor editor)
	{
		editors.put(aClass, variant, editor);
	}

	public void setEditor(Class aClass, String variant, DialogLookup lookup)
	{
		editors.put(aClass, variant, new TableDialogLookupEditor(lookup));
	}

	static class StyleEditor implements TableCellEditor
	{
		private ObjectEditor editor;
		private ObjectStyle style;

		private StyleEditor(ObjectEditor editor, ObjectStyle style)
		{
			this.editor=editor.cloneEditor();
			this.style=style;
		}

		void setQuickEditable(boolean editable)
		{
			editor.setQuickEditable(editable);
		}

		public void removeCellEditorListener(CellEditorListener l)
		{
			editor.removeCellEditorListener(l);
		}

		public boolean isCellEditable(EventObject anEvent)
		{
			return editor.isCellEditable(anEvent);
		}

		public void addCellEditorListener(CellEditorListener l)
		{
			editor.addCellEditorListener(l);
		}

		public Object getCellEditorValue()
		{
			return editor.getCellEditorValue();
		}

		public void cancelCellEditing()
		{
			editor.cancelCellEditing();
		}

		public boolean shouldSelectCell(EventObject anEvent)
		{
			return editor.shouldSelectCell(anEvent);
		}

		public boolean stopCellEditing()
		{
			return editor.stopCellEditing();
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			if (style.getForeground()!=null)
				editor.setForeground(style.getForeground());
			if (style.getBackground()!=null)
				editor.setBackground(style.getBackground());
			return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		public StyleEditor cloneEditor()
		{
			return new StyleEditor(editor, style);
		}
	}

}
