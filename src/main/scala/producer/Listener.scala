package producer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.alpakka.sse.scaladsl.EventSource
import producer.ProducerApp.{Listen, Tweet}

import scala.concurrent.Future

class Listener(messageWorker: ActorRef) extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system

  val send: HttpRequest => Future[HttpResponse] = Http().singleRequest(_)
  val uri: Uri = Uri(s"http://localhost:4000//tweets/");

  def receive(): Receive = {
    case Listen(nr) =>
      println("SSE stream opened on URL: http://localhost:4000//tweets/" + nr)
      EventSource(uri = uri.toString() + nr, send = send)
        .runForeach(event => {
          messageWorker ! Tweet(event.data)
        })
  }
}
