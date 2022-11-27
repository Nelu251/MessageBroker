package producer.actors


import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

import java.net.InetSocketAddress

object Client {
  def props(remote: InetSocketAddress, listener: ActorRef) = Props(new Client(remote, listener))
}

class Client(remote: InetSocketAddress, var listener: ActorRef) extends Actor {

  import Tcp._
  import context.system

  if (listener == null) listener = Tcp.get(context.system).manager
  IO(Tcp) ! Connect(remote)

  def receive: Receive = {
    case CommandFailed(_: Connect) =>
      listener ! "connect failed"
      context stop self

    case c @ Connected(_, _) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      context become {
        case data: ByteString => connection ! Write(data)
        case CommandFailed(w: Write) => listener ! "write failed"
        case Received(data) => listener ! data
        case "close" => connection ! Close
        case _: ConnectionClosed =>
          listener ! "connection closed"
          context stop self
      }
  }
}