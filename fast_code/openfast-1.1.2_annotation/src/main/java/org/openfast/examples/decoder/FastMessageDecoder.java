package org.openfast.examples.decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.openfast.Context;
import org.openfast.Global;
import org.openfast.Message;
import org.openfast.MessageBlockReader;
import org.openfast.MessageInputStream;
import org.openfast.error.FastConstants;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class FastMessageDecoder {
    private final TemplateRegistry templateRegistry;
    private final File fastDataFile;
    private final int readOffset;
    private boolean trace;
    private MessageBlockReader blockReader = MessageBlockReader.NULL;

    /**
     * Construct the decoder.
     * @param fastDataFile A file containing raw FAST messages.
     * @param templatesFile A file from which the FAST templates can be read.
     * @param namespaceAware Enables namespace awareness.
     */
    public FastMessageDecoder(File fastDataFile, File templatesFile, boolean namespaceAware) {
        this(fastDataFile, templatesFile, namespaceAware, 0);
    }

    /**
     * Construct the decoder.
     * @param fastDataFile A file containing raw FAST messages.
     * @param templatesFile A file from which the FAST templates can be read.
     * @param namespaceAware Enables namespace awareness.
     * @param readOffset The number of leading bytes that should be discarded when reading each message.
     */
    public FastMessageDecoder(File fastDataFile, File templatesFile, boolean namespaceAware, int readOffset) {
        this.readOffset = readOffset;
        this.fastDataFile = fastDataFile;
        Global.setErrorHandler(FastConstants.BASIC_ERROR_HANDLER);
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader(namespaceAware);
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
    }

    public void start() throws IOException {
        Context context = new Context();
        context.setTraceEnabled(trace);
        context.startTrace();
        context.setTemplateRegistry(templateRegistry);
//        context.set
        InputStream fileIn = null;
        try {
            fileIn = new FileInputStream(fastDataFile);
            MessageInputStream messageIn = new MessageInputStream(fileIn, context);
            messageIn.setBlockReader(blockReader);
            while (true) {
                Message message = messageIn.readMessage();
                if (message == null)
                    break;
                System.out.println(message.toString());
            }
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
    }

    public void setTraceEnabled() {
        this.trace = true;
    }

    public void setBlockReader(MessageBlockReader messageBlockReader) {
        this.blockReader = messageBlockReader;
    }
}
