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
package com.kiwisoft.sqlPlugin.config;

import java.util.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.awt.Color;

import org.jdom.Element;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.NamedJDOMExternalizable;

import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.db.DatabaseManager;
import com.kiwisoft.sqlPlugin.templates.TemplateManager;
import com.kiwisoft.sqlPlugin.JdbcLibrary;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadManager;
import com.kiwisoft.utils.Utils;
import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:59:52 $
 */
public class SQLPluginAppConfig implements ApplicationComponent, NamedJDOMExternalizable, SQLPluginConstants
{

	private static SQLPluginAppConfig instance;

	public static SQLPluginAppConfig getInstance()
	{
		Application application=ApplicationManager.getApplication();
		if (application!=null)
		{
			Object component=application.getComponent(SQLPluginAppConfig.class);
			return (SQLPluginAppConfig)component;
		}
		else
		{
			if (instance==null)
			{
				instance=new SQLPluginAppConfig();
				instance.initComponent();
			}
			return instance;
		}
	}

	private ExportConfiguration export;

	public ExportConfiguration getExportConfiguration()
	{
		if (export==null) export=new ExportConfiguration();
		return export;
	}

	private TableConfigurationAdapter tableAdapter;

	public TableConfigurationAdapter getTableAdapter()
	{
		if (tableAdapter==null) tableAdapter=new TableConfigurationAdapter();
		return tableAdapter;
	}

	private Set jdbcLibraries=Collections.EMPTY_SET;

	public void setJdbcLibraries(Set urls)
	{
		if (!Utils.equals(jdbcLibraries, urls))
		{
			if (urls==null) jdbcLibraries=Collections.EMPTY_SET;
			else jdbcLibraries=new HashSet(urls);
			DatabaseDriverManager.reloadDrivers(jdbcLibraries);
		}
	}

	public Set getJdbcLibraries()
	{
		return Collections.unmodifiableSet(jdbcLibraries);
	}

	private boolean keepConnectionOpen;

	public boolean isKeepConnectionOpen()
	{
		return keepConnectionOpen;
	}

	public void setKeepConnectionOpen(boolean keepConnectionOpen)
	{
		this.keepConnectionOpen=keepConnectionOpen;
	}

	private boolean saveQueries;
	private String queryPath;
	private boolean rowLimitEnabled=true;
	private int rowLimit=1000;
	private boolean savePasswords;
	private boolean encodePasswords=true;
	private boolean resizeColumnsToHeader=true;
	private boolean resizeColumnsToContent=true;
	private boolean saveResultTableConfiguration;
	private boolean includeProjectClasses;
	private String nullString;
	private Map confirmations=new HashMap();
	private Map defaultFormats=new HashMap();
	private boolean loadLOBs;
	private boolean stopOnError;

	public boolean isIncludeProjectClasses()
	{
		return includeProjectClasses;
	}

	public void setIncludeProjectClasses(boolean newValue)
	{
		boolean oldValue=includeProjectClasses;
		includeProjectClasses=newValue;
		propertyChangeSupport.firePropertyChange("includePropertyClasses", oldValue, newValue);
	}

	public String getNullString()
	{
		return nullString;
	}

	public void setNullString(String newValue)
	{
		String oldValue=nullString;
		nullString=newValue;
		propertyChangeSupport.firePropertyChange("nullString", oldValue, newValue);
	}

	public Boolean getConfirmation(String key)
	{
		return (Boolean)confirmations.get(key);
	}

	public void setConfirmation(String key, Boolean newValue)
	{
		Boolean oldValue=(Boolean)confirmations.put(key, newValue);
		propertyChangeSupport.firePropertyChange(key, oldValue, newValue);
	}

	public int getRowLimit()
	{
		return rowLimit;
	}

	public void setRowLimit(int newValue)
	{
		int oldValue=rowLimit;
		if (newValue<0) this.rowLimit=1000;
		this.rowLimit=newValue;
		propertyChangeSupport.firePropertyChange("rowLimit", oldValue, rowLimit);
	}

	public void setRowLimitEnabled(boolean newValue)
	{
		boolean oldValue=rowLimitEnabled;
		this.rowLimitEnabled=newValue;
		propertyChangeSupport.firePropertyChange("rowLimitEnabled", oldValue, rowLimitEnabled);
	}

	public boolean isRowLimitEnabled()
	{
		return rowLimitEnabled;
	}

	public boolean isSaveResultTableConfiguration()
	{
		return saveResultTableConfiguration;
	}

	public void setSaveResultTableConfiguration(boolean saveResultTableConfiguration)
	{
		this.saveResultTableConfiguration=saveResultTableConfiguration;
	}

	public void setDefaultFormat(Class aClass, String format)
	{
		String oldFormat=getDefaultFormat(aClass);
		defaultFormats.put(aClass.getName(), format);
		propertyChangeSupport.firePropertyChange("defaultFormat", oldFormat, format);
	}

	public String getDefaultFormat(Class aClass)
	{
		return (String)defaultFormats.get(aClass.getName());
	}

	public boolean isSaveQueries()
	{
		return saveQueries;
	}

	public void setSaveQueries(boolean saveQueries)
	{
		this.saveQueries=saveQueries;
	}

	private boolean highlightKeyColumns=true;

	public boolean isHighlightKeyColumns()
	{
		return highlightKeyColumns;
	}

	public void setHighlightKeyColumns(boolean highlightKeyColumns)
	{
		this.highlightKeyColumns=highlightKeyColumns;
	}

	private Color primaryKeyColor;

	public Color getPrimaryKeyColor()
	{
		return primaryKeyColor!=null ? primaryKeyColor : new Color(150, 150, 230);
	}

	public void setPrimaryKeyColor(Color primaryKeyColor)
	{
		this.primaryKeyColor=primaryKeyColor;
	}

	private Color foreignKeyColor;

	public Color getForeignKeyColor()
	{
		return foreignKeyColor!=null ? foreignKeyColor : new Color(150, 230, 150);
	}

	public void setForeignKeyColor(Color foreignKeyColor)
	{
		this.foreignKeyColor=foreignKeyColor;
	}

	public boolean isSavePasswords()
	{
		return savePasswords;
	}

	public void setSavePasswords(boolean savePasswords)
	{
		this.savePasswords=savePasswords;
	}

	public boolean isEncodePasswords()
	{
		return encodePasswords;
	}

	public void setEncodePasswords(boolean encodePasswords)
	{
		this.encodePasswords=encodePasswords;
	}

	public void setDriversPath(String driversPath)
	{
	}

	public String getQueryPath()
	{
		return queryPath;
	}

	public void setQueryPath(String queryPath)
	{
		this.queryPath=queryPath;
	}

	public boolean isResizeColumnsToHeader()
	{
		return resizeColumnsToHeader;
	}

	public void setResizeColumnsToHeader(boolean resizeColumnsToHeader)
	{
		this.resizeColumnsToHeader=resizeColumnsToHeader;
	}

	public boolean isResizeColumnsToContent()
	{
		return resizeColumnsToContent;
	}

	public void setResizeColumnsToContent(boolean resizeColumnsToContent)
	{
		this.resizeColumnsToContent=resizeColumnsToContent;
	}

	public boolean isLoadLargeObjects()
	{
		return loadLOBs;
	}

	public void setLoadLargeObjects(boolean newValue)
	{
		loadLOBs=newValue;
	}

	public boolean isStopOnError()
	{
		return stopOnError;
	}

	public void setStopOnError(boolean stopOnError)
	{
		this.stopOnError=stopOnError;
	}

	private boolean useAlternateRowColors;
	private Color alternateRowBackground;
	private Color alternateRowForeground;

	public boolean isUseAlternateRowColors()
	{
		return useAlternateRowColors;
	}

	public void setUseAlternateRowColors(boolean useAlternateRowColors)
	{
		this.useAlternateRowColors=useAlternateRowColors;
	}

	public Color getAlternateRowBackground()
	{
		return alternateRowBackground!=null ? alternateRowBackground : new Color(235, 255, 235);
	}

	public void setAlternateRowBackground(Color alternateRowBackground)
	{
		this.alternateRowBackground=alternateRowBackground;
	}

	public Color getAlternateRowForeground()
	{
		return alternateRowForeground!=null ? alternateRowForeground : Color.BLACK;
	}

	public void setAlternateRowForeground(Color alternateRowForeground)
	{
		this.alternateRowForeground=alternateRowForeground;
	}

	private boolean showGrid;

	public boolean isShowGrid()
	{
		return showGrid;
	}

	public void setShowGrid(boolean showGrid)
	{
		this.showGrid=showGrid;
	}

	// Listener Support

	private PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	// ApplicationComponent interface

	public String getComponentName()
	{
		return "SQLPlugin.Configuration";
	}

	public void initComponent()
	{
	}

	public void disposeComponent()
	{
		DatabaseManager.closeApplicationInstance();
	}

	// JDOMExternalizable interface

	public String getExternalFileName()
	{
		return "sql.plugin";
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		try
		{
			// General options
			Element generalElement=new Element(GENERAL);
			PluginUtils.setValue(generalElement, CONFIRM_COMMIT, getConfirmation(CONFIRM_COMMIT));
			PluginUtils.setValue(generalElement, CONFIRM_ROLLBACK, getConfirmation(CONFIRM_ROLLBACK));
			PluginUtils.setValue(generalElement, CONFIRM_DISCONNECT, getConfirmation(CONFIRM_DISCONNECT));
			generalElement.setAttribute(SAVE_PASSWORDS, Boolean.toString(isSavePasswords()));
			generalElement.setAttribute(ENCODE_PASSWORDS, Boolean.toString(isEncodePasswords()));
			generalElement.setAttribute(KEEP_CONNECTIONS_OPEN, Boolean.toString(keepConnectionOpen));
			element.addContent(generalElement);

			// Export Options
			Element exportElement=new Element(EXPORT);
			element.addContent(exportElement);
			getExportConfiguration().writeExternal(element);

			// JDBC Drivers
			Element driversElement=new Element(DRIVERS);
			for (Iterator it=jdbcLibraries.iterator(); it.hasNext();)
			{
				String url=((JdbcLibrary)it.next()).getVfsUrl();
				Element driverElement=new Element(DRIVER);
				driverElement.setAttribute(URL, url);
				driversElement.addContent(driverElement);
			}
			element.addContent(driversElement);

			// Databases
			Element databasesElement=new Element(DATABASES);
			DatabaseManager.getApplicationInstance().writeExternal(databasesElement);
			element.addContent(databasesElement);

			// Queries
			Element child=new Element(QUERIES);
			PluginUtils.setValue(child, PATH, getQueryPath());
			PluginUtils.setValue(child, PARTIAL_EXECUTE, getConfirmation(PARTIAL_EXECUTE));
			child.setAttribute(PROJECT_CLASS_LOADER, Boolean.toString(isIncludeProjectClasses()));
			child.setAttribute(SAVE, Boolean.toString(isSaveQueries()));
			child.setAttribute(STOP_ON_ERROR, Boolean.toString(isStopOnError()));
			element.addContent(child);

			// Results
			Element resultsElement=new Element(RESULTS);
			PluginUtils.setValue(resultsElement, HIGHLIGHT_KEY_COLUMNS, isHighlightKeyColumns());
			PluginUtils.setValue(resultsElement, PRIMARY_KEY_COLOR, getPrimaryKeyColor().getRGB());
			PluginUtils.setValue(resultsElement, FOREIGN_KEY_COLOR, getForeignKeyColor().getRGB());
			PluginUtils.setValue(resultsElement, LOAD_LOBS, isLoadLargeObjects());
			resultsElement.setAttribute(SAVE_TABLE_CONFIG, Boolean.toString(isSaveResultTableConfiguration()));
			for (Iterator it=defaultFormats.keySet().iterator(); it.hasNext();)
			{
				String className=(String)it.next();
				Element formatElement=new Element(DEFAULT_FORMAT);
				PluginUtils.setValue(formatElement, CLASS, className);
				PluginUtils.setValue(formatElement, FORMAT, defaultFormats.get(className));
				resultsElement.addContent(formatElement);
			}
			PluginUtils.setValue(resultsElement, NULL_STRING, getNullString());
			PluginUtils.setValue(resultsElement, USE_ALTERNATE_ROW_COLORS, isUseAlternateRowColors());
			PluginUtils.setValue(resultsElement, ALTERNATE_ROW_BACKGROUND, getAlternateRowBackground().getRGB());
			PluginUtils.setValue(resultsElement, ALTERNATE_ROW_FOREGROUND, getAlternateRowForeground().getRGB());
			PluginUtils.setValue(resultsElement, SHOW_GRID, isShowGrid());
			element.addContent(resultsElement);

			Element rowLimitElement=new Element(ROW_LIMIT);
			PluginUtils.setValue(rowLimitElement, ENABLED, isRowLimitEnabled());
			PluginUtils.setValue(rowLimitElement, VALUE, getRowLimit());
			resultsElement.addContent(rowLimitElement);

			Element resizeColumnsElement=new Element(RESIZE_COLUMNS);
			PluginUtils.setValue(resizeColumnsElement, TO_HEADER, isResizeColumnsToHeader());
			PluginUtils.setValue(resizeColumnsElement, TO_CONTENT, isResizeColumnsToContent());
			resultsElement.addContent(resizeColumnsElement);

			// Tables
			Element tablesElement=new Element(TABLES);
			element.addContent(tablesElement);
			getTableAdapter().writeExternal(tablesElement);

			if (TemplateManager.hasInstance()) TemplateManager.getInstance().saveTemplates();
			if (DataLoadManager.hasInstance()) DataLoadManager.getInstance().saveDescriptors();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void readExternal(Element element) throws InvalidDataException
	{
		try
		{
			// General Options
			Element generalElement=element.getChild(GENERAL);
			if (generalElement!=null)
			{
				setConfirmation(CONFIRM_COMMIT, PluginUtils.getBoolean(generalElement, CONFIRM_COMMIT, null));
				setConfirmation(CONFIRM_ROLLBACK, PluginUtils.getBoolean(generalElement, CONFIRM_ROLLBACK, null));
				setConfirmation(CONFIRM_DISCONNECT, PluginUtils.getBoolean(generalElement, CONFIRM_DISCONNECT, null));
				setSavePasswords(PluginUtils.getBoolean(generalElement, SAVE_PASSWORDS, false));
				setEncodePasswords(PluginUtils.getBoolean(generalElement, ENCODE_PASSWORDS, true));
				setKeepConnectionOpen(PluginUtils.getBoolean(generalElement, KEEP_CONNECTIONS_OPEN, false));
			}

			// Export Options
			Element exportElement=element.getChild(EXPORT);
			if (exportElement!=null) getExportConfiguration().readExternal(exportElement);

			// JDBC Drivers
			Element driversElement=element.getChild(DRIVERS);
			Set libraries=new HashSet();
			if (driversElement!=null)
			{
				List children=driversElement.getChildren(DRIVER);
				for (Iterator it=children.iterator(); it.hasNext();)
				{
					Element driverElement=(Element)it.next();
					String url=PluginUtils.getString(driverElement, URL, null);
					if (url!=null)
					{
						libraries.add(new JdbcLibrary(url));
					}
				}
			}
			setJdbcLibraries(libraries);

			// Databases
			Element databasesElement=element.getChild(DATABASES);
			if (databasesElement!=null)
			{
				DatabaseManager.getApplicationInstance().readExternal(databasesElement, isEncodePasswords());
			}

			// Queries
			Element queriesElement=element.getChild(QUERIES);
			if (queriesElement!=null)
			{
				setConfirmation(PARTIAL_EXECUTE, PluginUtils.getBoolean(queriesElement, PARTIAL_EXECUTE, null));
				setQueryPath(queriesElement.getAttributeValue(PATH));
				setSaveQueries(PluginUtils.getBoolean(queriesElement, SAVE, false));
				setIncludeProjectClasses(PluginUtils.getBoolean(queriesElement, PROJECT_CLASS_LOADER, false));
				setStopOnError(PluginUtils.getBoolean(queriesElement, STOP_ON_ERROR, true));
			}

			// Results
			Element resultsElement=element.getChild(RESULTS);
			if (resultsElement!=null)
			{
				setHighlightKeyColumns(PluginUtils.getBoolean(resultsElement, HIGHLIGHT_KEY_COLUMNS, true));
				Integer colorRGB=PluginUtils.getInteger(resultsElement, PRIMARY_KEY_COLOR, null);
				if (colorRGB!=null) setPrimaryKeyColor(new Color(colorRGB.intValue()));
				colorRGB=PluginUtils.getInteger(resultsElement, FOREIGN_KEY_COLOR, null);
				if (colorRGB!=null) setForeignKeyColor(new Color(colorRGB.intValue()));
				setLoadLargeObjects(PluginUtils.getBoolean(resultsElement, LOAD_LOBS, true));
				setSaveResultTableConfiguration(PluginUtils.getBoolean(resultsElement, SAVE_TABLE_CONFIG, false));
				List children=resultsElement.getChildren(DEFAULT_FORMAT);
				if (children!=null)
				{
					for (Iterator it=children.iterator(); it.hasNext();)
					{
						Element formatElement=(Element)it.next();
						String className=formatElement.getAttributeValue(CLASS);
						String format=formatElement.getAttributeValue(FORMAT);
						defaultFormats.put(className, format);
					}
				}
				setNullString(resultsElement.getAttributeValue(NULL_STRING));
				setUseAlternateRowColors(PluginUtils.getBoolean(resultsElement, USE_ALTERNATE_ROW_COLORS, false));
				colorRGB=PluginUtils.getInteger(resultsElement, ALTERNATE_ROW_BACKGROUND, null);
				if (colorRGB!=null) setAlternateRowBackground(new Color(colorRGB.intValue()));
				colorRGB=PluginUtils.getInteger(resultsElement, ALTERNATE_ROW_FOREGROUND, null);
				if (colorRGB!=null) setAlternateRowForeground(new Color(colorRGB.intValue()));
				setShowGrid(PluginUtils.getBoolean(resultsElement, SHOW_GRID, true));

				Element rowLimitElement=resultsElement.getChild(ROW_LIMIT);
				setRowLimitEnabled(PluginUtils.getBoolean(rowLimitElement, ENABLED, true));
				setRowLimit(PluginUtils.getInteger(rowLimitElement, VALUE, new Integer(1000)).intValue());

				Element resizeElement=element.getChild(RESIZE_COLUMNS);
				if (resizeElement!=null)
				{
					setResizeColumnsToHeader(PluginUtils.getBoolean(resizeElement, TO_HEADER, true));
					setResizeColumnsToContent(PluginUtils.getBoolean(resizeElement, TO_CONTENT, true));
				}
			}

			// Tables
			Element tablesElement=element.getChild(TABLES);
			if (tablesElement!=null) getTableAdapter().readExternal(tablesElement);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
