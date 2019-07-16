/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast.util;

import java.util.Arrays;

public class Key {
    private final Object[] keys;

    public Key(Object key1, Object key2) {
        this(new Object[] { key1, key2 });
    }

    public Key(Object key1, Object key2, Object key3) {
        this(new Object[] { key1, key2, key3 });
    }

    public Key(Object[] keys) {
        this.keys = keys;
        checkNull();
    }

    private void checkNull() {
        for (int i = 0; i < keys.length; i++)
            if (keys[i] == null)
                throw new NullPointerException();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || !(obj instanceof Key))
            return false;
        Key other = ((Key) obj);
        if (other.keys.length != keys.length)
            return false;
        for (int i = 0; i < keys.length; i++)
            if (!other.keys[i].equals(keys[i]))
                return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < keys.length; i++)
            hashCode += keys[i].hashCode() * (37 ^ i);
        return hashCode;
    }

    public String toString() {
        return Arrays.toString(keys);
    }
}
