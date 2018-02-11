package config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.hazelcast.Scala.client.clientConf2scala
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.config.{Config, EvictionPolicy}
import com.hazelcast.core.HazelcastInstance

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
/**
  * Created by mirob on 2/11/2018.
  */
object Config {

  import com.hazelcast.client.config.ClientNetworkConfig

  private val cnc = new ClientNetworkConfig
  // Will attempt to reconnect indefinitely in case of a hazelcast server failure
  cnc.setConnectionAttemptLimit(0)
  private val clientConfig = new ClientConfig
  private val config = new Config
  implicit def durToSeconds(duration: Duration): Int = duration.toSeconds.toInt
  config.getMapConfig("jobs").setTimeToLiveSeconds(40.seconds)
  config.getMapConfig("reports").setTimeToLiveSeconds(7.days)

  clientConfig.setNetworkConfig(cnc)
  val hzClient: HazelcastInstance = clientConfig.newClient()

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val numDBConnections = 10
}
