package com.kiwisoft.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 16.11.2006
 * Time: 18:37:00
 * To change this template use File | Settings | File Templates.
 */
public class SQLValue
{
	private Object value;
	private int jdbcType;

	public SQLValue(Object value, int jdbcType)
	{
		this.value=value;
		this.jdbcType=jdbcType;
	}

	public void set(PreparedStatement statement, int index) throws SQLException
	{
		DatabaseUtils.setParameter(statement, index, value, jdbcType);
	}

	public Object getValue()
	{
		return value;
	}
}
