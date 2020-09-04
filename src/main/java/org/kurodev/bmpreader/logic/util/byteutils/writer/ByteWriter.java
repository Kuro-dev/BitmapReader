package org.kurodev.bmpreader.logic.util.byteutils.writer;

import org.kurodev.bmpreader.logic.util.EncodingException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Kuro
 */
public class ByteWriter {

    private final WritableByte current = new WritableByte();
    private final OutputStream out;

    public ByteWriter(OutputStream out) {
        this.out = out;
    }

    private void checkCurrentByte() {
        if (!current.hasSpace()) {
            byte aByte = current.getByte();
            try {
                out.write(aByte);
                out.flush();
            } catch (IOException e) {
                throw new EncodingException(e);
            }
            current.reset();
        }
    }

    public void writeZero() {
        checkCurrentByte();
        current.addZero();
    }

    public void writeOne() {
        checkCurrentByte();
        current.addOne();
    }

    /**
     * Fills up the currently started byte with zeros and pushes it to the stream.
     */
    public void fillLastByte() {
        if (current.getByte() > 0) {
            current.fill();
            try {
                out.write(current.getByte());
            } catch (IOException e) {
                throw new EncodingException(e);
            }
            current.reset();
        }
    }
}
