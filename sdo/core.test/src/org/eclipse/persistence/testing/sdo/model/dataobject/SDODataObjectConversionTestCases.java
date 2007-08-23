/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectConversionTestCases extends SDOTestCase {//TestCase {
    private static final String URINAME = "uri";
    private static final String TYPENAME = "TypeName";
    protected static final String PROPERTY_NAME = "PName";
    protected static final int PROPERTY_INDEX = 0;
    protected SDODataObject dataObject;
    protected SDOType type;

    public SDODataObjectConversionTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        type = new SDOType(URINAME, TYPENAME);

        SDOProperty property = new SDOProperty(aHelperContext);
        property.setName(PROPERTY_NAME);
        property.setType(SDOConstants.SDO_STRING);
        type.addDeclaredProperty(property);

        dataObject = (SDODataObject)dataFactory.create(type);

    }
}