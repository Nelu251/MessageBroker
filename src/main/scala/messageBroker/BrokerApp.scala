package messageBroker

import java.net.InetSocketAddress
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString
import play.api.libs.json.JsValue

object BrokerApp extends App {
  case class Publish(message: JsValue)
  case class Subscribe(message: JsValue, sender: ActorRef)
  val host = "localhost"
  val port = 1235

  val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")

  val dispatcher: ActorRef = actorSystem.actorOf(Props(new TopicDispatcher))
  val serverActor: ActorRef = actorSystem.actorOf(Server.props(new InetSocketAddress(host, port), dispatcher))
  println(s"Server started! listening to ${host}:${port}")
}