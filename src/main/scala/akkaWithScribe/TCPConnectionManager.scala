package akkaWithScribe

import java.net.InetSocketAddress

import akka.actor.{ Actor, ActorLogging, Props }
import akka.io.Tcp.{ Bind, Bound, Connected, Register }
import akka.io.{ IO, Tcp }

object TCPConnectionManager {

  def props( hostname: String, port: Int ): Props = {
    Props( new TCPConnectionManager( hostname, port ) )
  }
}

class TCPConnectionManager( hostname: String, port: Int ) extends Actor with ActorLogging {
  import context.system
  IO( Tcp ) ! Bind( self, new InetSocketAddress( hostname, port ) )

  override def receive: Receive = {
    case m @ Bound( local ) => {
      log.info( s"ACTOR_LOGGING:Server started on $local" )
      scribe.info( s"SCRIBE:Server started on $local" )
    }

    case m @ Connected( remote, local ) => {
      org.slf4j.MDC.put( "remote", remote.toString )
      org.slf4j.MDC.put( "local", local.toString )
      log.debug( "FIRST LOG Received a RequestA" )
      val handler = context.actorOf( TCPConnectionHandler.props )
      log.info( s"ACTOR_LOGGING:New connnection: $local -> $remote" )
      scribe.info( s"SCRIBE:New connnection: $local -> $remote" )
      sender() ! Register( handler )
    }
  }
}
