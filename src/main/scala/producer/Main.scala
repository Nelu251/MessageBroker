package producer

import akka.actor.{ActorSystem, Props}

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("System")

  case class Listen(nr: String)

  val listener = system.actorOf(Props(new Listener), name = "Listener")
   listener ! Listen("1")
   listener ! Listen("2")

}
