package org.openfast.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Stack;

public class XmlWriter {
    private final static int DEFAULT_TAB_SIZE = 4;
    private final PrintWriter writer;
    private int tabSize = DEFAULT_TAB_SIZE;
    private String indent = "";
    private boolean open;
    private boolean hasChildren;
    private Stack elementStack = new Stack();
    private boolean processingInstructionsEnabled;
    private boolean started = false;

    public XmlWriter(OutputStream destination) {
        this.writer = new PrintWriter(new OutputStreamWriter(destination));
    }
    
    public void setEnableProcessingInstructions(boolean enableProcessingInstructions) {
        this.processingInstructionsEnabled = enableProcessingInstructions;
    }
    
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public void start(String nodeName) {
        if (!started && processingInstructionsEnabled)
            writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        started = true;
        hasChildren = false;
        if (open) {
            writer.println(">");
        }
        writer.print(indent + "<" + nodeName);
        open = true;
        indent();
        elementStack.push(nodeName);
    }
    

    public void addAttribute(String name, String value) {
        if (!open) {
            throw new IllegalStateException("Cannot add attribute here.");
        }
        writer.print(" " + name + "=\"" + value + "\"");
    }
    
    public void end() {
        unindent();
        String nodeName = (String) elementStack.pop();
        if (open || !hasChildren) {
            writer.println("/>");
        } else {
            writer.println(indent + "</" + nodeName + ">");
        }
        writer.flush();
        hasChildren = true;
        open = false;
    }

    private void indent() {
        for (int i=0; i<tabSize; i++)
            indent += " ";
    }
    
    private void unindent() {
        indent = indent.substring(tabSize);
    }
}