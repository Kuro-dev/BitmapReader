package org.kurodev.bmpreader.logic.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author kuro
 **/
public class EasyByteWriter extends ByteArrayOutputStream {
    static final byte[] ENCODABLE_DELIMITER = {0, 127, 0};

    public EasyByteWriter() {
    }

    public EasyByteWriter(int size) {
        super(size);
    }

    public void write(byte[] b) {
        try {
            super.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte b) {
        super.write(b);
    }

    public void write(int i) {
        write(ByteUtils.intToByte(i));
    }

    public void write(int[] ints) {
        write(ByteUtils.intToByte(ints));
    }

    public void write(long i) {
        write(ByteUtils.longToByte(i));
    }

    public void write(String s) {
        write(StringEncoder.encode(s));
    }

    public void write(Encodable en) {
        en.encode(this);
        this.write(ENCODABLE_DELIMITER);
    }
}
