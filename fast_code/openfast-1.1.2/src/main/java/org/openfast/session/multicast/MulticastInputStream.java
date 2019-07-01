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
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MulticastInputStream extends InputStream {
    private static final int BUFFER_SIZE = 64 * 1024;
    private final MulticastSocket socket;
    private final ByteBuffer buffer;

    public MulticastInputStream(MulticastSocket socket) {
        this(socket, BUFFER_SIZE);
    }

    public MulticastInputStream(MulticastSocket socket, int bufferSize) {
        this.socket = socket;
        this.buffer = ByteBuffer.allocate(bufferSize);
        buffer.flip();
    }

    @Override
    public int read() throws IOException {
        if (socket.isClosed()) return -1;
        if (!buffer.hasRemaining()) {
            buffer.clear();
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.capacity());
            socket.receive(packet);
            buffer.flip();
            buffer.limit(packet.getLength());
        }
        return (buffer.get() & 0xFF);
    }
    
    @Override
    public int available() throws IOException {
        return buffer.remaining();
    }
}
