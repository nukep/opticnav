package opticnav.ardd.protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * PrimitiveReader can read any data type from the ardd protocols.
 * 
 * All integers are little-endian.
 *
 */
public final class PrimitiveReader {
    private InputStream in;
    
    public PrimitiveReader(InputStream input) {
        this.in = input;
    }
    
    public byte[] readBlob() throws IOException {
        int length = readSInt32();
        assert length > 0;
        
        byte[] buf = new byte[length];
        this.in.read(buf);
        return buf;
    }
    
    public byte[] readFixedBlob(int length) throws IOException {
        assert length > 0;
        
        byte[] buf = new byte[length];
        this.in.read(buf);
        this.in.read
        return buf;
    }
    
    public String readString() throws IOException {
        int length = readUInt16();
        assert length > 0;
        
        byte[] buf = new byte[length];
        this.in.read(buf);
        return new String(buf, "UTF-8");
    }
    
    public int readUInt8() throws IOException {
        return this.in.read();
    }
    
    public int readUInt16() throws IOException {
        return (int)readUInt(2);
    }
    
    public int readUInt31() throws IOException {
        int value = (int)readUInt(4);
        if (value >= 0) {
            return value;
        } else {
            throw new IOException("Out of range: " + value);
        }
    }
    
    public int readSInt32() throws IOException {
        return (int)readUInt(4);
    }
    
    private long readUInt(int bytes) throws IOException {
        assert bytes < 8;
        
        byte[] buf = new byte[bytes];
        this.in.read(buf);
        
        int value = buf[0];
        
        for (int i = 1; i < bytes; i++) {
            value |= buf[i] >> (i*8);
        }
        
        return value;
    }
}
