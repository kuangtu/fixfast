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
package org.openfast.submitted;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.MessageInputStream;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.test.OpenFastTestCase;

public class OpraFeedTest extends OpenFastTestCase {
    private final class OpraBlockReader implements MessageBlockReader {
        private int bytesLeft;

        public boolean readBlock(InputStream in) {
            try {
                if (bytesLeft == 0) {
                    bytesLeft = ((in.read() << 24) + (in.read() << 16) + (in.read() << 8) + (in.read() << 0));
                    in.read(); // read SOH byte
                    bytesLeft--;
                }
                int msgLen = (0x000000FF & in.read()); // read message length
                bytesLeft--;
                if (msgLen == 3) {
                    return readBlock(in);
                } else {
                    bytesLeft -= msgLen;
                }
                return true;
            } catch (IOException ioe) {
                return false;
            }
        }

        public void messageRead(InputStream in, Message message) {
        }
    }
    
    public void testEmpty() {}

    public void IGNOREtestReadFeed() {
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.load(resource("OPRA/OPRATemplate.xml"));
        loader.getTemplateRegistry().register(0, "OPRA");

        MessageInputStream in = new MessageInputStream(resource("OPRA/messages.fast"));
        OpraBlockReader opraBlockReader = new OpraBlockReader();
        in.setBlockReader(opraBlockReader);
        in.setTemplateRegistry(loader.getTemplateRegistry());
        in.readMessage();
        while (true) {
            try {
                in.readMessage();
            } catch (FastException e) {
                assertEquals(FastConstants.END_OF_STREAM, e.getCode());
                return;
            }
        }
    }
}
