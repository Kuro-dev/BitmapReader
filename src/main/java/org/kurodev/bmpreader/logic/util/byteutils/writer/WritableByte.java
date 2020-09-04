package org.kurodev.bmpreader.logic.util.byteutils.writer;

/**
 * @author kuro
 **/
public class WritableByte {

    private int value;
    private int count = 0;

    public WritableByte() {
        this(0);
    }

    public WritableByte(int initialValue) {
        this.value = initialValue;
    }

    public void addZero() {
        if (hasSpace()) {
            value <<= 1;
            count++;
        }
    }

    public void addOne() {
        if (hasSpace()) {
            value <<= 1;
            value += 1;
            count++;
        }
    }

    public boolean hasSpace() {
        return count < Byte.SIZE && value < 256;
    }

    public byte getByte() {
        return (byte) value;
    }

    public void reset() {
        value = 0;
        count = 0;
    }

    public void fill() {
        while (hasSpace()) {
            addZero();
        }
    }

    @Override
    public String toString() {
        return "WritableByte{" +
                "value=" + Integer.toBinaryString(value) +
                ", count=" + count +
                '}';
    }
}