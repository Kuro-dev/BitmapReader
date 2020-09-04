package org.kurodev.bmpreader.logic.util.byteutils.reader;

import org.kurodev.bmpreader.logic.util.EncodingException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kuro
 **/
public class ByteReader {

    private final InputStream bytes;
    private final int totalBytes;
    int index = 0;
    int current;
    byte turn;
    private boolean hasMore = true;

    public ByteReader(InputStream bytes) {
        this.bytes = bytes;
        try {
            totalBytes = bytes.available();
        } catch (IOException e) {
            throw new EncodingException(e);
        }
        assignNextByte();
    }

    private void assignNextByte() {
        try {
            current = bytes.read();
            index++;
        } catch (IOException e) {
            throw new EncodingException(e);
        }
    }

    void checkTurn() {
        if (turn < 8) {
            return;
        }
        if (index < totalBytes) {
            assignNextByte();
            turn = 0;
        } else {
            hasMore = false;
        }
    }

    public boolean read() {
        checkTurn();
        int checkBit = 128;
        boolean ret = ((current & checkBit) == checkBit);
        current <<= 1;
        turn++;
        return ret;
    }

    public boolean hasMore() {
        return hasMore;
    }

    @Override
    public String toString() {
        return "ByteReader{" +
                "index=" + index +
                ", current=" + Integer.toBinaryString(current) +
                ", turn=" + turn +
                '}';
    }
}
