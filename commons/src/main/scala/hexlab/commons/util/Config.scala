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

import hexlab.config.{ConfigFactory => CF, ConfigRenderOptions => CRO, ConfigValueFactory => CVF}
import java.io.{FileWriter, File}
import java.net.InetAddress
import java.util
import scala.annotation.StaticAnnotation
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
 * This is set of classes for configuration support
 *
 * @author hex1r0
 */
case class Config(path: String) extends StaticAnnotation

// TODO implement multiline comments
// TODO implement default 'default'
// TODO implement default comment
case class ConfigProperty(path: String, default: Any /* = ""*/ , comment: String /* = List[String]*/) extends StaticAnnotation

object Config {
  private val _log = Log[Config.type]

  def load[T: ClassTag](root: String, m: Mirror, clazz: Class[T]): Option[T] = {
    for (configAnnotation <- Reflection.findClassAnnotation[Config](m, clazz)) yield {
      val props = Reflection.findAnnotatedClassFields[ConfigProperty](m, clazz)
      if (props.isEmpty) return None

      val instance = clazz.newInstance()
      val instanceMirror = m.reflect(instance)

      val fields = props map {
        case (a, f) => (a, instanceMirror.reflectField(f))
      }

      val configPath = root + "/" + configAnnotation.path
      val configFile = new File(configPath)
      if (configFile.isFile && configFile.exists()) {
        _log.info(s"Loading config `$configPath`")

        val config = CF.parseFile(configFile)
        fields.foreach {
          case (annot, prop) =>
            val propPath = annot.path
            if (config.hasPath(propPath)) bindValue(prop, config.getString(propPath))
            else bindValue(prop, annot.default)
        }
      } else {
        _log.info(s"Loading unexisting config `$configPath` failed. Using default values.")
        fields.foreach {
          case (annot, prop) =>
            bindValue(prop, annot.default)
        }
      }
      instance
    }
  }

  def create(root: String, m: Mirror, clazz: Class[_]) {
    for (configAnnotation <- Reflection.findClassAnnotation[Config](m, clazz)) yield {
      val props = Reflection.findAnnotatedClassFields[ConfigProperty](m, clazz)
      if (props.isEmpty) return


      var conf = CF.empty()
      for (p <- props.map(_._1)) {
        conf = conf.withValue(p.path, CVF.fromAnyRefWithComments(p.default, util.Arrays.asList(p.comment)))
      }

      val renderOpts = CRO.defaults()
        .setOriginComments(false)
        .setComments(true)
        .setJson(false)

      val res = conf.root().render(renderOpts)

      val configPath = root + "/" + configAnnotation.path
      val configFile = new File(configPath)
      if (configFile.isFile && configFile.exists()) {
        _log.info(s"Skipping existing config `$configPath`")
        // TODO
      } else {
        configFile.createNewFile()
        new FileWriter(configFile).append(res).flush()
        _log.info(s"Created config `$configPath`")
      }
    }
  }

  private def bindValue(f: FieldMirror, value0: Any) {
    implicit val value = value0.toString
    val tn = f.symbol.typeSignature.toString

    def numeric[R](f: (String) => R)(implicit d: String): R = if (d == "") f("0") else f(d)

    // FIXME temp solution
    f.set(tn match {
      case "Boolean" => numeric(_.toBoolean)(value match {
        case "0" => "false"
        case "1" => "true"
        case x => x.toString
      })
      case "Byte" => numeric(_.toByte)
      case "Short" => numeric(_.toShort)
      case "Int" => numeric(_.toInt)
      case "Long" => numeric(_.toLong)
      case "Float" => numeric(_.toFloat)
      case "Double" => numeric(_.toDouble)
      case "String" => value
      case "java.net.InetAddress" => InetAddress.getByName(value)
      case _ => throw new NotImplementedError(tn + " is not supported yet")
    })
  }
}
