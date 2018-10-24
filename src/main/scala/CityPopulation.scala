import city.events.EventHandler
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
    val count = EventHandler.inhabitants()
    Ok(s"$count")
  }

  private val adults: Endpoint[String] = get("city" :: "adults") {
    val count = EventHandler.adults()
    Ok(s"$count")
  }

  private val partners: Endpoint[String] = get("city" :: "partners") {
    val count = EventHandler.partners()
    Ok(s"$count")
  }

  val api: Service[Request, Response] =
    (inhabitant :+: city :+: adults :+: partners)
      .toServiceAs[Text.Plain]

  Await.ready(Http.server.serve(":8080", api))
}
