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
package com.kiwisoft.sqlPlugin;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.JTableHeader;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import com.kiwisoft.db.BLOBWrapper;
import com.kiwisoft.db.CLOBWrapper;
import com.kiwisoft.db.ColumnData;
import com.kiwisoft.db.QueryManager;
import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.db.sql.SystemStatement;
import com.kiwisoft.sqlPlugin.actions.ManageColumnsAction;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.utils.SetMap;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.IOUtils;
import com.kiwisoft.utils.gui.EmptyAction;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.utils.format.ObjectFormat;
import com.kiwisoft.utils.gui.*;
import com.kiwisoft.utils.gui.inspect.InspectorDialog;
import com.kiwisoft.utils.gui.table.RendererFactory;
import com.kiwisoft.utils.gui.table.SortableTable;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.17 $, $Date: 2006/03/24 18:12:24 $
 */
public class ResultSetPanel extends JPanel
{
	private static int idSequence=1;

	private SortableTable tblResult;
	private QueryPanel queryPanel;
	private Project project;
	private AutoRefreshThread autoRefreshThread;
	private int id;
	private JScrollPane scrlResult;

	public ResultSetPanel(Project project, QueryPanel queryPanel, ResultSetTableModel tableModel, String statusText)
	{
		id=idSequence++;
		this.project=project;
		this.queryPanel=queryPanel;
		SQLPluginAppConfig appConfig=SQLPluginAppConfig.getInstance();

		JLabel lblResult=new JLabel(statusText);

		tblResult=new SortableTable(tableModel);
		if (appConfig.isUseAlternateRowColors())
		{
			tblResult.putClientProperty(SortableTable.ALTERNATE_ROW_BACKGROUND, appConfig.getAlternateRowBackground());
			tblResult.putClientProperty(SortableTable.ALTERNATE_ROW_FOREGROUND, appConfig.getAlternateRowForeground());
		}
		tblResult.putClientProperty(SortableTable.NO_QUICK_EDITING, Boolean.TRUE);
		tblResult.setShowGrid(appConfig.isShowGrid());
		tblResult.putClientProperty(SortableTable.NULL_STRING, appConfig.getNullString());
		tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblResult.setCellSelectionEnabled(true);
		TableMouseListener tableMouseListener=new TableMouseListener();
		tblResult.addMouseListener(tableMouseListener);
		tblResult.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
		tblResult.getActionMap().put("select", tableMouseListener);
		tblResult.getTableHeader().addMouseListener(tableMouseListener);
		ResultTableConfiguration tableConfiguration=tableModel.getTableConfiguration();
		tblResult.initializeColumns(tableConfiguration);
		if (!tableConfiguration.isSupportWidths())
			tblResult.sizeColumnsToFit(appConfig.isResizeColumnsToHeader(), appConfig.isResizeColumnsToContent());
		tableConfiguration.setInitalized(true);

		scrlResult=new JScrollPane(tblResult);
		scrlResult.addMouseListener(tableMouseListener);

		setLayout(new BorderLayout());
		add(scrlResult, BorderLayout.CENTER);
		add(lblResult, BorderLayout.SOUTH);
	}

	public int getId()
	{
		return id;
	}

	public void removeNotify()
	{
		if (autoRefreshThread!=null && autoRefreshThread.isAlive()) autoRefreshThread.setStopped(true);
		super.removeNotify();
	}

	private DefaultActionGroup createContextActions(int[] rows, int[] columns)
	{
		ResultSetTableModel tableModel=(ResultSetTableModel)tblResult.getModel();

		int row=rows!=null && rows.length==1 ? rows[0] : -1;
		int column=columns!=null && columns.length==1 ? columns[0] : -1;

		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new SetPrimaryKeyAction(column));
		actionGroup.addSeparator();

		if (column>=0)
		{
			ColumnInfo columnInfo=tableModel.getColumnInfo(column);
			Class type=columnInfo.getType();
			String currentVariant=tableModel.getColumnFormat(column);
			if (currentVariant==null) currentVariant=ObjectFormat.DEFAULT;
			SetMap variants=RendererFactory.getInstance().getVariants(type);

			if (!variants.isEmpty())
			{
				DefaultActionGroup formatActions=new DefaultActionGroup("Format", true);
				formatActions.add(new SetFormatAction(column, currentVariant, ObjectFormat.DEFAULT));
				variants.remove(null, ObjectFormat.DEFAULT);

				formatActions.addSeparator();

				for (Iterator it=new TreeSet(variants.remove(null)).iterator(); it.hasNext();)
				{
					formatActions.add(new SetFormatAction(column, currentVariant, (String)it.next()));
				}
				for (Iterator itGroup=new TreeSet(variants.keySet()).iterator(); itGroup.hasNext();)
				{
					String group=(String)itGroup.next();
					DefaultActionGroup groupActions=new DefaultActionGroup(group, true);
					for (Iterator it=new TreeSet(variants.get(group)).iterator(); it.hasNext();)
					{
						groupActions.add(new SetFormatAction(column, currentVariant, (String)it.next()));
					}
					formatActions.add(groupActions);
				}

				actionGroup.add(formatActions);

				Font currentFont=columnInfo.getFont();
				if (currentFont==null) currentFont=tblResult.getFont();
				String currentFontFamily=currentFont!=null ? currentFont.getFamily() : null;
				actionGroup.add(new SetFontAction(column, currentFontFamily));
			}
			actionGroup.add(new ManageColumnsAction(project, tblResult));
			actionGroup.addSeparator();
		}

		if (row>=0)
		{
			actionGroup.add(new ShowReferencedRowAction(row, column));
			if (column>=0)
			{
				ColumnInfo info=tableModel.getColumnInfo(column);
				DefaultActionGroup subActionGroup=new DefaultActionGroup("Show referencing rows", true);

				Set actions=new TreeSet(new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						String name1=((AnAction)o1).getTemplatePresentation().getText();
						String name2=((AnAction)o2).getTemplatePresentation().getText();
						return name1.compareToIgnoreCase(name2);
					}
				});
				if (info.isExportedKey())
				{
					Object value=tableModel.getValueAt(row, column);
					for (Iterator it=info.getExportedKeys().iterator(); it.hasNext();)
					{
						ColumnData columnData=(ColumnData)it.next();
						actions.add(new ShowReferencingRowsAction(info, value, columnData));
					}
				}
				if (actions.isEmpty()) subActionGroup.add(new EmptyAction());
				else
				{
					for (Iterator it=actions.iterator(); it.hasNext();) subActionGroup.add((AnAction)it.next());
				}
				actionGroup.add(subActionGroup);
			}
		}
		if (column>=0)
		{
			actionGroup.add(new ShowReferencedTableAction(column));
			ColumnInfo info=tableModel.getColumnInfo(column);
			DefaultActionGroup subActions=new DefaultActionGroup("Show referencing tables", true);
			if (info.isExportedKey())
			{
				Set tables=new TreeSet();
				for (Iterator it=info.getExportedKeys().iterator(); it.hasNext();)
				{
					ColumnData columnData=(ColumnData)it.next();
					String tableName="";
					if (!StringUtils.isEmpty(columnData.getSchema()) && !StringUtils.equal(columnData.getSchema(), info.getSchemaName()))
						tableName=tableName+columnData.getSchema()+".";
					tableName=tableName+columnData.getTable();
					tables.add(tableName);
				}
				for (Iterator it=tables.iterator(); it.hasNext();)
				{
					String tableName=(String)it.next();
					subActions.add(new ShowReferencingTablesAction(tableName));
				}
			}
			if (subActions.getChildrenCount()==0) subActions.add(new EmptyAction());
			actionGroup.add(subActions);
		}
		actionGroup.addSeparator();

		actionGroup.add(new DeleteRowAction(row));
		actionGroup.addSeparator();

		actionGroup.add(new CopyToClipboardAction(rows, columns));
		actionGroup.add(new CopyColumnNameAction(column));
		actionGroup.add(new SaveDataAction(row, column));
		if (rows!=null && rows.length>0)
			actionGroup.add(new CellStatisticsAction(rows, columns));
		else
			actionGroup.add(new ColumnStatisticsAction(column));
		actionGroup.add(new ColumnInfoAction(column));
		actionGroup.addSeparator();

		actionGroup.add(new RefreshAction());
		actionGroup.add(new AutoRefreshAction());

		return actionGroup;
	}

	private class TableMouseListener extends AbstractAction implements MouseListener
	{
		public void actionPerformed(ActionEvent e)
		{
			showSelection();
		}

		public void mouseClicked(MouseEvent e)
		{
			if (e.getSource()==tblResult)
			{
				if (e.getClickCount()>1 && e.getButton()==MouseEvent.BUTTON1)
				{
					e.consume();
					showSelection();
				}
				else if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					if (tblResult.getModel() instanceof ResultSetTableModel)
					{
						e.consume();
						int[] selectedRows=tblResult.getSelectedRows();
						int[] selectedColumns=tblResult.getSelectedColumns();
						if (selectedColumns!=null)
						{
							for (int i=0; i<selectedColumns.length; i++)
							{
								selectedColumns[i]=tblResult.convertColumnIndexToModel(selectedColumns[i]);
							}
						}
						DefaultActionGroup actionGroup=createContextActions(selectedRows, selectedColumns);

						ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("ResultTablePopup", actionGroup);
						popupMenu.getComponent().show(tblResult, e.getX(), e.getY());
					}
				}
			}
			else if (e.getSource()==scrlResult)
			{
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					e.consume();
					if (tblResult.getModel() instanceof ResultSetTableModel)
					{
						DefaultActionGroup actionGroup=createContextActions(null, null);
						ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("ResultTablePopup", actionGroup);
						popupMenu.getComponent().show(scrlResult, e.getX(), e.getY());
					}
				}
			}
			else if (e.getSource()==tblResult.getTableHeader())
			{
				if (e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3)
				{
					e.consume();
					if (tblResult.getModel() instanceof ResultSetTableModel)
					{
						JTableHeader tableHeader=(JTableHeader)e.getSource();
						int column=tableHeader.columnAtPoint(e.getPoint());
						if (column>=0)
						{
							column=tblResult.convertColumnIndexToModel(column);
						}
						int[] columns=column>=0 ? new int[]{column} : null;
						DefaultActionGroup actionGroup=createContextActions(null, columns);
						ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("ResultPanel.HeaderPopup", actionGroup);
						popupMenu.getComponent().show(tableHeader, e.getX(), e.getY());
					}
				}
			}
		}

		private void showSelection()
		{
			int column=tblResult.getSelectedColumn();
			int row=tblResult.getSelectedRow();
			if (column>=0 && row>=0)
			{
				DialogWrapper dialog=null;
				Object value=tblResult.getValueAt(row, column);
				if (value instanceof BLOBWrapper)
				{
					BLOBWrapper blobWrapper=(BLOBWrapper)value;
					if (blobWrapper.isLoaded())
					{
						Object object=blobWrapper.getObject();
						if (object!=null) dialog=new InspectorDialog(project, "Binary Large Object", object);
						else
						{
							byte[] data=blobWrapper.getData();
							Action saveAction=new SaveDataDialogAction(data);
							String mimeType=blobWrapper.getMimeType();
							if (isJavaCompatibleImage(mimeType))
							{
								try
								{
									ImageIcon icon=new ImageIcon(data);
									dialog=new ImageDialog(project, "Image Object", icon, saveAction);
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
							if (dialog==null)
							{
								if (mimeType!=null && mimeType.startsWith("text/"))
									dialog=new TextDialog(project, "Text Object", new String(data), saveAction);
								else
									dialog=new ByteArrayDialog(project, "Binary Large Object", data, saveAction);
							}
						}
					}
					else if (blobWrapper.getThrowable()!=null)
					{
						dialog=new TextDialog(project, "Stack Trace", Utils.toString(blobWrapper.getThrowable()));
					}
				}
				else if (value instanceof CLOBWrapper)
				{
					CLOBWrapper clobWrapper=(CLOBWrapper)value;
					if (clobWrapper.isLoaded())
					{
						String text=clobWrapper.getText();
						SaveDataDialogAction saveAction=new SaveDataDialogAction(text.getBytes());
						dialog=new TextDialog(project, "Character Large Object", text, saveAction);
					}
				}
				else if (value instanceof byte[])
				{
					byte[] data=(byte[])value;
					dialog=new ByteArrayDialog(project, "Bytes", data, new SaveDataDialogAction(data));
				}
				else if (value instanceof Throwable)
				{
					dialog=new TextDialog(project, "Stack Trace", Utils.toString((Throwable)value));
				}
				else if (value instanceof String)
				{
					String text=(String)value;
					dialog=new TextDialog(project, "Text", text, new SaveDataDialogAction(text.getBytes()));
				}
				if (dialog!=null) PluginUtils.showDialog(dialog, false, true);
			}
		}

		/** @noinspection RedundantIfStatement*/
		private boolean isJavaCompatibleImage(String mimeType)
		{
			if (mimeType==null) return false;
			if (IOUtils.IMAGE_JPEG.equals(mimeType)) return true;
			if (IOUtils.IMAGE_GIF.equals(mimeType)) return true;
			if (IOUtils.IMAGE_PNG.equals(mimeType)) return true;
			if (IOUtils.IMAGE_XBM.equals(mimeType)) return true;
			return false;
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

	private class SaveDataDialogAction extends AbstractAction
	{
		private byte[] data;

		public SaveDataDialogAction(byte[] data)
		{
			super("Save...");
			putValue(SMALL_ICON, IconManager.getIcon(Icons.SAVE));
			this.data=data;
			setEnabled(data!=null);
		}

		public void actionPerformed(ActionEvent e)
		{
			saveData(data);
		}
	}

	private class SaveDataAction extends AnAction
	{
		private byte[] data;

		public SaveDataAction(int row, int column)
		{
			super("Save to File...");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.SAVE));
			if (row>=0 && column>=0)
			{
				ResultSetTableModel resultModel=(ResultSetTableModel)tblResult.getModel();
				Object value=resultModel.getValueAt(row, column);
				if (value instanceof BLOBWrapper)
				{
					BLOBWrapper blobWrapper=(BLOBWrapper)value;
					if (blobWrapper.isLoaded()) data=blobWrapper.getData();
				}
				else if (value instanceof CLOBWrapper)
				{
					CLOBWrapper clobWrapper=(CLOBWrapper)value;
					if (clobWrapper.isLoaded())
					{
						String text=clobWrapper.getText();
						if (text!=null) data=text.getBytes();
					}
				}
				else if (value instanceof byte[])
				{
					data=(byte[])value;
				}
				else if (value instanceof String)
				{
					data=((String)value).getBytes();
				}
			}
			getTemplatePresentation().setEnabled(data!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			saveData(data);
		}
	}

	private void saveData(byte[] data)
	{
		JFileChooser chooser=new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setDialogTitle("Save data as ...");
		chooser.setMultiSelectionEnabled(false);
		chooser.setCurrentDirectory(Utils.getExistingPath(SQLPluginAppConfig.getInstance().getQueryPath()));
		if (chooser.showDialog(ResultSetPanel.this, "Save As")==JFileChooser.APPROVE_OPTION)
		{
			File file=chooser.getSelectedFile();
			SQLPluginAppConfig.getInstance().setQueryPath(file.getParent());
			try
			{
				if (file.exists())
				{
					ConfirmationDialog dialog=new ConfirmationDialog(project,
																	 "Warning", "File '"+file.getName()+"' exists. Do you want override the file ?",
																	 new String[]{"Yes", "No"}, "No", false);
					PluginUtils.showDialog(dialog, true, true);
					if ("Yes".equals(dialog.getReturnValue())) save(file, data);
				}
				else
				{
					if (file.createNewFile()) save(file, data);
				}
			}
			catch (Exception e1)
			{
				JOptionPane.showMessageDialog(ResultSetPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void save(File file, byte[] data) throws IOException
	{
		DataOutputStream stream=new DataOutputStream(new FileOutputStream(file));
		stream.write(data);
		stream.close();
	}

	private class SetPrimaryKeyAction extends AnAction
	{
		private int col;

		public SetPrimaryKeyAction(int col)
		{
			super("Primary key");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.PRIMARY_KEY));
			this.col=col;
			ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
			if (col>=0 && model.isPrimaryKey(col)) getTemplatePresentation().setText("Unmark column as primary key");
			else getTemplatePresentation().setText("Mark column as primary key");
			if (col<0) getTemplatePresentation().setEnabled(false);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (col>=0)
			{
				if (tblResult.getModel() instanceof ResultSetTableModel)
				{
					ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
					model.setPrimaryKey(col, !model.isPrimaryKey(col));
				}
			}
		}
	}

	private class ShowReferencedRowAction extends AnAction
	{
		private String table;
		private String column;
		private Object value;

		public ShowReferencedRowAction(int row, int column)
		{
			super("Show referenced row");
			if (column>=0 && row>=0)
			{
				ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
				ColumnInfo columnInfo=model.getColumnInfo(column);
				ColumnData foreignKey=columnInfo.getForeignKey();
				if (foreignKey!=null)
				{
					StringBuffer table=new StringBuffer();
					if (!StringUtils.isEmpty(foreignKey.getSchema()) && !StringUtils.equal(foreignKey.getSchema(), columnInfo.getSchemaName()))
						table.append(foreignKey.getSchema()).append(".");
					table.append(foreignKey.getTable());
					this.table=table.toString();
					this.column=foreignKey.getColumn();
					value=model.getValueAt(row, column);
				}
			}
			getTemplatePresentation().setEnabled(table!=null && this.column!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (table!=null && column!=null)
			{
				StringBuffer sql=new StringBuffer("select * from ");
				sql.append(table);
				sql.append(" where ");
				sql.append(column).append("=?/*").append(value).append("*/");
				SystemStatement statement=new SystemStatement(sql.toString(), table, Collections.singletonList(value));
				QueryManager.getInstance(project).createQuery(statement);
			}
		}
	}

	private class ShowReferencedTableAction extends AnAction
	{
		private String table;

		public ShowReferencedTableAction(int col)
		{
			super("Show referenced table");
			if (col>=0 && tblResult.getModel() instanceof ResultSetTableModel)
			{
				ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
				ColumnInfo columnInfo=model.getColumnInfo(col);
				ColumnData foreignKey=columnInfo.getForeignKey();
				if (foreignKey!=null)
				{
					StringBuffer table=new StringBuffer();
					if (!StringUtils.isEmpty(foreignKey.getSchema()) && !StringUtils.equal(foreignKey.getSchema(), columnInfo.getSchemaName()))
						table.append(foreignKey.getSchema()).append(".");
					table.append(foreignKey.getTable());
					this.table=table.toString();
				}
			}
			getTemplatePresentation().setEnabled(table!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (table!=null)
			{
				StringBuffer sql=new StringBuffer("select * from ");
				sql.append(table);
				SystemStatement statement=new SystemStatement(sql.toString(), table, Collections.EMPTY_LIST);
				QueryManager.getInstance(project).createQuery(statement);
			}
		}
	}

	private class ShowReferencingRowsAction extends AnAction
	{
		private Object value;
		private String columnName;
		private String tableName;

		public ShowReferencingRowsAction(ColumnInfo columnInfo, Object value, ColumnData exportedColumn)
		{
			tableName="";
			if (!StringUtils.isEmpty(exportedColumn.getSchema()) && !StringUtils.equal(exportedColumn.getSchema(), columnInfo.getSchemaName()))
				tableName=tableName+exportedColumn.getSchema()+".";
			tableName=tableName+exportedColumn.getTable();
			columnName=exportedColumn.getColumn();
			getTemplatePresentation().setText(tableName+"."+columnName, false);
			this.value=value;
		}

		public void actionPerformed(AnActionEvent e)
		{
			StringBuffer sql=new StringBuffer("select * from ");
			sql.append(tableName);
			sql.append(" where ");
			sql.append(columnName).append("=?/*").append(value).append("*/");
			SystemStatement statement=new SystemStatement(sql.toString(), tableName, Collections.singletonList(value));
			QueryManager.getInstance(project).createQuery(statement);
		}
	}

	private class ShowReferencingTablesAction extends AnAction
	{
		private String tableName;

		public ShowReferencingTablesAction(String tableName)
		{
			getTemplatePresentation().setText(tableName, false);
			this.tableName=tableName;
		}

		public void actionPerformed(AnActionEvent e)
		{
			StringBuffer sql=new StringBuffer("select * from ");
			sql.append(tableName);
			SystemStatement statement=new SystemStatement(sql.toString(), tableName, Collections.EMPTY_LIST);
			QueryManager.getInstance(project).createQuery(statement);
		}
	}

	private class CopyToClipboardAction extends AnAction
	{
		private int[] rows;
		private int[] cols;

		public CopyToClipboardAction(int[] rows, int[] cols)
		{
			super("Copy to Clipboard");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.COPY));
			this.rows=rows;
			this.cols=cols;
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(rows!=null && rows.length>0 && cols!=null && cols.length>0);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (rows!=null && rows.length>0 && cols!=null && cols.length>0)
			{
				boolean multiline=rows.length>1 && cols.length>1;
				TableModel tableModel=tblResult.getModel();
				StringBuffer buffer=new StringBuffer();
				for (int i=0; i<rows.length; i++)
				{
					int row=rows[i];
					if (multiline && i>0) buffer.append("\n");
					for (int j=0; j<cols.length; j++)
					{
						int col=cols[j];
						Object cellValue=tableModel.getValueAt(row, col);
						int viewColumn=tblResult.convertColumnIndexToView(col);
						TableCellRenderer cellRenderer=tblResult.getCellRenderer(row, viewColumn);
						Component component=cellRenderer.getTableCellRendererComponent(tblResult, cellValue, false, false, row, cols[j]);
						if (component instanceof JLabel) cellValue=((JLabel)component).getText();
						if ((multiline ? j : i+j)>0) buffer.append(", ");
						buffer.append(cellValue);
					}
				}
				Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(buffer.toString()), null);
			}
		}
	}

	private class CopyColumnNameAction extends AnAction
	{
		private String name;

		public CopyColumnNameAction(int column)
		{
			super("Copy Name to Clipboard");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.COPY));
			if (column>=0)
			{
				column=tblResult.convertColumnIndexToView(column);
				name=String.valueOf(tblResult.getColumnModel().getColumn(column).getHeaderValue());
			}
			getTemplatePresentation().setEnabled(name!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(name), null);
		}
	}

	private class ColumnStatisticsAction extends AnAction
	{
		private int column;

		public ColumnStatisticsAction(int column)
		{
			super("Statistics...");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.STATISTIC));
			this.column=column;
			getTemplatePresentation().setEnabled(column>=0);
		}

		public void actionPerformed(AnActionEvent e)
		{
			try
			{
				TableModel model=tblResult.getModel();
				double sum=0;
				double product=1;
				double minimum=Double.NaN;
				double maximum=Double.NaN;
				int rowCount=model.getRowCount();
				List values=new ArrayList();
				for (int row=0; row<rowCount; row++)
				{
					Object object=model.getValueAt(row, column);
					if (object!=null)
					{
						double doubleValue;
						if (object instanceof Number)
							doubleValue=((Number)object).doubleValue();
						else
							doubleValue=Double.parseDouble(object.toString());
						values.add(new Double(doubleValue));
						sum+=doubleValue;
						if (doubleValue<=0)
							product=Double.NaN;
						else
							product*=doubleValue;
						if (Double.isNaN(minimum) || doubleValue<minimum) minimum=doubleValue;
						if (Double.isNaN(maximum) || doubleValue>maximum) maximum=doubleValue;
					}
				}
				double arithmetricMean=Double.NaN;
				double geometricMean=Double.NaN;
				double standardDeviation=Double.NaN;
				double averageDeviation=Double.NaN;
				if (rowCount>0)
				{
					arithmetricMean=sum/rowCount;
					if (!Double.isNaN(product)) geometricMean=Math.pow(product, (double)1/rowCount);
					standardDeviation=0.0;
					averageDeviation=0.0;
					for (Iterator it=values.iterator(); it.hasNext();)
					{
						double value=((Double)it.next()).doubleValue()-arithmetricMean;
						standardDeviation+=value*value;
						averageDeviation+=Math.abs(value);
					}
					standardDeviation=Math.sqrt(standardDeviation/(rowCount-1));
					averageDeviation=averageDeviation/rowCount;
				}

				StatisticsDialog dialog=new StatisticsDialog(project, "Column Statistics");
				dialog.setNumber(rowCount);
				dialog.setArithmetricMean(arithmetricMean);
				dialog.setGeometricMean(geometricMean);
				dialog.setSum(sum);
				dialog.setMinimum(minimum);
				dialog.setMaximum(maximum);
				dialog.setStandardDeviation(standardDeviation);
				dialog.setAverageDeviation(averageDeviation);
				PluginUtils.showDialog(dialog, false, true);
			}
			catch (NumberFormatException e1)
			{
				JOptionPane.showMessageDialog(ResultSetPanel.this, "Column contained values not convertable into a number.", "Sum", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class ColumnInfoAction extends AnAction
	{
		private ColumnInfo columnInfo;

		public ColumnInfoAction(int column)
		{
			super("JDBC Info...");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.STATISTIC));
			if (column>=0)
			{
				ResultSetTableModel resultModel=(ResultSetTableModel)tblResult.getModel();
				columnInfo=resultModel.getColumnInfo(column);
			}
			getTemplatePresentation().setEnabled(columnInfo!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (columnInfo!=null)
			{
				ColumnInfoDialog dialog=new ColumnInfoDialog(project, "Column Info", columnInfo);
				PluginUtils.showDialog(dialog, false, true);
			}
		}
	}

	private class CellStatisticsAction extends AnAction
	{
		private int[] rows;
		private int[] cols;

		public CellStatisticsAction(int[] rows, int[] cols)
		{
			super("Statistics...");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.STATISTIC));
			this.rows=rows;
			this.cols=cols;
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(rows!=null && rows.length>0 && cols!=null && cols.length>0);
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (rows!=null && cols!=null)
			{
				try
				{
					double sum=0;
					double product=1;
					int number=0;
					double minimum=Double.NaN;
					double maximum=Double.NaN;
					List values=new ArrayList();
					for (int i=0; i<rows.length; i++)
					{
						int row=rows[i];
						for (int j=0; j<cols.length; j++)
						{
							int col=cols[j];
							Object object=tblResult.getModel().getValueAt(row, col);
							if (object!=null)
							{
								number++;
								double value;
								if (object instanceof Number)
									value=((Number)object).doubleValue();
								else
									value=Double.parseDouble(object.toString());
								values.add(new Double(value));
								sum+=value;
								if (value<=0)
									product=Double.NaN;
								else
									product*=value;
								if (Double.isNaN(minimum) || value<minimum) minimum=value;
								if (Double.isNaN(maximum) || value>maximum) maximum=value;
							}
						}
					}
					double arithmetricMean=Double.NaN;
					double geometricMean=Double.NaN;
					double standardDeviation=Double.NaN;
					double averageDeviation=Double.NaN;
					if (number>0)
					{
						arithmetricMean=sum/number;
						if (!Double.isNaN(product)) geometricMean=Math.pow(product, (double)1/number);
						standardDeviation=0.0;
						averageDeviation=0.0;
						for (Iterator it=values.iterator(); it.hasNext();)
						{
							double value=((Double)it.next()).doubleValue()-arithmetricMean;
							standardDeviation+=value*value;
							averageDeviation+=Math.abs(value);
						}
						standardDeviation=Math.sqrt(standardDeviation/(number-1));
						averageDeviation=averageDeviation/number;
					}

					StatisticsDialog dialog=new StatisticsDialog(project, "Cell Statistics");
					dialog.setNumber(number);
					dialog.setArithmetricMean(arithmetricMean);
					dialog.setGeometricMean(geometricMean);
					dialog.setSum(sum);
					dialog.setMinimum(minimum);
					dialog.setMaximum(maximum);
					dialog.setStandardDeviation(standardDeviation);
					dialog.setAverageDeviation(averageDeviation);
					PluginUtils.showDialog(dialog, false, true);
				}
				catch (NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(ResultSetPanel.this, "Selection contained values not convertable into a number.", "Sum", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class SetFormatAction extends ToggleAction
	{
		private int column;
		private String currentFormat;
		private String format;

		public SetFormatAction(int column, String currentFormat, String format)
		{
			super(format);
			this.column=column;
			this.currentFormat=currentFormat;
			this.format=format;
		}

		public boolean isSelected(AnActionEvent event)
		{
			return StringUtils.equal(currentFormat, format);
		}

		public void setSelected(AnActionEvent event, boolean b)
		{
			if (b && tblResult.getModel() instanceof ResultSetTableModel)
			{
				ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
				model.setColumnFormat(column, format);
				tblResult.repaint();
			}
		}
	}

	private class SetFontAction extends AnAction
	{
		private int column;
		private String currentFont;

		public SetFontAction(int column, String currentFont)
		{
			super("Set Font...");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.FONT));
			this.column=column;
			this.currentFont=currentFont;
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (tblResult.getModel() instanceof ResultSetTableModel)
			{
				ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
				FontSelectionDialog dialog=new FontSelectionDialog(project, currentFont);
				PluginUtils.showDialog(dialog, true, true);
				if (dialog.getReturnValue())
				{
					if (dialog.getFont()!=null)
						model.setColumnFont(column, new Font(dialog.getFont(), Font.PLAIN, tblResult.getFont().getSize()));
					else
						model.setColumnFont(column, null);
					tblResult.repaint();
				}
			}
		}
	}

	private class RefreshAction extends AnAction
	{
		public RefreshAction()
		{
			super("Refresh Data");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REFRESH));
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			SQLStatement statement=((ResultSetTableModel)tblResult.getModel()).getStatement();
			event.getPresentation().setEnabled(statement.getResultCount()==1);
		}

		public void actionPerformed(AnActionEvent e)
		{
			try
			{
				SQLStatement statement=((ResultSetTableModel)tblResult.getModel()).getStatement();
				queryPanel.executeStatement(statement, ResultSetPanel.this);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	private class AutoRefreshAction extends AnAction
	{
		public AutoRefreshAction()
		{
			super("Auto-Refresh Data");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REFRESH));
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			if (autoRefreshThread!=null && autoRefreshThread.isAlive())
			{
				event.getPresentation().setText("Stop Auto-Refresh");
			}
			else
			{
				SQLStatement statement=((ResultSetTableModel)tblResult.getModel()).getStatement();
				event.getPresentation().setEnabled(statement.getResultCount()==1);
			}
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (autoRefreshThread==null || !autoRefreshThread.isAlive())
			{
				String value=(String)JOptionPane.showInputDialog(ResultSetPanel.this,
																 "Time between 2 executions (s):", "Interval", JOptionPane.QUESTION_MESSAGE, null, null, "1");
				if (value!=null)
				{
					try
					{
						final int interval=Integer.parseInt(value);
						if (interval<=0) throw new NumberFormatException();
						autoRefreshThread=new AutoRefreshThread(interval*1000);
						autoRefreshThread.start();
					}
					catch (NumberFormatException e1)
					{
						JOptionPane.showMessageDialog(ResultSetPanel.this, "Invalid interval.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else if (autoRefreshThread!=null)
			{
				autoRefreshThread.setStopped(true);
			}
		}
	}

	private class DeleteRowAction extends AnAction
	{
		private ResultSetTableModel.TableRow row;

		public DeleteRowAction(int row)
		{
			super("Delete Row");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REMOVE));
			if (row>=0)
			{
				ResultSetTableModel model=(ResultSetTableModel)tblResult.getModel();
				this.row=(ResultSetTableModel.TableRow)model.getRow(row);
			}
			getTemplatePresentation().setEnabled(this.row!=null && this.row.isDeletable());
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (row!=null && row.isDeletable())
			{
				row.delete();
				ResultSetTableModel tableModel=(ResultSetTableModel)tblResult.getModel();
				int index=tableModel.indexOfRow(row);
				tableModel.fireTableRowsUpdated(index, index);
			}
		}
	}

	public SortableTable getResultTable()
	{
		return tblResult;
	}

	public void setTableModel(ResultSetTableModel model)
	{
		tblResult.setModel(model);
		ResultTableConfiguration tableConfiguration=model.getTableConfiguration();
		SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();
		tblResult.initializeColumns(tableConfiguration);
		if (!tableConfiguration.isSupportWidths())
			tblResult.sizeColumnsToFit(configuration.isResizeColumnsToHeader(), configuration.isResizeColumnsToContent());
		tableConfiguration.setInitalized(true);
	}

	private class AutoRefreshThread extends Thread
	{
		private boolean stopped;
		private final int interval;
		private boolean interuptable;

		public AutoRefreshThread(int interval)
		{
			this.interval=interval;
		}

		public void setStopped(boolean stopped)
		{
			this.stopped=stopped;
			if (interuptable) interrupt();
		}

		public void run()
		{
			try
			{
				while (!stopped)
				{
					interuptable=false;
					Container topLevelAncestor=ResultSetPanel.this.getTopLevelAncestor();
					if (topLevelAncestor!=null && topLevelAncestor.isVisible())
					{
						SQLStatement statement=((ResultSetTableModel)tblResult.getModel()).getStatement();
						queryPanel.executeStatement(statement, ResultSetPanel.this);
						if (queryPanel.hasErrors()) break;
					}
					interuptable=true;
					try
					{
						sleep(interval);
					}
					catch (InterruptedException e1)
					{
						break;
					}
				}
			}
			catch (Throwable e1)
			{
				e1.printStackTrace();
			}
		}
	}

	public SQLStatement getStatement()
	{
		if (tblResult.getModel() instanceof ResultSetTableModel)
		{
			return ((ResultSetTableModel)tblResult.getModel()).getStatement();
		}
		return null;
	}
}
