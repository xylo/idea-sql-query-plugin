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
package com.kiwisoft.sqlPlugin.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.actionSystem.*;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseColumn;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.DatabaseTable;
import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.sqlPlugin.QueryPanel;
import com.kiwisoft.sqlPlugin.SQLPluginPanel;
import com.kiwisoft.sqlPlugin.SQLPlugin;
import com.kiwisoft.sqlPlugin.actions.*;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.inspect.InfoNode;
import com.kiwisoft.utils.gui.table.SortableTable;
import com.kiwisoft.utils.gui.table.PropertiesTableModel;
import com.kiwisoft.utils.gui.tree.DynamicTree;
import com.kiwisoft.utils.gui.tree.DynamicTreeNode;
import com.kiwisoft.utils.gui.tree.DynamicTreeNodeRenderer;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.10 $, $Date: 2006/03/24 17:58:46 $
 */
public class BrowserPanel extends JPanel
{
	private static Map instanceMap=new WeakHashMap();

	public static BrowserPanel getInstance(Project project)
	{
		BrowserPanel instance=(BrowserPanel)instanceMap.get(project);
		if (instance==null)
		{
			try
			{
				instance=new BrowserPanel(project);
				instanceMap.put(project, instance);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return instance;
	}

	public static void closeInstance(Project project)
	{
		BrowserPanel browserPanel=(BrowserPanel)instanceMap.get(project);
		if (browserPanel!=null) browserPanel.dispose();
		instanceMap.remove(project);
	}

	private JScrollPane treePane;
	private Map treeMap=new HashMap();
	private Project project;
	private SelectionListener selectionListener;
	private MouseListener mouseListener;
	private PropertiesTableModel tmProperties;
	private ListenerSupport listenerSupport=new ListenerSupport();

	public BrowserPanel(Project project)
	{
		this.project=project;
		initializeComponents();
	}

	private void initializeComponents()
	{
		DynamicTree tree=getTree(null);
		treePane=new JScrollPane(tree);

		tmProperties=new PropertiesTableModel(project, null);
		JTable tblProperties=new SortableTable(tmProperties);

		JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(500);
		splitPane.setOneTouchExpandable(true);
		splitPane.add(treePane);
		splitPane.add(new JScrollPane(tblProperties));
		splitPane.setPreferredSize(new Dimension(500, 300));

		DatabaseManager databaseManager=DatabaseManager.getApplicationInstance();

		setLayout(new BorderLayout());
		add(createToolBar(), BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		listenerSupport.installObserver(databaseManager, new DatabaseManagerObserver());

		selectionListener=new SelectionListener();
		mouseListener=new TreeMouseListener();

		setTree(getTree(databaseManager.getCurrentDatabase()));
	}

	private JComponent createToolBar()
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new SelectDatabaseListAction(project, DatabaseManager.getApplicationInstance()));
		actionGroup.add(new SelectGroupListAction(DatabaseManager.getApplicationInstance()));
		actionGroup.add(new PropertiesAction(project));
		actionGroup.addSeparator();
		actionGroup.add(new RefreshAction());
		actionGroup.add(new SaveSnapshotAction(this));
		actionGroup.addSeparator();
		actionGroup.add(new CancelAction());
		actionGroup.addSeparator();
		actionGroup.add(new HelpAction("KiwiSQL.browserWindow"));
		actionGroup.add(new AboutAction(this));
		ActionToolbar actionToolbar=ActionManager.getInstance().createActionToolbar("BrowserPanel", actionGroup, true);
		return actionToolbar.getComponent();
	}

	public void dispose()
	{
		listenerSupport.dispose();
	}

	public void removeNotify()
	{
		DatabaseManager.getApplicationInstance().closeAllConnections();
		super.removeNotify();
	}

	private void showProperties(PropertyHolder propertyHolder)
	{
		tmProperties.setPropertyHolder(propertyHolder);
	}

	private void setTree(DynamicTree tree)
	{
		treePane.setViewportView(tree);
		tree.addTreeSelectionListener(selectionListener);
		tree.addMouseListener(mouseListener);
		updateProperties(tree.getSelectionPath());
	}

	private void updateProperties(TreePath selectionPath)
	{
		if (selectionPath!=null)
		{
			DynamicTreeNode treeNode=(DynamicTreeNode)selectionPath.getLastPathComponent();
			Object userObject=treeNode.getUserObject();
			if (userObject instanceof PropertyHolder)
			{
				showProperties((PropertyHolder)userObject);
				return;
			}
		}
		showProperties(null);
	}

	private class SelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e)
		{
			updateProperties(e.getNewLeadSelectionPath());
		}
	}

	private class TreeMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if ((e.isPopupTrigger() || e.getButton()==MouseEvent.BUTTON3) && e.getClickCount()>0 && e.getSource() instanceof DynamicTree)
			{
				DynamicTree tree=(DynamicTree)e.getSource();

				List insertActions=new ArrayList();
				List selectActions=new ArrayList();
				List copyActions=new ArrayList();

				TreePath[] selectionPaths=tree.getSelectionPaths();
				if (selectionPaths!=null && selectionPaths.length>0)
				{
					List tables=new ArrayList();
					ListMap columnTables=new ListMap();
					boolean prependSchema=false;

					for (int i=0; i<selectionPaths.length; i++)
					{
						TreePath treePath=selectionPaths[i];
						Object node=treePath.getLastPathComponent();
						if (node instanceof DynamicTreeNode)
						{
							DynamicTreeNode treeNode=(DynamicTreeNode)node;
							Object userObject=treeNode.getUserObject();
							if (userObject instanceof DatabaseTable)
							{
								if (treeNode.getParent(2) instanceof SchemaNode) prependSchema=true;
								tables.add(userObject);
							}
							else if (userObject instanceof DatabaseColumn)
							{
								if (treeNode.getParent(4) instanceof SchemaNode) prependSchema=true;
								DatabaseColumn column=(DatabaseColumn)userObject;
								columnTables.add(column.getTable(), column);
							}
						}
					}

					if (tables.size()==1 && columnTables.size()==0)
					{
						DatabaseTable table=(DatabaseTable)tables.get(0);
						String tableName=table.getTableName();
						if (prependSchema) tableName=table.getSchema().getSchemaName()+"."+tableName;

						copyActions.add(new CopyTextAction(tableName));

						insertActions.add(new InsertTextAction(tableName));
						insertActions.add("-");
						insertActions.add(new InsertTextAction("select * from "+tableName));
						insertActions.add(new InsertTextAction("delete from "+tableName));

						selectActions.add(new ExecuteAction("select * from "+tableName, true));
						selectActions.add(new ExecuteAction("delete from "+tableName, false));
					}
					else if (tables.size()==0 && columnTables.size()==1)
					{
						DatabaseTable table=(DatabaseTable)columnTables.keySet().iterator().next();
						String tableName=table.getTableName();
						if (prependSchema) tableName=table.getSchema().getSchemaName()+"."+tableName;

						List columns=columnTables.get(table);
						String columnNames=StringUtils.enumerate(columns, ", ");

						copyActions.add(new CopyTextAction(columnNames));
						insertActions.add(new InsertTextAction(columnNames));
						insertActions.add("-");
						insertActions.add(new InsertTextAction("select "+columnNames+" from "+tableName));
						selectActions.add(new ExecuteAction("select "+columnNames+" from "+tableName, true));
						if (columns.size()==1)
						{
							insertActions.add(new InsertTextAction("select * from "+tableName+" where "+columnNames+"="));
							selectActions.add(new ExecuteAction("select * from "+tableName+" where "+columnNames+"=", false));
						}
					}
				}
				e.consume();
				JPopupMenu popup=null;
				popup=addActions(popup, "Copy", copyActions);
				popup=addActions(popup, "Insert", insertActions);
				popup=addActions(popup, "Execute", selectActions);
				if (popup!=null) popup.show(tree, e.getX(), e.getY());
			}
		}

		private JPopupMenu addActions(JPopupMenu popup, String title, List actions)
		{
			if (!actions.isEmpty())
			{
				JMenu menuInsert=new JMenu(title);
				for (Iterator it=actions.iterator(); it.hasNext();)
				{
					Object item=it.next();
					if (item instanceof Action)
						menuInsert.add((Action)item);
					else
						menuInsert.addSeparator();
				}

				if (popup==null) popup=new JPopupMenu();
				popup.add(menuInsert);
			}
			return popup;
		}
	}

	private class CopyTextAction extends AbstractAction
	{
		private String text;

		public CopyTextAction(String text)
		{
			super(text);
			this.text=text;
		}

		public void actionPerformed(ActionEvent e)
		{
			Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection=new StringSelection(text);
			clipboard.setContents(stringSelection, stringSelection);
		}
	}

	private class InsertTextAction extends AbstractAction
	{
		private String text;

		public InsertTextAction(String text)
		{
			super(text);
			this.text=text;
		}

		public void actionPerformed(ActionEvent e)
		{
			SQLPluginPanel mainPanel=SQLPluginPanel.getInstance(project, false);
			if (mainPanel!=null)
			{
				QueryPanel queryPanel=mainPanel.getActiveQueryPanel();
				if (queryPanel!=null)
				{
					if (project!=null)
					{
						ToolWindowManager manager=ToolWindowManager.getInstance(project);
						ToolWindow console=manager.getToolWindow(SQLPlugin.SQL_TOOL_WINDOW);
						if (console!=null)
						{
							console.show(null);
							console.activate(null);
						}
					}
					else
						mainPanel.getTopLevelAncestor().setVisible(true);
					queryPanel.insertString(text);
				}
			}
		}
	}

	private class ExecuteAction extends AbstractAction
	{
		private String text;
		private boolean execute;

		public ExecuteAction(String text, boolean execute)
		{
			super(text);
			this.text=text;
			this.execute=execute;
		}

		public void actionPerformed(ActionEvent e)
		{
			SQLPluginPanel mainPanel=SQLPluginPanel.getInstance(project, false);
			if (mainPanel!=null)
			{
				if (project!=null)
				{
					ToolWindowManager manager=ToolWindowManager.getInstance(project);
					ToolWindow console=manager.getToolWindow(SQLPlugin.SQL_TOOL_WINDOW);
					if (console!=null)
					{
						console.show(null);
						console.activate(null);
					}
				}
				else
					mainPanel.getTopLevelAncestor().setVisible(true);
				mainPanel.createQuery(text, execute);
			}
		}
	}

	private DynamicTree getTree(Database database)
	{
		DynamicTree tree=(DynamicTree)treeMap.get(database);
		if (tree==null)
		{
			tree=new DynamicTree();
			tree.setCellRenderer(new DynamicTreeNodeRenderer());
			DynamicTreeNode rootNode;
			if (database!=null)
				rootNode=new DatabaseNode(project, database, null);
			else
				rootNode=new InfoNode("No database selected", InfoNode.INFO, null);
			DefaultTreeModel treeModel=new DefaultTreeModel(rootNode);
			rootNode.setTreeModel(treeModel);
			tree.setModel(treeModel);
			treeMap.put(database, tree);
		}
		return tree;
	}

	private void updateTree()
	{
		Database current=DatabaseManager.getApplicationInstance().getCurrentDatabase();
		setTree(getTree(current));
	}

	private class DatabaseManagerObserver implements Observer
	{
		public void update(Observable o, Object arg)
		{
			NotifyObject note=(NotifyObject)arg;
			if ("current database changed".equals(note.getArgument(0)))
			{
				updateTree();
			}
		}
	}

	private class RefreshAction extends AnAction
	{
		public RefreshAction()
		{
			super("Refresh");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.REFRESH));
		}

		public void actionPerformed(AnActionEvent e)
		{
			Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			if (database!=null)
			{
				DynamicTree tree=(DynamicTree)treeMap.get(database);
				if (tree!=null)
				{
					tree.removeTreeSelectionListener(selectionListener);
					tree.removeMouseListener(mouseListener);
				}
				treeMap.remove(database);
				setTree(getTree(database));
			}
		}
	}

	private class CancelAction extends AnAction
	{
		public CancelAction()
		{
			super("Close browser");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.CANCEL));
			getTemplatePresentation().setDescription("Close this tool window.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			closeInstance(project);
			ToolWindowManager toolWindowManager=ToolWindowManager.getInstance(project);
			toolWindowManager.unregisterToolWindow("SQL Schema");
		}
	}
}
