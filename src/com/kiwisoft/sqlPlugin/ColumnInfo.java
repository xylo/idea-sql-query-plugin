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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.awt.Font;
import java.awt.Color;

import com.kiwisoft.utils.gui.ObjectStyle;
import com.kiwisoft.utils.gui.ColorBarDecorator;
import com.kiwisoft.db.ColumnData;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.7 $, $Date: 2006/03/24 17:59:51 $
 */
public class ColumnInfo
{
	private static final ObjectStyle KEY_STYLE=new ObjectStyle(new ColorBarDecorator(null, 10)
	{
		public Color getColor()
		{
			return SQLPluginAppConfig.getInstance().getPrimaryKeyColor();
		}
	});
	private static final ObjectStyle REFERENCE_STYLE=new ObjectStyle(new ColorBarDecorator(null, 10)
	{
		public Color getColor()
		{
			return SQLPluginAppConfig.getInstance().getForeignKeyColor();
		}
	});

	private String format;
	private String name;
	private int jdbcType;
	private Class type;
	private boolean isPrimaryKey;
	private ColumnData foreignKey;
	private boolean editable;
	private Font font;
	private ObjectStyle style;
	private String schemaName;
	private Set exportedKeys;
	private String columnClassName;

	public ColumnInfo(String name)
	{
		this.name=name;
	}

	public String getName()
	{
		return name;
	}

	public int getJdbcType()
	{
		return jdbcType;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format=format;
	}

	public Class getType()
	{
		return type;
	}

	public void setType(Class type)
	{
		this.type=type;
		editable=type!=null && (String.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)
								|| Long.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)
								|| Float.class.isAssignableFrom(type) || BigInteger.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
								|| Boolean.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type));
	}

	public boolean isPrimaryKey()
	{
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean value)
	{
		isPrimaryKey=value;
		style=null;
	}

	public boolean isForeignKey()
	{
		return foreignKey!=null;
	}

	public boolean isEditable()
	{
		return editable;
	}

	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font=font;
		style=null;
	}

	public ObjectStyle getStyle()
	{
		if (style==null)
		{
			if (isPrimaryKey)
			{
				if (font!=null) style=new ObjectStyle(KEY_STYLE, font);
				else style=KEY_STYLE;
			}
			else if (foreignKey!=null)
			{
				if (font!=null) style=new ObjectStyle(REFERENCE_STYLE, font);
				else style=REFERENCE_STYLE;
			}
			else style=new ObjectStyle(font);
		}
		return style;
	}

	public void setForeignKey(ColumnData foreignKey)
	{
		this.foreignKey=foreignKey;
}

	public void setSchemaName(String schemaName)
	{
		this.schemaName=schemaName;
	}

	public void setJdbcType(int jdbcType)
	{
		this.jdbcType=jdbcType;
	}

	public ColumnData getForeignKey()
	{
		return foreignKey;
	}

	public String getSchemaName()
	{
		return schemaName;
}

	public void setExportedKeys(Set exportedKeys)
	{
		this.exportedKeys=exportedKeys;
	}

	public boolean isExportedKey()
	{
		return exportedKeys!=null && !exportedKeys.isEmpty();
	}

	public Set getExportedKeys()
	{
		return exportedKeys;
	}

	public void setColumnClassName(String columnClassName)
	{
		this.columnClassName=columnClassName;
	}

	public String getColumnClassName()
	{
		return columnClassName;
	}
}
