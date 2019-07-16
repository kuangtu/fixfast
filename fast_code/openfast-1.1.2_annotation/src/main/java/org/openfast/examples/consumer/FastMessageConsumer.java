package org.openfast.examples.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageBlockReader;
import org.openfast.error.FastException;
import org.openfast.codec.FastDecoder;
import org.openfast.session.Connection;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.examples.MessageBlockReaderFactory;

public class FastMessageConsumer {
    private final Endpoint endpoint;
    private final TemplateRegistry templateRegistry;
    protected final MessageBlockReaderFactory messageBlockReaderFactory;
    protected final boolean shouldResetOnEveryMessage;

    public FastMessageConsumer(Endpoint endpoint, File templatesFile) {
        this(endpoint, templatesFile, new MessageBlockReaderFactory(), false);
    }

    public FastMessageConsumer(Endpoint endpoint, File templatesFile,
            MessageBlockReaderFactory messageBlockReaderFactory, boolean shouldResetOnEveryMessage) {
        this.endpoint = endpoint;
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
        this.messageBlockReaderFactory = messageBlockReaderFactory;
        this.shouldResetOnEveryMessage = shouldResetOnEveryMessage;
    }

    public void start() throws FastConnectionException, IOException {
        final Connection connection = endpoint.connect();
        Context context = new Context();
        context.setTemplateRegistry(templateRegistry);
        MessageInputStream msgInStream = new MessageInputStream(connection.getInputStream(), context);
        MessageBlockReader msgBlockReader = messageBlockReaderFactory.create();
		msgInStream.setBlockReader(msgBlockReader);
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                connection.close();
            }
        });
        while (true) {
            try {
                Message message = msgInStream.readMessage();
                System.out.println(msgBlockReader.toString() + ' ' + message.toString());
                if(shouldResetOnEveryMessage) {
                    msgInStream.reset();
                }
            }
            catch(final FastException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
