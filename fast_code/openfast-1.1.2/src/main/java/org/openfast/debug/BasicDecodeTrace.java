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
package org.openfast.debug;

import java.io.PrintWriter;
import org.openfast.ByteUtil;
import org.openfast.FieldValue;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class BasicDecodeTrace implements Trace {
    private String indent = "";
    private PrintWriter out = new PrintWriter(System.out);

    public void groupStart(Group group) {
        print(group);
        moveDown();
    }

    private void moveDown() {
        indent += "  ";
    }

    private void moveUp() {
        indent = indent.substring(0, indent.length() - 2);
    }

    private void print(Object object) {
        out.print(indent);
        out.println(object);
        out.flush();
    }

    public void groupEnd() {
        moveUp();
    }

    public void field(Field field, FieldValue value, FieldValue decodedValue, byte[] encoding, int pmapIndex) {
        StringBuilder scalarDecode = new StringBuilder();
        scalarDecode.append(field.getName()).append(": ");
        scalarDecode.append(ByteUtil.convertByteArrayToHexString(encoding));
        scalarDecode.append(" -> ").append(value).append('(').append(decodedValue).append(')');
        print(scalarDecode);
    }

    public void pmap(byte[] bytes) {
        print("PMAP: " + ByteUtil.convertByteArrayToHexString(bytes));
    }

    public void setWriter(PrintWriter traceWriter) {
        this.out = traceWriter;
    }
}
