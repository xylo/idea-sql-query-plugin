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
package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import com.kiwisoft.db.Database;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.FileDescriptor;
import com.kiwisoft.sqlPlugin.dataLoad.io.DataFileReader;
import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;
import com.kiwisoft.utils.StringUtils;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2002/02/22 10:00:24 $
 */
public class DataLoadWizard extends WizardDialog
{
	private DataLoadDescriptor descriptor;
	private Database database;
	private VirtualFile file;
	private List sampleData=new ArrayList();
	private String[] sampleDataTitles;
	private int sampleDataColumnCount=-1;
	private Set sourceColumns;

	public DataLoadWizard(Project project, Database database, VirtualFile file)
	{
		super(project, "Data Load Wizard");
		this.database=database;
		this.file=file;
	}

	public DataLoadDescriptor getDescriptor()
	{
		return descriptor;
	}

	public void setDescriptor(DataLoadDescriptor descriptor)
	{
		this.descriptor=descriptor;
	}

	public Database getDatabase()
	{
		return database;
	}

	public VirtualFile getFile()
	{
		return file;
	}

	protected WizardPane getFirstPane()
	{
		return new WelcomePage(this);
	}

	public void loadSampleData() throws IOException
	{
		sampleData.clear();
		sampleDataColumnCount=0;
		FileDescriptor fileDescriptor=descriptor.getFileDescriptor();
		// todo: edited line
		DataFileReader dataReader=fileDescriptor.createReader(new InputStreamReader(file.getInputStream()));
		dataReader.start();
		String[] row;
		String[] titles=dataReader.getTitles();
		if (sampleDataTitles!=null) sampleDataColumnCount=sampleDataTitles.length;
		while ((row=dataReader.readRow())!=null && sampleData.size()<50)
		{
			sampleDataColumnCount=Math.max(sampleDataColumnCount, row.length);
			sampleData.add(row);
		}
		sampleDataTitles=new String[sampleDataColumnCount];
		if (titles!=null) System.arraycopy(titles, 0, sampleDataTitles, 0, titles.length);
		for (int i=0; i<sampleDataTitles.length; i++)
		{
			if (StringUtils.isEmpty(sampleDataTitles[i])) sampleDataTitles[i]="Column #"+(i+1);
		}
		dataReader.close();
	}

	public List getSampleData()
	{
		return sampleData;
	}

	public String[] getSampleDataTitles()
	{
		return sampleDataTitles;
	}

	public int getSampleDataColumnCount()
	{
		return sampleDataColumnCount;
	}

	public void setSourceColumns(Set sourceColumns)
	{
		this.sourceColumns=sourceColumns;
	}

	public Set getSourceColumns()
	{
		return sourceColumns;
	}

	public String getHelpTopic()
	{
		return "KiwiSQL.dataLoadWizard";
	}
}
