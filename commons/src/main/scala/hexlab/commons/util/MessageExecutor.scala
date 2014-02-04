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

import akka.actor.{ActorSystem, ActorRef, Actor}
import hexlab.commons.util.MessageExecutor._
import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture, TimeUnit}
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.collection.mutable.ArrayBuffer

/**
 * This class ...
 *
 * @author hex1r0
 */
object MessageExecutor {

  type MessageFunc[T] = (T) => Any
  type SuccessFunc[T] = (T) => Unit
  type FailureFunc = (Throwable) => Unit
  type TaskId = String
  type Task = () => Unit
  type TaskWithId = (TaskId) => Unit

  case class SubscribeFunc(clazz: Class[_], instance: MessageHandler, func: MessageFunc[_])
  case class SendRequest(target: ActorRef, arg: Any, onSuccess: SuccessFunc[Any], onFailure: FailureFunc)
  case class DoRequest(id: Int, message: Any)
  case class RequestSucceed(id: Int, result: AnyRef)
  case class RequestFailed(id: Int, e: Throwable)
  case class RunScheduledTask(task: Task)
  case class RunScheduledTaskWithId(taskId: TaskId, task: TaskWithId)
  case class NewExecutorTransactionStart(newExecutor: ActorRef)
  case class CommitNewExecutor(actor: ActorRef, newExecutor: ActorRef)
  case class NewExecutorTransactionEnd(newExecutor: ActorRef)

  trait MessageHandler {

    implicit val actorSystem: ActorSystem
    implicit val executor: ActorRef
    val scheduler: ScheduledExecutorService

    private[MessageExecutor] var _activeMessage: Any = _
    private val _tasks = new mutable.HashMap[TaskId, ScheduledFuture[_]]

    def activeMessage = _activeMessage

    protected final def bind[T: ClassTag](f: MessageFunc[T]) {
      executor ! SubscribeFunc(GenericsClass[T], this, f.asInstanceOf[MessageFunc[T]])
    }

    protected final def request[T](target: ActorRef, arg: AnyRef)
                                  (onSuccess: SuccessFunc[T], onFailure: FailureFunc = defaultOnFailure) {
      executor ! SendRequest(target, arg, onSuccess.asInstanceOf[SuccessFunc[Any]], onFailure)
    }

    def task(taskId: TaskId) = {
      _tasks.get(taskId)
    }

    def hasTask(taskId: TaskId) = {
      task(taskId).map(t => !t.isCancelled && !t.isDone).get
    }

    def schedule(taskId: TaskId, task: TaskWithId, timeout: Long) {
      val r = scheduler.schedule((taskId, task), timeout, TimeUnit.MILLISECONDS)
      _tasks += taskId -> r
    }

    def schedule(taskId: TaskId, task: TaskWithId, timeout: Long, period: Long) {
      val r = scheduler.scheduleAtFixedRate((taskId, task), timeout, period, TimeUnit.MILLISECONDS)
      if (hasTask(taskId)) {
        throw new IllegalStateException(taskId + " already exists")
      }

      _tasks += taskId -> r
    }

    def schedule(taskId: TaskId, task: Task, timeout: Long) {
      val r = scheduler.schedule(task, timeout, TimeUnit.MILLISECONDS)
      _tasks += taskId -> r
    }

    def schedule(taskId: TaskId, task: Task, timeout: Long, period: Long) {
      val r = scheduler.scheduleAtFixedRate(task, timeout, period, TimeUnit.MILLISECONDS)
      if (hasTask(taskId)) {
        throw new IllegalStateException(taskId + " already exists")
      }

      _tasks += taskId -> r
    }

    private implicit def task2Runnable(t: Task): Runnable = new Runnable {
      def run(): Unit = executor ! RunScheduledTask(t)
    }

    private implicit def taskWithId2Runnable(t: (TaskId, TaskWithId)): Runnable = new Runnable {
      def run(): Unit = executor ! RunScheduledTaskWithId(t._1, t._2)
    }
  }

  private[MessageExecutor] object UnhandledMessageHandler extends MessageHandler {
    val actorSystem = null
    val executor = ActorRef.noSender
    val scheduler: ScheduledExecutorService = null

    def unhandledMessage(m: AnyRef) {
      Log[UnhandledMessageHandler.type].warn("Unhandled message = " + m.getClass)
    }
  }

  private def defaultOnFailure(e: Throwable) {
    val st = e.getStackTrace
    e.setStackTrace(st.take(st.length - 11))
    Log[MessageHandler].error("", e)
  }
}

class MessageExecutor extends Actor {

  private val _handlers = new mutable.HashMap[Class[_], (MessageHandler, MessageFunc[_])]
  private val _unhandled = (UnhandledMessageHandler, UnhandledMessageHandler.unhandledMessage _)

  private val _requests = new mutable.HashMap[Int, (SuccessFunc[Any], FailureFunc)]
  private var _nextRequestId = 0

  override def receive: Actor.Receive = {
    case SendRequest(target, arg, onSuccess, onFailure) =>
      _nextRequestId += 1
      _requests += _nextRequestId ->(onSuccess, onFailure)

      target ! DoRequest(_nextRequestId, arg)

    case RequestSucceed(id, result) =>
      _requests.remove(id) foreach (_._1(result))

    case RequestFailed(id, e) =>
      _requests.remove(id) foreach (_._1(e))

    case SubscribeFunc(clazz, instance, func) =>
      _handlers += clazz ->(instance, func)

    case RunScheduledTask(task) =>
      try task()
      catch {
        case e: Throwable => defaultOnFailure(e)
      }

    case RunScheduledTaskWithId(taskId, task) =>
      try task(taskId)
      catch {
        case e: Throwable => defaultOnFailure(e)
      }

    case DoRequest(id, m) =>
      val (handler, func) = _handlers.getOrElse(m.getClass, _unhandled)
      //handler.executorActor = this
      handler._activeMessage = m
      try {
        func.asInstanceOf[MessageFunc[Any]](m) match {
          case v: AnyRef => sender ! RequestSucceed(id, v)
          case _ =>
        }
      } catch {
        case e: Throwable => sender ! RequestFailed(id, e)
      }

    case CommitNewExecutor(actor, newExecutor) =>
      actor ! NewExecutorTransactionEnd(newExecutor)

    // this case should be the last
    case m: AnyRef =>
      val (handler, func) = _handlers.getOrElse(m.getClass, _unhandled)
      //handler.executorActor = this
      handler._activeMessage = m
      try func.asInstanceOf[MessageFunc[AnyRef]](m)
      catch {
        case e: Throwable => defaultOnFailure(e)
      }
  }
}

trait Executable[T] {
  private var _enquenedMessages = new ArrayBuffer[T]
  protected var _executor: ActorRef = _
  protected var _submit: (T) => Unit = send

  def transactionHandler: Actor.Receive = {
    case NewExecutorTransactionStart(newExecutor) =>
      if (_executor == null) {
        _executor = newExecutor
      } else {
        _submit = enqueue
        _executor ! CommitNewExecutor(selfRef, newExecutor)
      }

    case NewExecutorTransactionEnd(newExecutor) =>
      _executor = newExecutor
      _submit = send
      _enquenedMessages foreach _submit
      _enquenedMessages.clear()
  }

  private def send(m: T) {
    _executor ! m
  }

  private def enqueue(m: T) {
    _enquenedMessages += m
  }

  def selfRef: ActorRef
}

