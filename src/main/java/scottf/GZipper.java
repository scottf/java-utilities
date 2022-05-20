package scottf;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipper {
    public static final int DEFAULT_READ_BLOCK_SIZE = 1024;
    private final ByteArrayBuilder bab;
    private final OutputStream out;
    private GZIPOutputStream zipOut;

    public GZipper() throws IOException {
        bab = new ByteArrayBuilder();
        out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bab.append(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                bab.append(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                bab.append(b, off, len);
            }
        };
        clear();
    }

    public GZipper clear() throws IOException {
        bab.clear();
        zipOut = new GZIPOutputStream(out);
        return this;
    }

    public byte[] finish() throws IOException {
        zipOut.flush();
        zipOut.finish();
        return bab.toByteArray();
    }

    public GZipper zip(byte[] bytes) throws IOException {
        return zip(bytes, 0, bytes.length);
    }

    public GZipper zip(byte[] bytes, int off, int len) throws IOException {
        zipOut.write(bytes, off, len);
        return this;
    }

    public static byte[] unzip(byte[] bytes) throws IOException {
        return unzip(bytes, DEFAULT_READ_BLOCK_SIZE, bytes.length * 2);
    }

    public static byte[] unzip(byte[] bytes, int projectedUnzippedSize) throws IOException {
        return unzip(bytes, DEFAULT_READ_BLOCK_SIZE, projectedUnzippedSize);
    }

    public static byte[] unzip(byte[] bytes, int readBlockSize, int projectedUnzippedSize) throws IOException {
        ByteArrayBuilder bab = new ByteArrayBuilder(projectedUnzippedSize);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bytes));
        byte[] buffer = new byte[readBlockSize];
        int red = in.read(buffer, 0, readBlockSize);
        while (red > 0) {
            // track what we got from the unzip
            bab.append(buffer, 0, red);

            // read more if we got a full read last time, otherwise, that's the last of the bytes
            red = red == readBlockSize ? in.read(buffer, 0, readBlockSize) : -1;
        }
        return bab.toByteArray();
    }

    public static void unzip(byte[] bytes, OutputStream out) throws IOException {
        unzip(bytes, DEFAULT_READ_BLOCK_SIZE, out);
    }

    public static void unzip(byte[] bytes, int readBlockSize, OutputStream out) throws IOException {
        unzip(new ByteArrayInputStream(bytes), out, readBlockSize);
    }

    public static void unzip(InputStream inputStream, OutputStream output) throws IOException {
        unzip(inputStream, output, DEFAULT_READ_BLOCK_SIZE);
    }

    public static void unzip(InputStream inputStream, OutputStream output, int readBlockSize) throws IOException {
        GZIPInputStream in = new GZIPInputStream(inputStream);
        byte[] buffer = new byte[readBlockSize];
        int red = in.read(buffer, 0, readBlockSize);
        while (red > 0) {
            // track what we got from the unzip
            output.write(buffer, 0, red);

            // read more if we got a full read last time, otherwise, that's the last of the bytes
            red = red == readBlockSize ? in.read(buffer, 0, readBlockSize) : -1;
        }
    }

    public static byte[] zip(File f) throws IOException {
        byte[] buffer = new byte[DEFAULT_READ_BLOCK_SIZE];

        GZipper gz = new GZipper();
        try (FileInputStream in = new FileInputStream(f)) {
            // read the first chunk
            int red = in.read(buffer);
            while (red > 0) {
                gz.zip(buffer, 0, red);
                // read more if we got a full read last time, otherwise, that's the last of the bytes
                red = red == DEFAULT_READ_BLOCK_SIZE ? in.read(buffer) : -1;
            }
        }
        return gz.finish();
    }
}
