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
package org.openfast.session.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;

public class TcpEndpoint implements Endpoint {
    private final int port;
    private String host;
    private ConnectionListener connectionListener = ConnectionListener.NULL;
    private ServerSocket serverSocket;
    private boolean closed = true;

    public TcpEndpoint(int port) {
        this.port = port;
    }
    public TcpEndpoint(String host, int port) {
        this(port);
        this.host = host;
    }
    public Connection connect() throws FastConnectionException {
        try {
            Socket socket = new Socket(host, port);
            Connection connection = new TcpConnection(socket);
            return connection;
        } catch (UnknownHostException e) {
            throw new FastConnectionException(e);
        } catch (IOException e) {
            throw new FastConnectionException(e);
        }
    }
    public void accept() throws FastConnectionException {
        closed = false;
        try {
            serverSocket = new ServerSocket(port);
            while (!closed) {
                Socket socket = serverSocket.accept();
                try {
                    connectionListener.onConnect(new TcpConnection(socket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (!closed)
                throw new FastConnectionException(e);
        }
    }
    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }
    public void close() {
        closed = true;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {}
    }
    
    public String toString() {
        return host + ":" + port;
    }
}
