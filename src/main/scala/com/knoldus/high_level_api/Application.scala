package com.knoldus.high_level_api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.knoldus.models.{Protocols, User}
import spray.json._

object Application extends App with Protocols{

  implicit val system = ActorSystem("LowLevelServerAPI")
  implicit val materializer = ActorMaterializer()

  val user = User("Kuldeepak", 22, "kuldeepak.gupta@knoldus.com")

  val highLevelApiRoute: Route =
    path("session") {
      get {
        complete(
          StatusCodes.OK,
          HttpEntity(
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
      }
    } ~ path("user"){
      get{
        complete(
          HttpEntity(
            ContentTypes.`application/json`,
            user.toJson.prettyPrint
          )
        )
      }
    } ~ pathPrefix("users"){
      path("user"/ Segment){ name =>
        get{
          complete(
            HttpEntity(
              ContentTypes.`application/json`,
              user.copy(name = name).toJson.prettyPrint
            )
          )
        }
      } ~ path("user"){
        parameter('age.as[Int]) { age =>
          get{
            complete(
              HttpEntity(
                ContentTypes.`application/json`,
                user.copy(age = age).toJson.prettyPrint
              )
            )
          }
        }
      }
    } ~ path("blog") {
      get {
        redirect("https://blog.knoldus.com/author/kuldeepakgupta/", StatusCodes.PermanentRedirect)
      }
    } ~ pathEndOrSingleSlash{
      get{
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            "Hello Welcome To Knolx Session!"
          )
        )
      }
    } ~ {
      get{
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            "OOPS, Wrong Path !! Keep Trying"
          )
        )
      }
    }

  Http().bindAndHandle(highLevelApiRoute,"localhost", 8008)
}
