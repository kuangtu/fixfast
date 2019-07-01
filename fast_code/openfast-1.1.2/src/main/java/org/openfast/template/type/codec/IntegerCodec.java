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
package org.openfast.template.type.codec;

import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.template.LongValue;
import org.openfast.util.Util;

public abstract class IntegerCodec extends TypeCodec {
    private static final long serialVersionUID = 1L;

    protected IntegerCodec() {}

    /**
     * Creates a new value, long or integer, depends on value passed to method
     * 
     * @param value
     *            The passed value to be created
     * @return If the long is larger than an integer - returns a new LongValue
     *         object, otherwise returns an ScalarValue object of type integer
     *         of the passed long value
     */
    protected static ScalarValue createValue(long value) {
        if (Util.isBiggerThanInt(value)) {
            return new LongValue(value);
        }
        return new IntegerValue((int) value);
    }

    /**
     * 
     * @param value
     *            The long to determine the unsigned integer
     * @return Returns an unsigned integer
     */
    public static int getUnsignedIntegerSize(long value) {
        if (value < 128) {
            return 1; // 2 ^ 7
        }
        if (value <= 16384) {
            return 2; // 2 ^ 14
        }
        if (value <= 2097152) {
            return 3; // 2 ^ 21
        }
        if (value <= 268435456) {
            return 4; // 2 ^ 28
        }
        if (value <= 34359738368L) {
            return 5; // 2 ^ 35
        }
        if (value <= 4398046511104L) {
            return 6; // 2 ^ 42
        }
        if (value <= 562949953421312L) {
            return 7; // 2 ^ 49
        }
        if (value <= 72057594037927936L) {
            return 8; // 2 ^ 56
        }
        return 9;
    }

    /**
     * Find the signed integer size for the passed long value
     * 
     * @param value
     *            The long value to be used to get the signed integer size
     * @return Returns an integer of the appropriate signed integer
     */
    public static int getSignedIntegerSize(long value) {
        if ((value >= -64) && (value <= 63)) {
            return 1; // - 2 ^ 6 ... 2 ^ 6 -1
        }
        if ((value >= -8192) && (value <= 8191)) {
            return 2; // - 2 ^ 13 ... 2 ^ 13 -1
        }
        if ((value >= -1048576) && (value <= 1048575)) {
            return 3; // - 2 ^ 20 ... 2 ^ 20 -1
        }
        if ((value >= -134217728) && (value <= 134217727)) {
            return 4; // - 2 ^ 27 ... 2 ^ 27 -1
        }
        if ((value >= -17179869184L) && (value <= 17179869183L)) {
            return 5; // - 2 ^ 34 ... 2 ^ 34 -1
        }
        if ((value >= -2199023255552L) && (value <= 2199023255551L)) {
            return 6; // - 2 ^ 41 ... 2 ^ 41 -1
        }
        if ((value >= -281474976710656L) && (value <= 281474976710655L)) {
            return 7; // - 2 ^ 48 ... 2 ^ 48 -1
        }
        if ((value >= -36028797018963968L) && (value <= 36028797018963967L)) {
            return 8; // - 2 ^ 55 ... 2 ^ 55 -1
        }
        if ((value >= -4611686018427387904L && value <= 4611686018427387903L)) {
            return 9;
        }
        return 10;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
