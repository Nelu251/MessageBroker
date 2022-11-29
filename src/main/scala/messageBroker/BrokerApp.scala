package messageBrocker

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.JsValue

import java.net.InetSocketAddress

object BrokerApp extends App {
  case class Subscribe(message: JsValue, sender: ActorRef)
  case class Publish(message: JsValue)

  val HOST = "localhost"
  val PORT = 9900
  val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")

  val topicDispatcher: ActorRef = actorSystem.actorOf(Props(new TopicDispatcher()))
  val serverActor: ActorRef = actorSystem.actorOf(TcpServer.props(
    new InetSocketAddress(
      ConfigFactory.load().getString("host"),
      ConfigFactory.load().getInt("port")
    ), topicDispatcher))
  println(s"Server started!")
}
