/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueCollectionPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;


public class ValueCollectionPropertyValueModelAdapterTests extends TestCase {
	private Junk junk;
	private SimplePropertyValueModel junkHolder;
	private ValueCollectionPropertyValueModelAdapter junkHolder2;


	public static Test suite() {
		return new TestSuite(ValueCollectionPropertyValueModelAdapterTests.class);
	}
	
	public ValueCollectionPropertyValueModelAdapterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.junk = new Junk("foo");
		this.junkHolder = new SimplePropertyValueModel(this.junk);
		this.junkHolder2 = new ValueCollectionPropertyValueModelAdapter(this.junkHolder, Junk.STUFF_COLLECTION);
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testWrappedPVM() {
		Junk junk2 = new Junk("bar");
		LocalListener l = new LocalListener(this.junkHolder2, this.junk, junk2);
		this.junkHolder2.addPropertyChangeListener(l);
		this.junkHolder.setValue(junk2);
		assertTrue(l.eventReceived());
	}

	public void testHasListeners() throws Exception {
		assertFalse(this.junkHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertFalse(this.junkHolder2.hasAnyPropertyChangeListeners(ValueModel.VALUE));

		PropertyChangeListener l = new LocalListener(this.junkHolder2, null, this.junk);
		this.junkHolder2.addPropertyChangeListener(l);
		assertTrue(this.junkHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertTrue(this.junkHolder2.hasAnyPropertyChangeListeners(ValueModel.VALUE));

		this.junkHolder2.removePropertyChangeListener(l);
		assertFalse(this.junkHolder.hasAnyPropertyChangeListeners(ValueModel.VALUE));
		assertFalse(this.junkHolder2.hasAnyPropertyChangeListeners(ValueModel.VALUE));
	}

	public void testCollectionAdd() {
		LocalListener l = new LocalListener(this.junkHolder2, null, this.junk);
		this.junkHolder2.addPropertyChangeListener(l);
		this.junk.addStuff("bar");
		assertTrue(l.eventReceived());
	}

	public void testCollectionRemove() {
		LocalListener l = new LocalListener(this.junkHolder2, null, this.junk);
		this.junkHolder2.addPropertyChangeListener(l);
		this.junk.removeStuff("foo");
		assertTrue(l.eventReceived());
	}


	private class LocalListener implements PropertyChangeListener {
		private boolean eventReceived = false;
		private final Object source;
		private final Object oldValue;
		private final Object newValue;
		LocalListener(Object source, Object oldValue, Object newValue) {
			super();
			this.source = source;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		public void propertyChange(PropertyChangeEvent e) {
			this.eventReceived = true;
			assertEquals(this.source, e.getSource());
			assertEquals(this.oldValue, e.getOldValue());
			assertEquals(this.newValue, e.getNewValue());
			assertEquals(ValueModel.VALUE, e.getPropertyName());
		}
		boolean eventReceived() {
			return this.eventReceived;
		}
	}

	private class Junk extends AbstractModel {
		private Collection stuff;
			public static final String STUFF_COLLECTION = "stuff";

		public Junk(String stuffItem) {
			this.stuff = new ArrayList();
			this.stuff.add(stuffItem);
		}
	
		public void addStuff(String stuffItem) {
			this.addItemToCollection(stuffItem, this.stuff, STUFF_COLLECTION);
		}
		
		public void removeStuff(String stuffItem) {
			this.removeItemFromCollection(stuffItem, this.stuff, STUFF_COLLECTION);
		}
	
		public String toString() {
			return "Junk(" + this.stuff + ")";
		}
	
	}

}
