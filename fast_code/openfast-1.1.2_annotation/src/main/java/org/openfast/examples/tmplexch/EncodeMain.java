package org.openfast.examples.tmplexch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;

public class EncodeMain extends OpenFastExample {
    
    private static Options options = new Options();
    
    static {
        options.addOption("?", HELP, false, "Displays this message");
        options.addOption("e", ERROR, false, "Show stacktrace information");
        options.addOption("n", NAMESPACE_AWARENESS, false, "Enables namespace awareness");
        options.addOption("t", MESSAGE_TEMPLATE_FILE, true, "Message template definition file");
        options.addOption("o", OUTPUT_FILE, true, "FAST Encoded output file");
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
        try {
            templatesFile = getFile(cl, MESSAGE_TEMPLATE_FILE);
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
            displayHelp("consumer", options);
        }
        OutputStream out = System.out;
        File outFile = null;
        if (cl.hasOption(OUTPUT_FILE)) {
            try {
                outFile = getFile(cl, OUTPUT_FILE);
                if (!outFile.exists())
                    outFile.createNewFile();
                out = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                System.out.println("Unable to create output file.");
                System.exit(1);
            } catch (IOException e) {
                System.out.println("Unable to create output file.");
                System.exit(1);
            }
        }
        TemplateExchangeDefinitionEncoder tmplExchanger = new TemplateExchangeDefinitionEncoder(templatesFile, cl.hasOption(NAMESPACE_AWARENESS), out);
        try {
            tmplExchanger.start();
            if (cl.hasOption(OUTPUT_FILE)) {
                System.out.println("Templates encoded to file " + outFile.getAbsolutePath() + ".");
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
            } catch (IOException e) {
            }
        }
    }
}
