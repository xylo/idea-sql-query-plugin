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

import java.sql.*;

import javax.swing.JOptionPane;

import com.intellij.openapi.project.Project;
import com.kiwisoft.sqlPlugin.PasswordDialog;
import com.kiwisoft.utils.idea.PluginUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.5 $, $Date: 2006/03/24 17:52:12 $
 */
public class DatabaseUtils
{
	private DatabaseUtils()
	{
	}

	public static Connection connect(Project project, Database database, boolean meta)
	{
		if (database==null) throw new RuntimeException("No database specified.");
		Connection connection;
		if (database.isPasswordSet())
		{
			try
			{
				connection=database.connect(database.getPassword(), meta);
				return connection;
			}
			catch (SQLException e)
			{
				if (!database.getDatabaseDriver().isPasswordFailure(e))
				{
					System.err.println("SQLException: Error Code="+e.getErrorCode());
					e.printStackTrace();
					database.setPasswordSet(false);
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return null;
				}
				else
				{
					database.setPasswordSet(false);
					JOptionPane.showMessageDialog(null, "Access Denied", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		while (true)
		{
			try
			{
				PasswordDialog dialog=new PasswordDialog(project, database.getName());
				PluginUtils.showDialog(dialog, true, true);
				if (dialog.getReturnValue())
				{
					String password=dialog.getPassword();
					connection=database.connect(password, meta);
					database.setPassword(password);
					return connection;
				}
				else return null;
			}
			catch (SQLException e)
			{
				if (!database.getDatabaseDriver().isPasswordFailure(e))
				{
					System.err.println("SQLException: Error Code="+e.getErrorCode());
					e.printStackTrace();
					database.setPasswordSet(false);
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					return null;
				}
				else
				{
					database.setPasswordSet(false);
					JOptionPane.showMessageDialog(null, "Access Denied", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
	}

	public static String transactionIsolation(int value)
	{
		switch (value)
		{
			case Connection.TRANSACTION_READ_UNCOMMITTED:
				return "read uncommited";
			case Connection.TRANSACTION_READ_COMMITTED:
				return "read commited";
			case Connection.TRANSACTION_REPEATABLE_READ:
				return "repeatable read";
			case Connection.TRANSACTION_SERIALIZABLE:
				return "transaction serializable";
			case Connection.TRANSACTION_NONE:
				return "none";
		}
		return null;
	}

	public static Object holdability(int holdability)
	{
		switch (holdability)
		{
			case ResultSet.CLOSE_CURSORS_AT_COMMIT:
				return "close cursors at commit";
			case ResultSet.HOLD_CURSORS_OVER_COMMIT:
				return "hold cursors over commit";
		}
		return null;
	}

	public static String getTypeString(int type)
	{
		switch (type)
		{
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.ARRAY:
				return "ARRAY";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.BLOB:
				return "BLOB";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.CHAR:
				return "CHAR";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REF:
				return "REF";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.BIT:
				return "BIT";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.BINARY:
				return "BINARY";
			case Types.OTHER:
				return "OTHER";
			case Types.TIME:
				return "TIME";
			case Types.INTEGER:
				return "INTEGER";
			case Types.CLOB:
				return "CLOB";
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.BIGINT:
				return "BIGINT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.STRUCT:
				return "STRUCT";
			case Types.DATE:
				return "DATE";
			case Types.NULL:
				return "NULL";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.DATALINK:
				return "DATALINK";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.REAL:
				return "REAL";
		}
		return "<Unknown>";
	}

	public static Object getObject(ResultSet resultSet, int column, int type, ClassLoader classLoader, boolean loadLOBs) throws SQLException
	{
		switch (type)
		{
			case Types.BLOB:
				Blob blob=resultSet.getBlob(column);
				return blob!=null ? new BLOBWrapper(blob, classLoader, loadLOBs) : null;
			case Types.CLOB:
				Clob clob=resultSet.getClob(column);
				return clob!=null ? new CLOBWrapper(clob, loadLOBs) : null;
			case Types.TIME:
				return resultSet.getTime(column);
			case Types.TIMESTAMP:
			case Types.DATE:
				try
				{
					return resultSet.getTimestamp(column);
				}
				catch (SQLException e)
				{
					// Just to be sure it works on most databases
					return resultSet.getDate(column);
				}
		}
		return resultSet.getObject(column);
	}

	public static Class getClass(int type)
	{
		switch (type)
		{
			case Types.BLOB: return BLOBWrapper.class;
			case Types.CLOB: return CLOBWrapper.class;
			case Types.TIMESTAMP: return java.sql.Timestamp.class;
			case Types.TIME: return java.sql.Time.class;
			case Types.DATE: return java.util.Date.class;
		}
		return null;
	}

	public static void setParameter(PreparedStatement statement, int index, Object value, int jdbcType) throws SQLException
	{
		if (value==null) statement.setNull(index, jdbcType);
		else if (value instanceof String) statement.setString(index, (String)value);
		else if (value instanceof Byte) statement.setByte(index, ((Byte)value).byteValue());
		else if (value instanceof Short) statement.setShort(index, ((Short)value).byteValue());
		else if (value instanceof Integer) statement.setInt(index, ((Integer)value).intValue());
		else if (value instanceof Long) statement.setLong(index, ((Long)value).longValue());
		else if (value instanceof Float) statement.setFloat(index, ((Float)value).floatValue());
		else if (value instanceof Double) statement.setDouble(index, ((Double)value).doubleValue());
		else if (value instanceof Boolean) statement.setBoolean(index, ((Boolean)value).booleanValue());
		else if (value instanceof Date) statement.setDate(index, (Date)value);
		else if (value instanceof Timestamp) statement.setTimestamp(index, (Timestamp)value);
		else statement.setObject(index, value);
	}
}
