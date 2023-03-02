package com.knoldus.low_level_api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.stream.ActorMaterializer
import com.knoldus.models._
import spray.json._

object Application extends App with Protocols {

  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val user = User("Kuldeepak", 22, "kuldeepak.gupta@knoldus.com")

  val handler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        StatusCodes.OK,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          "Hello Welcome To Knolx Session!"
        )
      )

    case HttpRequest(HttpMethods.GET, Uri.Path("/session"), _, _, _) =>
      HttpResponse(
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          """
            |<html>
            | <body>
            |   This is Knolx Session
            | </body>
            |</html>
          """.stripMargin
        )
      )

    case HttpRequest(HttpMethods.GET, Uri.Path("/blogs"), _, _, _) =>
      HttpResponse(
        StatusCodes.Found,
        headers = List(Location("https://blog.knoldus.com/author/kuldeepakgupta/"))
      )

    case HttpRequest(HttpMethods.GET,Uri.Path("/user"),_,_,_) =>
      HttpResponse(
        StatusCodes.Found,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          user.toJson.prettyPrint
        )
      )

    case HttpRequest(HttpMethods.GET, uri@Uri.Path("/users/user"),_,_,_) =>
      val query = uri.query()
      val userName = query.get("name").getOrElse("default")
      HttpResponse(
        entity = HttpEntity(
          ContentTypes.`application/json`,
          user.copy(name = userName).toJson.prettyPrint
        )
      )


    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound,
        entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          "OOPS, Wrong Path !! Keep Trying"
        )
      )
  }

  val bindingFuture = Http().bindAndHandleSync(handler, "localhost", 8008)

  // shutdown the server:
  bindingFuture
    .flatMap(binding => binding.unbind())
    .onComplete(_ => system.terminate())
}