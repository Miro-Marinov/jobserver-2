package config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.hazelcast.Scala.client.clientConf2scala
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by mirob on 2/11/2018.
  */
object Config {

  import com.hazelcast.client.config.ClientNetworkConfig

  private val cnc = new ClientNetworkConfig
  // Will attempt to reconnect indefinitely in case of a hazelcast server failure
  cnc.setConnectionAttemptLimit(0)
  private val clientConfig = new ClientConfig
  clientConfig.setNetworkConfig(cnc)
  val hzClient: HazelcastInstance = clientConfig.newClient()

  // needed to run the route
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val numDBConnections = 10
}
