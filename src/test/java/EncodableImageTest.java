import org.junit.Test;
import org.kurodev.bmpreader.logic.image.EncodableImage;
import org.kurodev.bmpreader.logic.util.EasyByteReader;
import org.kurodev.bmpreader.logic.util.EasyByteWriter;
import org.kurodev.bmpreader.logic.util.Encodable;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author kuro
 **/
public class EncodableImageTest {
    public void testEncodable(Encodable a, Encodable b) {
        assertNotEquals(a, b);
        EasyByteWriter writer = new EasyByteWriter();
        a.encode(writer);
        byte[] bytes = writer.toByteArray();
//        System.out.println("Written:\n" + byteArrayToString(bytes));
        EasyByteReader reader = new EasyByteReader(bytes);
        b.decode(reader);
        assertEquals(a, b);
    }

    String byteArrayToString(byte[] b) {
        StringBuilder a = new StringBuilder();
        for (byte value : b) {
            a.append(Integer.toBinaryString(value)).append("\n");
        }
        return a.toString();
    }

    @Test
    public void imagesWorkReliably() {
        for (int i = 0; i < 100; i++) {
            imagesEncodingTest();
        }
    }

    @Test
    public void toImageAndFromImageTest() {
        EncodableImage a = new EncodableImage(53, new Random().nextInt(21516));
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                a.write(x, y, new Random().nextBoolean());
            }
        }
        EncodableImage b = new EncodableImage(a.toImage());
        assertEquals(a, b);
    }

    public void imagesEncodingTest() {
        EncodableImage a = new EncodableImage(2, 3);
        Encodable b = new EncodableImage(60, 60);
        for (int x = 0; x < a.getWidth(); x++) {
            for (int y = 0; y < a.getHeight(); y++) {
                boolean bool = new Random().nextBoolean();
                a.write(x, y, bool);
//                System.out.printf("writing a %dx%d value %s\n", x, y, bool ? "black" : "white");
            }
        }
        testEncodable(a, b);
    }
}
