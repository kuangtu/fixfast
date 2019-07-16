package org.openfast.session;

import java.io.IOException;

import junit.framework.TestCase;

import org.openfast.Message;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.template.BasicTemplateRegistry;
import org.openfast.template.TemplateRegistry;
import org.openfast.test.ObjectMother;
import org.openfast.util.RecordingOutputStream;

public class SCP_1_1_Test extends TestCase {
    private static final int MAX_TIMEOUT = 5000;
    private LocalEndpoint serverEndpoint;
    private RecordingEndpoint clientEndpoint;
    private FastServer server;
    private FastClient client;
    int successfullThreadsCount = 0;

    protected void setUp() throws Exception {
        serverEndpoint = new LocalEndpoint();
        server = new FastServer("server", SessionConstants.SCP_1_1, serverEndpoint);
        clientEndpoint = new RecordingEndpoint(new LocalEndpoint(serverEndpoint));
        client = new FastClient("client", SessionConstants.SCP_1_1, clientEndpoint);
    }
    protected void tearDown() throws Exception {
        server.close();
    }
    public void IGNOREtestSessionEstablishment() throws Exception {
        server.setSessionHandler(new SessionHandler() {
            public void newSession(Session session) {
                assertEquals("client", session.getClient().getName());
                assertEquals(SessionConstants.VENDOR_ID, session.getClient().getVendorId());
                successfullThreadsCount++;
                notify0();
            }
        });
        server.listen();
        Session session = client.connect();
        assertEquals("server", session.getClient().getName());
        assertEquals(SessionConstants.VENDOR_ID, session.getClient().getVendorId());
        if (successfullThreadsCount < 1)
            wait0(1000);
        assertEquals(1, successfullThreadsCount);
    }
    public void IGNOREtestSessionReset() throws Exception {
        server.setSessionHandler(new SessionHandler() {
            public void newSession(Session session) {
                session.in.registerTemplate(1, ObjectMother.quoteTemplate());
                wait0(MAX_TIMEOUT);
                Message quote = session.in.readMessage();
                assertEquals(1.0, quote.getDouble(1), .001);
                assertEquals(2.0, quote.getDouble(2), .001);
                quote = session.in.readMessage();
                assertEquals(1.0, quote.getDouble(1), .001);
                assertEquals(2.0, quote.getDouble(2), .001);
                quote = session.in.readMessage();
                assertEquals(1.0, quote.getDouble(1), .001);
                assertEquals(2.0, quote.getDouble(2), .001);
                quote = session.in.readMessage();
                assertEquals(1.0, quote.getDouble(1), .001);
                assertEquals(2.0, quote.getDouble(2), .001);
                successfullThreadsCount++;
                notify0();
            }
        });
        server.listen();
        Session session = client.connect();
        session.out.registerTemplate(1, ObjectMother.quoteTemplate());
        RecordingOutputStream recordOut = (RecordingOutputStream) session.getConnection().getOutputStream();
        Message quote = ObjectMother.quote(1.0, 2.0);
        session.out.writeMessage(quote);
        recordOut.clear();
        session.out.writeMessage(quote);
        String firstDupe = recordOut.toString();
        recordOut.clear();
        session.out.writeMessage(quote);
        String secondDupe = recordOut.toString();
        assertEquals(firstDupe, secondDupe);
        session.reset();
        recordOut.clear();
        session.out.writeMessage(quote);
        String lastDupe = recordOut.toString();
        notify0();
        wait0(MAX_TIMEOUT);
        assertFalse(firstDupe.equals(lastDupe));
        assertEquals(1, successfullThreadsCount);
    }
    public void testEmpty() {}
    public void IGNOREtestAlert() throws FastConnectionException, IOException {
        server.setSessionHandler(new SessionHandler() {
            public void newSession(Session session) {
                wait0(MAX_TIMEOUT);
                session.setListening(true);
                session.setErrorHandler(ErrorHandler.NULL);
                notify0();
                if (successfullThreadsCount < 0)
                    wait0(MAX_TIMEOUT);
            }
        });
        server.listen();
        Session session = client.connect();
        session.setErrorHandler(new ErrorHandler() {
            public void error(ErrorCode code, String message) {
                assertEquals(SessionConstants.TEMPLATE_NOT_SUPPORTED, code);
                successfullThreadsCount++;
                notifyAll0();
            }
            public void error(ErrorCode code, String message, Throwable t) {}
        });
        session.out.registerTemplate(1, ObjectMother.quoteTemplate());
        session.out.writeMessage(ObjectMother.quote(1.0, 2.0));
        notify0();
        wait0(MAX_TIMEOUT);
        session.setListening(true);
        if (successfullThreadsCount < 1)
            wait0(MAX_TIMEOUT);
        assertEquals(1, successfullThreadsCount);
    }
    public void IGNOREtestTemplateExchange() throws Exception {
        server.setSessionHandler(new SessionHandler() {
            public void newSession(Session session) {
                session.setListening(true);
                TemplateRegistry registry = new BasicTemplateRegistry();
                registry.register(24, ObjectMother.quoteTemplate());
                // Exchange quote template and send a quote with the newly
                // exchanged template.
                session.sendTemplates(registry);
                session.out.writeMessage(ObjectMother.quote(101.3, 102.4));
                wait0(MAX_TIMEOUT);
                try {
                    session.close();
                } catch (FastConnectionException e) {}
            }
        });
        server.listen();
        Session session = client.connect();
        session.setMessageHandler(new MessageListener() {
            public void onMessage(Session session, Message message) {
                if (message.getTemplate().equals(ObjectMother.quoteTemplate())) {
                    assertEquals(101.3, message.getDouble(1), .1);
                    assertEquals(102.4, message.getDouble(2), .1);
                    successfullThreadsCount++;
                    notifyAll0();
                }
            }
        });
        session.out.registerTemplate(1, ObjectMother.quoteTemplate());
        if (successfullThreadsCount < 1)
            wait0(MAX_TIMEOUT);
        assertEquals(1, successfullThreadsCount);
    }
    // TODO - Figure out why these tests occasionally fail
    public void IGNOREtestReceiveTemplateDefinitionWithTemplateId() throws Exception {
        server.setSessionHandler(new SessionHandler() {
            public void newSession(Session session) {
                session.setListening(true);
                Message templateDef = SessionConstants.SCP_1_1.createTemplateDefinitionMessage(ObjectMother.quoteTemplate());
                templateDef.setInteger("TemplateId", 24);
                session.out.registerTemplate(24, ObjectMother.quoteTemplate());
                session.out.writeMessage(templateDef);
                session.out.writeMessage(ObjectMother.quote(101.3, 102.4));
                wait0(MAX_TIMEOUT);
                try {
                    session.close();
                } catch (FastConnectionException e) {}
            }
        });
        server.listen();
        Session session = client.connect();
        session.setMessageHandler(new MessageListener() {
            public void onMessage(Session session, Message message) {
                if (message.getTemplate().equals(ObjectMother.quoteTemplate())) {
                    assertEquals(101.3, message.getDouble(1), .1);
                    assertEquals(102.4, message.getDouble(2), .1);
                    successfullThreadsCount++;
                    synchronized (SCP_1_1_Test.this) {
                        notifyAll0();
                    }
                }
            }
        });
        session.out.registerTemplate(1, ObjectMother.quoteTemplate());
        if (successfullThreadsCount < 1)
            wait0(MAX_TIMEOUT);
        assertEquals(1, successfullThreadsCount);
    }
    private synchronized void wait0(int timeout) {
        try {
            this.wait(timeout);
        } catch (InterruptedException e) {}
    }
    private synchronized void notify0() {
        this.notify();
    }
    private synchronized void notifyAll0() {
        this.notifyAll();
    }
}
