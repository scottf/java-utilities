package scottf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Digester {
    private static final String DEFAULT_DIGEST_ALGORITHM = "SHA-256";
    private static final Charset DEFAULT_STRING_ENCODING = StandardCharsets.UTF_8;

    private final Charset stringCharset;
    private final Base64.Encoder encoder;
    private final MessageDigest digest;

    public Digester() throws NoSuchAlgorithmException {
        this(null, null, null);
    }

    public Digester(Base64.Encoder encoder) throws NoSuchAlgorithmException {
        this(null, null, encoder);
    }

    public Digester(String digestAlgorithm) throws NoSuchAlgorithmException {
        this(digestAlgorithm, null, null);
    }

    public Digester(String digestAlgorithm, Charset stringCharset, Base64.Encoder encoder) throws NoSuchAlgorithmException {
        this.stringCharset = stringCharset == null ? DEFAULT_STRING_ENCODING : stringCharset;
        this.encoder = encoder == null ? Base64.getUrlEncoder() : encoder;
        this.digest = MessageDigest.getInstance(
            digestAlgorithm == null ? DEFAULT_DIGEST_ALGORITHM : digestAlgorithm);
    }

    public Digester update(String input) {
        digest.update(input.getBytes(stringCharset));
        return this;
    }

    public Digester update(byte[] input) {
        digest.update(input);
        return this;
    }

    public Digester update(byte[] input, int offset, int len) {
        digest.update(input, offset, len);
        return this;
    }

    public Digester reset() {
        digest.reset();
        return this;
    }

    public Digester reset(String input) {
        return reset().update(input);
    }

    public Digester reset(byte[] input) {
        return reset().update(input);
    }

    public Digester reset(byte[] input, int offset, int len) {
        return reset().update(input, offset, len);
    }

    public String getDigestValue() {
        return encoder.encodeToString(digest.digest());
    }
}
