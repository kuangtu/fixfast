package org.openfast.examples.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FastToXmlMain {
    /**
     * @param args
     */
    public static void main(String[] args) {
        InputStream in = System.in;
        OutputStream out = System.out;
        File xmlFile = null;
        File fastFile = null;
        if (args.length >= 1) {
            xmlFile = new File(args[0]);
            assertTrue(xmlFile.exists(), "The file \"" + xmlFile.getAbsolutePath() + "\" does not exist.");
            assertTrue(!xmlFile.isDirectory(), "The file \"" + xmlFile.getAbsolutePath() + "\" is a directory.");
            assertTrue(xmlFile.canRead(), "The file \"" + xmlFile.getAbsolutePath() + "\" cannot be read.");
            try {
                in = new FileInputStream(xmlFile);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        if (args.length >= 2) {
            fastFile = new File(args[1]);
            assertTrue(!fastFile.isDirectory(), "The file \"" + fastFile.getAbsolutePath() + "\" is a directory.");
            if (!fastFile.exists())
                try {
                    assertTrue(fastFile.createNewFile(), "The file \"" + fastFile.getAbsolutePath() + "\" could not be created.");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            assertTrue(fastFile.canWrite(), "The file \"" + fastFile.getAbsolutePath() + "\" cannot be written to.");
            try {
                out = new FileOutputStream(fastFile);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        FastToXmlConverter converter = new FastToXmlConverter();
        converter.convert(in, out);
    }

    private static void assertTrue(boolean condition, String error) {
        if (!condition) {
            System.out.println(error);
            System.exit(1);
        }
    }
}
