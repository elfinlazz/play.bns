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

package hexlab.commons.crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util

/**
 * This class implements AES Cipher
 *
 * @author hex1r0
 */
class AESCipher(key: Array[Byte]) {
  private val _enc = init(Cipher.ENCRYPT_MODE)
  private val _dec = init(Cipher.DECRYPT_MODE)

  def encode(data: Array[Byte]) = {
    var input = data
    if (data.length % 16 != 0) {
      val paddedLength = data.length + 16 - (data.length % 16)
      input = util.Arrays.copyOf(data, paddedLength)
    }

    _enc.doFinal(input)
  }

  def decode(data: Array[Byte]) = _dec.doFinal(data)

  private def init(mode: Int) = {
    val keySpec = new SecretKeySpec(key, "AES")
    val cipher = Cipher.getInstance("AES/ECB/NoPadding")
    cipher.init(mode, keySpec)
    cipher
  }
}

