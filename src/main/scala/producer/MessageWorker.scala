package producer

import akka.actor.{Actor, ActorRef}
import akka.util.ByteString
import net.liftweb.json
import producer.ProducerApp.{MessageToSend, Tweet}
import play.api.libs.json.{JsValue, Json}
import producer.actors.Client

import java.net.InetSocketAddress

class MessageWorker extends Actor {
  val client: ActorRef = context.actorOf(Client.props(new InetSocketAddress("localhost", 1235), null))


  implicit val formats: json.DefaultFormats.type = net.liftweb.json.DefaultFormats

  override def receive: Receive = {
    case Tweet(message) => {
      val result = processMessage(message)
      if (result != null) {
        println(MessageToSend(isProducer = true, result).toString)
        client ! ByteString(MessageToSend(isProducer = true, result).toString)
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
    if (message.length.<(19)) {
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

    val result = Json.obj(
      "source" -> source,
      "name" -> name,
      "lang" -> lang,
      "text" -> text
    )
    result.toString()
  }

}
