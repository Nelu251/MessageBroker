package messageBrocker

import akka.actor.{Actor, ActorLogging, ActorRef}
import messageBrocker.BrokerApp.{Publish, Subscribe}
import play.api.libs.json.{JsValue, Json}

class TopicActor(label: String, storageActor: ActorRef) extends Actor with ActorLogging{

  var list: List[String] = List();

  override def receive: Receive = {
    case Publish(message) => {
      val value: String = (message \ "data").get.toString()
      val processedMessage = processMessage(value)
      list = list.::(processedMessage)
      log.info(s"Receiving request for publish with topic $label")
    }

    case Subscribe(message, senderActor) => {
      log.info(s"Receiving request for subscribing with topic $label")
      if (list.nonEmpty) {
        list.foreach(tweet => {
          senderActor ! tweet
        })
        list = List()
      } else senderActor ! "No messages yet"
    }
  }

  def processMessage(message: String): String = {
    val messageToJson: JsValue = Json.parse(message)
    val hz = extractMessage(messageToJson)
    hz
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
