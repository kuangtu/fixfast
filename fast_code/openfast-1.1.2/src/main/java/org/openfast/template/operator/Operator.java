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
package org.openfast.template.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

public class Operator implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Map OPERATOR_NAME_MAP = new HashMap();

    private final String name;

    public static final Operator NONE = new Operator("none") {
        private static final long serialVersionUID = 2L;

        public boolean usesDictionary() {
            return false;
        }

        public boolean shouldStoreValue(ScalarValue value) {
            return false;
        }
    };
    
    public static final Operator CONSTANT = new Operator("constant") {
        private static final long serialVersionUID = 1L;

        public void validate(Scalar scalar) {
            if (scalar.getDefaultValue().isUndefined())
                Global.handleError(FastConstants.S4_NO_INITIAL_VALUE_FOR_CONST, "The field " + scalar
                        + " must have a default value defined.");
        }

        public boolean shouldStoreValue(ScalarValue value) {
            return false;
        }

        public boolean usesDictionary() {
            return false;
        }
    };

    public static final Operator DEFAULT = new Operator("default") {
        private static final long serialVersionUID = 1L;

        public void validate(Scalar scalar) {
            if (!scalar.isOptional() && scalar.getDefaultValue().isUndefined())
                Global.handleError(FastConstants.S5_NO_INITVAL_MNDTRY_DFALT, "The field " + scalar
                        + " must have a default value defined.");
        }

        public boolean shouldStoreValue(ScalarValue value) {
            return value != null;
        }
    };

    public static final Operator COPY = new Operator("copy") {
        private static final long serialVersionUID = 1L;

        public OperatorCodec getCodec(Type type) {
            return OperatorCodec.COPY_ALL;
        }
    };

    public static final Operator INCREMENT = new Operator("increment");

    public static final Operator DELTA = new Operator("delta") {
        private static final long serialVersionUID = 1L;

        public boolean shouldStoreValue(ScalarValue value) {
            return value != null;
        }
    };

    public static final Operator TAIL = new Operator("tail");

    public Operator(String name) {
        this.name = name;
        OPERATOR_NAME_MAP.put(name, this);
    }

    public static Operator getOperator(String name) {
        if (!OPERATOR_NAME_MAP.containsKey(name))
            throw new IllegalArgumentException("The operator \"" + name + "\" does not exist.");
        return (Operator) OPERATOR_NAME_MAP.get(name);
    }

    public OperatorCodec getCodec(Type type) {
        return OperatorCodec.getCodec(this, type);
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public boolean shouldStoreValue(ScalarValue value) {
        return true;
    }

    public void validate(Scalar scalar) {
    }

    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || !(other instanceof Operator))
            return false;
        return equals((Operator) other);
    }

    private boolean equals(Operator other) {
        return name.equals(other.name);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean usesDictionary() {
        return true;
    }
}
