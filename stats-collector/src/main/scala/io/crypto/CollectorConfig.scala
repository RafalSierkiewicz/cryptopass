package io.crypto

import zio.config._
import ConfigDescriptor._
import zio.config.typesafe.TypesafeConfigSource
import java.io.File
import zio.ZIO
import akka.routing.RouterConfig
import scala.concurrent.duration.Duration

object CollectorConfig {
  final case class CollectorConfig(blockchainService: BlockchainServiceConfig, routes: RouteConfig)
  final case class BlockchainServiceConfig(wssUrl: String, token: String)
  final case class RouteConfig(askTimeout: Duration)

  private val blockchainDescriptor = (nested("blockchain-service")(string("wss").zip(string("token")))).to[BlockchainServiceConfig]
  private val routeConfigDescriptor = (nested("routes")(duration("ask-timeout"))).to[RouteConfig]
  private val descriptor: ConfigDescriptor[CollectorConfig] =
    blockchainDescriptor.zip(routeConfigDescriptor)
      .to[CollectorConfig]

  val readLayer = read(descriptor.from(
    TypesafeConfigSource
      .fromHoconFile(new File(getClass()
        .getResource("/application.conf").getPath()))
  )).toLayer

  val service = ZIO.service[CollectorConfig]

}
