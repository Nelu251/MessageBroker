package messageBroker

import akka.actor.{Actor, ActorLogging, ActorRef}
import messageBroker.BrokerApp.{Publish, Subscribe}

class TopicActor(label: String, dispatcher: ActorRef) extends Actor with ActorLogging{

  var list: List[String] = List();

  override def receive: Receive = {
    case Publish(message) => {
      val userName: String = (message \ "name").get.toString()
      list = list.::(userName)
      log.info(s"Publisher for source $label")
    }

    case Subscribe(message, senderActor) => {
      log.info(s"Soubscribe for source $label")
      if (list.nonEmpty) {
        //        senderActor ! Result(list)
        list.foreach(tweet => {
          println(s"sending $tweet to ${senderActor.toString()}")
          senderActor ! tweet
        })
        list = List()
      } else senderActor ! "No messages yet"
    }
  }
}