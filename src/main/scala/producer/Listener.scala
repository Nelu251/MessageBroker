package producer

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.alpakka.sse.scaladsl.EventSource
import producer.Main.{Listen, Tweet}

import scala.concurrent.Future

class Listener(messageWorker: ActorRef) extends Actor {

  implicit val system: ActorSystem = context.system

  val send: HttpRequest => Future[HttpResponse] = Http().singleRequest(_)
  val uri: Uri = Uri(s"http://localhost:4000//tweets/");
  println(s"stream source:: ${uri.toString()}")

  def receive(): Receive = {
    case Listen(nr) =>
      EventSource(uri.toString() + nr, send = send)
        .runForeach(event => {
        messageWorker ! Tweet(event.data)
      })
  }
}

