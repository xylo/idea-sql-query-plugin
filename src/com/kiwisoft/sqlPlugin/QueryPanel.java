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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.PopupHandler;

import com.kiwisoft.db.*;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.db.export.*;
import com.kiwisoft.db.sql.*;
import com.kiwisoft.sqlPlugin.actions.*;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.sqlPlugin.config.SQLPluginConstants;
import com.kiwisoft.utils.DocumentAdapter;
import com.kiwisoft.utils.NotifyObject;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.utils.text.MessagePane;
import com.kiwisoft.utils.text.SourceTextPane;
import com.kiwisoft.utils.text.SyntaxDefinitionFactory;
import com.kiwisoft.utils.gui.ConfirmationDialog;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.12 $, $Date: 2006/03/24 18:12:22 $
 */
public class QueryPanel extends JPanel
{
	// Data

	private Project project;
	private Query query;
	private Database currentDatabase;
	private int errorCount;
	private ExecutionThread executionThread;

	// Components

	private JTextComponent tfQuery;
	private MessagePane tfResult;
	private JTabbedPane pnlResults;
	private StatusBar pnlStatus;
	private JButton btnAutoCommit;

	// Listeners

	private ConfigurationListener configurationListener;
	private DatabaseObserver databaseObserver;

	// Actions

	private ExecuteAction executeAction;
	private ExecuteAction executeComplexAction;
	private SuspendAction suspendAction;
	private AutoCommitAction autoCommitAction;

	public QueryPanel(Project project, Query query)
	{
		assert query!=null;
		this.project=project;
		this.query=query;
		setLayout(new BorderLayout());

		executeAction=new ExecuteAction(false);
		executeComplexAction=new ExecuteAction(true);
		suspendAction=new SuspendAction();
		autoCommitAction=new AutoCommitAction();

		tfQuery=new SourceTextPane(SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("sql"));

		if (query.getStatement()!=null) tfQuery.setText(query.getStatement());
		tfQuery.getDocument().addDocumentListener(new QueryDocumentListener());
		tfQuery.setEditable(!query.isSystemQuery());
		DefaultActionGroup actionGroup=new DefaultActionGroup();
		actionGroup.add(new SaveTemplateAction(project, tfQuery));
		PopupHandler.installPopupHandler(tfQuery, actionGroup, "QueryTextField", ActionManager.getInstance());

		JScrollPane pnlQuery=new JScrollPane(tfQuery);

		tfResult=new MessagePane();
		tfResult.setFont(tfQuery.getFont());
		tfResult.createBaseStyles();
		tfResult.addHyperlinkListener(new MessagesLinkListener());

		pnlResults=new JTabbedPane();
		pnlResults.setTabPlacement(JTabbedPane.BOTTOM);
		pnlResults.addTab(null, IconManager.getIcon(Icons.OUTPUT_MESSAGES), new JScrollPane(tfResult));
		pnlResults.setToolTipTextAt(0, "<html>Messages</html>");
		pnlResults.setPreferredSize(new Dimension(200, 200));

		btnAutoCommit=new JButton(autoCommitAction);
		btnAutoCommit.setBorder(new BevelBorder(BevelBorder.LOWERED));

		pnlStatus=new StatusBar();
		pnlStatus.setPreferredSize(new Dimension(20, 20));
		pnlStatus.addComponent(btnAutoCommit);

		JSplitPane splitPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setOneTouchExpandable(true);
		splitPane.setTopComponent(pnlQuery);
		splitPane.setBottomComponent(pnlResults);
		splitPane.setDividerLocation(100);

		JPanel pnlMain=new JPanel(new BorderLayout());
		pnlMain.add(splitPane, BorderLayout.CENTER);
		pnlMain.add(pnlStatus, BorderLayout.SOUTH);

		add(createToolbar(), BorderLayout.WEST);
		add(pnlMain, BorderLayout.CENTER);
	}

	private JComponent createToolbar()
	{
		DefaultActionGroup actionGroup=new DefaultActionGroup("SQLPlugin.QueryPanel", false);
		actionGroup.add(executeAction);
		actionGroup.add(executeComplexAction);
		actionGroup.add(suspendAction);
		actionGroup.add(new CancelAction());
		actionGroup.add(new QuickPropertiesAction());
		actionGroup.add(new StartExportAction());
		ActionManager actionManager=ActionManager.getInstance();
		ActionToolbar toolbar=actionManager.createActionToolbar("SQLPlugin.QueryPanel", actionGroup, false);
		return toolbar.getComponent();
	}

	public void addNotify()
	{
		super.addNotify();
		configurationListener=new ConfigurationListener();
		SQLPluginAppConfig.getInstance().addPropertyChangeListener("defaultFormat", configurationListener);
		databaseObserver=new DatabaseObserver();
		DatabaseManager databaseManager=DatabaseManager.getApplicationInstance();
		databaseManager.addObserver(databaseObserver);
		currentDatabase=databaseManager.getCurrentDatabase();
		if (currentDatabase!=null) currentDatabase.addObserver(databaseObserver);
		updateActions();
	}

	public void removeNotify()
	{
		if (configurationListener!=null)
			SQLPluginAppConfig.getInstance().removePropertyChangeListener("defaultFormat", configurationListener);
		if (databaseObserver!=null)
		{
			if (currentDatabase!=null) currentDatabase.deleteObserver(databaseObserver);
			DatabaseManager.getApplicationInstance().deleteObserver(databaseObserver);
		}
		super.removeNotify();
	}

	public Query getQuery()
	{
		return query;
	}

	public String getQueryText()
	{
		String selectedText=tfQuery.getSelectedText();
		if (!StringUtils.isEmpty(selectedText)) return selectedText;
		else return tfQuery.getText();
	}

	private void updateActions()
	{
		Database currentDatabase=DatabaseManager.getApplicationInstance().getCurrentDatabase();
		if (autoCommitAction!=null) autoCommitAction.update(currentDatabase);
		if (btnAutoCommit!=null && currentDatabase!=null)
		{
			Object value=currentDatabase.getProperty(DatabaseDriver.AUTO_COMMIT);
			if (Boolean.TRUE.equals(value)) btnAutoCommit.setForeground(Color.RED.darker());
			else if (Boolean.FALSE.equals(value)) btnAutoCommit.setForeground(Color.GREEN.darker());
			else btnAutoCommit.setForeground(Color.ORANGE.darker());
		}
	}

	private class QueryDocumentListener extends DocumentAdapter
	{
		public void changedUpdate(DocumentEvent e)
		{
			query.setStatement(tfQuery.getText());
			updateActions();
		}
	}

	private class DatabaseObserver implements Observer
	{
		public void update(Observable o, Object arg)
		{
			NotifyObject note=(NotifyObject)arg;
			if (o instanceof DatabaseManager)
			{
				if ("current database changed".equals(note.getArgument(0)))
				{
					if (currentDatabase!=null) currentDatabase.deleteObserver(this);
					currentDatabase=DatabaseManager.getApplicationInstance().getCurrentDatabase();
					if (currentDatabase!=null) currentDatabase.addObserver(this);
					updateActions();
				}
			}
			else if (o instanceof Database)
			{
				if (note.getArgument(0).equals(DatabaseDriver.AUTO_COMMIT.getId()+" changed"))
				{
					updateActions();
				}
			}
		}
	}

	protected void executeStatement(boolean complex)
	{
		if (executionThread==null || !executionThread.isAlive())
		{
			pnlStatus.showMessage("");

			if (!query.isSystemQuery())
			{
				String fullText=tfQuery.getText();
				String queryText=tfQuery.getSelectedText();

				if (StringUtils.isEmpty(queryText))
					queryText=fullText;
				else
				{
					SQLPluginAppConfig commonConfiguration=SQLPluginAppConfig.getInstance();
					Boolean confirm=commonConfiguration.getConfirmation(SQLPluginConstants.PARTIAL_EXECUTE);
					if (Boolean.FALSE.equals(confirm))
					{
						queryText=fullText;
					}
					else if (confirm==null)
					{
						ConfirmationDialog dialog=new ConfirmationDialog(project,
																		 "Question", "Do you want to execute only the selected part of the query?",
																		 new String[]{"Yes", "No", "Cancel"}, "Yes", true);
						PluginUtils.showDialog(dialog, true, true);
						if ("Yes".equals(dialog.getReturnValue()))
						{
							if (dialog.isDontShowAgain())
								commonConfiguration.setConfirmation(SQLPluginConstants.PARTIAL_EXECUTE, Boolean.TRUE);
						}
						else if ("No".equals(dialog.getReturnValue()))
						{
							if (dialog.isDontShowAgain())
								commonConfiguration.setConfirmation(SQLPluginConstants.PARTIAL_EXECUTE, Boolean.FALSE);
							queryText=fullText;
						}
						else
							return;
					}
				}

				queryText=queryText.trim();
				executeStatement(queryText, complex);
			}
			else
			{
				ClassLoader classLoader=getQueryClassLoader();
				StatementList statementList=new StatementList(query.getSystemStatement());
				executionThread=new ExecutionThread(statementList, classLoader);
				initExecution(false);
				executionThread.start();
			}
		}
	}

	private void executeStatement(String queryText, boolean complex)
	{
		// The classloader is created here because he has to be created in AWTEventThread
		ClassLoader classLoader=getQueryClassLoader();

		StatementList statementList;
		if (complex) statementList=new StatementList(new ComplexSQLStatement(queryText));
		else statementList=SQLParser.parse(queryText);

		executionThread=new ExecutionThread(statementList, classLoader);
		initExecution(false);
		executionThread.start();
	}

	private ClassLoader getQueryClassLoader()
	{
		ClassLoader classLoader;
		try
		{
			if (SQLPluginAppConfig.getInstance().isIncludeProjectClasses())
			{
				classLoader=PluginUtils.getProjectClassLoader(project);
			}
			else
			{
				classLoader=getClass().getClassLoader();
			}
		}
		catch (Throwable e)
		{
			classLoader=getClass().getClassLoader();
		}
		return classLoader;
	}

	public void executeStatement(final SQLStatement statement, final ResultSetPanel panel)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (executionThread==null || !executionThread.isAlive())
				{
					pnlStatus.showMessage("");
					errorCount=0;

					ClassLoader classLoader;
					try
					{
						classLoader=PluginUtils.getProjectClassLoader(project);
					}
					catch (Throwable e)
					{
						classLoader=getClass().getClassLoader();
					}
					executionThread=new ExecutionThread(new StatementList(statement), classLoader);
					executionThread.setResultSetPanel(panel);
					initExecution(true);
					executionThread.start();
				}
			}
		});
	}

	private void initExecution(boolean refresh)
	{
		if (!refresh) clearLastResults();
		pnlStatus.startProgress();
	}

	private class ExecuteAction extends AnAction
	{
		private boolean complex;

		public ExecuteAction(boolean complex)
		{
			super("Query");
			this.complex=complex;
			if (complex)
			{
				getTemplatePresentation().setIcon(IconManager.getIcon(Icons.EXECUTE_COMPLEX));
				getTemplatePresentation().setDescription("Execute complex statements without parsing.");
			}
			else
			{
				getTemplatePresentation().setIcon(IconManager.getIcon(Icons.EXECUTE));
				getTemplatePresentation().setDescription("Execute query.");
				CustomShortcutSet shortcutSet=new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK));
				registerCustomShortcutSet(shortcutSet, QueryPanel.this);
			}
		}

		public void actionPerformed(AnActionEvent anActionEvent)
		{
			try
			{
				executeStatement(complex);
			}
			catch (Exception e1)
			{
				handleThrowable(e1);
			}
		}

		public void update(AnActionEvent anActionEvent)
		{
			super.update(anActionEvent);
			boolean enabled=currentDatabase!=null && !StringUtils.isEmpty(tfQuery.getText())
							&& (executionThread==null || !executionThread.isAlive());
			anActionEvent.getPresentation().setEnabled(enabled);
		}
	}

	public void handleThrowable(Throwable e)
	{
		e.printStackTrace();
		showMessage(e.getMessage(), MessagePane.ERROR_STYLE, false, true);
		errorCount++;
	}

	private void clearLastResults()
	{
		errorCount=0;
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				tfResult.clear();
				while (pnlResults.getTabCount()>1)
					pnlResults.removeTabAt(1);
			}
		});
	}

	public void showMessage(final String message, final String styleName, final boolean space, final boolean activate)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				String text=message!=null ? message.trim() : null;
				if (space && !tfResult.isEmpty()) tfResult.appendLineBreak();
				tfResult.appendText(text, styleName);
				tfResult.appendLineBreak();
				if (activate) pnlResults.setSelectedIndex(0);
			}
		});
	}

	private class CancelAction extends AnAction
	{
		public CancelAction()
		{
			super("Close query");
			getTemplatePresentation().setDescription("Close current query.");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.CANCEL));
		}

		public void actionPerformed(AnActionEvent event)
		{
			QueryManager.getInstance(project).removeQuery(query);
		}
	}

	private class StartExportAction extends AnAction
	{
		public StartExportAction()
		{
			super("Export");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.EXPORT));
			getTemplatePresentation().setDescription("Export result table.");
		}

		public void actionPerformed(AnActionEvent event)
		{
			DefaultActionGroup actionGroup=new DefaultActionGroup();
			actionGroup.add(new ExportAction(HTMLExporter.getInstance()));
			actionGroup.add(new ExportAction(XMLExporter.getInstance()));
			actionGroup.add(new ExportAction(CSVExporter.getInstance()));
			actionGroup.add(new ExportAction(FixedWidthExporter.getInstance()));
			actionGroup.add(new ExportAction(ExcelExporter.getInstance()));
			actionGroup.addSeparator();
			actionGroup.add(new ConfigureExportAction());
			ActionPopupMenu popupMenu=ActionManager.getInstance().createActionPopupMenu("ExportMenu", actionGroup);
			popupMenu.getComponent().show((Component)event.getInputEvent().getSource(), 20, 20);
		}
	}

	private class ExportAction extends AnAction
	{
		private Exporter exporter;

		public ExportAction(Exporter exporter)
		{
			super(exporter.getName());
			getTemplatePresentation().setIcon(IconManager.getIcon("/icons/"+exporter.getIcon()+".png"));
			this.exporter=exporter;
		}

		public void update(AnActionEvent event)
		{
			super.update(event);
			if (exporter.isEnabled() && pnlResults.getSelectedComponent() instanceof ResultSetPanel)
			{
				ResultSetPanel resultSetPanel=(ResultSetPanel)pnlResults.getSelectedComponent();
				event.getPresentation().setEnabled(resultSetPanel.getResultTable().getModel().getRowCount()>0);
			}
			else
				event.getPresentation().setEnabled(false);
		}

		public void actionPerformed(AnActionEvent event)
		{
			if (pnlResults.getSelectedComponent() instanceof ResultSetPanel)
			{
				ResultSetPanel resultSetPanel=(ResultSetPanel)pnlResults.getSelectedComponent();
				JFileChooser chooser=createExportDialog(exporter.getFileFilter());
				if (JFileChooser.APPROVE_OPTION==chooser.showDialog(null, "Export"))
				{
					File file=chooser.getSelectedFile();
					String exportPath=file.getParent();
					ExportConfiguration exportConfiguration=SQLPluginAppConfig.getInstance().getExportConfiguration();
					exportConfiguration.setPath(exportPath);
					try
					{
						JTable resultTable=resultSetPanel.getResultTable();
						SQLStatement statement=resultSetPanel.getStatement();
						exporter.exportTable(resultTable, statement, file, exportConfiguration);
					}
					catch (Exception e1)
					{
						handleThrowable(e1);
					}
				}
			}
		}
	}

	private JFileChooser createExportDialog(FileFilter fileFilter)
	{
		JFileChooser chooser=new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setDialogTitle("Export Result...");
		chooser.addChoosableFileFilter(fileFilter);
		chooser.setFileFilter(fileFilter);

		File path=Utils.getExistingPath(SQLPluginAppConfig.getInstance().getExportConfiguration().getPath());
		chooser.setCurrentDirectory(path);

		return chooser;
	}

	private class SuspendAction extends AnAction
	{
		public SuspendAction()
		{
			super("Suspend");
			getTemplatePresentation().setIcon(IconManager.getIcon(Icons.SUSPEND));
		}

		public void actionPerformed(AnActionEvent e)
		{
			if (executionThread!=null && executionThread.isAlive())
			{
				executionThread.cancel();
			}
		}

		public void update(AnActionEvent anActionEvent)
		{
			super.update(anActionEvent);
			anActionEvent.getPresentation().setEnabled(executionThread!=null && executionThread.isAlive());
		}
	}

	private void addTableModel(final ResultSetTableModel model, final ResultSetPanel panel, final boolean select)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				StringBuffer statusText=new StringBuffer();
				if (model.isIncomplete())
				{
					statusText.append(model.getRowCount()).append(" row(s) loaded.");
					statusText.append(" Result set contained more rows.");
				}
				else
					statusText.append(model.getRowCount()).append(" row(s) found.");

				ResultSetPanel resultSetPanel=panel;
				if (resultSetPanel==null)
				{
					int index=pnlResults.getTabCount();
					resultSetPanel=new ResultSetPanel(project, QueryPanel.this, model, statusText.toString());
					pnlResults.addTab(String.valueOf(index), IconManager.getIcon(Icons.RESULT), resultSetPanel);
					pnlResults.setToolTipTextAt(pnlResults.getTabCount()-1, StringUtils.createHTMLToolTip(model.getStatement().getText().trim()));
					if (index==1 || select) pnlResults.setSelectedIndex(index);
				}
				else resultSetPanel.setTableModel(model);

				tfResult.appendText(statusText.toString(), MessagePane.INFO_STYLE);
				tfResult.appendText(" (", MessagePane.INFO_STYLE);
				tfResult.appendLink("View", "result."+resultSetPanel.getId(), MessagePane.LINK_STYLE);
				tfResult.appendText(")", MessagePane.INFO_STYLE);
				tfResult.appendLineBreak();
				if (!select) pnlResults.setSelectedIndex(0);
			}
		});
	}

	public void insertString(final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Document document=tfQuery.getDocument();
				try
				{
					int selectionStart=tfQuery.getSelectionStart();
					int selectionEnd=tfQuery.getSelectionEnd();
					int position=tfQuery.getCaretPosition();
					if (selectionStart<selectionEnd)
					{
						position=selectionStart;
						document.remove(selectionStart, selectionEnd-selectionStart);
					}
					document.insertString(position, text, null);
					tfQuery.requestFocus();
				}
				catch (BadLocationException e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public boolean hasErrors()
	{
		return errorCount>0;
	}

	private class ExecutionThread extends Thread
	{
		private StatementList statementList;
		private ClassLoader classLoader;
		private ResultSetPanel resultSetPanel;
		private PreparedStatement statement;
		private boolean cancelled;

		public ExecutionThread(StatementList statementList, ClassLoader classLoader)
		{
			this.statementList=statementList;
			this.classLoader=classLoader;
		}

		public void run()
		{
			long time1=System.currentTimeMillis();
			try
			{
				Database database=DatabaseManager.getApplicationInstance().getCurrentDatabase();
				Connection connection=DatabaseUtils.connect(project, database, false);
				if (connection==null) throw new RuntimeException("Connection couldn't be established.");
				Iterator statements=statementList.getStatements().iterator();
				boolean stopOnError=SQLPluginAppConfig.getInstance().isStopOnError();
				while (statements.hasNext() && !cancelled)
				{
					try
					{
						SQLStatement sqlStatement=(SQLStatement)statements.next();
						showMessage(sqlStatement.getText(), MessagePane.DEFAULT_STYLE, true, true);
						if (sqlStatement instanceof DescribeStatement)
							executeDescribe(database, connection, (DescribeStatement)sqlStatement);
						else if (sqlStatement instanceof SystemStatement)
							executeSystem(database, connection, (SystemStatement)sqlStatement);
						else
							executeSelect(database, connection, sqlStatement);
					}
					catch (SQLException e)
					{
						handleThrowable(e);
						if (stopOnError) break;
					}
				}
			}
			catch (Throwable e)
			{
				handleThrowable(e);
			}
			finally
			{
				final long time=System.currentTimeMillis()-time1;
				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						if (errorCount>0) pnlStatus.appendMessage("Errors: "+errorCount);
						pnlStatus.appendMessage("Execution time: "+time+"ms");
						if (!hasErrors() && pnlResults.getTabCount()>1) pnlResults.setSelectedIndex(1);
						pnlStatus.stopProgress();
					}
				});
			}
		}

		private void executeDescribe(Database database, Connection connection, DescribeStatement statement)
			throws Exception
		{
			ResultSet resultSet=null;
			ResultSetTableModel model;
			statement.setResultCount(0);
			try
			{
				DatabaseMetaData metaData=connection.getMetaData();
				String catalog=connection.getCatalog();
				String schema=statement.getSchema();
				if (schema==null) schema=database.getDefaultSchema(project).getName();
				String table=statement.getTable().getName();
				if (metaData.storesLowerCaseIdentifiers())
				{
					if (schema!=null) schema=schema.toLowerCase();
					if (table!=null) table=table.toLowerCase();
				}
				else if (metaData.storesUpperCaseIdentifiers())
				{
					if (schema!=null) schema=schema.toUpperCase();
					if (table!=null) table=table.toUpperCase();
				}
				resultSet=metaData.getColumns(catalog, schema, table, null);
				model=new ResultSetTableModel(QueryPanel.this, project, database, statement, resultSet, classLoader);
			}
			finally
			{
				if (resultSet!=null) resultSet.close();
			}
			addTableModel(model, resultSetPanel, false);
			statement.setResultCount(1);
		}

		private void executeSelect(Database database, Connection connection, SQLStatement sqlStatement)
			throws Exception
		{
			String text=sqlStatement.getText();
			statement=connection.prepareStatement(text);
			sqlStatement.setResultCount(0);
			try
			{
				int resultCount=0;
				boolean hasResultSet=statement.execute();
				showWarnings(statement);
				int updateCount=statement.getUpdateCount();
				while (hasResultSet || updateCount!=-1)
				{
					if (hasResultSet)
					{
						resultCount++;
						ResultSetTableModel model;
						ResultSet resultSet=null;
						try
						{
							resultSet=statement.getResultSet();
							model=new ResultSetTableModel(QueryPanel.this, project, database, sqlStatement, resultSet, classLoader);
						}
						finally
						{
							if (resultSet!=null) resultSet.close();
						}
						addTableModel(model, resultSetPanel, false);
					}
					else
					{
						showMessage(sqlStatement.getSuccessMessage(updateCount), MessagePane.INFO_STYLE, false, true);
					}
					hasResultSet=statement.getMoreResults();
					updateCount=-1;
				}
				sqlStatement.setResultCount(resultCount);
			}
			finally
			{
				if (statement!=null) statement.close();
				statement=null;
			}
		}

		private void showWarnings(PreparedStatement statement)
			throws SQLException
		{
			SQLWarning warning=statement.getWarnings();
			while (warning!=null)
			{
				showMessage(warning.getErrorCode()+": "+warning.getMessage(), MessagePane.WARNING_STYLE, false, false);
				warning=warning.getNextWarning();
			}
			statement.clearWarnings();
		}

		private void executeSystem(Database database, Connection connection, SystemStatement sqlStatement)
			throws Exception
		{
			String text=sqlStatement.getText();
			statement=connection.prepareStatement(text);
			sqlStatement.setResultCount(0);
			try
			{
				List parameters=sqlStatement.getParameters();
				for (int i=0; i<parameters.size(); i++)
					statement.setObject(i+1, parameters.get(i));
				ResultSet resultSet=statement.executeQuery();
				showWarnings(statement);
				ResultSetTableModel model=new ResultSetTableModel(QueryPanel.this, project, database, sqlStatement, resultSet, classLoader);
				addTableModel(model, resultSetPanel, true);
				sqlStatement.setResultCount(1);
			}
			finally
			{
				if (statement!=null) statement.close();
				statement=null;
			}
		}

		public void cancel()
		{
			cancelled=true;
			PreparedStatement statement=this.statement;
			if (statement!=null)
			{
				try
				{
					statement.cancel();
					showMessage("Query aborted by user.", MessagePane.WARNING_STYLE, false, true);
				}
				catch (SQLException e)
				{
					handleThrowable(e);
				}
			}
		}

		public void setResultSetPanel(ResultSetPanel resultSetPanel)
		{
			this.resultSetPanel=resultSetPanel;
		}
	}

	private class ConfigurationListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			pnlResults.repaint();
		}
	}

	private class AutoCommitAction extends AbstractAction
	{
		public AutoCommitAction()
		{
			super("Auto-Commit:");
			putValue(Action.SHORT_DESCRIPTION, "The 'Auto-Commit' value for the current database.");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			Database currentDatabase=DatabaseManager.getApplicationInstance().getCurrentDatabase();
			if (currentDatabase!=null)
			{
				Object value=currentDatabase.getProperty(DatabaseDriver.AUTO_COMMIT);
				if (Boolean.TRUE.equals(value))
					currentDatabase.setProperty(DatabaseDriver.AUTO_COMMIT, Boolean.FALSE);
				else if (Boolean.FALSE.equals(value))
					currentDatabase.setProperty(DatabaseDriver.AUTO_COMMIT, null);
				else
					currentDatabase.setProperty(DatabaseDriver.AUTO_COMMIT, Boolean.TRUE);
			}
		}

		public void update(Database currentDatabase)
		{
			boolean enabled=currentDatabase!=null;
			setEnabled(enabled);
			if (enabled)
			{
				Object value=currentDatabase.getProperty(DatabaseDriver.AUTO_COMMIT);
				if (Boolean.TRUE.equals(value))
					putValue(Action.NAME, "Auto-Commit: ON");
				else if (Boolean.FALSE.equals(value))
					putValue(Action.NAME, "Auto-Commit: OFF");
				else
					putValue(Action.NAME, "Auto-Commit: DEFAULT");
			}
		}
	}

	private class MessagesLinkListener implements HyperlinkListener
	{
		public void hyperlinkUpdate(HyperlinkEvent e)
		{
			if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
			{
				try
				{
					String ref=e.getDescription();
					if (ref!=null && ref.startsWith("result"))
					{
						int resultId=Integer.parseInt(ref.substring(ref.indexOf(".")+1));
						for (int i=0; i<pnlResults.getTabCount(); i++)
						{
							Component component=pnlResults.getComponentAt(i);
							if (component instanceof ResultSetPanel)
							{
								ResultSetPanel resultSetPanel=(ResultSetPanel)component;
								if (resultSetPanel.getId()==resultId)
								{
									pnlResults.setSelectedIndex(i);
									break;
								}
							}
						}
					}
				}
				catch (NumberFormatException e1)
				{
					e1.printStackTrace();
				}

			}
		}
	}
}
