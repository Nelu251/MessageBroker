package producer

import akka.actor.Actor
import net.liftweb.json
import org.json4s.DefaultFormats
import producer.Main.Tweet
import play.api.libs.json.{JsValue, Json, Writes}
import net.liftweb.json._
import net.liftweb.json.Serialization.write

class MessageWorker extends Actor {

  case class SimplifiedTweet(name: String, lang: String, text: String, source: String)
  implicit val formats: json.DefaultFormats.type = net.liftweb.json.DefaultFormats

  override def receive: Receive = {
    case Tweet(message) => {
      val result = processMessage(message)
      if (result != null) {
//        client ! result
        println(result)
      }
    }
  }

  def processMessage(message: String): String = {
    val isMessageValid = isValid(message)

    if (isMessageValid) {
      val messageToJson: JsValue = Json.parse(message)
      val hz  = extractMessage(messageToJson)
      hz
    } else null
  }

  def isValid(message: String): Boolean = {
    if (message.length.<(16)) {
      false
    }
    else {
      true
    }
  }

  def extractMessage(json: JsValue): String = {
    val name = (json \ "message" \ "tweet" \ "user" \ "name").get.toString.replaceAll("\"", "")
    val lang = (json \ "message" \ "tweet" \ "user" \ "lang").get.toString.replaceAll("\"", "")
    val text = (json \ "message" \ "tweet" \ "text").get.toString.replaceAll("\"", "")
    val source = (json \ "message" \ "tweet" \ "source").get.toString.replaceAll("\"", "")

//    val simplifiedTweet = SimplifiedTweet(name, lang, text, source)
//
//    val jsonString = write(simplifiedTweet)
//    jsonString

    val result = Json.obj(
      "source" -> source,
      "name" -> name,
      "lang" -> lang,
      "text" -> text
    )
    result.toString()
  }

}
