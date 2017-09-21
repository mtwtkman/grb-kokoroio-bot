package io.kokoro.bot

import org.scalatra._
import org.json4s.{JObject, JField, JString, JArray}
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import scalaj.http.Http

class GrbrServlet extends ScalatraServlet {
  var logger = LoggerFactory.getLogger(getClass)

  val access_token = sys.env.get("GRBR_KOKOROIO_BOT_ACCESS_TOKEN") match {
    case Some(x) => x
    case None => "not found GRBR_KOKOROIO_BOT_ACCESS_TOKEN"
  }

  val callback_secret = sys.env.get("GRBR_KOKOROIO_BOT_CALLBACK_SECRET") match {
    case Some(x) => x
    case None => "not found GRBR_KOKOROIO_BOT_CALLBACK_SECRET"
  }

  val GRBR_PTN = """^!grbr(?:#(\d+))?$"""
  val GRBR_REGEX = GRBR_PTN.r
  val GURABURU_COMIC_LIST_URL = "http://game.granbluefantasy.jp/comic/list/1"
  val GURABURU_EPISODE_URL = "http://game-a1.granbluefantasy.jp/assets/img/sp/assets/comic/episode/episode_"
  val API_ENDPOINT = "https://kokoro.io/api/v1/bot/rooms/"

  get("/") {
    <html>
      <body>
        ぐらぶるっ bot for <a href="https://kokoro.io">kokoro.io</a>
      </body>
    </html>
  }

  post("/") {
    val body = parse(request.body)
    logger.info(s"received: $body")
    request.getHeader("Authorization") match {
      case x if x == callback_secret => x
      case _ => {
        logger.debug("Invalid callback_secret")
        halt(401, "Invalid callback_secret")
      }
    }
    val parsed: List[(String, String)] = for {
      JObject(elem) <- body
      JField("raw_content", JString(message)) <- elem
      JField("room", JObject(room)) <- elem
      JField("id", JString(room_id)) <- room
    } yield (message, room_id)
    parsed match {
      case List((message, room_id)) if message.matches(GRBR_PTN) && room_id != "" => {
        val GRBR_REGEX(episode) = message
        val grbr_message: String = episode == null match {
          case true => {
            logger.info("fetch latest episode")
            val comic_list_resp = Http(GURABURU_COMIC_LIST_URL)
              .header("Accept", "application/json, text/javascript, */*; q=0.01")
              .asString
              .body
            logger.info(s"fetched: ${comic_list_resp}")
            val episode_id = (for {
              JObject(elem) <- parse(comic_list_resp)
              JField("list", JArray(list)) <- elem
              JObject(item) <- list(0)
              JField("id", JString(id)) <- item
            } yield id)(0)
            s"${GURABURU_EPISODE_URL}${episode_id}.jpg"
          }
          case false => {
            logger.info("check the episode exists or not")
            val episode_url = s"${GURABURU_EPISODE_URL}${episode}.jpg"
            Http(episode_url).asString.code match {
              case status_code if status_code != 200 => s"${episode}話は見つかりませんでした"
              case status_code => episode_url
            }
          }
        }
        val req_data = s"""{
          "message": "$grbr_message",
          "display_name": "ぐらぶるっ"
        }"""
        val url = s"${API_ENDPOINT}${room_id}/messages"
        logger.info(s"post ${grbr_message} to kokoro.io")
        val resp = Http(url)
          .postData(req_data)
          .header("Content-Type", "application/json")
          .header("X-Access-Token", access_token)
          .asString
        logger.info(s"response: $resp")
      }
      case _ => {
        logger.debug(s"Not matched with `${GRBR_PTN}`")
        halt(401, "Not matched")
      }
    }
  }

}
