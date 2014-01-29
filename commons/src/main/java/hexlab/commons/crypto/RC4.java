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

package hexlab.commons.crypto;

public class RC4 {
    /**
     * Contents of the current set S-box.
     */
    private final int[] sBox = new int[256];

    /**
     * The two indices for the S-box computation referred to as i and j
     * in Schneier.
     */
    private int x, y;

    public RC4(byte[] key) {
        makeKey(key);
    }

    /**
     * RC4 encryption/decryption.
     *
     * @param in
     *     the input data.
     * @param length
     *     length to process
     *
     * @return the output data.
     */
    public byte[] process(byte[] in, int length) {
        int xorIndex, t;
        byte[] out = new byte[length];
        for (int i = 0; i < length; i++) {
            x = x + 1 & 0xFF;
            y = sBox[x] + y & 0xFF;
            t = sBox[x];
            sBox[x] = sBox[y];
            sBox[y] = t;
            xorIndex = sBox[x] + sBox[y] & 0xFF;
            out[i] = (byte) (in[i] ^ sBox[xorIndex]);
        }
        return out;
    }

    /**
     * RC4 encryption/decryption.
     *
     * @param in
     *     the input data.
     *
     * @return the output data.
     */
    public byte[] process(byte[] in) {
        return process(in, in.length);
    }

    /**
     * Expands a user-key to a working key schedule.
     * <p/>
     * The key bytes are first extracted from the user-key and then used to build the contents of this key schedule.
     *
     * @param key
     *     the user-key object to use.
     */
    private void makeKey(byte[] key) {
        x = y = 0;
        for (int i = 0; i < 256; i++) {
            sBox[i] = i;
        }
        int i1 = 0, i2 = 0, t;
        for (int i = 0; i < 256; i++) {
            i2 = (key[i1] & 0xFF) + sBox[i] + i2 & 0xFF;
            t = sBox[i];
            sBox[i] = sBox[i2];
            sBox[i2] = t;
            i1 = (i1 + 1) % key.length;
        }
    }
}
