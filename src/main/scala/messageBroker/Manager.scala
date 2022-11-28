package messageBroker

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import akka.io.Tcp.{PeerClosed, Received, Write}
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import messageBroker.BrokerApp.{Publish, Subscribe}
import play.api.libs.json.{JsValue, Json}
import producer.ProducerApp.MessageToSend

import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class Manager(storageActor: ActorRef) extends Actor with ActorLogging {

  var map: Map[String, List[String]] = Map();

  implicit val timeout: Timeout = Timeout(12 seconds)
  implicit val executionContext: ExecutionContextExecutor = global
  def receive: Receive = {
    case Received(data) => {
      val tweet = data.utf8String
      tweet match {
        case s"MessageToSend(${isProducer},${message})" => {
          val recievedMessage = MessageToSend(isProducer = isProducer.toBoolean, message = message)
          val deserializedMessage = Json.parse(recievedMessage.message)
          val senderActor = sender()

          if (recievedMessage.isProducer) handleProducerMessage(deserializedMessage)
          else if (!recievedMessage.isProducer) handleConsumerMessage(deserializedMessage, senderActor)
          else handleUnknownMessage(deserializedMessage)
        }
        case _ => None
      }
    }

    case PeerClosed => context stop self

    case Terminated(_) => println("terminated")
  }
  private def handleUnknownMessage(message: JsValue) = {
    log.error(s"Handling unknown message ${message.toString()}")
    Write(ByteString("Unknown message type"))
  }

  private def handleConsumerMessage(message: JsValue, senderActor: ActorRef): Unit = {
    while (true){
      (storageActor ? Subscribe(message, senderActor)).map { result =>
        senderActor ! Write(ByteString(result.toString))
      }
      Thread.sleep(200)
    }
    //    storageActor ! Consume(message, senderActor)
  }

  private def handleProducerMessage(message: JsValue): Unit = {
    storageActor ! Publish(message)
  }
}