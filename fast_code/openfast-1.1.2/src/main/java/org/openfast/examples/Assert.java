package org.openfast.examples;

public class Assert {
    public static void assertTrue(boolean condition, String errorMessage) {
        if (!condition)
            throw new AssertionError(errorMessage);
    }
}
