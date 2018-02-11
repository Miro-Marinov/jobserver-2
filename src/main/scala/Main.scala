import akka.http.scaladsl.Http
import controller.JobController.jobRoute

/**
  * Created by mirob on 2/9/2018.
  */
object Main extends App {

  val bindingFuture = Http().bindAndHandle(jobRoute, "localhost", 8080)
  println(s"Server online at http://localhost:8080")
}
