package akkaWithScribe

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.io.Tcp.{ ConnectionClosed, Received, Write }
import akka.util.ByteString

object TCPConnectionHandler {
  def props: Props = Props( new TCPConnectionHandler )
}

class TCPConnectionHandler extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case m @ Received( data ) => {
      val decoded = data.utf8String
      log.info( s"ACTOR_LOGGING:connection received: ${decoded}" )
      scribe.info( s"SCRIBE:connection received: ${decoded}" )
      sender() ! Write( ByteString( s"You told us: $decoded" ) )
    }

    case message: ConnectionClosed => {
      log.info( "ACTOR_LOGGING:Connection has been closed" )
      scribe.info( "SCRIBE:Connection has been closed" )
      context stop self
    }
  }
}
