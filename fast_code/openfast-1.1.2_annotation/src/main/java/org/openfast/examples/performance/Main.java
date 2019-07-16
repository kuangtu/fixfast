package org.openfast.examples.performance;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.openfast.examples.Assert;
import org.openfast.examples.OpenFastExample;

public class Main extends OpenFastExample {
    private static Options options = new Options();
    
    static {
        options.addOption("?", "help", false, "Displays this message.");
        options.addOption("n", "ns", false, "Enables namespace awareness");
        options.addOption("t", "template", true, "Message Template definition file");
        options.addOption("d", "data", true, "FAST Encoded data");
        options.addOption("p", "preload", false, "Preload data into memory instead of decoding directly from file");
        options.addOption("e", "error", false, "Show stacktrace information");
        options.addOption("r", "repeat", true, "Re process data file X number of times");
        options.addOption("f", "format", true, "Data format [hex|binary] default is binary");
        options.addOption("c", "continuous", false, "Keep repeating the test until the process is killed");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine("perf", args, options);
        if (cl.hasOption("help")) {
            displayHelp("perf", options);
        }
        try {
            File templatesFile = new File(getString(cl, "template"));
            File dataFile = new File(getString(cl, "data"));
            PerformanceRunner performanceRunner = new PerformanceRunner(templatesFile, dataFile);
            if (cl.hasOption("ns"))
                performanceRunner.setNamespaceAwareness(true);
            if (cl.hasOption("error"))
                performanceRunner.setShowStacktrace(true);
            if (cl.hasOption("preload"))
                performanceRunner.setPreloadData(true);
            if (cl.hasOption("format"))
                performanceRunner.setFormat(cl.getOptionValue("format"));
            
            if (cl.hasOption("continuous"))
                runContinuous(performanceRunner);
            else if (cl.hasOption("repeat"))
                runRepeat(performanceRunner, Integer.parseInt(cl.getOptionValue("repeat")));
            else {
                run(performanceRunner);
                run(performanceRunner);
            }
        } catch (AssertionError ae) {
            System.out.println(ae.getMessage());
            displayHelp("perf", options);
        } catch (Exception e) {
            if (cl.hasOption("error"))
                e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static void runRepeat(PerformanceRunner performanceRunner, int repeat) {
        for (int i=0; i<repeat; i++)
            run(performanceRunner);
    }

    private static void runContinuous(PerformanceRunner performanceRunner) {
        while (true) {
            run (performanceRunner);
        }
    }

    private static void run(PerformanceRunner performanceRunner) {
        PerformanceResult result = performanceRunner.run();
        if (result == null)
            return;
        System.out.println("Decoded " + result.getMessageCount() + " messages in " + result.getTime() + " milliseconds.");
        System.out.println("Average decode time per message: " + ((result.getTime() * 1000) / (result.getMessageCount())) + " microseconds");
    }

    private static String getString(CommandLine cl, String option) {
        Assert.assertTrue(cl.hasOption(option), "The required parameter \"" + option + "\" was not specified.");
        return cl.getOptionValue(option);
    }
}
