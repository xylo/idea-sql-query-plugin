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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.db.Query;
import com.kiwisoft.db.QueryManager;
import com.kiwisoft.sqlPlugin.actions.*;
import com.kiwisoft.sqlPlugin.browser.BrowserPanel;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.sqlPlugin.config.SQLPluginConstants;
import com.kiwisoft.sqlPlugin.templates.StatementTemplate;
import com.kiwisoft.sqlPlugin.templates.StatementTemplateFormat;
import com.kiwisoft.sqlPlugin.templates.ThrowableFormat;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadAction;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.utils.format.ByteArrayFormat;
import com.kiwisoft.utils.format.ObjectFormat;
import com.kiwisoft.utils.gui.ConfirmationDialog;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.gui.IconManager;
import com.kiwisoft.utils.gui.table.RendererFactory;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.11 $, $Date: 2006/03/24 18:15:17 $
 */
public class SQLPluginPanel extends JPanel
{
	private static Map instanceMap=new WeakHashMap();

	public static SQLPluginPanel getInstance(Project project, boolean create)
	{
		SQLPluginPanel instance=(SQLPluginPanel)instanceMap.get(project);
		if (instance==null && create)
		{
			try
			{
				instance=new SQLPluginPanel(project);
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
		SQLPluginPanel instance=(SQLPluginPanel)instanceMap.get(project);
		if (instance!=null) instance.dispose();
		instanceMap.remove(project);
	}

	private JTabbedPane tabs;
	private Project project;

	private ListenerSupport listenerSupport=new ListenerSupport();

	private SQLPluginPanel(Project project)
	{
		RendererFactory rendererFactory=RendererFactory.getInstance();
		rendererFactory.setRenderer(StatementTemplate.class, new StatementTemplateFormat(ObjectFormat.DEFAULT));
		rendererFactory.setRenderer(byte[].class, new ByteArrayFormat(ObjectFormat.DEFAULT));
		rendererFactory.setRenderer(Throwable.class, new ThrowableFormat(ObjectFormat.DEFAULT));

		this.project=project;
		setLayout(new BorderLayout());

		tabs=new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.BOTTOM);

		add(createToolBar(), BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);

		QueryManager queryList=QueryManager.getInstance(project);
		listenerSupport.installObserver(queryList, new QueryListObserver());

		List queries=queryList.getQueries();
		if (queries.isEmpty())
			queryList.createQuery();
		else
		{
			Iterator it=queries.iterator();
			while (it.hasNext()) addQuery((Query)it.next());
		}

		listenerSupport.installMouseListener(tabs, new TabsMouseListener());
	}

	private JComponent createToolBar()
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new OpenAction());
		actionGroup.add(new SaveAction());
		actionGroup.addSeparator();
		actionGroup.add(new CreateQueryAction());
		actionGroup.add(new CopyQueryAction());
		actionGroup.addSeparator();
		actionGroup.add(new SelectDatabaseListAction(project, DatabaseManager.getApplicationInstance()));
		actionGroup.add(new SelectGroupListAction(DatabaseManager.getApplicationInstance()));
		actionGroup.add(new PropertiesAction(project));
		actionGroup.add(new BrowserAction());
		actionGroup.add(new DataLoadAction(project));
		actionGroup.addSeparator();
		actionGroup.add(new DisconnectAction());
		actionGroup.add(new CommitAction());
		actionGroup.add(new RollbackAction());
		actionGroup.addSeparator();
		actionGroup.add(new HelpAction("KiwiSQL.queryWindow"));
		actionGroup.add(new AboutAction(this));
		actionGroup.addSeparator();
		actionGroup.add(new DonateAction());
		ActionManager actionManager=ActionManager.getInstance();
		ActionToolbar toolbar=actionManager.createActionToolbar("SQLPlugin.MainPanel", actionGroup, true);
		return toolbar.getComponent();
	}

	private Query getSelectedQuery()
	{
		if (tabs!=null)
		{
			QueryPanel queryPanel=(QueryPanel)tabs.getSelectedComponent();
			if (queryPanel!=null) return queryPanel.getQuery();
		}
		return null;
	}

	private QueryPanel getQueryPanel(Query query)
	{
		Component[] panels=tabs.getComponents();
		for (int i=0; i<panels.length; i++)
		{
			if (panels[i] instanceof QueryPanel)
			{
				QueryPanel queryPanel=(QueryPanel)panels[i];
				if (queryPanel.getQuery()==query) return queryPanel;
			}
		}
		return null;
	}

	private void addQuery(Query newQuery)
	{
		final QueryPanel queryPanel=new QueryPanel(project, newQuery);
		tabs.add(getTabName(newQuery), queryPanel);
		tabs.setSelectedComponent(queryPanel);
		if (newQuery.isSystemQuery())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					queryPanel.executeStatement(false);
				}
			});
		}
	}

	public void createQuery(final String text, final boolean execute)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Query query=QueryManager.getInstance(project).createQuery(text);
				if (execute)
				{
					QueryPanel queryPanel=getActiveQueryPanel();
					if (queryPanel.getQuery()==query) queryPanel.executeStatement(false);
				}
			}
		});
	}

	private static String getTabName(Query query)
	{
		return query.getName()+(query.isSaveable() ? "*" : "");
	}

	private class QueryListObserver implements Observer
	{
		public void update(Observable o, Object arg)
		{
			NotifyObject note=(NotifyObject)arg;
			Object type=note.getArgument(0);
			if ("query added".equals(type))
			{
				Query newQuery=(Query)note.getArgument(1);
				addQuery(newQuery);
			}
			else if ("query removed".equals(type))
			{
				Query query=(Query)note.getArgument(1);
				QueryPanel queryPanel=getQueryPanel(query);
				if (queryPanel!=null) tabs.remove(queryPanel);
				if (tabs.getTabCount()==0 && project!=null)
				{
					ToolWindow toolWindow=ToolWindowManager.getInstance(project).getToolWindow(SQLPlugin.SQL_TOOL_WINDOW);
					if (toolWindow!=null) toolWindow.hide(null);
				}
			}
			else if ("query changed".equals(type) || "query saved".equals(type) || "query name changed".equals(type))
			{
				Query query=(Query)note.getArgument(1);
				QueryPanel queryPanel=getQueryPanel(query);
				if (queryPanel!=null)
				{
					int index=tabs.indexOfComponent(queryPanel);
					tabs.setTitleAt(index, getTabName(query));
				}
			}
		}
	}

	public void dispose()
	{
		listenerSupport.dispose();
	}

	public void removeNotify()
	{
		if (!SQLPluginAppConfig.getInstance().isKeepConnectionOpen())
			DatabaseManager.getApplicationInstance().closeAllConnections();
		super.removeNotify();
	}

	public QueryPanel getActiveQueryPanel()
	{
		Component selectedComponent=tabs.getSelectedComponent();
		if (selectedComponent instanceof QueryPanel) return (QueryPanel)selectedComponent;
		return null;
	}

	private class CreateQueryAction extends AnAction
	{
		public CreateQueryAction()
		{
			super("Create Query");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.ADD));
			getTemplatePresentation().setDescription("Open a new empty query.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			QueryManager.getInstance(project).createQuery();
		}
	}

	private class CopyQueryAction extends AnAction
	{
		public CopyQueryAction()
		{
			super("Copy Query");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.COPY));
			getTemplatePresentation().setDescription("Create a new query and copy the statement from the selected one.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			QueryPanel queryPanel=(QueryPanel)tabs.getSelectedComponent();
			if (queryPanel!=null)
			{
				Query query=queryPanel.getQuery();
				if (query!=null)
				{
					QueryManager.getInstance(project).createQuery(query, queryPanel.getQueryText());
				}
			}
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(getSelectedQuery()!=null);
		}
	}

	private class OpenAction extends AnAction
	{
		public OpenAction()
		{
			super("Open");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.OPEN));
		}

		public void actionPerformed(AnActionEvent event)
		{
			JFileChooser chooser=new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setDialogTitle("Load Query");
			chooser.setMultiSelectionEnabled(false);
			chooser.setCurrentDirectory(Utils.getExistingPath(SQLPluginAppConfig.getInstance().getQueryPath()));

			ExtentionFileFilter sqlFileFilter=new ExtentionFileFilter("SQL Sources", new String[]{"sql", "ddl"});
			ExtentionFileFilter txtFileFilter=new ExtentionFileFilter("Text Files", "txt");
			chooser.addChoosableFileFilter(sqlFileFilter);
			chooser.addChoosableFileFilter(txtFileFilter);
			chooser.setFileFilter(sqlFileFilter);

			int res=chooser.showDialog(SQLPluginPanel.this, "Load");
			if (res==JFileChooser.APPROVE_OPTION)
			{
				File file=chooser.getSelectedFile();
				SQLPluginAppConfig.getInstance().setQueryPath(file.getParent());
				if (file.canRead())
				{
					FileInputStream fis=null;
					try
					{
						fis=new FileInputStream(file);
						byte[] buf=new byte[fis.available()];
						fis.read(buf);
						QueryManager.getInstance(project).createQuery(file, new String(buf));
					}
					catch (IOException e1)
					{
						JOptionPane.showMessageDialog(SQLPluginPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
					finally
					{
						if (fis!=null)
						{
							try
							{
								fis.close();
							}
							catch (IOException e1)
							{
								JOptionPane.showMessageDialog(SQLPluginPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(SQLPluginPanel.this, "Can't read file '"+file+"'", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private static File addDefaultExtension(File file, String extension)
	{
		File parent=file.getParentFile();
		String fileName=file.getName();
		int index=fileName.lastIndexOf('.');
		if (index<0)
			return new File(parent, fileName+'.'+extension);
		else
			return file;
	}

	private class SaveAction extends AnAction
	{
		public SaveAction()
		{
			super("Save");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.SAVE));
		}

		public void actionPerformed(AnActionEvent e)
		{
			Query selectedQuery=getSelectedQuery();
			if (selectedQuery!=null)
			{
				JFileChooser chooser=new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setDialogTitle("Save Query as ...");
				chooser.setMultiSelectionEnabled(false);
				if (selectedQuery.getFile()!=null)
					chooser.setSelectedFile(selectedQuery.getFile());
				else
					chooser.setCurrentDirectory(Utils.getExistingPath(SQLPluginAppConfig.getInstance().getQueryPath()));

				ExtentionFileFilter sqlFileFilter=new ExtentionFileFilter("SQL Sources", new String[]{"sql", "ddl", "pgb", "pgh"});
				ExtentionFileFilter txtFileFilter=new ExtentionFileFilter("Text Files", "txt");

				sqlFileFilter.setShowReadOnly(false);
				txtFileFilter.setShowReadOnly(false);

				chooser.addChoosableFileFilter(sqlFileFilter);
				chooser.addChoosableFileFilter(txtFileFilter);
				chooser.setFileFilter(sqlFileFilter);

				int res=chooser.showDialog(SQLPluginPanel.this, "Save As");
				if (res==JFileChooser.APPROVE_OPTION)
				{
					File file=chooser.getSelectedFile();
					file=addDefaultExtension(file, "sql");
					SQLPluginAppConfig.getInstance().setQueryPath(file.getParent());
					try
					{
						if (file.exists())
						{
							ConfirmationDialog dialog=new ConfirmationDialog(project, "Warning",
																			 "File '"+file.getName()+"' exists. Do you want override the file ?",
																			 new String[]{"Yes", "No"}, "No", false);
							PluginUtils.showDialog(dialog, true, true);
							if ("Yes".equals(dialog.getReturnValue())) save(file, selectedQuery);
						}
						else
						{
							if (file.createNewFile()) save(file, selectedQuery);
						}
					}
					catch (Exception e1)
					{
						JOptionPane.showMessageDialog(SQLPluginPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			Query query=getSelectedQuery();
			event.getPresentation().setEnabled(query!=null && query.isSaveable());
		}

		private void save(File file, Query query) throws Exception
		{
			FileWriter fw=null;
			try
			{
				fw=new FileWriter(file);
				fw.write(query.getStatement());
				query.setFile(file);
				query.setChanged(false);
			}
			finally
			{
				if (fw!=null)
				{
					fw.close();
				}
			}
		}
	}

	private class BrowserAction extends AnAction
	{
		public BrowserAction()
		{
			super("Browser");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.BROWSER));
			getTemplatePresentation().setDescription("Show database struture.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (project!=null)
			{
				ToolWindowManager manager=ToolWindowManager.getInstance(project);
				BrowserPanel browserPanel=BrowserPanel.getInstance(project);
				if (browserPanel!=null)
				{
					ToolWindow console=manager.getToolWindow(SQLPlugin.BROWSER_TOOL_WINDOW);
					if (console==null)
					{
						console=manager.registerToolWindow(SQLPlugin.BROWSER_TOOL_WINDOW, browserPanel, ToolWindowAnchor.BOTTOM);
						if (console!=null)
						{
							console.setIcon(IconManager.getIcon(Icons.DATABASE_STRUCTURE));
						}
					}
					if (console!=null)
					{
						console.show(null);
						console.activate(null);
					}
				}
			}
			else
			{
				JFrame frame=new JFrame("SQL Schema");
				frame.getContentPane().add(new BrowserPanel(null));
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setIconImage(IconManager.getIcon(Icons.DATABASE_STRUCTURE).getImage());
				frame.setSize(800, 300);
				frame.setVisible(true);
			}
		}
	}

	private class DisconnectAction extends AnAction
	{
		public DisconnectAction()
		{
			super("Disconnect");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.DISCONNECT));
			getTemplatePresentation().setDescription("Close all connections.");
		}

		public void actionPerformed(AnActionEvent e)
		{
			SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();
			Boolean confirm=configuration.getConfirmation(SQLPluginConstants.CONFIRM_DISCONNECT);
			if (Boolean.TRUE.equals(confirm))
				DatabaseManager.getApplicationInstance().closeAllConnections();
			else
			{
				ConfirmationDialog dialog=new ConfirmationDialog(project,
																 "Question", "Do you really want to close all connections?",
																 new String[]{"Yes", "No"}, "No", true);
				PluginUtils.showDialog(dialog, true, true);
				if ("Yes".equals(dialog.getReturnValue()))
				{
					if (dialog.isDontShowAgain())
						configuration.setConfirmation(SQLPluginConstants.CONFIRM_DISCONNECT, Boolean.TRUE);
					DatabaseManager.getApplicationInstance().closeAllConnections();
				}
			}
		}
	}

	private class CommitAction extends AnAction
	{
		public CommitAction()
		{
			super("Commit");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.COMMIT));
			getTemplatePresentation().setDescription("Commit all changes for the current connection.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			event.getPresentation().setEnabled(database!=null && database.isConnected());
		}

		public void actionPerformed(AnActionEvent e)
		{
			Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			if (database!=null)
			{
				Connection connection=database.getConnection();
				try
				{
					if (connection!=null)
					{
						SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();
						Boolean confirm=configuration.getConfirmation(SQLPluginConstants.CONFIRM_COMMIT);
						if (Boolean.TRUE.equals(confirm))
							connection.commit();
						else
						{
							ConfirmationDialog dialog=new ConfirmationDialog(project,
																			 "Question", "Do you really want to commit the connection?",
																			 new String[]{"Yes", "No"}, "No", true);
							PluginUtils.showDialog(dialog, true, true);
							if ("Yes".equals(dialog.getReturnValue()))
							{
								if (dialog.isDontShowAgain())
									configuration.setConfirmation(SQLPluginConstants.CONFIRM_COMMIT, Boolean.TRUE);
								connection.commit();
							}
						}
					}
					else
					{
						JOptionPane.showMessageDialog(SQLPluginPanel.this, "No connection open.", "Information", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(SQLPluginPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class RollbackAction extends AnAction
	{
		public RollbackAction()
		{
			super("Rollback");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.ROLLBACK));
			getTemplatePresentation().setDescription("Rollback all changes for the current connection.");
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			event.getPresentation().setEnabled(database!=null && database.isConnected());
		}

		public void actionPerformed(AnActionEvent e)
		{
			Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			if (database!=null)
			{
				Connection connection=database.getConnection();
				try
				{
					if (connection!=null)
					{
						SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();
						Boolean confirm=configuration.getConfirmation(SQLPluginConstants.CONFIRM_ROLLBACK);
						if (Boolean.TRUE.equals(confirm))
							connection.rollback();
						else
						{
							ConfirmationDialog dialog=new ConfirmationDialog(project,
																			 "Question", "Do you really want to rollback the connection?",
																			 new String[]{"Yes", "No"}, "No", true);
							PluginUtils.showDialog(dialog, true, true);
							if ("Yes".equals(dialog.getReturnValue()))
							{
								if (dialog.isDontShowAgain())
									configuration.setConfirmation(SQLPluginConstants.CONFIRM_ROLLBACK, Boolean.TRUE);
								connection.rollback();
							}
						}
					}
					else
					{
						JOptionPane.showMessageDialog(SQLPluginPanel.this, "No connection open.", "Information", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(SQLPluginPanel.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class TabsMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if ((e.getModifiers()&MouseEvent.BUTTON3_MASK)!=0)
			{
				int index=tabs.getUI().tabForCoordinate(tabs, e.getX(), e.getY());
				if (index>=0)
				{
					Component component=tabs.getComponentAt(index);
					if (component instanceof QueryPanel)
					{
						QueryPanel panel=(QueryPanel)component;
						DefaultActionGroup actionGroup=new DefaultActionGroup();
						actionGroup.add(new RenameQueryAction(panel.getQuery()));
						ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("SQLPluginPanel.QueryTabPopup", actionGroup);
						popupMenu.getComponent().show(tabs, e.getX(), e.getY()-20);
					}
				}
			}
		}
	}

	private class RenameQueryAction extends AnAction
	{
		private Query query;

		public RenameQueryAction(Query query)
		{
			super("Rename query");
			this.query=query;
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			event.getPresentation().setEnabled(query!=null);
		}

		public void actionPerformed(AnActionEvent e)
		{
			RenameDialog dialog=new RenameDialog(project, "Rename Query", query.getName());
			PluginUtils.showDialog(dialog, true, true);
			String newName=dialog.getReturnValue();
			if (!StringUtils.isEmpty(newName)) query.setName(newName);
		}
	}

}
