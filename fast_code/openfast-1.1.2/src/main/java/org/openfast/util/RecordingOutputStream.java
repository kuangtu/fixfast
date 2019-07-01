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

import org.openfast.ByteUtil;
import java.io.IOException;
import java.io.OutputStream;

public class RecordingOutputStream extends OutputStream {
    private byte[] buffer = new byte[1024];
    private int index = 0;
    private OutputStream out;

    public RecordingOutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    public void write(int b) throws IOException {
        buffer[index++] = (byte) b;
        out.write(b);
    }

    public String toString() {
        return ByteUtil.convertByteArrayToBitString(buffer, index);
    }

    public void clear() {
        index = 0;
    }
}
