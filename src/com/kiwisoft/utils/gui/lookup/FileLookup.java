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
package com.kiwisoft.utils.gui.lookup;

import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.6 $, $Date: 2006/03/24 18:04:41 $
 */
public class FileLookup implements DialogLookup
{
	private int selectionMode;
	private boolean mustExist;

	public FileLookup(int mode, boolean mustExist)
	{
		selectionMode=mode;
		this.mustExist=mustExist;
	}

	public void open(JTextField field)
	{
		JFileChooser chooser=new JFileChooser();
		chooser.setFileSelectionMode(selectionMode);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		if (selectionMode==JFileChooser.DIRECTORIES_ONLY)
		{
			chooser.setDialogTitle("Choose Directory");
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.addChoosableFileFilter(new DirectoriesFilter());
		}
		else
			chooser.setDialogTitle("Choose File");

		File file=new File(field.getText());
		if (file.exists())
			chooser.setCurrentDirectory(file);
		else
		{
			String currentDir=getCurrentDirectory();
			if (currentDir!=null)
			{
				File currentDirectory=new File(currentDir);
				if (currentDirectory.isDirectory()) chooser.setCurrentDirectory(currentDirectory);
			}
		}

		if (JFileChooser.APPROVE_OPTION==chooser.showDialog(field, "Select"))
		{
			file=chooser.getSelectedFile();
			if (!mustExist || file.exists())
			{
				field.setText(file.getAbsolutePath());
				setCurrentDirectory(file.getParent());
			}
		}
		return;
	}

	public String getCurrentDirectory()
	{
		return null;
	}

	public void setCurrentDirectory(String path)
	{
	}

	public Icon getIcon()
	{
		return IconManager.getIcon("/icons/open.png");
	}

	private static class DirectoriesFilter extends FileFilter
	{
		/**
		 * Whether the given file is accepted by this filter.
		 */
		public boolean accept(File f)
		{
			return f.isDirectory();
		}

		/**
		 * The description of this filter. For example: "JPG and GIF Images"
		 *
		 * @see javax.swing.filechooser.FileView#getName
		 */
		public String getDescription()
		{
			return "Directories";
		}
	}
}
