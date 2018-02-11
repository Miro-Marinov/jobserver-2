package domain

import akka.http.scaladsl.model.StatusCode
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/**
  * Created by mirob on 2/10/2018.
  */
object ResponseDomain {

  final case class Response(resCode: StatusCode, data: Option[String])

  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat2(Response)
}
