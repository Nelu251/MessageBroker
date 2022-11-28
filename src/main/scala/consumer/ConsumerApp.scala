package consumer

import akka.actor.{ActorRef, ActorSystem}
import akka.util.ByteString
import play.api.libs.json.{Json, Writes}
import producer.ProducerApp.MessageToSend

import java.net.InetSocketAddress

object ConsumerApp extends App {
//  val HOST = "localhost"
//  val PORT = 1234
//
//  val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
//  val clientActor: ActorRef = actorSystem.actorOf(ClientTcp.props(new InetSocketAddress(HOST, PORT), null))
//  println(s"Connecting to ${HOST}:${PORT}")
//
//  val messageToSend = MessageToSend(isProducer = false, args(0))
//  clientActor ! ByteString(messageToSend.toString)

    case class MessageToSend (isProducer: Boolean, message: String)
    val HOST = "localhost"
    val PORT = 1235

    val actorSystem: ActorSystem = ActorSystem.create("MyActorSystem")
    val clientActor: ActorRef = actorSystem.actorOf(ClientTcp.props(new InetSocketAddress(HOST, PORT), null))

    println(s"Connecting to ${HOST}:${PORT}")

    val messageToSend = MessageToSend(isProducer = false, args(0))
    clientActor ! ByteString(messageToSend.toString)

}
