package org.openfast.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import org.openfast.Message;
import org.openfast.template.MessageTemplate;

public class FileFastMessageLogger implements FastMessageLogger {
    private final File file;
    private OutputStream out;

    public FileFastMessageLogger(String filePath) {
        this(new File(filePath));
    }

    public FileFastMessageLogger(File f) {
        if (f.exists() && !f.canWrite())
            throw new IllegalArgumentException("Unable to write to file: " + f.getAbsolutePath());
        this.file = f;
    }

    public void log(Message message, byte[] bytes, Direction direction) {
        try {
            createOut();
            out.write(new Date().toString().getBytes());
            out.write('\n');
            out.write((direction + "  ").getBytes());
            out.write(print(message));
            out.write('\n');
            out.write(bytes);
            out.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] print(Message message) {
        StringBuilder msgBuilder = new StringBuilder();
        MessageTemplate template = message.getTemplate();
        msgBuilder.append(template.getName()).append("[ ");
        for (int i=1; i<template.getFieldCount(); i++) {
            msgBuilder.append(template.getField(i).getName()).append('=');
            if (message.isDefined(i)) {
                msgBuilder.append(message.getString(i));
            } else {
                msgBuilder.append("null");
            }
            msgBuilder.append(' ');
        }
        msgBuilder.append(']');
        return msgBuilder.toString().getBytes();
    }

    private void createOut() throws IOException {
        if (out == null) {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            out = new FileOutputStream(file, true);
        }
    }

    public void close() {
        try {
            out.close();
        } catch (Exception e) {
        }
    }
}
