package consumer

import akka.actor.{ActorRef, ActorSystem, actorRef2Scala}
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{Json, Writes}
import producer.ProducerApp.TweetBind

import java.net.InetSocketAddress
import scala.language.postfixOps


object AppConsumer extends App {

    implicit val tweetBind: Writes[TweetBind] = (tweetBind: TweetBind) => Json.obj(
        "isProducer" -> tweetBind.isProducer,
        "data" -> tweetBind.data
    )

    val actorSystem: ActorSystem = ActorSystem.create("ActorSystem")
    val clientActor: ActorRef = actorSystem.actorOf(TcpClient.props(
        new InetSocketAddress(
            ConfigFactory.load().getString("host"),
            ConfigFactory.load().getInt("port")
        ), null))

    println("Connecting to Broker")
    val messageString = Json.toJson(TweetBind("false", args(0))).toString()
    clientActor ! ByteString(messageString)
}