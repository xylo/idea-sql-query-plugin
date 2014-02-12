/*
 * Copyright (C) 1998-2006 Stefan Stiller
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
package com.kiwisoft.utils;

import java.util.Vector;

/**
 * @author Stefan Stiller
 * @version $Revision: $, $Date: $
 */
public class CollectionChangeSupport
{
	private transient Vector listeners;

	private Object source;

	public CollectionChangeSupport(Object sourceBean)
	{
		if (sourceBean==null) throw new NullPointerException();
		source=sourceBean;
	}

	public synchronized void addListener(CollectionChangeListener listener)
	{
		if (listener==null) throw new IllegalArgumentException("listener");
		if (listeners==null) listeners=new Vector();
		listeners.addElement(listener);
	}

	public synchronized void removeListener(CollectionChangeListener listener)
	{
		if (listeners==null) return;
		listeners.removeElement(listener);
	}

	public synchronized CollectionChangeListener[] getCollectionChangeListeners()
	{
		if (listeners==null) return new CollectionChangeListener[0];
		return (CollectionChangeListener[])listeners.toArray(new CollectionChangeListener[0]);
	}

	public void fireElementAdded(String propertyName, Object newElement)
	{
		CollectionChangeListener[] listeners=getCollectionChangeListeners();
		if (listeners==null || listeners.length==0) return;
		CollectionChangeEvent event=new CollectionChangeEvent(source, propertyName, newElement, CollectionChangeEvent.ADDED);
		for (int i=0; i<listeners.length; i++)
		{
			CollectionChangeListener listener=listeners[i];
			if (listener!=null) listener.collectionChanged(event);
		}
	}

	public void fireElementRemoved(String propertyName, Object oldElement)
	{
		CollectionChangeListener[] listeners=getCollectionChangeListeners();
		if (listeners==null || listeners.length==0) return;
		CollectionChangeEvent event=new CollectionChangeEvent(source, propertyName, oldElement, CollectionChangeEvent.REMOVED);
		for (int i=0; i<listeners.length; i++)
		{
			CollectionChangeListener listener=listeners[i];
			if (listener!=null) listener.collectionChanged(event);
		}
	}

	public void fireElementChanged(String propertyName, Object oldElement)
	{
		CollectionChangeListener[] listeners=getCollectionChangeListeners();
		if (listeners==null || listeners.length==0) return;
		CollectionChangeEvent event=new CollectionChangeEvent(source, propertyName, oldElement, CollectionChangeEvent.CHANGED);
		for (int i=0; i<listeners.length; i++)
		{
			CollectionChangeListener listener=listeners[i];
			if (listener!=null) listener.collectionChanged(event);
		}
	}

	public void fireCollectionChanged(String propertyName, Object collection)
	{
		CollectionChangeListener[] listeners=getCollectionChangeListeners();
		if (listeners==null || listeners.length==0) return;
		CollectionChangeEvent event=new CollectionChangeEvent(source, propertyName, collection, CollectionChangeEvent.COLLECTION_CHANGED);
		for (int i=0; i<listeners.length; i++)
		{
			CollectionChangeListener listener=listeners[i];
			if (listener!=null) listener.collectionChanged(event);
		}
	}
}
