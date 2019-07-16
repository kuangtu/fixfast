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
package org.openfast.template;

import org.openfast.ScalarValue;

public class TwinValue extends ScalarValue {
    private static final long serialVersionUID = 1L;
    public final ScalarValue first;
    public final ScalarValue second;

    /**
     * TwinValue Constructor - takes two ScalarValues
     * 
     * @param first
     *            ScalarValue
     * @param second
     *            ScalarValue
     */
    public TwinValue(ScalarValue first, ScalarValue second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Checks to see if the object passed to it is a TwinValue object
     * 
     * @param obj
     *            The object to be checked if it is a TwinValue object
     * @return If the passed object is a TwinValue object, returns true, false
     *         otherwise
     */
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof TwinValue)) {
            return false;
        }
        return equals((TwinValue) obj);
    }

    /**
     * Compares the current TwinValue ScalarValues with another TwinValue
     * ScalarValues
     * 
     * @param other
     *            The TwinValue object that is being compared
     * @return True if the the first and second ScalarValues equal the compared
     *         TwinValue ScalarValues, false otherwise
     */
    private boolean equals(TwinValue other) {
        return (first.equals(other.first) && second.equals(other.second));
    }

    public int hashCode() {
        return first.hashCode() * 37 + second.hashCode();
    }

    /**
     * Converts the ScalarValues first and second to strings
     * 
     * @return string in the form (first ScalarValue, second ScalarValue)
     */
    public String toString() {
        return first.toString() + ", " + second.toString();
    }
}
