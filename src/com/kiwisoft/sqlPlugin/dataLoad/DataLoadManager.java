/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 04.04.2006
 * Time: 21:14:58
 * To change this template use File | Settings | File Templates.
 */
package com.kiwisoft.sqlPlugin.dataLoad;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import com.intellij.openapi.application.PathManager;

import com.kiwisoft.utils.xml.*;

public class DataLoadManager
{
	private static final String PATH=System.getProperty("sqlPlugin.dataLoads")==null
									 ? PathManager.getConfigPath()+File.separator+"sqlPlugin"+File.separator+"dataLoads.xml"
									 : System.getProperty("sqlPlugin.dataLoads");

	private static DataLoadManager instance;

	public static DataLoadManager getInstance()
	{
		if (instance==null)
		{
			instance=new DataLoadManager();
			instance.loadDescriptors();
		}
		return instance;
	}

	public static boolean hasInstance()
	{
		return instance!=null;
	}

	private Map descriptors=new HashMap();

	private DataLoadManager()
	{
	}

	private void loadDescriptors()
	{
		File file=new File(PATH);
		if (file.exists())
		{
			XMLHandler handler=new XMLHandler();
			handler.addTagMapping("dataLoads", new MyXMLAdapter());
			handler.addTagMapping("dataLoad", DataLoadDescriptor.class);
			handler.addTagMapping("fileType", FileDescriptor.getXMLFactory());
			handler.addTagMapping("sourceColumn", SourceColumnDescriptor.class);
			handler.addTagMapping("table", DataLoadDescriptor.TableXMLAdapter.class);
			handler.addTagMapping("column", TargetColumnDescriptor.class);
			handler.setTrimText(false);
			handler.loadFile(file);
		}
	}

	public void saveDescriptors()
	{
		File file=new File(PATH);
		file.getParentFile().mkdirs();
		try
		{
			XMLWriter xml=new XMLWriter(new FileOutputStream(file), null);
			xml.start();
			xml.startElement("dataLoads");
			for (Iterator it=descriptors.values().iterator(); it.hasNext();)
			{
				DataLoadDescriptor descriptor=(DataLoadDescriptor)it.next();
				descriptor.writeXML(xml);
			}
			xml.closeElement("dataLoads");
			xml.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void addDescriptor(DataLoadDescriptor descriptor)
	{
		descriptors.put(descriptor.getName(), descriptor);
	}

	public Collection getDescriptors()
	{
		return Collections.unmodifiableCollection(descriptors.values());
	}

	public void setDescriptors(Set descriptors)
	{
		this.descriptors.clear();
		if (descriptors!=null)
		{
			for (Iterator it=descriptors.iterator(); it.hasNext();)
			{
				addDescriptor((DataLoadDescriptor)it.next());
			}
		}
	}

	private class MyXMLAdapter extends XMLAdapter
	{
		public void addXMLElement(XMLContext context, XMLObject element)
		{
			if (element instanceof DataLoadDescriptor)
			{
				addDescriptor((DataLoadDescriptor)element);
			}
		}
	}
}
