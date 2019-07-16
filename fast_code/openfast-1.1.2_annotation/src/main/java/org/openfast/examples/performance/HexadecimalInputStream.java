package org.openfast.examples.performance;

import java.io.IOException;
import java.io.InputStream;

public class HexadecimalInputStream extends InputStream {
    private final InputStream in;
    private byte[] buffer = new byte[32];

    public HexadecimalInputStream(InputStream in) {
        this.in = in;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (buffer.length < len * 2) {
            buffer = new byte[len * 2];
        }
        int byteCount = 0;
        while (byteCount < len) {
            int toRead = (len - byteCount) * 2;
            int numRead = in.read(buffer, 0, toRead);
            boolean eof = numRead < toRead;
            int index = 0;
            while (index < numRead) {
                if (buffer[index] == '\r' || buffer[index] == '\n') {
                    index++;
                    continue;
                }
                if (index + 1 == numRead) {
                    byte[] temp = new byte[2];
                    temp[0] = buffer[index];
                    temp[1] = (byte) in.read();
                    String data = new String(temp);
                    b[off + byteCount] = (byte) Integer.parseInt(data, 16);
                    byteCount++;
                    break;
                }
                String data = new String(buffer, index, 2);
                b[off + byteCount] = (byte) Integer.parseInt(data, 16);
                byteCount++;
                index+=2;
            }
            if (eof)
                return byteCount;
        }
        return byteCount;
    }
    
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void close() throws IOException {
        in.close();
    }
}
