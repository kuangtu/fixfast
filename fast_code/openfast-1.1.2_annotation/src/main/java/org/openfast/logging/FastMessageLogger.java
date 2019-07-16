package org.openfast.logging;

import org.openfast.Message;

public interface FastMessageLogger {

    public enum Direction {
        INBOUND("[<--]"), OUTBOUND("[-->]");

        private final String directionString;

        private Direction(String direction) {
            this.directionString = direction;
        }

        @Override
        public String toString() {
            return directionString;
        }
    }

    FastMessageLogger NULL = new FastMessageLogger() {
        public void log(Message message, byte[] bytes, Direction direction) {
        }
    };

    public void log(Message message, byte[] bytes, Direction direction);
}
