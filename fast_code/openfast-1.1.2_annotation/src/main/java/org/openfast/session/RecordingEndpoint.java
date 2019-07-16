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
import org.openfast.util.RecordingInputStream;
import org.openfast.util.RecordingOutputStream;

public class RecordingEndpoint implements Endpoint, ConnectionListener {
    private Endpoint underlyingEndpoint;
    private ConnectionListener listener;

    public RecordingEndpoint(Endpoint endpoint) {
        this.underlyingEndpoint = endpoint;
        underlyingEndpoint.setConnectionListener(this);
    }

    public void accept() throws FastConnectionException {
        underlyingEndpoint.accept();
    }

    public Connection connect() throws FastConnectionException {
        final Connection connection = underlyingEndpoint.connect();
        Connection connectionWrapper = new RecordingConnection(connection);
        return connectionWrapper;
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.listener = listener;
    }

    private final class RecordingConnection implements Connection {
        private final RecordingInputStream recordingInputStream;
        private final RecordingOutputStream recordingOutputStream;

        private RecordingConnection(Connection connection) {
            try {
                this.recordingInputStream = new RecordingInputStream(connection.getInputStream());
                this.recordingOutputStream = new RecordingOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            System.out.println("IN: " + new String(recordingInputStream.toString()));
            System.out.println("OUT: " + new String(recordingOutputStream.toString()));
        }

        public InputStream getInputStream() throws IOException {
            return recordingInputStream;
        }

        public OutputStream getOutputStream() throws IOException {
            return recordingOutputStream;
        }
    }

    public void close() {
        underlyingEndpoint.close();
    }

    public void onConnect(Connection connection) {
        listener.onConnect(new RecordingConnection(connection));
    }
}
