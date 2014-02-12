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
package com.kiwisoft.db;

import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.*;

import org.jdom.Element;
import org.jdom.Attribute;

import com.intellij.openapi.project.Project;
import com.kiwisoft.db.driver.DatabaseDriver;
import com.kiwisoft.db.driver.DatabaseDriverManager;
import com.kiwisoft.db.driver.DriverProperties;
import com.kiwisoft.db.driver.DriverProperty;
import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.ResultTableConfiguration;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.utils.*;
import com.kiwisoft.utils.idea.PluginUtils;
import com.kiwisoft.utils.gui.table.SortableTableModel;
import com.kiwisoft.utils.gui.table.TableConstants;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.13 $, $Date: 2006/03/24 17:51:18 $
 */
public class Database extends Observable implements PropertyHolder, DriverProperties
{
	private static final String AUTO_COMMIT="Auto Commit";
	private static final String CATALOG="Catalog";
	private static final String TRANSACTION_ISOLATION="Transaction Isolation";
	private static final String HOLDABILITY="Holdability";
	private static final String PRODUCT_NAME="Product Name";
	private static final String PRODUCT_VERSION="Product Version";
	private static final String DRIVER_NAME="Driver Name";
	private static final String DRIVER_VERSION="Driver Version";
	private static final String JDBC_VERSION="JDBC Version";

	private String name;
	private String driver;
	private String group;
	private Map properties=new HashMap();
	private Map customProperties;

	private String password;
	private boolean passwordSet;

	private Connection connection;
	private Connection metaConnection;
	private Map metaData;
	private ObjectCache resultTableConfigurations=new ObjectCache(10);

	public Database(String name)
	{
		this.name=name;
	}

	public Connection connect(String password, boolean meta) throws Exception
	{
		Connection connection=meta ? this.metaConnection : this.connection;
		if (connection==null || connection.isClosed())
		{
			DatabaseDriver databaseDriver=getDatabaseDriver();
			connection=databaseDriver.createConnection(this, password);
			if (meta) this.metaConnection=connection;
			else this.connection=connection;
		}
		Object autoCommit=getProperty(DatabaseDriver.AUTO_COMMIT);
		if (Boolean.FALSE.equals(autoCommit)) connection.setAutoCommit(false);
		else if (Boolean.TRUE.equals(autoCommit)) connection.setAutoCommit(true);
		return connection;
	}

	public DatabaseDriver getDatabaseDriver()
	{
		return DatabaseDriverManager.getInstance().getDriver(driver);
	}

	public Connection getConnection()
	{
		try
		{
			if (connection==null || connection.isClosed()) return null;
		}
		catch (SQLException e)
		{
			return null;

		}
		return connection;
	}

	public Connection getMetaConnection()
	{
		try
		{
			if (metaConnection==null || metaConnection.isClosed()) return null;
		}
		catch (SQLException e)
		{
			return null;

		}
		return connection;
	}

	public void closeConnection()
	{
		if (connection!=null || metaConnection!=null)
		{
			System.out.print("Closing connection...");
			try
			{
				try
				{
					if (connection!=null && !connection.isClosed()) connection.close();
					if (metaConnection!=null && !metaConnection.isClosed()) metaConnection.close();
				}
				finally
				{
					connection=null;
					metaConnection=null;
				}
				System.out.println("Done");
			}
			catch (SQLException e)
			{
				System.out.println("Failed");
				System.out.println(e.getMessage());
			}
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name=name;
	}


	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		if (!StringUtils.equal(this.driver, driver))
		{
			this.driver=driver;
			closeConnection();
		}
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group=group;
	}

	public void clearAllProperties()
	{
		properties.clear();
	}

	public Map getPropertyMap()
	{
		return Collections.unmodifiableMap(properties);
	}

	public Set getProperties()
	{
		return properties.keySet();
	}

	public void setProperty(DriverProperty property, Object value)
	{
		Object oldValue=properties.get(property);
		if (oldValue!=null ? !oldValue.equals(value) : value!=null)
		{
			properties.put(property, value);
			closeConnection();
			setChanged();
			notifyObservers(new NotifyObject(property.getId()+" changed"));
		}
	}

	public Object getProperty(DriverProperty property)
	{
		Object value=properties.get(property);
		if (value==null) return property.getDefaultValue();
		return value;
	}

	private Map getCustomProprtiesMap()
	{
		if (customProperties==null) customProperties=new HashMap();
		return customProperties;
	}

	public Set getCustomProperties()
	{
		if (customProperties==null) return Collections.EMPTY_SET;
		return Collections.unmodifiableSet(customProperties.keySet());
	}

	public void clearAllCustomProperties()
	{
		if (customProperties!=null) customProperties.clear();
	}

	public void setCustomProperty(String name, String value)
	{
		getCustomProprtiesMap().put(name, value);
	}

	public String getCustomProperty(String name)
	{
		return (String)getCustomProprtiesMap().get(name);
	}

	public boolean isPasswordSet()
	{
		return passwordSet;
	}

	public void setPasswordSet(boolean passwordSet)
	{
		this.passwordSet=passwordSet;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password=password;
		setPasswordSet(true);
	}

	public String toString()
	{
		if (name!=null)
			return name;
		else
			return "";
	}

	public String[] getPropertyNames()
	{
		return new String[]{
			CATALOG,
			AUTO_COMMIT,
			TRANSACTION_ISOLATION,
			HOLDABILITY,
			PRODUCT_NAME,
			PRODUCT_VERSION,
			DRIVER_NAME,
			DRIVER_VERSION,
			JDBC_VERSION
		};
	}

	public Object getProperty(Project project, String propertyName)
	{
		try
		{
			return getMetaData(project).get(propertyName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Map getMetaData(Project project) throws Exception
	{
		if (metaData==null)
		{
			Connection connection=DatabaseUtils.connect(project, this, true);
			if (connection==null) return new HashMap();
			metaData=new HashMap();
			metaData.put(AUTO_COMMIT, Boolean.valueOf(connection.getAutoCommit()));
			metaData.put(CATALOG, connection.getCatalog());
			metaData.put(TRANSACTION_ISOLATION, DatabaseUtils.transactionIsolation(connection.getTransactionIsolation()));
			DatabaseMetaData dbMetaData=connection.getMetaData();
			metaData.put(PRODUCT_NAME, dbMetaData.getDatabaseProductName());
			metaData.put(PRODUCT_VERSION, dbMetaData.getDatabaseProductVersion());
			metaData.put(DRIVER_NAME, dbMetaData.getDriverName());
			metaData.put(DRIVER_VERSION, dbMetaData.getDriverVersion());
			try
			{
				metaData.put(HOLDABILITY, DatabaseUtils.holdability(connection.getHoldability()));
			}
			catch (AbstractMethodError error)
			{
			}
			try
			{
				int minorVersion=dbMetaData.getJDBCMinorVersion();
				int majorVersion=dbMetaData.getJDBCMajorVersion();
				metaData.put(JDBC_VERSION, majorVersion+"."+minorVersion);
			}
			catch (AbstractMethodError error)
			{
			}
		}
		return metaData;
	}

	public Collection getSchemas(Project project) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		String catalog=connection.getCatalog();
		ResultSet resultSet=metaData.getSchemas();
		try
		{
			List schemas=new LinkedList();
			while (resultSet.next())
			{
				String schemaName=resultSet.getString(1);
				schemas.add(new DatabaseSchema(this, schemaName, catalog));
			}
			if (schemas.isEmpty()) schemas.add(getDefaultSchema(project));
			return schemas;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public String getCatalog(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return null;
		return connection.getCatalog();
	}

	public int getIdentifiersCase(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return StringUtils.MIXED_CASE;
		DatabaseMetaData metaData=connection.getMetaData();
		if (metaData.storesLowerCaseIdentifiers()) return StringUtils.LOWER_CASE;
		else if (metaData.storesUpperCaseIdentifiers()) return StringUtils.UPPER_CASE;
		return StringUtils.MIXED_CASE;
	}

	public DatabaseSchema getDefaultSchema(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return null;
		DatabaseMetaData metaData=connection.getMetaData();
		String defaultSchema=getDatabaseDriver().getDefaultSchemaName(this);
		if (defaultSchema!=null)
		{
			if (metaData.storesLowerCaseIdentifiers()) defaultSchema=defaultSchema.toLowerCase();
			else if (metaData.storesUpperCaseIdentifiers()) defaultSchema=defaultSchema.toUpperCase();
		}
		return new DatabaseSchema(this, defaultSchema, connection.getCatalog());
	}

	public Collection getTableTypes(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getTableTypes();
		try
		{
			List tableTypes=new LinkedList();
			while (resultSet.next())
			{
				String tableType=resultSet.getString("TABLE_TYPE");
				if (tableType!=null) tableType=tableType.trim();
				tableTypes.add(tableType);
			}
			return tableTypes;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getTables(Project project, DatabaseSchema schema, String type) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getTables(schema.getCatalog(), schema.getSchemaName(), null, new String[]{type});
		try
		{
			List tables=new LinkedList();
			while (resultSet.next())
			{
				DatabaseTable table=new DatabaseTable(schema, resultSet.getString("TABLE_NAME"));
				table.setType(type);
				table.setRemark(resultSet.getString(5));
				tables.add(table);
			}
			return tables;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Set getPrimaryKeys(Project project, DatabaseTable table) throws Exception
	{
		DatabaseSchema schema=table.getSchema();
		return getPrimaryKeys(project, schema.getCatalog(), schema.getSchemaName(), table.getTableName());
	}

	public Set getPrimaryKeys(Project project, String catalog, String schemaName, String tableName) throws Exception
	{
		if (StringUtils.isEmpty(tableName)) return Collections.EMPTY_SET;
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getPrimaryKeys(catalog, schemaName, tableName);
		try
		{
			Set keys=new HashSet();
			while (resultSet.next())
			{
				keys.add(resultSet.getString("COLUMN_NAME"));
			}
			return keys;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public SetMap getExportedKeys(Project project, String catalog, String schemaName, String tableName) throws Exception
	{
		SetMap keys=new SetMap();
		if (StringUtils.isEmpty(tableName)) return keys;
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return keys;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getExportedKeys(catalog, schemaName, tableName);
		try
		{
			while (resultSet.next())
			{
				String column=resultSet.getString("PKCOLUMN_NAME");
				String referenceSchema=resultSet.getString("FKTABLE_SCHEM");
				String referenceTable=resultSet.getString("FKTABLE_NAME");
				String referenceColumn=resultSet.getString("FKCOLUMN_NAME");
				keys.add(column, new ColumnData(referenceSchema, referenceTable, referenceColumn));
			}
			return keys;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Map getImportedKeys(Project project, String catalog, String schemaName, String tableName) throws Exception
	{
		if (StringUtils.isEmpty(tableName)) return Collections.EMPTY_MAP;
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_MAP;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getImportedKeys(catalog, schemaName, tableName);
		try
		{
			Map keys=new HashMap();
			Set complexKeys=new HashSet();
			while (resultSet.next())
			{
				String column=resultSet.getString("FKCOLUMN_NAME");
				String referenceSchema=resultSet.getString("PKTABLE_SCHEM");
				String referenceTable=resultSet.getString("PKTABLE_NAME");
				String referenceColumn=resultSet.getString("PKCOLUMN_NAME");
				if (!keys.containsKey(column) && !complexKeys.contains(column))
				{
					keys.put(column, new ColumnData(referenceSchema, referenceTable, referenceColumn));
				}
				else
				{
					keys.remove(column);
					complexKeys.add(column);
				}
			}
			return keys;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getImportedKeys(Project project, DatabaseTable table) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		DatabaseSchema schema=table.getSchema();
		ResultSet resultSet=metaData.getImportedKeys(schema.getCatalog(), schema.getName(), table.getTableName());
		try
		{
			Map keys=new HashMap();
			while (resultSet.next())
			{
				String keyName=resultSet.getString("FK_NAME");
				ImportedKey key=(ImportedKey)keys.get(keyName);
				if (key==null)
				{
					keys.put(keyName, key=new ImportedKey(table, keyName));
					key.setPrimaryKeyName(resultSet.getString("PK_NAME"));
					key.setUpdateRule(resultSet.getShort("UPDATE_RULE"));
					key.setDeleteRule(resultSet.getShort("DELETE_RULE"));
					key.setDeferrability(resultSet.getShort("DEFERRABILITY"));
				}

				ImportedKeyPart keyPart=key.createPart(resultSet.getShort("KEY_SEQ"));
				keyPart.setPrimaryKeyCatalog(resultSet.getString("PKTABLE_CAT"));
				keyPart.setPrimaryKeySchema(resultSet.getString("PKTABLE_SCHEM"));
				keyPart.setPrimaryKeyTable(resultSet.getString("PKTABLE_NAME"));
				keyPart.setPrimaryKeyColumn(resultSet.getString("PKCOLUMN_NAME"));
				keyPart.setColumn(resultSet.getString("FKCOLUMN_NAME"));
			}
			return keys.values();
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getExportedKeys(Project project, DatabaseTable table) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		DatabaseSchema schema=table.getSchema();
		ResultSet resultSet=metaData.getExportedKeys(schema.getCatalog(), schema.getName(), table.getTableName());
		try
		{
			Map keys=new HashMap();
			while (resultSet.next())
			{
				String keyName=resultSet.getString("FK_NAME");
				ExportedKey key=(ExportedKey)keys.get(keyName);
				if (key==null)
				{
					keys.put(keyName, key=new ExportedKey(table, keyName));
					key.setForeignKeyName(resultSet.getString("FK_NAME"));
					key.setUpdateRule(resultSet.getShort("UPDATE_RULE"));
					key.setDeleteRule(resultSet.getShort("DELETE_RULE"));
					key.setDeferrability(resultSet.getShort("DEFERRABILITY"));
				}

				ExportedKeyPart keyPart=key.createPart(resultSet.getShort("KEY_SEQ"));
				keyPart.setForeignKeyCatalog(resultSet.getString("FKTABLE_CAT"));
				keyPart.setForeignKeySchema(resultSet.getString("FKTABLE_SCHEM"));
				keyPart.setForeignKeyTable(resultSet.getString("FKTABLE_NAME"));
				keyPart.setForeignKeyColumn(resultSet.getString("FKCOLUMN_NAME"));
				keyPart.setColumn(resultSet.getString("PKCOLUMN_NAME"));
			}
			return keys.values();
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getTableColumns(Project project, DatabaseTable table) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		DatabaseSchema schema=table.getSchema();
		ResultSet resultSet=metaData.getColumns(schema.getCatalog(), schema.getSchemaName(), table.getTableName(), null);
		try
		{
			List tables=new LinkedList();
			while (resultSet.next())
			{
				DatabaseColumn column=new DatabaseColumn(table, resultSet.getString("COLUMN_NAME"));
				column.setDefaultValue(String.valueOf(resultSet.getObject("COLUMN_DEF")));
				column.setType(resultSet.getString("TYPE_NAME"));
				column.setSize(resultSet.getInt("COLUMN_SIZE"));
				String isNullable=resultSet.getString("IS_NULLABLE");
				if ("NO".equals(isNullable))
					column.setNullable(Boolean.FALSE);
				else if ("YES".equals(isNullable)) column.setNullable(Boolean.TRUE);
				column.setRemarks(resultSet.getString("REMARKS"));
				tables.add(column);
			}
			return tables;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getTableIndices(Project project, DatabaseTable table) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		DatabaseSchema schema=table.getSchema();
		ResultSet resultSet=metaData.getIndexInfo(schema.getCatalog(), schema.getSchemaName(), table.getTableName(), false, true);
		try
		{
			List indices=new LinkedList();
			while (resultSet.next())
			{
				Index index=new Index(table, resultSet.getString("INDEX_NAME"));
				index.setNonUnique(resultSet.getBoolean("NON_UNIQUE"));
				index.setType(resultSet.getShort("TYPE"));
				index.setColumnName(resultSet.getString("COLUMN_NAME"));
				index.setSorting(resultSet.getString("ASC_OR_DESC"));
				index.setCardinality(resultSet.getInt("CARDINALITY"));
				index.setPages(resultSet.getInt("PAGES"));
				index.setFilterCondition(resultSet.getString("FILTER_CONDITION"));
				indices.add(index);
			}
			return indices;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getProcecures(Project project, DatabaseSchema schema) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getProcedures(schema.getCatalog(), schema.getSchemaName(), null);
		try
		{
			List procedures=new LinkedList();
			while (resultSet.next())
			{
				StoredProcedure procedure=new StoredProcedure(schema, resultSet.getString("PROCEDURE_NAME"));
				procedure.setRemark(resultSet.getString("REMARKS"));
				procedure.setResult(resultSet.getShort("PROCEDURE_TYPE"));
				if (!procedures.contains(procedure)) procedures.add(procedure);
			}
			return procedures;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getProcedureParameters(Project project, StoredProcedure procedure) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		DatabaseSchema schema=procedure.getSchema();
		ResultSet resultSet=metaData.getProcedureColumns(schema.getCatalog(), schema.getSchemaName(), procedure.getName(), null);
		try
		{
			List parameters=new LinkedList();
			while (resultSet.next())
			{
				ProcedureParameter parameter=new ProcedureParameter(procedure, resultSet.getString("COLUMN_NAME"));
				parameter.setType(resultSet.getShort("COLUMN_TYPE"));
				parameter.setDataType(resultSet.getInt("DATA_TYPE"));
				parameter.setTypeName(resultSet.getString("TYPE_NAME"));
				parameter.setPrecision(resultSet.getLong("PRECISION"));
				parameter.setLength(resultSet.getInt("LENGTH"));
				parameter.setScale(resultSet.getShort("SCALE"));
				parameter.setRadix(resultSet.getShort("RADIX"));
				parameter.setNullable(resultSet.getShort("NULLABLE"));
				parameter.setRemarks(resultSet.getString("REMARKS"));
				parameters.add(parameter);
			}
			return parameters;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public Collection getNumericFunctions(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		return StringUtils.tokenize(metaData.getNumericFunctions(), ",");
	}

	public Collection getStringFunctions(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		return StringUtils.tokenize(metaData.getStringFunctions(), ",");
	}

	public Collection getDateTimeFunctions(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		return StringUtils.tokenize(metaData.getTimeDateFunctions(), ",");
	}

	public Collection getSystemFunctions(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		return StringUtils.tokenize(metaData.getSystemFunctions(), ",");
	}

	public Collection getDataTypes(Project project) throws SQLException
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return Collections.EMPTY_SET;
		DatabaseMetaData metaData=connection.getMetaData();
		ResultSet resultSet=metaData.getTypeInfo();
		try
		{
			List types=new LinkedList();
			while (resultSet.next())
			{
				DataType type=new DataType(resultSet.getString("TYPE_NAME"));
				type.setDataType(resultSet.getInt("DATA_TYPE"));
				type.setPrecision(resultSet.getLong("PRECISION"));
				type.setLiteralPrefix(resultSet.getString("LITERAL_PREFIX"));
				type.setLiteralSuffix(resultSet.getString("LITERAL_SUFFIX"));
				type.setCreateParams(resultSet.getString("CREATE_PARAMS"));
				type.setNullable(resultSet.getShort("NULLABLE"));
				type.setCaseSensitive(resultSet.getBoolean("CASE_SENSITIVE"));
				type.setSearchable(resultSet.getShort("SEARCHABLE"));
				type.setUnsigned(resultSet.getBoolean("UNSIGNED_ATTRIBUTE"));
				type.setFixedPrecisionScale(resultSet.getBoolean("FIXED_PREC_SCALE"));
				type.setAutoIncrement(resultSet.getBoolean("AUTO_INCREMENT"));
				type.setLocalTypeName(resultSet.getString("LOCAL_TYPE_NAME"));
				type.setMinimumScale(resultSet.getShort("MINIMUM_SCALE"));
				type.setMaximumScale(resultSet.getShort("MAXIMUM_SCALE"));
				types.add(type);
			}
			return types;
		}
		finally
		{
			if (resultSet!=null) resultSet.close();
		}
	}

	public ResultTableConfiguration getResultTableConfiguration(SQLStatement statement)
	{
		String text=statement.getNormalizedText();
		ResultTableConfiguration data=(ResultTableConfiguration)resultTableConfigurations.get(text);
		if (data==null) return new ResultTableConfiguration(this, text);
		return data;
	}

	public void configurationChanged(String statement, ResultTableConfiguration tableConfiguration)
	{
		resultTableConfigurations.put(statement, tableConfiguration);
	}

	public static Database readDatabase(Element element, boolean encodePasswords)
	{
		Database database=new Database(element.getAttributeValue("name"));
		database.setDriver(element.getAttributeValue("driver"));
		database.setGroup(element.getAttributeValue("group"));
		DatabaseDriver driver=DatabaseDriverManager.getInstance().getDriver(database.getDriver());
		if (driver!=null)
		{
			Iterator itProps=driver.getDriverProperties().iterator();
			while (itProps.hasNext())
			{
				DriverProperty property=(DriverProperty)itProps.next();
				if (property.getType()==Integer.class)
					database.setProperty(property, PluginUtils.getInteger(element, property.getId(), (Integer)property.getDefaultValue()));
				else if (property.getType()==Boolean.class)
					database.setProperty(property, PluginUtils.getBoolean(element, property.getId(), (Boolean)property.getDefaultValue()));
				else
					database.setProperty(property, element.getAttributeValue(property.getId()));
			}
		}
		String password=element.getAttributeValue("password");
		if (password!=null)
		{
			if (encodePasswords)
			{
				if (Encoder.isAvailable())
				{
					try
					{
						password=Encoder.getInstance().decrypt(password, "kiwisoft");
						database.setPassword(password);
					}
					catch (Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
			else
				database.setPassword(password);
		}
		else
		{
			Boolean noPassword=PluginUtils.getBoolean(element, "noPassword", null);
			if (Boolean.TRUE.equals(noPassword)) database.setPassword(null);
		}
		Element customPropsElement=element.getChild("customProperties");
		if (customPropsElement!=null)
		{
			for (Iterator it=customPropsElement.getAttributes().iterator(); it.hasNext();)
			{
				Attribute attribute=(Attribute)it.next();
				database.setCustomProperty(attribute.getName(), attribute.getValue());
			}
		}
		List resultElements=element.getChildren("resultTable");
		if (resultElements!=null)
		{
			for (Iterator it=resultElements.iterator(); it.hasNext();)
			{
				Element configElement=(Element)it.next();
				String statement=StringUtils.decodeURL(configElement.getAttributeValue("statement"));
				ResultTableConfiguration tableConfiguration=new ResultTableConfiguration(database, statement);
				boolean initalized=PluginUtils.getBoolean(configElement, "initialized", false);
				tableConfiguration.setInitalized(initalized);
				List columnElements=configElement.getChildren("column");
				if (columnElements!=null)
				{
					for (Iterator itColumns=columnElements.iterator(); itColumns.hasNext();)
					{
						Element columnElement=(Element)itColumns.next();
						Integer index=PluginUtils.getInteger(columnElement, "index", null);
						if (index!=null)
						{
							String format=PluginUtils.getString(columnElement, "format", null);
							if (format!=null) tableConfiguration.setFormat(index.intValue(), format);
							String font=PluginUtils.getString(columnElement, "font", null);
							if (font!=null) tableConfiguration.setFontName(index.intValue(), font);
							Integer sortIndex=PluginUtils.getInteger(columnElement, "sortIndex", null);
							Integer sortDir=PluginUtils.getInteger(columnElement, "sortDir", null);
							if (sortDir!=null && sortIndex!=null)
							{
								if (sortIndex.intValue()>=0 && (TableConstants.ASCEND.equals(sortDir) || SortableTableModel.DESCEND.equals(sortDir)))
								{
									tableConfiguration.setSortIndex(index, sortIndex.intValue());
									tableConfiguration.setSortDirection(index, sortDir);
								}
							}
							Integer width=PluginUtils.getInteger(columnElement, "width", null);
							if (width!=null) tableConfiguration.setWidth(index, width.intValue());
							Integer viewIndex=PluginUtils.getInteger(columnElement, "viewIndex", null);
							if (viewIndex!=null && viewIndex.intValue()>=0) tableConfiguration.setIndex(index, viewIndex.intValue());
							tableConfiguration.setHidden(index, PluginUtils.getBoolean(columnElement, "hidden", false));
						}
					}
				}
			}
		}
		return database;
	}

	public Element writeDatabase()
	{
		SQLPluginAppConfig configuration=SQLPluginAppConfig.getInstance();

		Element element=new Element("database");
		PluginUtils.setValue(element, "name", getName());
		PluginUtils.setValue(element, "driver", getDriver());
		PluginUtils.setValue(element, "group", getGroup());
		Iterator itProps=getProperties().iterator();
		while (itProps.hasNext())
		{
			DriverProperty property=(DriverProperty)itProps.next();
			Object value=getProperty(property);
			if (value!=null) PluginUtils.setValue(element, property.getId().replace(' ', '_'), value);
		}
		if (isPasswordSet())
		{
			String password=getPassword();
			if (configuration.isSavePasswords())
			{
				if (password!=null)
				{
					if (configuration.isEncodePasswords())
					{
						if (Encoder.isAvailable())
						{
							try
							{
								password=Encoder.getInstance().encrypt(password, "kiwisoft");
								PluginUtils.setValue(element, "password", password);
							}
							catch (GeneralSecurityException e)
							{
								System.out.println(e.getMessage());
							}
						}
					}
					else
						PluginUtils.setValue(element, "password", password);
				}
				else
					PluginUtils.setValue(element, "noPassword", Boolean.TRUE);
			}
		}
		Set customProperties=getCustomProperties();
		if (!customProperties.isEmpty())
		{
			Element customPropsElement=new Element("customProperties");
			for (Iterator it=customProperties.iterator(); it.hasNext();)
			{
				String propertyName=(String)it.next();
				if (!StringUtils.isEmpty(propertyName))
					PluginUtils.setValue(customPropsElement, propertyName, getCustomProperty(propertyName));
			}
			element.addContent(customPropsElement);
		}
		if (configuration.isSaveResultTableConfiguration())
		{
			for (Iterator it=resultTableConfigurations.keyIterator(); it.hasNext();)
			{
				String key=(String)it.next();
				ResultTableConfiguration tableConfiguration=(ResultTableConfiguration)resultTableConfigurations.get(key, false);
				Element configElement=new Element("resultTable");
				configElement.setAttribute("statement", StringUtils.encodeURL(key));
				configElement.setAttribute("initialized", String.valueOf(tableConfiguration.isInitalized()));
				for (Iterator itColumns=new TreeSet(tableConfiguration.getColumns()).iterator(); itColumns.hasNext();)
				{
					Integer column=(Integer)itColumns.next();
					Element columnElement=new Element("column");
					PluginUtils.setValue(columnElement, "index", column);
					boolean changed=PluginUtils.setValue(columnElement, "format", tableConfiguration.getFormat(column.intValue()));
					changed=PluginUtils.setValue(columnElement, "font", tableConfiguration.getFontName(column.intValue())) || changed;
					int index=tableConfiguration.getIndex(column);
					if (index>=0 && index!=column.intValue())
						changed=PluginUtils.setValue(columnElement, "viewIndex", new Integer(index)) || changed;
					if (tableConfiguration.isHidden(column))
						PluginUtils.setValue(columnElement, "hidden", "true");
					int sortIndex=tableConfiguration.getSortIndex(column);
					if (sortIndex>=0)
					{
						changed=PluginUtils.setValue(columnElement, "sortIndex", new Integer(sortIndex)) || changed;
						changed=PluginUtils.setValue(columnElement, "sortDir", tableConfiguration.getSortDirection(column)) || changed;
					}
					changed=PluginUtils.setValue(columnElement, "width", new Integer(tableConfiguration.getWidth(column))) || changed;
					if (changed) configElement.addContent(columnElement);
				}
				element.addContent(configElement);
			}
		}
		return element;
	}

	public String getURL()
	{
		DatabaseDriver driver=getDatabaseDriver();
		if (driver!=null) return driver.buildURL(this);
		return null;
	}

	public DatabaseSchema loadSnapshot(Project project) throws Exception
	{
		Connection connection=DatabaseUtils.connect(project, this, true);
		if (connection==null) return null;
		DatabaseSchema schema=getDefaultSchema(project);
		if (schema!=null)
		{
			Collection tables=schema.getTables(project, "TABLE");
			for (Iterator it=tables.iterator(); it.hasNext();)
			{
				DatabaseTable table=(DatabaseTable)it.next();
				PreparedStatement statement=connection.prepareStatement("select count(*) from "+table.getTableName());
				try
				{
					ResultSet resultSet=statement.executeQuery();
					while (resultSet.next())
					{
						long count=resultSet.getLong(1);
						table.setRowCount(count);
					}
					resultSet.close();
				}
				finally
				{
					if (statement!=null) statement.close();
				}
			}
		}
		return schema;
	}

	public boolean isConnected()
	{
		try
		{
			return connection!=null && !connection.isClosed();
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public boolean isDriverValid()
	{
		DatabaseDriver driver=getDatabaseDriver();
		return driver!=null && driver.isValid(this);
	}
}
