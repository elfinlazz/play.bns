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

import java.lang.reflect.Method
import hexlab.commons.util.Enum.EnumValue
import scala.collection.mutable
import scala.reflect.runtime.universe

/**
 * This is alternative, more flexible and powerful implementation of Enums
 *
 * @author hex1r0
 */
object Enum {

  trait EnumValue extends Serializable {
    private[Enum] var _parentClassName: String = _
    private[Enum] var _name: String = _
    @transient private var _fullName: String = _
    @transient private[Enum] var _parent: Enum[_] = _
    @transient private[Enum] var _ordinal: Int = 0

    def ordinal = {
      tryInit()
      _ordinal
    }

    override def toString = {
      tryInit()
      _name
    }

    override def equals(obj: scala.Any): Boolean = obj match {
      case e: EnumValue =>
        e.tryInit() // required due to deserialization
        tryInit()
        e._parent == _parent && e._name == _name

      case _ => false
    }

    override def hashCode() = {
      tryInit()
      _fullName.hashCode()
    }

    private def tryInit() {
      if (_parent == null) {
        val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
        val module = runtimeMirror.staticModule(_parentClassName)
        val enum = runtimeMirror.reflectModule(module).instance.asInstanceOf[Enum[_]]
        val real = enum.valueOf(_name).get.asInstanceOf[EnumValue]

        _parent = real._parent
        _name = real._name
        _ordinal = real.ordinal
        _fullName = _parentClassName + _name
      }
    }
  }

}

trait Enum[V <: EnumValue] extends Iterable[V] with DelayedInit {
  protected implicit val _implicitThisRef = this
  private var _values: Seq[V] = null
  private var _nextOrdinal: Int = 0

  def values = _values

  def valueOf(name: String): Option[V] = values find (_._name == name)

  def iterator: Iterator[V] = values.iterator

  protected def Value[EV <: EnumValue](v: EV)(implicit e: Enum[_]): EV = {
    v._parent = e
    v._parentClassName = e.getClass.getName
    v._ordinal = _nextOrdinal
    _nextOrdinal += 1
    v
  }

  def delayedInit(ctor: => Unit): Unit = {
    ctor
    buildValues()
  }

  private def buildValues() {
    def isValue(m: Method) = getClass.getDeclaredFields exists {
      fd =>
        fd.getName == m.getName && fd.getType == m.getReturnType
    }

    val methods = getClass.getMethods filter (
      m => {
        m.getParameterTypes.isEmpty &&
          classOf[EnumValue].isAssignableFrom(m.getReturnType) &&
          m.getDeclaringClass != classOf[Enum[_]] &&
          isValue(m)
      })

    val values = new mutable.ArraySeq[AnyRef](methods.length)
    for (m <- methods) {
      val v = m.invoke(this).asInstanceOf[EnumValue]
      v._name = m.getName
      values.update(v._ordinal, v)
    }
    _values = values.map(x => x.asInstanceOf[V]).toSeq
  }
}