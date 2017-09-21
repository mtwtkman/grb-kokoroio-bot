package io.kokoro.bot

import org.scalatra._
import org.json4s.{JObject, JField, JString}
import org.json4s.jackson.JsonMethods._

object RequestParser {
  def parse(body: String): Option[List(String, String)] = {
    val parsed = parse(body)
  }
}

class GrbServlet extends ScalatraServlet {

  val access_token = sys.env.get("GRB_KOKOROIO_BOT_ACCESS_TOKEN") match {
    case Some(x) => x
    case None => "not found GRB_KOKOROIO_BOT_ACCESS_TOKEN"
  }

  val callback_secret = sys.env.get("GRB_KOKOROIO_BOT_CALLBACK_SECRET") match {
    case Some(x) => x
    case None => "not found GRB_KOKOROIO_BOT_CALLBACK_SECRET"
  }

  val GRBR_PTN = "^!grbr(#(\d+))?$"
  val GURABURU_URL = "http://gbf.game-a1.mbga.jp/assets/1505809531/img_mid/sp/assets/comic/episode/episode_"
  val API_ENDPOINT = "https://kokoro.io/api/v1/bot/rooms/"

  get("/") {
    <html>
      <body>
        ぐらぶるっ bot for <a href="https://kokoro.io">kokoro.io</a>
      </body>
    </html>
  }

  post("/") {
  }

}
