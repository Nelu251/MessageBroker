package messageBrocker

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import messageBrocker.BrokerApp.{Publish, Subscribe}

class TopicDispatcher extends Actor with ActorLogging {
  var map: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case Publish(message) => {
      val label: String = (message \ "data" \ "message" \ "tweet" \ "source").get.toString()

      if (!map.contains(label)) {
        val topicActor = context.actorOf(Props(new TopicActor(label, self)))
        map += (label -> topicActor)
        topicActor ! Publish(message)
      } else {
        val topicActor = map(label)
        topicActor ! Publish(message)
      }

    }

    case Subscribe(message, senderActor) => {
      val label = (message \ "data").getOrElse(null).toString()
      val topicActor = map.getOrElse(label, null)
      if (topicActor != null) {
        println("sending message to consumer")
        topicActor ! Subscribe(message, sender)
      }
    }
  }
}
