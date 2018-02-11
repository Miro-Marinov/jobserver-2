package repository

import scala.concurrent.Future

/**
  * Created by mirob on 2/11/2018.
  */
class ReportRepository {
  def compute(data: String): Future[String] = Future {
    Thread.sleep(1000) // simulate long-running DB operation
    "result"
  }
}
