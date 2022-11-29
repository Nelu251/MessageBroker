package messageBrocker

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}

import java.net.InetSocketAddress

object TcpServer {
  def props(remote: InetSocketAddress, storageActor: ActorRef): Props =
    Props(new TcpServer(remote, storageActor))
}

class TcpServer(remote: InetSocketAddress, topicDispatcher: ActorRef) extends Actor with ActorLogging{

  import context.system

  IO(Tcp) ! Bind(self, remote)

  def receive: Receive = {
    case b @ Bound(_) => {
      context.parent ! b
    }
    case CommandFailed(_: Bind) ⇒ {
      context stop self
    }
    case Connected(remote, local) => {
      log.info(s"Client connected - Remote(Client): ${remote.getAddress} Local(Server): ${local.getAddress}")
      val handler: ActorRef = context.actorOf(Props(new Manager(topicDispatcher)))
      val connection = sender()
      connection ! Register(handler)

    }
    case Closed => println("actor closed")
  }
}
