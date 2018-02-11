package service

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.http.scaladsl.model.StatusCodes
import com.hazelcast.core.{HazelcastException, HazelcastInstance}
import config.Config._
import domain.JobDomain.{ComputeReport, Report}
import domain.ResponseDomain.Response

import scala.concurrent.duration._

/**
  * Created by mirob on 2/11/2018.
  */
class JobCreatingActor(hzClient: HazelcastInstance) extends Actor {

  override def receive: Receive = {
    case report: Report =>
      val jobId = hash(report)
      val jobsCache = hzClient.getMap[String, String]("jobs")
      val reportsCache = hzClient.getMap[String, String]("reports")
      val response = Option(reportsCache.get(jobId)).map(cachedReport => Response(StatusCodes.OK, Some(cachedReport))).getOrElse {
        Option(jobsCache.putIfAbsent(jobId, jobId)).map(_ => Response(StatusCodes.Accepted, None)).getOrElse {
          system.actorOf(ReportComputingActor.props(hzClient)) ! ComputeReport(jobId, report)
          Response(StatusCodes.Accepted, None)
        }
      }
      sender ! response
  }

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(
    maxNrOfRetries = 3,
    withinTimeRange = 1.minute) {
    case HazelcastException => Restart
  }

  def hash(report: Report): String = report.name
}

object JobCreatingActor {
  def props(hzClient: HazelcastInstance) = Props(new JobCreatingActor(hzClient))
}
