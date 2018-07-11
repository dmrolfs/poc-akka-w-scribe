package akkaWithScribe

import java.net.InetSocketAddress

import akka.actor.{ Actor, Props }
import akka.io.Tcp.{ Bind, Bound, Connected, Register }
import akka.io.{ IO, Tcp }

object TCPConnectionManager {

  def props( hostname: String, port: Int ): Props = {
    Props( new TCPConnectionManager( hostname, port ) )
  }
}

class TCPConnectionManager( hostname: String, port: Int ) extends Actor {
  import context.system
  IO( Tcp ) ! Bind( self, new InetSocketAddress( hostname, port ) )

  override def receive: Receive = {
    case m @ Bound( local ) => {
      scribe.info( s"Server started on $local" )
    }

    case m @ Connected( remote, local ) => {
      val handler = context.actorOf( TCPConnectionHandler.props )
      scribe.info( s"New connnection: $local -> $remote" )
      sender() ! Register( handler )
    }
  }
}
