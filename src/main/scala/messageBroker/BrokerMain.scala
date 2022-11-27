package messageBroker

import java.net.InetSocketAddress
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

object BrokerMain extends App {
  val host = "0.0.0.0"
  val port = 1234
  println(s"Server started! listening to ${host}:${port}")

  val serverProps = Server.props(new InetSocketAddress(host, port))
  val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
  val serverActor: ActorRef = actorSystem.actorOf(serverProps)
  serverActor ! ByteString("Starting server...")
}