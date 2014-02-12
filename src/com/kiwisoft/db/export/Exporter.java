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
package com.kiwisoft.db.export;

import java.io.File;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import com.kiwisoft.db.sql.SQLStatement;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 17:53:37 $
 */
public interface Exporter
{
	public boolean isEnabled();

	public String getName();

    public String getIcon();

    public FileFilter getFileFilter();

    public void exportTable(JTable table, SQLStatement statement, File file, ExportConfiguration configuration) throws Exception;
}
