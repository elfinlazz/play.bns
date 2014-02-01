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

package hexlab.bns.auth.config

import hexlab.commons.util.{ConfigProperty, Config}

/**
 * This class ...
 *
 * @author hex1r0
 */
@Config("main.conf")
class MainConfig {
  @ConfigProperty("authserver.network.client.host", "0.0.0.0")
  var CLIENT_HOST: String = _

  @ConfigProperty("authserver.network.client.port", "6600")
  var CLIENT_PORT: Int = _

  @ConfigProperty("authserver.network.lobby.host", "0.0.0.0")
  var LOBBY_HOST: String = _

  @ConfigProperty("authserver.network.lobby.port", "9001")
  var LOBBY_PORT: Int = _

  @ConfigProperty("authserver.database.driver", "com.mysql.jdbc.Driver")
  var DATABASE_DRIVER: String = _

  @ConfigProperty("authserver.database.url", "jdbc:mysq://127.0.0.1:3306/playbns?useUnicode=true&characterEncoding=UTF-8")
  var DATABASE_URL: String = _

  @ConfigProperty("authserver.database.user", "root")
  var DATABASE_USER: String = _

  @ConfigProperty("authserver.database.password", "1")
  var DATABASE_PWD: String = _
}
