package org.openfast.examples.producer;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openfast.Context;
import org.openfast.Global;
import org.openfast.Message;
import org.openfast.MessageBlockWriter;
import org.openfast.MessageOutputStream;
import org.openfast.error.ErrorHandler;
import org.openfast.session.Connection;
import org.openfast.session.ConnectionListener;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.examples.MessageBlockWriterFactory;

public class FastMessageProducer implements ConnectionListener {
    protected final Endpoint endpoint;
    protected final TemplateRegistry templateRegistry;
    protected Thread acceptThread;
    protected List connections = new ArrayList();
    protected XmlCompressedMessageConverter converter = new XmlCompressedMessageConverter();
    protected final MessageBlockWriterFactory messageBlockWriterFactory;
    protected final boolean shouldResetOnEveryMessage;

    public FastMessageProducer(Endpoint endpoint, File templatesFile) {
		this(endpoint, templatesFile, new MessageBlockWriterFactory(), false);
	}

	public FastMessageProducer(Endpoint endpoint, File templatesFile,
            MessageBlockWriterFactory messageBlockWriterFactory, boolean shouldResetOnEveryMessage) {
        Global.setErrorHandler(ErrorHandler.NULL);
        this.endpoint = endpoint;
        XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader();
        loader.setLoadTemplateIdFromAuxId(true);
        try {
            loader.load(new FileInputStream(templatesFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.templateRegistry = loader.getTemplateRegistry();
        this.converter.setTemplateRegistry(this.templateRegistry);
        this.messageBlockWriterFactory = messageBlockWriterFactory;
        this.shouldResetOnEveryMessage = shouldResetOnEveryMessage;
	}

    public void encode(File xmlDataFile) throws FastConnectionException, IOException {
        encode(new FileInputStream(xmlDataFile), true);
    }

    public void encode(InputStream xmlData) throws FastConnectionException, IOException {
        encode(xmlData, true);
    }

    public void encode(InputStream xmlData, boolean loopForever) throws FastConnectionException, IOException {
        List messages = converter.parse(xmlData);
        if(messages == null)
            throw new IllegalArgumentException("The XML data stream contains no FAST messages!");

        do {
            publish(messages, connections);
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
        }
        while(loopForever);
    }

    protected void publish(List messages, List msgOutputStreams) {
        for (int i = 0; i < messages.size(); ++i) {
            Message message = (Message) messages.get(i);
            for (int j = 0; j < msgOutputStreams.size(); ++j) {
                MessageOutputStream out = (MessageOutputStream)msgOutputStreams.get(j);
                out.writeMessage(message);
                if(shouldResetOnEveryMessage) {
                    out.reset();
                }
            }
        }
    }

    public void start() {
        System.out.println("Listening on " + endpoint);
        if (acceptThread != null) return;
        endpoint.setConnectionListener(this);
        acceptThread = new Thread("Producer Accept Thread") {
            public void run() {
                try {
                    endpoint.accept();
                } catch (FastConnectionException e) {
                    System.out.println("Error occurred while listening for connections: " + e.getMessage());
                }
            }
        };
        acceptThread.start();
    }

    public void stop() {
        endpoint.close();
    }

    public void onConnect(Connection connection) {
        synchronized(connections) {
            Context context = new Context();
            context.setErrorHandler(ErrorHandler.NULL);
            context.setTemplateRegistry(templateRegistry);
            try {
                MessageOutputStream out = new MessageOutputStream(connection.getOutputStream(), context);
				out.setBlockWriter(messageBlockWriterFactory.create());
				connections.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

