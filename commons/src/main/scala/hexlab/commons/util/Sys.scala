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
 * @author hex1r0
 */
object Sys {

  def nanos: Long = System.nanoTime
  def millis: Long = System.currentTimeMillis
  def seconds: Int = (System.currentTimeMillis / 1000).toInt

  def sizeof[T: Numeric](n: T) = n match {
    case _: Byte => 1
    case _: Short => 2
    case _: Int => 4
    case _: Long => 8
  }

}
