package org.kurodev.bmpreader.logic.image;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.kurodev.bmpreader.logic.util.EasyByteReader;
import org.kurodev.bmpreader.logic.util.EasyByteWriter;
import org.kurodev.bmpreader.logic.util.Encodable;
import org.kurodev.bmpreader.logic.util.EncodingException;
import org.kurodev.bmpreader.logic.util.byteutils.reader.ByteReader;
import org.kurodev.bmpreader.logic.util.byteutils.writer.ByteWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author kuro
 **/
public class EncodableImage implements Encodable {
    public static final String EXTENSION = ".kbp"; //kuro BitMap
    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;
    Color[][] bytes;
    int width;
    int height;

    public EncodableImage(Image img) {
        decode(img);
    }

    public EncodableImage(EasyByteReader in) {
        decode(in);
    }

    public EncodableImage(int width, int height) {
        this.width = width;
        this.height = height;
        bytes = new Color[width][height];
        fillImage();
    }

    public void decode(Image img) {
        PixelReader reader = img.getPixelReader();
        height = (int) img.getHeight();
        width = (int) img.getWidth();
        bytes = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bytes[x][y] = reader.getColor(x, y);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void fillImage() {
        for (Color[] y : bytes) {
            for (int i = 0, yLength = y.length; i < yLength; i++) {
                y[i] = WHITE;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncodableImage image = (EncodableImage) o;
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            if (!Arrays.equals(bytes[i], image.bytes[i])) {
                return false;
            }
        }
        return width == image.width &&
                height == image.height;
    }

    @Override
    public String toString() {
        return "Image{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(width, height);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    private byte[] writeImageBytes() {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        ByteWriter writer = new ByteWriter(ous);
        for (int x = 0, bytesLength = bytes.length; x < bytesLength; x++) {
            for (int y = 0; y < bytes[x].length; y++) {
                if (WHITE.equals(bytes[x][y])) {
                    writer.writeZero();
                } else {
                    writer.writeOne();
                }
            }
        }
        writer.fillLastByte();
        return ous.toByteArray();
    }

    @Override
    public void decode(EasyByteReader data) {
        width = data.readInt();
        height = data.readInt();
        bytes = readImageBytes(data.readRemaining());
    }

    private Color[][] readImageBytes(byte[] b) {
        Color[][] out = new Color[width][height];
        ByteReader reader = new ByteReader(new ByteArrayInputStream(b));
        for (int x = 0; x < out.length; x++) {
            for (int y = 0; y < out[0].length; y++) {
                if (reader.hasMore()) {
                    if (reader.read()) {
                        //black
                        out[x][y] = BLACK;
                    } else {
                        //white
                        out[x][y] = WHITE;
                    }
                } else {
                    throw new EncodingException("Reached end of stream");
                }
            }
        }
        return out;
    }

    public Image toImage() {
        WritableImage img = new WritableImage(width, height);
        for (int x = 0; x < bytes.length; x++) {
            for (int y = 0; y < bytes[x].length; y++) {
                img.getPixelWriter().setColor(x, y, bytes[x][y]);
            }
        }
        return img;
    }

    public Image toImage(Consumer<Double> progressTrack) {
        WritableImage img = new WritableImage(width, height);
        double total = width * height;
        int oldProgress = 0;
        int written = 0;
        for (int x = 0; x < bytes.length; x++) {
            for (int y = 0; y < bytes[x].length; y++) {
                img.getPixelWriter().setColor(x, y, bytes[x][y]);
                written++;
                int newProgress = (int) (100 * (written / total));
                if ((newProgress - 5) >= oldProgress) {
                    progressTrack.accept((double) newProgress / 100);
                    oldProgress = newProgress;
                }
            }
        }
        return img;
    }

    public void write(int x, int y, boolean isBlack) {
        Color color = isBlack ? BLACK : WHITE;
        try {
            bytes[x][y] = color;
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void encode(EasyByteWriter out) {
        out.write(width);
        out.write(height);
        out.write(writeImageBytes());
        out.write((byte) 1);
    }

    public void copy(EncodableImage img) {
        System.out.println("copying state from: " + img);
        this.width = img.width;
        this.height = img.height;
        if (img.bytes.length != width || img.bytes[0].length != height) {
            throw new RuntimeException(String.format("WHAT THE FUCK DUDE\nExpected: x:%d,y:%d\nActual: x:%d,y:%d\n"
                    , width, height, img.bytes.length, img.bytes[0].length));
        }
        this.bytes = img.bytes;
    }
}
