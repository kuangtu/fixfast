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
package org.openfast.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class LocalConnection implements Connection {
    private PipedInputStream in;
    private PipedOutputStream out;

    public LocalConnection(LocalEndpoint remote, LocalEndpoint local) {
        this.in = new PipedInputStream();
        this.out = new PipedOutputStream();
    }
    public LocalConnection(LocalConnection localConnection) {
        try {
            this.in = new PipedInputStream((PipedOutputStream) localConnection.getOutputStream());
            this.out = new PipedOutputStream((PipedInputStream) localConnection.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void close() {
        try {
            in.close();
        } catch (IOException e) {}
        try {
            out.close();
        } catch (IOException e) {}
    }
    public InputStream getInputStream() throws IOException {
        return in;
    }
    public OutputStream getOutputStream() throws IOException {
        return out;
    }
}
