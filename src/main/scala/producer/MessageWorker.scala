package producer

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import play.api.libs.json._
import producer.ProducerApp.{Tweet, TweetBind}

import java.net.InetSocketAddress

class MessageWorker extends Actor with ActorLogging {
  val clientActor: ActorRef = context.actorOf(TcpClient.props(
    new InetSocketAddress(
      ConfigFactory.load().getString("host"),
      ConfigFactory.load().getInt("port")
    ), null))

  implicit val tweetBind: Writes[TweetBind] = (tweetBind: TweetBind) => Json.obj(
    "isProducer" -> tweetBind.isProducer,
    "data" -> tweetBind.data
  )

  override def receive: Receive = {

    case tweetMessage: Tweet => {

      if (isValid(tweetMessage.content)) {
        val message = bindMessageToProducer(tweetMessage)
        clientActor ! ByteString(message)
      }
    }
  }
  def bindMessageToProducer(tweet: Tweet): String = {
    val tweetBindToJson = Json.toJson(TweetBind("true", ""))
    val jsonTransformer = (__ \ "data").json.update(__.read[JsValue].map { _ => Json.parse(tweet.content) })
    val str = tweetBindToJson.transform(jsonTransformer).get.toString()
    str
  }

  def isValid(message: String): Boolean = {
    if (message.length.<(19)) {
      false
    }
    else {
      true
    }
  }

}

