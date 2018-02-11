package actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, OneForOneStrategy, Props, ReceiveTimeout}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import config.Config._
import controller.JobController.ImperativeRequestContext
import domain.JobDomain._
import domain.ResponseDomain._
import service.JobCreatingActor

import scala.concurrent.duration._

/**
  * An actor created for the purpose of serving an HTTP request (one actor per request).
  */
class EntryActor(reqContext: ImperativeRequestContext) extends Actor {

  import context._

  setReceiveTimeout(5.seconds)

  def receive: PartialFunction[Any, Unit] = {
    case report: Report => system.actorOf(JobCreatingActor.props(hzClient)) ! report
    case Response(code, None) => complete(code)
    case Response(code, Some(data)) => complete(code -> data)
    case ReceiveTimeout => complete(StatusCodes.GatewayTimeout)
    case _ => complete(StatusCodes.BadRequest)
  }

  def complete[T <: AnyRef](m: => ToResponseMarshallable): Unit = {
    reqContext.complete(m)
    stop(self)
  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy() {
      case _ =>
        complete(StatusCodes.InternalServerError)
        Stop
    }
}

object EntryActor {
  def props(reqContext: ImperativeRequestContext) = Props(new EntryActor(reqContext))
}
