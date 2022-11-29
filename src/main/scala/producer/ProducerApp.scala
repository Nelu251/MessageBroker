package producer

import akka.actor.{ActorSystem, Props}

object ProducerApp extends App {
  case class Tweet(content: String)
  case class TweetBind(isProducer: String, data: String)
  case class Listen(nr: String)

  implicit val system: ActorSystem = ActorSystem()

  val messageWorker = system.actorOf(Props(new MessageWorker), name = "messageWorker")
  val listener1 = system.actorOf(Props(new Listener(messageWorker)), name = "listener1")
  val listener2 = system.actorOf(Props(new Listener(messageWorker)), name = "listener2")
  listener1 ! Listen("1")
  listener2 ! Listen("2")

}
