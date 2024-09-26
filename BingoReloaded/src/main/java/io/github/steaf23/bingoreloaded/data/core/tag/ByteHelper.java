package io.github.steaf23.bingoreloaded.data.core.tag;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ByteHelper
{
    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
    }

    public static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    public static byte[] longToBytes(long value) {
        return new byte[]{
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
    }

    public static long bytesToLong(byte[] bytes) {
        return ((long)(bytes[0] & 0xFF) << 56) |
                ((long)(bytes[1] & 0xFF) << 48) |
                ((long)(bytes[2] & 0xFF) << 40) |
                ((long)(bytes[3] & 0xFF) << 32) |
                ((long)(bytes[4] & 0xFF) << 24) |
                ((long)(bytes[5] & 0xFF) << 16) |
                ((long)(bytes[6] & 0xFF) << 8) |
                ((long)(bytes[7] & 0xFF));
    }

    public static void writeInt(int value, ByteArrayOutputStream stream) {
        stream.writeBytes(ByteHelper.intToBytes(value));
    }

    public static int readInt(ByteArrayInputStream stream) {
        byte[] bytes = new byte[4];
        stream.readNBytes(bytes, 0, 4);
        return bytesToInt(bytes);
    }

    public static void writeLong(long value, ByteArrayOutputStream stream) {
        stream.writeBytes(ByteHelper.longToBytes(value));
    }

    public static long readLong(ByteArrayInputStream stream) {
        byte[] bytes = new byte[8];
        stream.readNBytes(bytes, 0, 8);
        return bytesToLong(bytes);
    }

    public static void writeString(@NotNull String value, ByteArrayOutputStream stream) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeShort((short)bytes.length, stream);
        stream.writeBytes(bytes);
    }

    public static String readString(ByteArrayInputStream stream) {
        short size = readShort(stream);
        byte[] bytes = new byte[size];
        stream.readNBytes(bytes, 0, size);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeShort(short value, ByteArrayOutputStream stream) {
        stream.write(value >> 8 & 0xFF);
        stream.write(value & 0xFF);
    }

    public static short readShort(ByteArrayInputStream stream) {
        return (short) (stream.read() << 8 | stream.read());
    }

    public static void writeFloat(float value, ByteArrayOutputStream stream) {
        ByteHelper.writeInt(Float.floatToIntBits(value), stream);
    }

    public static float readFloat(ByteArrayInputStream stream) {
        return Float.intBitsToFloat(ByteHelper.readInt(stream));
    }

    public static void writeDouble(double value, ByteArrayOutputStream stream) {
        ByteHelper.writeLong(Double.doubleToLongBits(value), stream);
    }

    public static double readDouble(ByteArrayInputStream stream) {
        return Double.longBitsToDouble(ByteHelper.readLong(stream));
    }
}
