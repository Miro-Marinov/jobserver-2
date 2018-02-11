package controller

import actor.EntryActor
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import config.Config._
import domain.JobDomain._
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContextExecutor, Promise}

object JobController extends SprayJsonSupport with DefaultJsonProtocol {


  // an imperative wrapper for request context
  final class ImperativeRequestContext(ctx: RequestContext, promise: Promise[RouteResult]) {
    val ec: ExecutionContextExecutor = ctx.executionContext

    def complete(obj: ToResponseMarshallable): Unit = ctx.complete(obj).onComplete(promise.complete)(ec)

    def fail(error: Throwable): Unit = ctx.fail(error).onComplete(promise.complete)(ec)
  }

  // a custom directive
  def imperativelyComplete(inner: ImperativeRequestContext => Unit): Route = { ctx: RequestContext =>
    val p = Promise[RouteResult]()
    inner(new ImperativeRequestContext(ctx, p))
    p.future
  }

  val jobRoute: Route =
    post {
      path("data" / Segment) { reportName =>
        entity(as[String]) { reportData =>
          imperativelyComplete { ctx =>
            system.actorOf(EntryActor.props(ctx)) ! Report(reportName, reportData)
          }
        }
      }
    }
}