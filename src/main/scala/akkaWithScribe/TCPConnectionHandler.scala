package akkaWithScribe

import akka.actor.{ Actor, ActorRef, Props }
import akka.io.Tcp.{ ConnectionClosed, Received, Write }
import akka.util.ByteString

object TCPConnectionHandler {
  def props: Props = Props( new TCPConnectionHandler )
}

class TCPConnectionHandler extends Actor {
  override def receive: Actor.Receive = {
    case m @ Received( data ) => {
      val decoded = data.utf8String
      scribe.info( s"connection received: ${decoded}" )
      sender() ! Write( ByteString( s"You told us: $decoded" ) )
    }

    case message: ConnectionClosed => {
      scribe.info( "Connection has been closed" )
      context stop self
    }
  }
}
