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

import java.util.Date;

public class DateValue extends ScalarValue {
    private static final long serialVersionUID = 1L;
    public final Date value;

    public DateValue(Date date) {
        this.value = date;
    }

    @Override
    public Object toObject() {
        return value;
    }

    @Override
    public long toLong() {
        return value.getTime();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || !(other instanceof DateValue))
            return false;
        return equals((DateValue) other);
    }

    private boolean equals(DateValue other) {
        return other.value.equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
