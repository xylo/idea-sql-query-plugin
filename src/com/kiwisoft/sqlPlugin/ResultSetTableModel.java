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

import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import java.util.Date;
import java.util.*;

import com.intellij.openapi.project.Project;

import com.kiwisoft.db.ColumnData;
import com.kiwisoft.db.Database;
import com.kiwisoft.db.DatabaseUtils;
import com.kiwisoft.db.SQLValue;
import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.text.MessagePane;
import com.kiwisoft.utils.format.ObjectFormat;
import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.StrikeThroughDecorator;
import com.kiwisoft.utils.gui.table.DefaultSortableTableRow;
import com.kiwisoft.utils.gui.table.SortableTableModel;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.12 $, $Date: 2006/03/24 18:12:24 $
 */
public class ResultSetTableModel extends SortableTableModel
{
	private Vector columnInfos;
	private QueryPanel queryPanel;
	private Project project;
	private Database database;
	private SQLStatement statement;
	private boolean editable;
	private Set primaryKeyColumns;

	private boolean incomplete;
	private ClassLoader projectClassLoader;
	private ResultTableConfiguration tableConfiguration;

	public final static ObjectStyle MODIFIED_STYLE=new ObjectStyle(null, new Color(215, 215, 255));
	public final static ObjectStyle DELETED_STYLE=new ObjectStyle(new StrikeThroughDecorator(Color.RED));
	public final static ObjectStyle DELETED_MODIFIED_STYLE=new ObjectStyle(null, new Color(215, 215, 255), new StrikeThroughDecorator(Color.RED));
	private static final String DEFAULT_SCHEMA_KEY="DEFAULT_SCHEMA";
	private static final String CATALOG="CATALOG";

	public ResultSetTableModel(QueryPanel queryPanel, Project project, Database database, SQLStatement statement, ResultSet resultSet,
							   ClassLoader classLoader) throws Exception
	{
		super();
		this.queryPanel=queryPanel;
		this.project=project;
		this.projectClassLoader=classLoader;
		this.database=database;
		this.statement=statement;
		primaryKeyColumns=new HashSet();
		createData(resultSet);
	}

	public SQLStatement getStatement()
	{
		return statement;
	}

	private void createData(ResultSet resultSet) throws Exception
	{
		SQLPluginAppConfig appConfig=SQLPluginAppConfig.getInstance();
		ResultSetMetaData metaData=resultSet.getMetaData();

		columnInfos=new Vector();
		editable=statement.isSimpleSelect();
		tableConfiguration=database.getResultTableConfiguration(statement);

		Map metaCache=new HashMap();

		for (int i=0; i<metaData.getColumnCount(); i++)
		{
			int column=i+1;
			String columnName=metaData.getColumnName(column);
			ColumnInfo columnInfo=new ColumnInfo(columnName);
			columnInfos.add(columnInfo);

			if (appConfig.isHighlightKeyColumns())
			{
				int identifierCase=database.getIdentifiersCase(project);
				try
				{
					String catalogName=getCatalogName(metaData, column);
					String schemaName=getSchemaName(metaData, column);
					String tableName=getTableName(metaData, column);
					if (editable)
					{
						if (StringUtils.isEmpty(tableName))
							tableName=StringUtils.applyCharacterCase(statement.getTableName(), identifierCase);
						if (StringUtils.isEmpty(schemaName)) schemaName=getDefaultSchemaName(metaCache);
						if (StringUtils.isEmpty(catalogName)) catalogName=getCatalog(metaCache);
					}
					columnInfo.setSchemaName(schemaName);

					if (columnName!=null)
					{
						TableMetaData tableMetaData=getTableMetaData(metaCache, catalogName, schemaName, tableName);
						String actualColumnName=StringUtils.applyCharacterCase(columnName, identifierCase);
						columnInfo.setPrimaryKey(tableMetaData.getPrimaryKeys().contains(actualColumnName));
						columnInfo.setForeignKey((ColumnData)tableMetaData.getImportedKeys().get(actualColumnName));
						if (columnInfo.isPrimaryKey())
						{
							columnInfo.setExportedKeys(tableMetaData.getExportedKeys().get(actualColumnName));
						}
					}
					if (columnInfo.isPrimaryKey()) primaryKeyColumns.add(new Integer(i));
				}
				catch (Throwable e)
				{
					e.printStackTrace();
					System.err.println("Error while getting primary/foreign keys: "+e.getMessage());
				}
			}
			Class columnType=null;
			String columnClassName=null;
			try
			{
				columnInfo.setJdbcType(metaData.getColumnType(column));
				columnClassName=metaData.getColumnClassName(column);
				if ("byte[]".equals(columnClassName)) // Returned by Oracle for VARBINARY (v$session)
					columnType=byte[].class;
				else
					columnType=DatabaseUtils.getClass(columnInfo.getJdbcType());
				if (columnType==null)
					columnType=Class.forName(columnClassName, true, DatabaseDriverManager.getClassLoader());
			}
			catch (Throwable e)
			{
				System.err.println("Error while getting column class name: "+e.getMessage());
			}
			columnInfo.setType(columnType);
			columnInfo.setColumnClassName(columnClassName);
			if (tableConfiguration!=null)
			{
				columnInfo.setFormat(tableConfiguration.getFormat(i));
				columnInfo.setFont(tableConfiguration.getFont(i));
			}
		}

		boolean limitRows=appConfig.isRowLimitEnabled();
		int rowLimit=appConfig.getRowLimit();
		boolean hasNext=resultSet.next();
		while (hasNext && (!limitRows || getRowCount()<rowLimit))
		{
			Vector row=new Vector();
			for (int i=0; i<metaData.getColumnCount(); i++)
			{
				ColumnInfo columnInfo=getColumnInfo(i);
				Object object;
				try
				{
					object=DatabaseUtils.getObject(resultSet, i+1, columnInfo.getJdbcType(), projectClassLoader, appConfig.isLoadLargeObjects());
				}
				catch (SQLException e)
				{
					object=e;
				}
				row.add(object);

				if (object!=null)
				{
					columnInfo.setType(ObjectUtils.getBaseClass(columnInfo.getType(), object.getClass()));
				}
			}
			addRow(new TableRow(row));
			hasNext=resultSet.next();
		}
		if (hasNext) incomplete=true;
	}

	private TableMetaData getTableMetaData(Map metaCache, String catalogName, String schemaName, String tableName)
	{
		String referenceKey=catalogName+"."+schemaName+"."+tableName;
		TableMetaData tableMetaData=(TableMetaData)metaCache.get(referenceKey);
		if (tableMetaData==null) metaCache.put(referenceKey, tableMetaData=new TableMetaData(catalogName, schemaName, tableName));
		return tableMetaData;
	}

	private String getDefaultSchemaName(Map metaCache) throws SQLException
	{
		String defaultSchema;
		if (metaCache.containsKey(DEFAULT_SCHEMA_KEY)) defaultSchema=(String)metaCache.get(DEFAULT_SCHEMA_KEY);
		else
		{
			defaultSchema=database.getDefaultSchema(project).getSchemaName();
			metaCache.put(DEFAULT_SCHEMA_KEY, defaultSchema);
		}
		return defaultSchema;
	}

	private String getCatalog(Map metaCache) throws SQLException
	{
		String catalog;
		if (metaCache.containsKey(CATALOG)) catalog=(String)metaCache.get(CATALOG);
		else
		{
			catalog=database.getCatalog(project);
			metaCache.put(CATALOG, catalog);
		}
		return catalog;
	}

	private static String getTableName(ResultSetMetaData metaData, int column)
	{
		String tableName=null;
		try
		{
			tableName=metaData.getTableName(column);
		}
		catch (SQLException e)
		{
			System.err.println("getTableName not supported: "+e.getMessage());
		}
		return tableName;
	}

	private static String getSchemaName(ResultSetMetaData metaData, int column)
	{
		String schemaName=null;
		try
		{
			schemaName=metaData.getSchemaName(column);
		}
		catch (SQLException e)
		{
			System.err.println("getSchemaName not supported: "+e.getMessage());
		}
		return schemaName;
	}

	private static String getCatalogName(ResultSetMetaData metaData, int column)
	{
		String catalogName=null;
		try
		{
			catalogName=metaData.getCatalogName(column);
		}
		catch (Exception e)
		{
			System.err.println("getCatalogName not supported: "+e.getMessage());
		}
		return catalogName;
	}

	public boolean isIncomplete()
	{
		return incomplete;
	}

	public int getColumnCount()
	{
		return columnInfos.size();
	}

	public String getColumnName(int col)
	{
		return getColumnInfo(col).getName();
	}

	public void setColumnFormat(int col, String format)
	{
		getColumnInfo(col).setFormat(format);
		tableConfiguration.setFormat(col, format);
	}

	public ColumnInfo getColumnInfo(int col)
	{
		return ((ColumnInfo)columnInfos.get(col));
	}

	public String getCellFormat(int row, int col)
	{
		return getColumnFormat(col);
	}

	public String getColumnFormat(int col)
	{
		ColumnInfo info=getColumnInfo(col);
		String format=info.getFormat();
		Class type=info.getType();
		if (type!=null && (format==null || ObjectFormat.DEFAULT.equals(format)))
		{
			String defaultFormat=null;
			SQLPluginAppConfig appConfig=SQLPluginAppConfig.getInstance();
			if (Date.class.isAssignableFrom(type)) defaultFormat=appConfig.getDefaultFormat(Date.class);
			else if (Number.class.isAssignableFrom(type)) defaultFormat=appConfig.getDefaultFormat(Number.class);
			else if (String.class.isAssignableFrom(type)) defaultFormat=appConfig.getDefaultFormat(String.class);
			else if (Boolean.class.isAssignableFrom(type)) defaultFormat=appConfig.getDefaultFormat(Boolean.class);
			if (defaultFormat!=null) return defaultFormat;
		}
		return format;
	}

	public ObjectStyle getCellStyle(int row, int col)
	{
		ObjectStyle style1=getColumnInfo(col).getStyle();
		ObjectStyle style2=super.getCellStyle(row, col);
		if (style1==null) return style2;
		if (style2==null) return style1;
		return style1.combine(style2);
	}

	public void setColumnFont(int col, Font font)
	{
		getColumnInfo(col).setFont(font);
		tableConfiguration.setFont(col, font);
	}

	public boolean isPrimaryKey(int col)
	{
		return getColumnInfo(col).isPrimaryKey();
	}

	public void setPrimaryKey(int col, boolean value)
	{
		getColumnInfo(col).setPrimaryKey(value);
		if (value)
			primaryKeyColumns.add(new Integer(col));
		else
			primaryKeyColumns.remove(new Integer(col));
		fireTableDataChanged();
	}

	protected class TableRow extends DefaultSortableTableRow
	{
		private boolean modified;
		private boolean deleted;

		public TableRow(Vector data)
		{
			super(data);
		}

		public Class getCellClass(int col)
		{
			Object value=getDisplayValue(col);
			if (value instanceof Throwable) return Throwable.class;
			Class type=getColumnInfo(col).getType();
			if (type==null) type=Object.class;
			if (value!=null && type.isInstance(value)) return value.getClass();
			return type;
		}

		public ObjectStyle getCellStyle(int col)
		{
			if (deleted)
			{
				if (modified) return DELETED_MODIFIED_STYLE;
				else return DELETED_STYLE;
			}
			if (modified) return MODIFIED_STYLE;
			return super.getCellStyle(col);
		}

		public int setValue(Object value, int col)
		{
			if ("".equals(value)) value=null;
			Object oldValue=getDisplayValue(col);
			if (oldValue!=null ? !oldValue.equals(value) : value!=null)
			{
				ColumnInfo columnInfo=getColumnInfo(col);
				int jdbcType=columnInfo.getJdbcType();
				if (value!=null)
				{
					switch (jdbcType)
					{
						case Types.DATE:
							if (value instanceof String)
							{
								Date date=DateUtils.parseDate((String)value);
								if (date==null) return NO_UPDATE;
								else value=date;
							}
							if (value instanceof Date && !(value instanceof java.sql.Date))
								value=new java.sql.Date(((Date)value).getTime());
							break;
						case Types.TIMESTAMP:
							if (value instanceof String)
							{
								Date date=DateUtils.parseDate((String)value);
								if (date==null) return NO_UPDATE;
								else value=date;
							}
							if (value instanceof Date && !(value instanceof java.sql.Timestamp))
								value=new java.sql.Timestamp(((Date)value).getTime());
					}
				}
				Vector parameters=new Vector();
				StringBuffer updateStatement=new StringBuffer("update ");
				updateStatement.append(statement.getTableName());
				updateStatement.append(" set ");
				updateStatement.append(columnInfo.getName());
				updateStatement.append("=?");
				parameters.add(new SQLValue(value, jdbcType));
				updateStatement.append(" where ");
				Iterator it=primaryKeyColumns.iterator();
				while (it.hasNext())
				{
					int column=((Integer)it.next()).intValue();
					ColumnInfo keyColumnInfo=getColumnInfo(column);
					updateStatement.append(keyColumnInfo.getName());
					updateStatement.append("=?");
					parameters.add(new SQLValue(getDisplayValue(column), keyColumnInfo.getJdbcType()));
					if (it.hasNext()) updateStatement.append(" and ");
				}
				Connection connection=DatabaseUtils.connect(project, database, false);
				try
				{
					PreparedStatement stmt=connection.prepareStatement(updateStatement.toString());
					for (int i=0; i<parameters.size(); i++)
					{
						SQLValue parameter=(SQLValue)parameters.get(i);
						parameter.set(stmt, i+1);
					}
					queryPanel.showMessage(bindParameters(updateStatement.toString(), parameters), MessagePane.DEFAULT_STYLE, true, false);
					int updateCount=stmt.executeUpdate();
					queryPanel.showMessage(updateCount+" row(s) updated.",
										   updateCount!=1 ? MessagePane.INFO_STYLE : MessagePane.WARNING_STYLE,
										   false, updateCount!=1);
					int updatedRows=0;
					if (updateCount>0)
					{
						((Vector)getUserObject()).set(col, value);
						modified=true;
						updatedRows++;
						if (updateCount>1)
						{
							int rowCount=getRowCount();
							for (int i=0; i<rowCount; i++)
							{
								TableRow row=(TableRow)getRow(i);
								if (row!=TableRow.this)
								{
									boolean equal=true;
									for (Iterator it2=primaryKeyColumns.iterator(); it2.hasNext();)
									{
										int column=((Integer)it2.next()).intValue();
										Object value1=getDisplayValue(column);
										Object value2=row.getDisplayValue(column);
										if (!Utils.equals(value1, value2))
										{
											equal=false;
											break;
										}
									}
									if (equal)
									{
										((Vector)row.getUserObject()).set(col, value);
										updatedRows++;
										row.modified=true;
									}
								}
							}
						}
					}
					switch (updatedRows)
					{
						case 0:
							return NO_UPDATE;
						case 1:
							return ROW_UPDATE;
						default:
							return TABLE_UPDATE;
					}
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					queryPanel.handleThrowable(e);
					return ROW_UPDATE;
				}
			}
			return NO_UPDATE;
		}

		public void delete()
		{
			Vector parameters=new Vector();
			StringBuffer updateStatement=new StringBuffer("delete from ");
			updateStatement.append(statement.getTableName());
			updateStatement.append(" where ");
			Iterator it=primaryKeyColumns.iterator();
			while (it.hasNext())
			{
				int column=((Integer)it.next()).intValue();
				ColumnInfo columnInfo=getColumnInfo(column);
				updateStatement.append(columnInfo.getName()).append("=?");
				parameters.add(new SQLValue(getDisplayValue(column), columnInfo.getJdbcType()));
				if (it.hasNext()) updateStatement.append(" and ");
			}
			Connection connection=DatabaseUtils.connect(project, database, false);
			try
			{
				PreparedStatement stmt=connection.prepareStatement(updateStatement.toString());
				for (int i=0; i<parameters.size(); i++)
				{
					SQLValue parameter=(SQLValue)parameters.get(i);
					parameter.set(stmt, i+1);
				}
				queryPanel.showMessage(bindParameters(updateStatement.toString(), parameters), MessagePane.DEFAULT_STYLE, true, false);
				int updateCount=stmt.executeUpdate();
				queryPanel.showMessage(updateCount+" row(s) deleted.",
									   updateCount!=1 ? MessagePane.INFO_STYLE : MessagePane.WARNING_STYLE,
									   false, updateCount!=1);
				if (updateCount>0)
				{
					deleted=true;
					if (updateCount>1)
					{
						int rowCount=getRowCount();
						for (int i=0; i<rowCount; i++)
						{
							TableRow row=(TableRow)getRow(i);
							if (row!=TableRow.this)
							{
								boolean equal=true;
								for (Iterator it2=primaryKeyColumns.iterator(); it2.hasNext();)
								{
									int column=((Integer)it2.next()).intValue();
									Object value1=getDisplayValue(column);
									Object value2=row.getDisplayValue(column);
									if (!Utils.equals(value1, value2))
									{
										equal=false;
										break;
									}
								}
								if (equal) row.deleted=true;
							}
						}
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				queryPanel.handleThrowable(e);
			}
		}

		public boolean isEditable(int column)
		{
			return !deleted && editable && !primaryKeyColumns.isEmpty() &&
				   getColumnInfo(column).isEditable();
		}

		public boolean isDeletable()
		{
			return editable && !primaryKeyColumns.isEmpty();
		}
	}

	private static String bindParameters(String statement, List paramenters)
	{
		StringBuffer buffer=new StringBuffer();
		Iterator itParameters=paramenters.iterator();
		for (int i=0; i<statement.length(); i++)
		{
			char ch=statement.charAt(i);
			if (ch=='?')
			{
				Object value=itParameters.next();
				if (value instanceof SQLValue) value=((SQLValue)value).getValue();
				buffer.append("?/*").append(value).append("*/");
			}
			else buffer.append(ch);
		}
		return buffer.toString();
	}

	public ResultTableConfiguration getTableConfiguration()
	{
		return tableConfiguration;
	}

	private class TableMetaData
	{
		private Set primaryKeys;
		private Map importedKeys;
		private SetMap exportedKeys;

		private String catalogName;
		private String schemaName;
		private String tableName;

		private TableMetaData(String catalogName, String schemaName, String tableName)
		{
			this.catalogName=catalogName;
			this.schemaName=schemaName;
			this.tableName=tableName;
		}

		private Set getPrimaryKeys() throws Exception
		{
			if (primaryKeys==null) primaryKeys=database.getPrimaryKeys(project, catalogName, schemaName, tableName);
			return primaryKeys;
		}

		private Map getImportedKeys() throws Exception
		{
			if (importedKeys==null) importedKeys=database.getImportedKeys(project, catalogName, schemaName, tableName);
			return importedKeys;
		}

		private SetMap getExportedKeys() throws Exception
		{
			if (exportedKeys==null) exportedKeys=database.getExportedKeys(project, catalogName, schemaName, tableName);
			return exportedKeys;
		}
	}
}
