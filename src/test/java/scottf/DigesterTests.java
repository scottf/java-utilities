package scottf;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DigesterTests {

    @Test
    public void testDigester() throws NoSuchAlgorithmException {
        String s = ResourceUtils.resourceAsString("digester_test_bytes_000100.txt");
        Digester d = new Digester();
        d.update(s);
        assertEquals("IdgP4UYMGt47rgecOqFoLrd24AXukHf5-SVzqQ5Psg8=", d.getDigestValue());

        s = ResourceUtils.resourceAsString("digester_test_bytes_001000.txt");
        d.reset(s);
        assertEquals("DZj4RnBpuEukzFIY0ueZ-xjnHY4Rt9XWn4Dh8nkNfnI=", d.getDigestValue());

        s = ResourceUtils.resourceAsString("digester_test_bytes_010000.txt");
        d.reset(s);
        assertEquals("RgaJ-VSJtjNvgXcujCKIvaheiX_6GRCcfdRYnAcVy38=", d.getDigestValue());

        s = ResourceUtils.resourceAsString("digester_test_bytes_100000.txt");
        d.reset(s);
        assertEquals("yan7pwBVnC1yORqqgBfd64_qAw6q9fNA60_KRiMMooE=", d.getDigestValue());
    }
}
