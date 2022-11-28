package messageBroker

import java.net.InetSocketAddress
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Tcp}


object Server {
  def props(remote: InetSocketAddress, dispatcher: ActorRef): Props =
    Props(new Server(remote, dispatcher))
}

class Server(remote: InetSocketAddress, dispatcher: ActorRef) extends Actor with ActorLogging{

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, remote)

  def receive: Receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) ⇒ context stop self

    case c @ Connected(remote, local) =>
      println(s"Client connected - Remote(Client): ${remote.getAddress} Local(Server): ${local.getAddress}")
      val handler = context.actorOf(Props(new Manager(dispatcher)))
      val connection = sender()
      log.info(sender().toString())
      log.info(remote.getPort.toString)
      connection ! Register(handler)
  }

}