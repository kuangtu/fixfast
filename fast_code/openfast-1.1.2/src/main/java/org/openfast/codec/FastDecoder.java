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
package org.openfast.codec;

import java.io.IOException;
import java.io.InputStream;

import org.openfast.BitVector;
import org.openfast.BitVectorReader;
import org.openfast.BitVectorValue;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.error.FastException;
import org.openfast.template.MessageTemplate;
import org.openfast.template.type.codec.TypeCodec;

/**
 * A FastDecoder is the core class for reading and decoding FAST messages from any input stream.  This class can be used
 * instead of {@link org.openfast.MessageInputStream} when working with FAST messages embedded in other protocols.
 * <br/><br/>
 * <b><i>NOTE:</i></b> The context object that is used to construct a FastDecoder should not be shared with any other decoders or encoders.
 */
public class FastDecoder implements Coder {
    private final InputStream in;

    private final Context context;

    /**
     * Construct a new FastDecoder with a context and input stream.
     * 
     * @param context The context containg templates to be used in decoding and where FAST dictionary data will be stored
     * @param in The input stream to read messages from
     */
    public FastDecoder(Context context, InputStream in) {
        this.in = in;
        this.context = context;
    }

    /**
     * Read the next FAST message from the input stream.
     * 
     * @return an object representing the decoded FAST message
     * @throws FastException if a decoding error occurs or the end of the input stream has been reached
     */
    public Message readMessage() throws FastException {
        BitVectorValue bitVectorValue = (BitVectorValue) TypeCodec.BIT_VECTOR.decode(in);
        if (bitVectorValue == null) {
            return null;
        }
        BitVector pmap = (bitVectorValue).value;
        BitVectorReader presenceMapReader = new BitVectorReader(pmap);

        // if template id is not present, use previous, else decode template id
        int templateId = (presenceMapReader.read()) ? TypeCodec.UINT.decode(in).toInt() : context.getLastTemplateId();
        MessageTemplate template = context.getTemplate(templateId);

        if (template == null) {
            return null;
        }
        context.newMessage(template);

        context.setLastTemplateId(templateId);

        return template.decode(in, templateId, presenceMapReader, context);
    }
    
    /**
     * Helper method that can be used to read past a non-FAST message header.
     * 
     * @param offset number of bytes in the stream to skip
     * @return the decoded FAST message encountered after skipping the offset. 
     */
    public Message readMessage(int offset) {
        if (offset > 0) {
            try {
                in.skip(offset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return readMessage();
    }

    /**
     * Reset the FAST dictionary.
     */
    public void reset() {
        context.reset();
    }
}
