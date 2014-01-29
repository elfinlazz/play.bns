/*
 * This file is part of PlayBnS
 *                      <https://github.com/HeXLaB/play.bns>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013-2014
 *               HeXLaB Team
 *                           All rights reserved
 */

package hexlab.commons.util;

import java.lang.Math;
import java.nio.ByteBuffer;

/**
 * This class provides functions to manipulate byte arrays in the programmer-friendly hexadecimal
 * octet format.
 *
 * @author l2jfree
 */
public final class HexUtil {
    private HexUtil() {
        // utility class
    }

    /**
     * Decodes a string of hex octets to a byte array.
     *
     * @param hex
     *         a byte array
     * @return decoded byte array
     */
    public static byte[] fromHex(String hex) {
        if (hex == null) {
            return null;
        }
        hex = hex.replace(" ", "");
        if (hex.length() % 2 == 1) {
            return null;
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            String byte_ = hex.substring(i * 2, (i + 1) * 2);
            bytes[i] = (byte) (Integer.parseInt(byte_, 16) & 0xFF);
        }
        return bytes;
    }

    public static String printData(ByteBuffer buf, int offset, int len) {
        byte[] tmp = new byte[len];
        int pos = buf.position();
        buf.position(offset);
        buf.get(tmp);
        buf.position(pos);
        return printData(tmp, len);
    }

    /**
     * This method is equivalent to <TT>printData(raw, raw.length)</TT>.
     *
     * @param raw
     *         a byte array
     * @return converted byte array
     * @see #printData(byte[], int)
     */
    public static String printData(byte[] raw) {
        return printData(raw, raw.length);
    }

    /**
     * Converts a byte array to string in a special form. <BR>
     * <BR>
     * On the left side of the generated string, each byte is printed as a hex octet with a trailing
     * space.<BR>
     * On the right side of the generated string, each byte is printed as an ASCII char, unless it's
     * a non-printing character (the same is done to extended ASCII characters): then a period is
     * printed instead.
     *
     * @param data
     *         a byte array
     * @param len
     *         number of bytes to print
     * @return converted byte array
     * @see #printData(byte[], int)
     */
    public static String printData(byte[] data, int len) {
        String eol = System.getProperty("line.separator", "\r\n");
        final StringBuilder result = new StringBuilder(eol);

        int counter = 0;

        for (int i = 0; i < len; i++) {
            if (counter % 16 == 0) {
                result.append(fillHex(i, 4));
                result.append(": ");
            }

            result.append(fillHex(data[i] & 0xff, 2));
            result.append(' ');
            counter++;
            if (counter == 16) {
                result.append("   ");

                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    int t1 = data[charpoint++];
                    if (t1 > 0x1f && t1 < 0x80) {
                        result.append((char) t1);
                    } else {
                        result.append('.');
                    }
                }

                result.append(eol);
                counter = 0;
            }
        }

        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < 17 - rest; i++) {
                result.append("   ");
            }

            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                int t1 = data[charpoint++];
                if (t1 > 0x1f && t1 < 0x80) {
                    result.append((char) t1);
                } else {
                    result.append('.');
                }
            }

            result.append(eol);
        }

        return result.toString();
    }

    /**
     * Converts a number to hexadecimal format and adds leading zeros if necessary.
     *
     * @param data
     *         a number
     * @param digits
     *         minimum hexadecimal digit count
     * @return given number in hexadecimal format
     */
    public static String fillHex(int data, int digits) {
        String hex = Integer.toHexString(data);

        StringBuilder number = new StringBuilder(Math.max(hex.length(), digits));
        for (int i = hex.length(); i < digits; i++) {
            number.append(0);
        }
        number.append(hex);

        return number.toString();
    }

    public static byte[] reverseChunk(byte[] in, int chunkSize) {
        if (in.length % chunkSize != 0) {
            byte[] tmp = new byte[in.length - 1];
            System.arraycopy(in, 1, tmp, 0, tmp.length);
            in = tmp;
        }


        int size = in.length;
        byte[] out = new byte[size];

        for (int i = 0, k = size - chunkSize; i < size; k -= chunkSize) {
            for (int j = 0; j < chunkSize; j++) {
                out[i++] = in[k + j];
            }
        }

        return out;
    }

    public static byte[] doubleReverseChunk(byte[] in) {
        byte[] tmp = reverseChunk(in, 4);
        return reverseChunk(tmp, 1);
    }

    public static String toHexString(byte[] data) {
        return toHexString(data, 0, data.length);
    }

    public static String toHexString(byte[] data, int offset, int len) {
        final StringBuilder result = new StringBuilder();

        for (int i = offset; i < len; i++) {
            result.append(fillHex(data[i] & 0xff, 2));
        }

        return result.toString();
    }
}
