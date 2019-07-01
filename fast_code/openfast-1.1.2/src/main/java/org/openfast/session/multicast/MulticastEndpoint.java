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
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;

public abstract class MulticastEndpoint implements Endpoint {
    protected int port;
    protected String group;
    protected String ifaddr;
    
    public MulticastEndpoint(int port, String group) {
        this(port, group, null);
    }
    
    public MulticastEndpoint(int port, String group, String ifaddr) {
        this.port = port;
        this.group = group;
    	this.ifaddr = ifaddr;
    }
 
    protected MulticastSocket createSocket() throws FastConnectionException {
        try {
            MulticastSocket socket = new MulticastSocket(new InetSocketAddress(group, port));
            if (ifaddr != null) {
                try {
                    socket.setInterface(InetAddress.getByName(ifaddr));	
                } catch (SocketException e) {
                    throw new FastConnectionException(e);
                }
            }
            return socket;
        } catch (IOException e) {
            throw new FastConnectionException(e);
        }
    }

    public String toString() {
        return new StringBuilder(getClass().getName())
            .append("[").append("group=").append(group)
            .append(",").append("port=").append(port)
            .append(",").append("ifaddr=").append(ifaddr)
            .append("]")
            .toString();
    }

    public abstract Connection connect() throws FastConnectionException;
    public void accept() throws FastConnectionException { }
    public void setConnectionListener(ConnectionListener listener) { }
    public void close() {}
}
