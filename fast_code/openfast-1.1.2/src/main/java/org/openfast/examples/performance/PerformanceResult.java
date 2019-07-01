package org.openfast.examples.performance;

public class PerformanceResult {

    private long startTime;
    private long stopTime;
    private int numMessages = 0;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void startMessage() {
        
    }

    public void finishMessage() {
        numMessages++;
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
    }

    public int getMessageCount() {
        return numMessages;
    }

    public long getTime() {
        return stopTime - startTime;
    }
}
