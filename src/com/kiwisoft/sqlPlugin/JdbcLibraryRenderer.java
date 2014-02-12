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

import java.awt.Component;
import java.awt.Color;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.util.IconLoader;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.2 $, $Date: 2006/03/24 17:59:51 $
 */
public class JdbcLibraryRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		setComponentOrientation(list.getComponentOrientation());
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value instanceof JdbcLibrary)
		{
			JdbcLibrary library=(JdbcLibrary)value;
			VirtualFile virtualFile=library.getVirtualFile();
			if (virtualFile!=null)
			{
				setText(virtualFile.getPresentableUrl());
				if (virtualFile.isDirectory()) setIcon(IconLoader.getIcon("/nodes/folder.png"));
				else setIcon(virtualFile.getFileType().getIcon());
			}
			else
			{
				setForeground(Color.red);
				String vfsUrl=library.getVfsUrl();
				try
				{
					String protocol=VirtualFileManager.extractProtocol(vfsUrl);
					String path=VirtualFileManager.extractPath(vfsUrl);
					VirtualFileSystem vfs=VirtualFileManager.getInstance().getFileSystem(protocol);
					setText(vfs.extractPresentableUrl(path));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					setText(vfsUrl);
				}
			}
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

		return this;
	}
}
