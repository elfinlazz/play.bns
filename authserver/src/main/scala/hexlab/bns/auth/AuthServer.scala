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

package hexlab.bns.auth

import hexlab.bns.auth.config.ConfigMarker
import hexlab.commons.util.{ConfigFactory, Log}

/**
 * This class is an AuthServer entry point
 *
 * @author hex1r0
 */
object AuthServer {
  private val _log = Log[AuthServer.type]

  def main(args: Array[String]) {
    val installConf = args exists (_ == "--install-config")
    val installDb = args exists (_ == "--install-db")

    if (installConf) {
      _log.info("Installing configs")
      ConfigFactory.createAllFrom("config", AuthServer.getClass, classOf[ConfigMarker].getPackage.getName)
      _log.info("Installing configs finished")
      sys.exit(0)
    }

    if (installDb) {
      _log.info("Installing database")
      // TODO
      _log.info("Installing database finished")
      sys.exit(0)
    }

    val configs = ConfigFactory.loadAllFrom("config", AuthServer.getClass, classOf[ConfigMarker].getPackage.getName)
    // TODO load database
    // TODO create actor system
    // TODO broadcast configs
    // TODO load modules
    // TODO start netork
  }
}
