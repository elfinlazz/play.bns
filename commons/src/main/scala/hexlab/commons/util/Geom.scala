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
object Geom {

  class Position2d[T: Numeric](var x: T, var y: T) extends Serializable {
    def xy = (x, y)

    def xy_=(xy: (T, T)) = {
      x = xy._1
      y = xy._2
    }
  }

  class Position3d[T: Numeric](_x: T, _y: T, var z: T) extends Position2d(_x, _y) {
    def xyz = (x, y, z)

    def xyz_=(xyz: (T, T, T)) = {
      xy = (xyz._1, xyz._2)
      z = xyz._3
    }
  }

}
