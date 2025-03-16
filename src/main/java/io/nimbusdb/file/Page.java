package io.nimbusdb.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Page {
    public static final Charset CHARSET = StandardCharsets.US_ASCII;
    private final ByteBuffer buffer;

    public Page(final int size) {
        this.buffer = ByteBuffer.allocateDirect(size);
    }

    public Page(final byte[] array) {
        this.buffer = ByteBuffer.wrap(array);
    }

    public static int maxByteLength(final int strLength) {
        float maxBytesPerChar = CHARSET.newEncoder().maxBytesPerChar();

        return Integer.BYTES + (strLength * (int) maxBytesPerChar);
    }

    public void setInt(final int offset, final int value) {
        buffer.putInt(offset, value);
    }

    public int getInt(final int offset) {
        return buffer.getInt(offset);
    }

    public void setDouble(final int offset, final double value) {
        buffer.putDouble(offset, value);
    }

    public double getDouble(final int offset) {
        return buffer.getDouble(offset);
    }

    public void setLong(final int offset, final long value) {
        buffer.putLong(offset, value);
    }

    public long getLong(final int offset) {
        return buffer.getLong(offset);
    }

    public void setBytes(final int offset, final byte[] src) {
        buffer.position(offset);

        buffer.putInt(src.length);
        buffer.put(src);
    }

    public byte[] getBytes(final int offset) {
        buffer.position(offset);

        int length = buffer.getInt();
        byte[] dst = new byte[length];
        buffer.get(dst);

        return dst;
    }

    public String getString(final int offset) {
        return new String(getBytes(offset), CHARSET);
    }

    public void setString(final int offset, final String value) {
        setBytes(offset, value.getBytes(CHARSET));
    }

    ByteBuffer contents() {
        buffer.position(0);

        return this.buffer;
    }
}
