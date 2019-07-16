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

import java.math.BigDecimal;

public class ScalarValue implements FieldValue {
    private static final long serialVersionUID = 1L;
    public static final ScalarValue UNDEFINED = new ScalarValue() {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isUndefined() {
            return true;
        }

        @Override
        public String toString() {
            return "UNDEFINED";
        }
    };
    static public final ScalarValue NULL = new ScalarValue() {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public String toString() {
            return "NULL";
        }
    };

    /**
     *
     * @return Returns false
     */
    public boolean equalsValue(String defaultValue) {
        return false;
    }

    public FieldValue copy() {
        return this; // immutable objects don't need actual copies.
    }

    /**
     *
     * @return Returns false
     */
    public boolean isUndefined() {
        return false;
    }

    /**
     *
     * @return Returns false
     */
    public boolean isNull() {
        return false;
    }

    public byte toByte() {
        throw new UnsupportedOperationException();
    }

    public short toShort() {
        throw new UnsupportedOperationException();
    }

    public int toInt() {
        throw new UnsupportedOperationException();
    }

    public long toLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }

    public String serialize() {
        return toString();
    }

    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    public double toDouble() {
        throw new UnsupportedOperationException();
    }

    public BigDecimal toBigDecimal() {
        throw new UnsupportedOperationException();
    }

    public Object toObject() {
        throw new UnsupportedOperationException();
    }
}
