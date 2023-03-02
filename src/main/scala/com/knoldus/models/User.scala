package com.knoldus.models

import spray.json
import spray.json.DefaultJsonProtocol

final case class User (name: String, age: Int, email: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val UserFormat = jsonFormat3(User)
}
