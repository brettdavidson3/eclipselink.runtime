/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import javax.activation.DataHandler;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

/**
 * Provide a testing implementation of AttachmentMarshaller that normally would be provided by the application server
 */
public class AttachmentUnmarshallerImpl implements XMLAttachmentUnmarshaller {
    public byte[] getAttachmentAsByteArray(String cid) {
        return "Testing".getBytes();
    }

    public DataHandler getAttachmentAsDataHandler(String id) {
        throw new UnsupportedOperationException("getAttachmentAsDataHandler not supported");

    }

    public boolean isXOPPackage() {
        // force attachment usage
        return true;
    }
}