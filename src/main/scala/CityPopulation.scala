import com.twitter.conversions.string
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.circe.generic.auto._
import io.finch._
import shapeless._
import io.finch.circe._
import io.finch.syntax._

object CityPopulation extends App {

  private val inhabitant = get("inhabitant" :: path[String]) {
    (id: String) =>
      Ok(id)
  }

  private val city: Endpoint[String] = get("city") {
    Ok("100")
  }

  private val adults: Endpoint[String] = get("city" :: "adults") {
    Ok("10")
  }

  private val partners: Endpoint[String] = get("city" :: "partners") {
    Ok("10")
  }

  val api: Service[Request, Response] =
    (inhabitant :+: city :+: adults :+: partners)
      .toServiceAs[Text.Plain]

  Await.ready(Http.server.serve(":8080", api))
}
