package org.openfast.examples.consumer;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;
import org.openfast.examples.MessageBlockReaderFactory;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.session.multicast.MulticastEndpoint;
import org.openfast.session.multicast.MulticastClientEndpoint;
import org.openfast.session.tcp.TcpEndpoint;

public class Main extends OpenFastExample {
    private static Options options = new Options();
    
    static {
        options.addOption("?", HELP, false, "Displays this message");
        options.addOption("r", PROTOCOL, true, "Protocol [tcp|udp] defaults to tcp");
        options.addOption("p", PORT, true, "Port to connect to");
        options.addOption("h", HOST, true, "The host name of the server (or group name for multicast)");
        options.addOption("i", INTERFACE, true, "The ip address of the network interface to use");
        options.addOption("e", ERROR, false, "Show stacktrace information");
        options.addOption("t", MESSAGE_TEMPLATE_FILE, true, "Message template definition file");
        options.addOption("j", READ_OFFSET, true, READ_OFFSET_DESCRIPTION);
        options.addOption("z", VARIANT, true, VARIANT_DESCRIPTION);
        options.addOption("d", RESET, false, RESET_DESCRIPTION);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine("consumer", args, options);
        if (cl.hasOption(HELP)) {
            displayHelp("consumer", options);
        }
        Endpoint endpoint = null;
        boolean showStacktrace = cl.hasOption(ERROR);
        File templatesFile = null;
        try {
            Assert.assertTrue(cl.hasOption(PORT), "The required parameter \"" + PORT + "\" is missing.");
            int port = getInteger(cl, PORT);
            String host = cl.hasOption(HOST) ? cl.getOptionValue(HOST) : "localhost";
            String ifaddr = cl.hasOption(INTERFACE) ? cl.getOptionValue(INTERFACE) : null;
            
            if(isMulticast(cl)) {
                endpoint = new MulticastClientEndpoint(port, host, ifaddr);
            }
            if (endpoint == null) {
                endpoint = new TcpEndpoint(host, port);
            }
            templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
            displayHelp("consumer", options);
        }
        

        final int readOffset = cl.hasOption(READ_OFFSET) ? getInteger(cl, READ_OFFSET) : 0;
		final Variant variant = cl.hasOption(VARIANT) ? getVariant(cl) : Variant.DEFAULT;
        final boolean shouldResetOnEveryMessage = (cl.hasOption(RESET) || (Variant.CME == variant));
		final MessageBlockReaderFactory msgBlockReaderFactory = new MessageBlockReaderFactory(variant, readOffset, isMulticast(cl));
		FastMessageConsumer consumer = new FastMessageConsumer(endpoint, templatesFile, msgBlockReaderFactory, shouldResetOnEveryMessage);
        
        try {
            consumer.start();
        } catch (FastConnectionException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("Unable to connect to endpoint: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("An IO error occurred while consuming messages: " + e.getMessage());
            System.exit(1);
        }
    }
}
