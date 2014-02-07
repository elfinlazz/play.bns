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

import java.io.InputStream
import javax.xml.bind.{JAXBElement, JAXBContext}
import scala.collection.mutable
import scala.language.dynamics
import scala.reflect._
import scala.xml.XML

/**
 * This class ...
 *
 * @author hex1r0
 */
object XmlUtil {
  def fromJaxb[T: ClassTag](inputStream: InputStream): T = {
    val packageName = classTag[T].runtimeClass.getPackage.getName
    val jc = JAXBContext.newInstance(packageName)
    val u = jc.createUnmarshaller()
    val doc = u.unmarshal(inputStream).asInstanceOf[JAXBElement[T]]
    doc.getValue
  }

  def fromScala(inputStream: InputStream): XmlDynamic = {
    val res = new XmlDynamic
    val xml = XML.load(inputStream)

    def parseNode(parent: XmlDynamic, n: scala.xml.Node): XmlDynamic = {
      n.attributes.foreach(a => parent.updateDynamic(a.key)(a.value.toString()))

      if (n.child.length > 0) {
        val name = n.label
        (parent updateDynamic name)(n.child map (n0 => parseNode(new XmlDynamic, n0)) filterNot (_.isEmpty))
      }
      parent
    }

    parseNode(res, xml)
  }
}

class XmlDynamic extends Dynamic {
  private val _values = new mutable.HashMap[String, AnyRef]

  def selectDynamic[T: ClassTag](name: String): T = {
    val v = _values get name getOrElse null

    classTag[T].runtimeClass match {
      case x if x == classOf[Byte] => v.toString.toByte.asInstanceOf[T]
      case x if x == classOf[Short] => v.toString.toShort.asInstanceOf[T]
      case x if x == classOf[Int] => v.toString.toInt.asInstanceOf[T]
      case x if x == classOf[Long] => v.toString.toLong.asInstanceOf[T]
      case x if x == classOf[Float] => v.toString.toFloat.asInstanceOf[T]
      case x if x == classOf[Double] => v.toString.toDouble.asInstanceOf[T]
      case _ => v.asInstanceOf[T]
    }
  }

  def updateDynamic(name: String)(value: AnyRef) {
    _values += name -> value
  }

  def isEmpty = _values.isEmpty

  override def toString: String = {
    "XmlDynamic(" + _values.mkString(", ") + ")"
  }
}