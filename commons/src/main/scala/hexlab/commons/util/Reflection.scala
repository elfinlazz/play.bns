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

import scala.reflect.runtime.universe._
import java.util.jar.JarInputStream
import java.io.{File, IOException, FileInputStream}
import scala.collection.mutable.ArrayBuffer
import scala.Predef._
import scala.Some

/**
 * This class provides simplified look on Scala Reflection
 *
 * @author hex1r0
 */
object Reflection {

  // ---------------------------------

  // TODO need to rework these methods
  def parsePackage(parent: Class[_], packageName: String): Stream[Class[_]] = {
    val runtime: String = parent.getProtectionDomain.getCodeSource.getLocation.getFile
    if (runtime.endsWith(".jar"))
      parseJarFile2(runtime, packageName)
    else
      parseClassDir2(new File(runtime + packageName.replace('.', '/')), packageName).toStream
  }

  def parseJarFile2(jar: String, packageName: String): Stream[Class[_]] = {
    val packageName0 = packageName.replace('.', '/')
    var jarFile: JarInputStream = null
    try {
      jarFile = new JarInputStream(new FileInputStream(jar))
      val entries = new ArrayBuffer[Class[_]]
      var jarEntry = jarFile.getNextJarEntry
      while (jarEntry != null) {
        var className: String = jarEntry.getName
        if (className.endsWith(".class")) {
          className = className.take(className.length - ".class".length)
          if (className.startsWith(packageName0)) {
            loadClass2(className.replace('/', '.')).foreach(entries +=)
          }
        }
        jarEntry = jarFile.getNextJarEntry
      }
      entries.toStream
    } catch {
      case e: IOException => e.
        printStackTrace()
        Stream.empty
    } finally {
      try if (jarFile != null) jarFile.close()
      catch {
        case e: IOException => e.printStackTrace()
      }
    }
  }

  private def parseClassDir2(dir: File, packageName: String): ArrayBuffer[Class[_]] = {
    val entries = new ArrayBuffer[Class[_]]
    for (file <- dir.listFiles) {
      if (file.isDirectory)
        entries ++= parseClassDir2(file, packageName + "." + file.getName)
      else if (file.getName.endsWith(".class"))
        loadClass2(packageName + '.' + file.getName.take(file.getName.length - ".class".length)).foreach(entries +=)
    }
    entries
  }

  private def loadClass2(name: String): Option[Class[_]] = {
    try {
      Some(Class.forName(name))
    } catch {
      case e: ClassNotFoundException =>
        e.printStackTrace()
        None
    }
  }

  // ---------------------------------

  def findClassAnnotation[T: TypeTag](clazz: Class[_]): Option[T] = {
    val m = runtimeMirror(getClass.getClassLoader)
    findClassAnnotation[T](m, clazz)
  }

  def findClassAnnotation[T: TypeTag](m: Mirror, clazz: Class[_]): Option[T] = {
    val s = classSymbolOf(m, clazz)
    findAnnotation[T](m, s)
  }

  def findAnnotatedClassFields[T: TypeTag](clazz: Class[_]): List[(T, TermSymbol)] = {
    val m = runtimeMirror(getClass.getClassLoader)
    findAnnotatedClassFields[T](m, clazz)
  }

  def findAnnotatedClassFields[T: TypeTag](m: Mirror, clazz: Class[_]): List[(T, TermSymbol)] = {
    val typ = class2Type(m, clazz)
    val allFields = typ.declarations.filter(s => s.asTerm.isGetter).map(_.asTerm.accessed.asTerm)
    val annotated =
      allFields map {
        f => (findAnnotation[T](m, f), f)
      } filter {
        case (a, _) => a.isDefined
      } map {
        case (a, t) => (a.get, t)
      }

    annotated.toList
  }

  def findAnnotation[T: TypeTag](m: Mirror, s: Symbol): Option[T] = {
    val aType = typeOf[T]

    for (a <- s.annotations.find(a => a.tpe == aType)) yield {
      instantiateAnnotation[T](m, a)
    }
  }

  def instantiateAnnotation[T: TypeTag](m: Mirror, a: Annotation): T = {
    val args = a.scalaArgs

    val argValues = args collect {
      case l: Literal => l.productElement(0).asInstanceOf[Constant].value
      case s: Select => throw new NotImplementedError("default arguments are not supported yet")
    }

    val classSymbol = a.tpe.typeSymbol.asClass
    val classMirror = m.reflectClass(classSymbol)
    val ctorSymbol = a.tpe.declaration(nme.CONSTRUCTOR).asMethod
    val ctorMirror = classMirror.reflectConstructor(ctorSymbol)
    ctorMirror(argValues: _*).asInstanceOf[T]
  }

  def classSymbolOf(m: Mirror, clazz: Class[_]): ClassSymbol = class2Type(m, clazz).typeSymbol.asClass

  def class2Type(m: Mirror, clazz: Class[_]): Type = m.classSymbol(clazz).toType
}
