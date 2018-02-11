package service

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, OneForOneStrategy, Props}
import akka.routing.RoundRobinPool
import com.hazelcast.core.HazelcastInstance
import config.Config.numDBConnections
import domain.JobDomain.{ComputeReport, Report}
import repository.ReportRepository

import scala.concurrent.duration._


/**
  * Created by mirob on 2/11/2018.
  */
class ReportComputingActor(hzClient: HazelcastInstance) extends Actor {
  override def receive: Receive = {
    case ComputeReport(jobId, Report(name, data)) =>
      val reportsCache = hzClient.getMap[String, String]("reports")

      // Fetch the appropriate dao by name - e.g. via reflection
      // Can use distributed executor service here if needed
      (new ReportRepository).compute(data).onSuccess { case result =>
        reportsCache.putAsync(jobId, result)
      }
  }
}

object ReportComputingActor {
  def props(hzClient: HazelcastInstance) = Props(new ReportComputingActor(hzClient: HazelcastInstance))

  // If we decide to have blocking operations (since JDBC drivers are synchronous) in the actor - use a pool of actors matching the # of DB connections
  def propsPooled(hzClient: HazelcastInstance): Props = {
    val props = JobCreatingActor.props(hzClient)
    val pool = RoundRobinPool(numDBConnections, supervisorStrategy = restartStrategy)
    pool.props(props)
  }

  // Probably should be handled at the DAO level by the framework we are using (e.g.: Slick?)
  // Use some kind of circuit breaking
  val restartStrategy: OneForOneStrategy = OneForOneStrategy(
    maxNrOfRetries = 3,
    withinTimeRange = 1.minute) {
    // DB specific exception instead of _
    case _ => Restart
  }
}
