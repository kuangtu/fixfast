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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastOutputStream extends OutputStream {
	public final static int BUFFER_SIZE = 2048;
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
	private byte[] packetBuffer;
	private ByteBuffer writeBuffer;

    public MulticastOutputStream(MulticastSocket socket, int port, InetAddress group) {
        this.socket = socket;
        this.group = group;
        this.port = port;
		packetBuffer = new byte[BUFFER_SIZE];
		writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		writeBuffer.clear();
    }

	public void flush() {
		writeBuffer.flip();
		if(writeBuffer.hasRemaining()) {
			try {
				byte[] data = new byte[writeBuffer.remaining()];
				writeBuffer.get(data);
				socket.send(new DatagramPacket(data, data.length, group, port));
			}
			catch(final IOException e) {
				e.printStackTrace();
			}
		}
		writeBuffer.clear();
	}   
    
	public void write(byte[] b, int off, int len) {
		writeBuffer.put(b, off, len);
	}

    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    public void write(int b) {
        writeBuffer.put((byte)b);
    }
}
