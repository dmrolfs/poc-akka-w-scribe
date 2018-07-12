package akkaWithScribe

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.io.Tcp.{ ConnectionClosed, Received, Write }
import akka.util.ByteString
import scribe.{ Level, Logger }

object TCPConnectionHandler {
  def props: Props = Props( new TCPConnectionHandler )
}

class TCPConnectionHandler extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case m @ Received( data ) => {
      val decoded = data.utf8String
      scribe.trace( s"SCRIBE[TRACE]:connection received: ${decoded}" )
      log.debug( s"ACTOR_LOGGING[DEBUG]:connection received: ${decoded}" )
      scribe.debug( s"SCRIBE[DEBUG]:connection received: ${decoded}" )
      log.info( s"ACTOR_LOGGING[INFO]:connection received: ${decoded}" )
      scribe.info( s"SCRIBE[INFO]:connection received: ${decoded}" )
      log.warning( s"ACTOR_LOGGING[WARNING]:connection received: ${decoded}" )
      scribe.warn( s"SCRIBE[WARN]:connection received: ${decoded}" )
      log.error( s"ACTOR_LOGGING[ERROR]:connection received: ${decoded}" )
      scribe.error( s"SCRIBE[ERROR]:connection received: '${decoded}'" )
      sender() ! Write( ByteString( s"You told us: $decoded" ) )

      val level = decoded.trim.toLowerCase match {
        case "all"     => Some( Level( "all", 0.0 ) )
        case "trace"   => Some( Level.Trace )
        case "debug"   => Some( Level.Debug )
        case "info"    => Some( Level.Info )
        case "warning" => Some( Level.Warn )
        case "error"   => Some( Level.Error )
        case "off"     => Some( Level( "off", Double.MaxValue ) )
        case _         => None
      }

      level foreach { l =>
        scribe.info( s"SCRIBE[INFO]:set log level to: ${l}" )
        log.info( s"SCRIBE[INFO]:set log level to: ${l}" )
        Logger.root.withMinimumLevel( l ).replace()
        sender() ! Write( ByteString( s"setting log level to ${l}" ) )
      }
    }

    case message: ConnectionClosed => {
      log.info( "ACTOR_LOGGING:Connection has been closed" )
      scribe.info( "SCRIBE:Connection has been closed" )
      org.slf4j.MDC.clear()
      context stop self
    }
  }
}
