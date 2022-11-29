package messageBrocker

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import akka.io.Tcp.{PeerClosed, Received, Write}
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import messageBrocker.BrokerApp.{Publish, Subscribe}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
class Manager(topiDispatcher: ActorRef) extends Actor with ActorLogging {
  var map: Map[String, List[String]] = Map();

  implicit val timeout: Timeout = Timeout(12 seconds)
  implicit val executionContext: ExecutionContextExecutor = global

  def receive: Receive = {
    case Received(data) => {
      val message = Json.parse(data.utf8String)
      val isProducer = (message \ "isProducer").get.toString().replaceAll("\"", "") == "true"

      val senderActor = sender()
      if (isProducer) {
        topiDispatcher ! Publish(message)
      } else {
        while (true) {
          (topiDispatcher ? Subscribe(message, senderActor)).map { result =>
            senderActor ! Write(ByteString(result.toString))
          }
          Thread.sleep(200)
        }
      }
    }

    case PeerClosed => context stop self

    case Terminated(_) => println("terminated")
  }
}