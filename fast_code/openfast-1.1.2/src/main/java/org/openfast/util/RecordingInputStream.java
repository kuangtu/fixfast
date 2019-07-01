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
import java.io.InputStream;


public class RecordingInputStream extends InputStream {
    private byte[] buffer = new byte[1024];
    private int index = 0;
    private InputStream in;

    public RecordingInputStream(InputStream inputStream) {
        this.in = inputStream;
    }
    
    public int read() throws IOException {
        int read = in.read();
        buffer[index++] = (byte) read;
        // Buffer overflow patch submitted by Erik Svensson
        if (index >= buffer.length){
            byte[] tmp = new byte[buffer.length*2];
            System.arraycopy(buffer, 0, tmp, 0, buffer.length);
            buffer = tmp;
        }
        return read;
    }

    public String toString() {
        return ByteUtil.convertByteArrayToBitString(buffer, index);
    }

    public byte[] getBuffer() {
        byte[] b = new byte[index];
        System.arraycopy(buffer, 0, b, 0, index);

        return b;
    }

    public void clear() {
        index = 0;
    }
}
