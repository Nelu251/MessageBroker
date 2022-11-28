package messageBroker

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import messageBroker.BrokerApp.{Publish, Subscribe}

class TopicDispatcher extends Actor with ActorLogging {

  var map: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case Publish(message) => {
      val source: String = (message \ "source").get.toString()

      if (!map.contains(source)) {
        val topicActor = context.actorOf(Props(new TopicActor(source, self)))
        map += (source -> topicActor)
        topicActor ! Publish(message)
      } else {
        val topicActor = map(source)
        topicActor ! Publish(message)
      }
    }

    case Subscribe(message, senderActor) => {
      val source = (message \ "source").getOrElse(null).toString()
      val topicActor = map.getOrElse(source, null)
      println("----- topicActor----" + source)
      if (topicActor != null) {
        println("sending message to consumer")
        topicActor ! Subscribe(message, sender)
      }
    }
  }
}
