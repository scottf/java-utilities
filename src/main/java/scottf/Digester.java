package scottf;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Digester {
    String DEFAULT_DIGEST_ALGORITHM = "SHA-256";

    private final MessageDigest digest;
    private final Base64.Encoder encoder;

    public Digester() throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance(DEFAULT_DIGEST_ALGORITHM);
        encoder = Base64.getUrlEncoder();
    }

    public Digester(Base64.Encoder encoder) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance(DEFAULT_DIGEST_ALGORITHM);
        this.encoder = encoder;
    }

    public Digester(String algorithm) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance(algorithm);
        encoder = Base64.getUrlEncoder();
    }

    public Digester(String algorithm, Base64.Encoder encoder) throws NoSuchAlgorithmException {
        this.digest = MessageDigest.getInstance(algorithm);
        this.encoder = encoder;
    }

    public Digester update(String input) {
        digest.update(input.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public Digester update(long input) {
        return update(Long.toString(input));
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

    public Digester reset(long input) {
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
