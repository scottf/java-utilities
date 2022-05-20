package scottf;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ByteArrayBuilderTests {
    public static final SecureRandom SRAND = new SecureRandom();
    public static final Random PRAND = new Random(bytesToLong(SRAND.generateSeed(8))); // seed with 8 bytes (64 bits)
    byte[] TEST_BYTES =  "abcdeg".getBytes();
    byte[] OTHER_BYTES = "uvwxyz".getBytes();

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.put(bytes);
        buffer.flip();// need flip
        return buffer.getLong();
    }

    @Test
    public void byte_array_builder_works() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        String testString = "abcdefghij";
        _test(PRAND, bab, Collections.singletonList(testString), StandardCharsets.US_ASCII);
    }

    @Test
    public void equalsBytes() {
        ByteArrayBuilder bab = new ByteArrayBuilder(TEST_BYTES);
        assertTrue(bab.equals(TEST_BYTES));
        assertFalse(bab.equals(OTHER_BYTES));
        assertFalse(bab.equals((byte[])null));
        assertFalse(bab.equals("x".getBytes()));
    }

    @Test
    public void copyTo() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        bab.append("0123456789");
        byte[] target = "AAAAAAAAAAAAAAAAAAAA".getBytes(StandardCharsets.US_ASCII);
        assertEquals(10, bab.copyTo(target, 0));
        assertEquals(10, bab.copyTo(target, 10));
        assertEquals("01234567890123456789", new String(target));
    }

    @Test
    public void constructorCoverage() {
        ByteArrayBuilder bab = new ByteArrayBuilder("0123456789".getBytes());
        assertEquals("0123456789", bab.toString());
        assertEquals(10, bab.internalArray().length);

        bab = new ByteArrayBuilder(-1, StandardCharsets.UTF_8);
        assertEquals(ByteArrayBuilder.DEFAULT_OTHER_ALLOCATION, bab.internalArray().length);

        bab = new ByteArrayBuilder(-1, -1, StandardCharsets.US_ASCII);
        assertEquals(ByteArrayBuilder.DEFAULT_ASCII_ALLOCATION, bab.internalArray().length);

        bab = new ByteArrayBuilder(-1, -1, StandardCharsets.UTF_8);
        assertEquals(ByteArrayBuilder.DEFAULT_OTHER_ALLOCATION, bab.internalArray().length);

        bab = new ByteArrayBuilder(-1, 100, StandardCharsets.US_ASCII);
        assertEquals(ByteArrayBuilder.DEFAULT_ASCII_ALLOCATION, bab.internalArray().length);

        bab = new ByteArrayBuilder(-1, 100, StandardCharsets.UTF_8);
        assertEquals(ByteArrayBuilder.DEFAULT_OTHER_ALLOCATION, bab.internalArray().length);

        bab = new ByteArrayBuilder(100, -1, StandardCharsets.US_ASCII);
        assertEquals(100, bab.internalArray().length);

        bab = new ByteArrayBuilder(100, -1, StandardCharsets.UTF_8);
        assertEquals(100, bab.internalArray().length);
    }

    @Test
    public void miscCoverage() {
        ByteArrayBuilder bab = new ByteArrayBuilder(1)
            .append(" ")
            .append("\r\n")
            .append((String)null)
            .append((ByteArrayBuilder)null)
            .append(new ByteArrayBuilder())
            .append("foo")
            .append((CharBuffer)null)
            .append(CharBuffer.wrap("bar"))
            .append(4273)
            .append(new byte[0])
            .append(new byte[0], 0)
            .append((byte)122)
            .append("baz".getBytes())
            .append(new byte[0], 0)
            .append("baz".getBytes(), 3)
            .append("baz".getBytes(), 2, 1)
            .append("baz".getBytes(), 0, 0)
            ;
        assertEquals(" \r\nnullfoonullbar4273zbazbazz", bab.toString());
        assertEquals(29, bab.length());
        bab.append(bab);
        assertEquals(" \r\nnullfoonullbar4273zbazbazz \r\nnullfoonullbar4273zbazbazz", bab.toString());
        assertEquals(58, bab.length());

        bab.setAllocationSize(100);
        bab.clear();
        assertEquals(0, bab.length());
    }

    private void _test(Random r, ByteArrayBuilder bab, List<String> testStrings, Charset charset) {
        String expectedString = "";
        for (String testString : testStrings) {
            int loops = 1000 / testString.length();
            for (int x = 1; x < loops; x++) {
                String more = testString + r.nextInt(Integer.MAX_VALUE);
                bab.append(more);
                expectedString = expectedString + more;
                byte[] bytes = bab.toByteArray();
                assertEquals(expectedString.getBytes(charset).length, bytes.length);
                assertEquals(expectedString, new String(bytes, charset));
            }
        }
    }
}
