package org.openfast.examples.util;

import org.openfast.Message;

import java.io.PrintStream;

/**
 * An interface for objects that handle decoded FAST messages.
 */
public interface FastMessageConsumer {
    /**
     * Handle an incoming message
     * @param message the message to be handled
     */
    void accept(Message message);

    /**
     * Add additonal information related to the message
     * @param message the additional information
     */
    void annotate(String message);
}
