package opticnav.ardd.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * BlockingInputStream ensures all buffers are read fully. The thread blocks
 * until the buffer is full or there's an exception.
 * 
 * For example, Socket.getInputStream() does not provide this guarantee.
 *
 */
public class BlockingInputStream extends InputStream {
    private InputStream in;
    
    public BlockingInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        int val;
        do {
            val = this.in.read();
        } while (val == 0);
        
        return val;
    }
    
    @Override
    public int read(byte[] b) throws IOException {
        return read_s(b, 0, b.length);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return read_s(b, off, len);
    }

    /**
     * Private implementation of read, so that it doesn't get overridden
     */
    public int read_s(byte[] b, int off, int len) throws IOException {
        int sz = 0;
        
        while (sz < len) {
            int ret = this.in.read(b, sz+off, b.length-sz);
            if (ret < 0) {
                return ret;
            } else {
                sz += ret;
            }
        }
        
        return sz;
    }

    @Override
    public int available() throws IOException {
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.in.skip(n);
    }
}
