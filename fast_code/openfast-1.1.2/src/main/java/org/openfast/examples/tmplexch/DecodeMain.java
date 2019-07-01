package org.openfast.examples.tmplexch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;

public class DecodeMain extends OpenFastExample {
    
    private static Options options = new Options();
    
    static {
        options.addOption("?", HELP, false, "Displays this message");
        options.addOption("e", ERROR, false, "Show stacktrace information");
        options.addOption("n", NAMESPACE_AWARENESS, false, "Enables namespace awareness");
        options.addOption("t", MESSAGE_TEMPLATE_FILE, true, "Output message template definition file");
        options.addOption("f", FAST_DATA_FILE, true, "FAST Encoded Template Exchange file");
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine("consumer", args, options);
        if (cl.hasOption("help")) {
            displayHelp("consumer", options);
        }
        boolean showStacktrace = cl.hasOption(ERROR);
        File templatesFile = null;
        OutputStream out = System.out;
        if (cl.hasOption(MESSAGE_TEMPLATE_FILE)) {
            try {
                templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
                Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
                if (!templatesFile.exists())
                    templatesFile.createNewFile();
                Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
                out = new FileOutputStream(templatesFile);
            } catch (AssertionError e) {
                System.out.println(e.getMessage());
                displayHelp("consumer", options);
            } catch (FileNotFoundException e) {
                System.out.println("Unable to create output file.");
                System.exit(1);
            } catch (IOException e) {
                System.out.println("Unable to create output file.");
                System.exit(1);
            }
        }
        InputStream fastIn = null;
        try {
            File inFile = getFile(cl, FAST_DATA_FILE);
            if (!inFile.exists())
                inFile.createNewFile();
            Assert.assertTrue(inFile.exists(), "The fast encoded data file \"" + inFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!inFile.isDirectory(), "The fast encoded data file \"" + inFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(inFile.canRead(), "The fast encoded data file \"" + inFile.getAbsolutePath() + "\" is not readable.");
            fastIn = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open data file.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Unable to open data file.");
            System.exit(1);
        }
        TemplateExchangeDefinitionDecoder tmplExchanger = new TemplateExchangeDefinitionDecoder(fastIn, cl.hasOption(NAMESPACE_AWARENESS), out);
        try {
            tmplExchanger.start();
            if (cl.hasOption(MESSAGE_TEMPLATE_FILE)) {
                System.out.println("Templates written to " + templatesFile.getAbsolutePath() + ".");
            }
        } catch (IOException e) {
            if (showStacktrace)
                e.printStackTrace();
            System.out.println("An IO error occurred while consuming messages: " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                out.flush();
                out.close();
                fastIn.close();
            } catch (IOException e) {
            }
        }
    }
}
