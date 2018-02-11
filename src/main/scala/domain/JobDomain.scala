package domain

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/**
  * Created by mirob on 2/10/2018.
  */
object JobDomain {
  final case class Report(name: String, data: String)

  final case class ComputeReport(jobId: String, report: Report)

  final case class ReportResult(result: String)

  implicit val reportFormat: RootJsonFormat[Report] = jsonFormat2(Report)
}




