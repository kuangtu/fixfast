/*
 * Contributed to OpenFAST by Object Computing, Inc.
 */
package org.openfast.examples.interpret;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;
import org.openfast.examples.util.FastMessageConsumer;
import org.openfast.examples.util.FastMessageReader;

public class Main extends OpenFastExample {
    private static final String COMMAND_NAME = "interpret";

    private static final String HELP = "help";
    private static final String TEMPLATES = "template";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final String EXPOSE = "raw";
    private static final String HEAD = "head";
    private static final String RESET = "reset";

    private static Options options = new Options();

    static {
        options.addOption("?", HELP, false, "Displays this message");
        options.addOption("i", INPUT, true, "File containing raw FAST messages");
        options.addOption("t", TEMPLATES, true, "Message template definition file");
        options.addOption("o", OUTPUT, true, "File to receive interpreted messages");
        options.addOption("x", EXPOSE, false, "Show (some) raw data for each record.");
        options.addOption("h", HEAD, true, "Process only the first 'n' records.");
        options.addOption("r", RESET, false, "Reset decoder on every record");
        options.addOption("j", READ_OFFSET, true, READ_OFFSET_DESCRIPTION);
    }

    /**
     * Check to be sure a required option is present.
     * This should be moved to OpenFastExample.
     * @param cl The interpreted command line options.
     * @param option The name of the required opotion
     */
    static void required(CommandLine cl, String option){
        Assert.assertTrue(cl.hasOption(option), "The required option \"" + option + "\" is missing.");
    }

    /**
     * @param args See command line options added to "options" above.
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine(COMMAND_NAME, args, options);
        if (cl.hasOption("help")) {
            displayHelp(COMMAND_NAME, options);
            System.exit(1);
        }

        boolean showRawData = cl.hasOption(EXPOSE);
        boolean resetEveryRecord = cl.hasOption(RESET);

        int head = 0;
        if(cl.hasOption(HEAD)){
            head = getInteger(cl, HEAD);
        }

        File inputFile = null;
        File outputFile = null;
        File templatesFile = null;
        FastMessageConsumer consumer = null;
        try {
            required(cl, INPUT);
            inputFile = getFile(cl, INPUT);
            Assert.assertTrue(inputFile.exists(), "The input file \"" + inputFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!inputFile.isDirectory(), "The input file \"" + inputFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(inputFile.canRead(), "The input file \"" + inputFile.getAbsolutePath() + "\" is not readable.");

            required(cl, OUTPUT);
            outputFile = getFile(cl, OUTPUT);
            if(outputFile.exists())
            {
                Assert.assertTrue(!outputFile.isDirectory(), "The output file \"" + inputFile.getAbsolutePath() + "\" is a directory.");
                Assert.assertTrue(outputFile.canWrite(), "The output file \"" + inputFile.getAbsolutePath() + "\" is not writable.");
            }
            consumer = new FastMessageInterpreter(new FileOutputStream(outputFile));

            required(cl, TEMPLATES);
            templatesFile = getFile(cl, TEMPLATES);
            Assert.assertTrue(templatesFile.exists(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
            Assert.assertTrue(!templatesFile.isDirectory(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is a directory.");
            Assert.assertTrue(templatesFile.canRead(), "The template definition file \"" + templatesFile.getAbsolutePath() + "\" is not readable.");
        } catch (AssertionError e) {
            System.out.println(e.getMessage());
            displayHelp(COMMAND_NAME, options);
            System.exit(-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            displayHelp(COMMAND_NAME, options);
            System.exit(-1);
        }

        try {
            final int readOffset = cl.hasOption(READ_OFFSET) ? getInteger(cl, READ_OFFSET) : 0;
            
            FastMessageReader reader = new FastMessageReader(
                    new BufferedInputStream(new FileInputStream(inputFile)),
                    new BufferedInputStream(new FileInputStream(templatesFile)),
                    consumer,
                    readOffset);
            reader.setHead(head);
            reader.showRawData(showRawData);
            reader.resetEveryRecord(resetEveryRecord);

            reader.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
