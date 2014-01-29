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

import java.lang.{Math => jMath}

/**
 * This class ...
 *
 * @author hex1r0
 */
object Math {

  def min(n1: Int, n2: Int, n3: Int): Int = jMath.min(n1, jMath.min(n2, n3))
  def min(n1: Long, n2: Long, n3: Long): Long = jMath.min(n1, jMath.min(n2, n3))
  def min(n1: Float, n2: Float, n3: Float): Float = jMath.min(n1, jMath.min(n2, n3))
  def min(n1: Double, n2: Double, n3: Double): Double = jMath.min(n1, jMath.min(n2, n3))
  def max(n1: Int, n2: Int, n3: Int): Int = jMath.max(n1, jMath.max(n2, n3))
  def max(n1: Long, n2: Long, n3: Long): Long = jMath.max(n1, jMath.max(n2, n3))
  def max(n1: Float, n2: Float, n3: Float): Float = jMath.max(n1, jMath.max(n2, n3))
  def max(n1: Double, n2: Double, n3: Double): Double = jMath.max(n1, jMath.max(n2, n3))
  def limit(min: Int, value: Long, max: Int): Int = jMath.max(min, jMath.min(value, max)).asInstanceOf[Int]
  def limit(min: Int, value: Int, max: Int): Int = jMath.max(min, jMath.min(value, max))
  def limit(min: Int, value: Double, max: Int): Int = jMath.max(min, jMath.min(value, max)).asInstanceOf[Int]
  def limit(min: Int, value: Float, max: Int): Int = jMath.max(min, jMath.min(value, max)).asInstanceOf[Int]
  def limit(min: Long, value: Long, max: Long): Long = jMath.max(min, jMath.min(value, max))
  def limit(min: Long, value: Double, max: Long): Long = jMath.max(min, jMath.min(value, max)).asInstanceOf[Long]
  def limit(min: Long, value: Float, max: Long): Long = jMath.max(min, jMath.min(value, max)).asInstanceOf[Long]
  def limit(min: Float, value: Double, max: Float): Float = jMath.max(min, jMath.min(value, max)).asInstanceOf[Float]
  def limit(min: Float, value: Float, max: Float): Float = jMath.max(min, jMath.min(value, max))
  def limit(min: Double, value: Double, max: Double): Double = jMath.max(min, jMath.min(value, max))

  /**
   * @param base the base
   * @param exponent the <b>NON-NEGATIVE INTEGER</b> exponent
   *
   * @return <code>base<sup>exponent</sup></code>
   *
   * @throws IllegalArgumentException if the exponent is negative
   */
  def pow(base: Int, exponent: Int): Long = {
    if (exponent < 0) {
      throw new IllegalArgumentException("Exponent must be non-negative!")
    }

    var result: Long = 1
    var e = exponent
    while (e > 0) {
      result *= base
      e -= 1
    }

    result
  }
  /**
   * @param base the base
   * @param exponent the <b>NON-NEGATIVE INTEGER</b> exponent
   *
   * @return <code>base<sup>exponent</sup></code>
   *
   * @throws IllegalArgumentException if the exponent is negative
   */
  def pow(base: Double, exponent: Int): Double = {
    if (exponent < 0) {
      throw new IllegalArgumentException("Exponent must be non-negative!")
    }

    var result: Double = 1
    var e = exponent
    while (e > 0) {
      result *= base
      e -= 1
    }

    result
  }
}
