/*******************************************************************************
* Copyright (c) 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Blaise Doughan = 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class ArrayValue<T> extends ManyValue<T, Object> {

    @Override
    public Object getItem() {
        if(null == adaptedValue) {
            return null;
        }
        Object array = Array.newInstance(containerClass(), adaptedValue.size());
        int x = 0;
        for(Object value : adaptedValue) {
            Array.set(array, x, value);
            x++;
        }
        return array;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public void setItem(Object array) {
        int arraySize = Array.getLength(array);
        adaptedValue = new ArrayList<T>(arraySize);
        for(int x=0; x<arraySize; x++) {
            adaptedValue.add((T) Array.get(array, x));
        }
    }

}