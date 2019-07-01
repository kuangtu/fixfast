package org.openfast.examples.scp10;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;
import org.openfast.session.Endpoint;
import org.openfast.session.FastConnectionException;
import org.openfast.session.tcp.TcpEndpoint;

public class Main extends OpenFastExample {
    private static Options options = new Options();
    
    static {
        options.addOption("?", HELP, false, "Displays this message");
        options.addOption("p", PORT, true, "Port to serve data on");
        options.addOption("h", HOST, true, "The host name of the server");
        options.addOption("e", ERROR, false, "Show stacktrace information");
        options.addOption("t", MESSAGE_TEMPLATE_FILE, true, "Message template definition file");
        options.addOption("x", XML_DATA_FILE, true, "The XML data to convert to FAST");
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine("scp10producer", args, options);
        if (cl.hasOption(HELP)) {
            displayHelp("scp10producer", options);
        }
        Endpoint endpoint = null;
        boolean showStacktrace = cl.hasOption(ERROR);
        File templatesFile = null;
        File xmlDataFile = null;
        try {
            Assert.assertTrue(cl.hasOption(PORT), "The required parameter \"" + PORT + "\" is missing.");
            int port = getInteger(cl, PORT);
            String host = cl.hasOption(HOST) ? cl.getOptionValue(HOST) : "localhost";
            
            endpoint = new TcpEndpoint(host, port);
            templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
            xmlDataFile = getFile(cl, XML_DATA_FILE);
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
            displayHelp("consumer", options);
        }
        ScpMessageProducer producer = new ScpMessageProducer(endpoint, templatesFile);
        try {
            producer.start(xmlDataFile);
            producer.stop();
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
