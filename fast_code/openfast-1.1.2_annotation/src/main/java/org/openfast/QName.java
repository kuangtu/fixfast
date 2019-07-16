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
package org.openfast;

import java.io.Serializable;

public class QName implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final QName NULL = new QName("", "");

    private final String namespace;

    private final String name;

    public QName(String name) {
        this(name, "");
    }

    public QName(String name, String namespace) {
        if (name == null)
            throw new NullPointerException();
        this.name = name;
        this.namespace = namespace == null ? "" : namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        QName other = (QName) obj;
        return other.namespace.equals(namespace) && other.name.equals(name);
    }

    public int hashCode() {
        return name.hashCode() + 31 * namespace.hashCode();
    }

    public String toString() {
        if (namespace.equals(""))
            return name;
        return name + "[" + namespace + "]";
    }
}
