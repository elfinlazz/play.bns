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

package hexlab.commons.util

/**
 * This class ...
 *
 * @author hex1r0
 */
trait BinaryReader {
  protected def readByte: Byte

  protected def readShort: Short

  protected def readInt: Int

  protected def readLong: Long

  protected def readFloat: Float

  protected def readDouble: Double

  protected def readBytes(count: Int): Array[Byte]

  protected def readBytes(out: Array[Byte])

  protected def skip(count: Int)


  protected def int8 = readByte

  protected def int16 = readShort

  protected def int32 = readInt

  protected def int64 = readLong

  protected def float32 = readFloat

  protected def float64 = readDouble


  protected def readC = readByte

  protected def readH = readShort

  protected def readD = readInt

  protected def readQ = readLong

  protected def readF = readFloat

  protected def readDF = readDouble

  protected def read(count: Int) = readBytes(count)
}
