package org.openfast.examples.performance;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.error.ErrorCode;
import org.openfast.error.ErrorHandler;
import org.openfast.examples.Assert;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

public class PerformanceRunner implements ErrorHandler {

    private File templatesFile;
    private File dataFile;
    private boolean showStacktrace;
    private boolean namespaceAware;
    private boolean preloadData;
    private String format;
    private ByteArrayInputStream byteIn;
    private TemplateRegistry templateRegistry;
    
    public PerformanceRunner(File templatesFile, File dataFile) {
        this.templatesFile = templatesFile;
        this.dataFile = dataFile;
    }
    
    public PerformanceResult run() {
        try {
            loadTemplates();
            InputStream dataIn = getFastEncodedDataStream();
            Context context = new Context();
            context.setTemplateRegistry(templateRegistry);
            context.setErrorHandler(this);
            FastDecoder decoder = new FastDecoder(context, dataIn);
            PerformanceResult result = new PerformanceResult();
            Message msg = null;
            result.start();
            do {
                result.startMessage();
                msg = decoder.readMessage();
                if (msg != null)
                    result.finishMessage();
            } while (msg != null);
            result.stop();
            return result;
        } catch (Exception e) {
            if (showStacktrace) {
                e.printStackTrace();
            } else {
                System.out.println("Error occurred while decoding messages: " + e.getMessage());
            }
        }
        return null;
    }

    private void loadTemplates() throws FileNotFoundException {
        InputStream source = null;
        if (templateRegistry == null) {
            try {
                XMLMessageTemplateLoader loader = new XMLMessageTemplateLoader(namespaceAware);
                loader.setLoadTemplateIdFromAuxId(true);
                Assert.assertTrue(templatesFile.exists(), "The message template file \"" + templatesFile.getAbsolutePath() + "\" does not exist.");
                source = new FileInputStream(templatesFile);
                loader.load(source);
                this.templateRegistry = loader.getTemplateRegistry();
            } finally {
                if (source != null)
                    try {
                        source.close();
                    } catch (IOException e) {
                    }
            }
        }
    }

    public void setPreloadData(boolean preloadData) {
        this.preloadData = preloadData;
    }

    public void setShowStacktrace(boolean showStacktrace) {
        this.showStacktrace = showStacktrace;
    }

    public void setNamespaceAwareness(boolean namespaceAwareness) {
        this.namespaceAware = namespaceAwareness;
    }

    private InputStream getFastEncodedDataStream() {
        if (preloadData && byteIn != null) {
            byteIn.reset();
            return byteIn;
        }
        Assert.assertTrue(dataFile.exists() && dataFile.canRead(), "The file \"" + dataFile.getAbsolutePath() + "\" does not exist.");
        try {
            InputStream dataIn = null;
            FileInputStream fileIn = new FileInputStream(dataFile);
            if ("hex".equals(format))
                dataIn = new HexadecimalInputStream(fileIn);
            else
                dataIn = fileIn;
            if (preloadData) {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream((int) dataFile.length());
                copy(dataIn, byteOut, 1024);
                byte[] buffer = byteOut.toByteArray();
                byteIn = new ByteArrayInputStream(buffer);
                return byteIn;
            }
            return new BufferedInputStream(dataIn);
        } catch (FileNotFoundException e) {
            error(null, "File \"" + dataFile.getAbsolutePath() + "\" could not be found.", e);
        } catch (IOException e) {
            error(null, "File \"" + dataFile.getAbsolutePath() + "\" could not be found.", e);
        }
            
        return null;
    }
    
    public static void copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        BufferedOutputStream bOut = new BufferedOutputStream(out);
        BufferedInputStream bIn = new BufferedInputStream(in);
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        try {
            do {
                len = bIn.read(buffer);
                if (len < 0) break;
                bOut.write(buffer, 0, len);
            } while (len == bufferSize);
        } finally {
            bOut.close();
            bIn.close();
        }
    }

    public void error(ErrorCode code, String message) {
        error(code, message, null);
    }

    public void error(ErrorCode code, String message, Throwable t) {
        if (showStacktrace && t != null)
            t.printStackTrace();
        throw new AssertionError(message);
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
