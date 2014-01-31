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

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.{universe => ru}
import com.typesafe.config.{ConfigFactory => TSConfigFactory}
import scala.reflect.ClassTag

/**
 * This is set of classes for configuration support
 *
 * @author hex1r0
 */
case class Config(path: String) extends StaticAnnotation

case class ConfigProperty(path: String, default: String /* = ""*/) extends StaticAnnotation

object Config {
  def apply[T: ClassTag](m: ru.Mirror, clazz: Class[T]): Option[T] = {
    for (configAnnotation <- Reflection.findClassAnnotation[Config](m, clazz)) yield {
      val props = Reflection.findAnnotatedClassFields[ConfigProperty](m, clazz)
      if (props.isEmpty) return None

      val instance = clazz.newInstance()
      val instanceMirror = m.reflect(instance)

      val fields = props map {
        case (a, f) => (a, instanceMirror.reflectField(f))
      }

      val configPath = configAnnotation.path
      val config = TSConfigFactory.load(configPath)

      def setValue(f: ru.FieldMirror, value0: String) {
        implicit val value = value0
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
          case _ => throw new NotImplementedError(tn + " is not supported yet")
        })
      }

      fields.foreach {
        case (annot, prop) =>
          val propPath = annot.path
          if (config.hasPath(propPath)) setValue(prop, config.getString(propPath))
          else setValue(prop, annot.default)
      }

      instance
    }
  }
}
