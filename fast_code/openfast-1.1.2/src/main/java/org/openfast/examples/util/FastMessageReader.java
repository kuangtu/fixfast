/*
 * Contributed to OpenFAST by Object Computing, Inc.
 */
package org.openfast.examples.util;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.examples.util.FastMessageConsumer;
import org.openfast.codec.FastDecoder;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * An object to decode incoming FAST messages and pas them to a FastMessageConsumer.
 */
public class FastMessageReader {
    private final TemplateRegistry templateRegistry;
    private final InputStream messageIn;
    private final FastMessageConsumer messageOut;
    private final int readOffset;
    private int head;
    private boolean raw = false;
    private boolean reset =false;
    
    /**
     * Construct the reader.
     * @param input a data stream containing raw FAST messages
     * @param templateStream a stream from which the FAST templates can be read.
     * @param output An object to accept the decoded messages.
     */
    public FastMessageReader(InputStream input, InputStream templateStream, FastMessageConsumer output) {
        this(input, templateStream, output, 0);
    }

    /**
     * Construct the reader.
     * @param input a data stream containing raw FAST messages
     * @param templateStream a stream from which the FAST templates can be read.
     * @param output An object to accept the decoded messages.
     * @param readOffset The number of leading bytes that should be discarded when reading each message.
     */
    public FastMessageReader(
            InputStream input,
            InputStream templateStream,
            FastMessageConsumer output,
            int readOffset) {
        this.readOffset = readOffset;
        messageIn = input;

        XMLMessageTemplateLoader templateLoader = new XMLMessageTemplateLoader();
        templateLoader.setLoadTemplateIdFromAuxId(true);
        templateLoader.load(templateStream);
        templateRegistry = templateLoader.getTemplateRegistry();

        messageOut = output;
    }


    public void start(){
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        FastDecoder decoder = new FastDecoder(context, messageIn);
        boolean more = true;
        int recordNumber = 0;
        if(this.head == 0){
            this.head = Integer.MAX_VALUE;
        }
        while (more && ++recordNumber < head) {
            if(reset){
                decoder.reset();
            }
            if(raw){
                try {
                    messageIn.mark(20);
                    byte[] sor = new byte[20];
                    //noinspection ResultOfMethodCallIgnored
                    messageIn.read(sor, 0, 20);
                    StringBuffer b = new StringBuffer("Record# " + recordNumber);
                    for (int i = 0; i < sor.length; i++) {
                        byte aSor = sor[i];
/*1.5                        b.append(String.format(" %02x", aSor));
 */
                        b.append(Integer.toHexString(aSor));
                    }
                    messageOut.annotate(b.toString());
                    messageIn.reset();
                } catch (IOException e) {
                    //noinspection EmptyCatchBlock
                    try{
                        messageIn.mark(20);
                    }catch(Throwable t){}
                }
            }

            Message message = decoder.readMessage(readOffset);
            if(message == null){
                more = false;
            }
            else{
                messageOut.accept(message);
            }
        }
    }

    /**
     * Limit the number of records to be processed
     * @param count How many records to handle;  zero means no limit.
     */
    public void setHead(int count) {
        head = count;
    }

    /**
     * Set flag to call the output annotate message with raw data in hex.
     * @param flag true if the raw data should be provided.
     */
    public void showRawData(boolean flag) {
        raw = flag;
    }

    /**
     * Reset the decoder on every record.   Some data feeds -- particularly those
     * that use UDB or multicast expect the decoder to start freshe for each incoming
     * record.  This flag enables that behavior.
     * @param resetEveryRecord true if each record should stand alone.
     */
    public void resetEveryRecord(boolean resetEveryRecord) {
        reset = resetEveryRecord;
    }
}
