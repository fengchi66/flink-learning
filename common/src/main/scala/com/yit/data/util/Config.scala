package com.yit.data.util

import com.typesafe.config.{Config, ConfigFactory}

/**
 * created by wufc 
 * on 2020/9/8
 */
object Config {

  private val load: Config = ConfigFactory.load()

  val dataKafkaZkConnect = load.getString("data.kafka.zookeeper.connect")
  val dataKafkaBsServers: String = load.getString("data.kafka.bootstrap.servers")
  val groupId = load.getString("data.kafka.group.id")
  val odsEventTopic = load.getString("kafka.ods.topic")

}
