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

public class BitVectorReader {
    public static final BitVectorReader NULL = new BitVectorReader(null) {
        public boolean read() {
            throw new IllegalStateException();
        }

        public boolean hasMoreBitsSet() {
            return false;
        }
    };
    public static final BitVectorReader INFINITE_TRUE = new BitVectorReader(null) {
        public boolean read() {
            return true;
        }
    };
    private final BitVector vector;
    private int index = 0;

    public BitVectorReader(BitVector vector) {
        this.vector = vector;
    }

    public boolean read() {
        return vector.isSet(index++);
    }

    public BitVector getBitVector() {
        return vector;
    }

    public boolean hasMoreBitsSet() {
        return vector.indexOfLastSet() > index;
    }

    public String toString() {
        return vector.toString();
    }

    public boolean peek() {
        return vector.isSet(index);
    }

    public int getIndex() {
        return index;
    }
}
