package producer

import akka.actor.{ActorSystem, Props}
import org.json4s.DefaultFormats

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("System")
  case class Tweet(message: String)
  case class Listen(nr: String)

  val messageWorker = system.actorOf(Props[MessageWorker], name = "Worker")
  val listener1 = system.actorOf(Props(new Listener(messageWorker)), name = "Listener1")
//  val listener2 = system.actorOf(Props(new Listener(messageWorker)), name = "Listener2")
   listener1 ! Listen("1")
//   listener2 ! Listen("2")

}
