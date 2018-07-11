package akkaWithScribe

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.dispatch.MonitorableThreadFactory
import scribe.Logger
import scribe.writer.FileWriter

object Server extends App {
  // creates logs/app.log but this isn't populated; want to rely on slf4j regardless
//  Logger.root.withHandler( writer = FileWriter.default ).replace()

  val system = ActorSystem()
  val tcpserver = system.actorOf( TCPConnectionManager.props( "localhost", 8080 ) )
  Runtime.getRuntime.addShutdownHook(
    MonitorableThreadFactory(
      "monitoring-thread-factory",
      false,
      Some( Thread.currentThread().getContextClassLoader )
    ).newThread(
      new Runnable {
        override def run(): Unit = {
          val terminate = system.terminate()
          Await.result( terminate, 10.seconds )
        }
      }
    )
  )
}
