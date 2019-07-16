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
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import junit.framework.TestCase;

public class MulticastOutputStreamTest extends TestCase {
    final static byte[] MESSAGE_A = "MESSAGE_A".getBytes();
    final static byte[] MESSAGE_B = "MESSAGE_B".getBytes();
    final static byte[] MESSAGE_C = "MESSAGE_C".getBytes();
    int port;
    InetAddress group;
    MockMulticastSocket socket;
    MulticastOutputStream multicastOutputStream;

    public class MockMulticastSocket extends MulticastSocket {
        public Deque packetsSent = new LinkedList();
        
        public MockMulticastSocket(int port) throws IOException {
            super(port);
        }

        public void send(DatagramPacket packet) throws IOException {
            packetsSent.addLast(packet);
        }

		public String toString() {
			StringBuffer msg = new StringBuffer("\nPackets Sent {");
			for(Object ix: packetsSent) {
				final DatagramPacket packet = (DatagramPacket)ix;
				msg.append("\n  ").append(new String(Arrays.copyOf(packet.getData(), packet.getLength())));
			}
			msg.append("\n}");
			return msg.toString();
		}
    }

    public void assertPacketEquals(byte[] expected, DatagramPacket actual) {
        final byte[] actualData = Arrays.copyOf(actual.getData(), actual.getLength());
        final String msg = "Expected '" + new String(expected) + "', but was '" + new String(actualData) + "'";
		assertTrue(msg, Arrays.equals(expected, actualData));
    }

    public void setUp() throws Exception {
        port = 4242;
        group = InetAddress.getByName("230.0.0.1");
        socket = new MockMulticastSocket(port);
        multicastOutputStream = new MulticastOutputStream(socket, port, group);
    }

	public void testYouMustCallFlushToCauseDatagramToBeSent() {
        multicastOutputStream.write(MESSAGE_A);
		assertEquals(0, socket.packetsSent.size());
		multicastOutputStream.flush();

		System.out.println(socket);
		assertPacketEquals(MESSAGE_A, (DatagramPacket)socket.packetsSent.removeFirst());
    }

	public void testWrite() {
        multicastOutputStream.write(MESSAGE_A);
		multicastOutputStream.flush();
		
		multicastOutputStream.write(MESSAGE_B);
		multicastOutputStream.flush();
        
		multicastOutputStream.write(MESSAGE_C);
		multicastOutputStream.flush();

		System.out.println(socket);
		assertPacketEquals(MESSAGE_A, (DatagramPacket)socket.packetsSent.removeFirst());
        assertPacketEquals(MESSAGE_B, (DatagramPacket)socket.packetsSent.removeFirst());
        assertPacketEquals(MESSAGE_C, (DatagramPacket)socket.packetsSent.removeFirst());
    }

    public void testWriteSingleByte() {
		for(byte b : MESSAGE_C)
			multicastOutputStream.write(b);
		multicastOutputStream.flush();
		
		for(byte b : MESSAGE_B)
			multicastOutputStream.write(b);
		multicastOutputStream.flush();
		
		for(byte b : MESSAGE_A)
			multicastOutputStream.write(b);
		multicastOutputStream.flush();
		
		System.out.println(socket);
		assertPacketEquals(MESSAGE_C, (DatagramPacket)socket.packetsSent.removeFirst());
        assertPacketEquals(MESSAGE_B, (DatagramPacket)socket.packetsSent.removeFirst());
        assertPacketEquals(MESSAGE_A, (DatagramPacket)socket.packetsSent.removeFirst());
	}
}

