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
package org.openfast.session.multicast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.openfast.session.Connection;

public class MulticastConnection implements Connection {
    protected MulticastOutputStream outputStream;
    protected MulticastSocket socket;
    protected int port;
    protected InetAddress group;

    public MulticastConnection(MulticastSocket socket, int port, InetAddress group) {
        this.socket = socket;
        this.port = port;
        this.group = group;
    }

    public void close() {
        try {
            socket.leaveGroup(group);
            socket.close();
        }
        catch (IOException e) {
        }
    }

    public InputStream getInputStream() throws IOException {
        return new MulticastInputStream(socket);
    }

    public OutputStream getOutputStream() throws IOException {
        if(outputStream == null)
            outputStream = new MulticastOutputStream(socket, port, group);
        return outputStream;
    }
}
