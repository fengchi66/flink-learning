package com.yit.data.common

/**
 * created by wufc 
 * on 2020/9/7
 */
object SqlQuery {

  // 创建kafka表，方便测试，定义proctime
  val createKafkaTable =
    """
      |CREATE TABLE ods_event_raw (
      |    `time` BIGINT,
      |    _track_id BIGINT,
      |    distinct_id STRING,
      |    login_id STRING,
      |    anonymous_id STRING,
      |    original_id STRING,
      |    type STRING,
      |    event STRING,
      |    lib MAP<STRING, STRING>,
      |    properties MAP<STRING, STRING>
      |) WITH (
      |    'connector.type' = 'kafka',
      |    'connector.version' = 'universal',
      |    'connector.topic' = 'ods_flow_sc_event_raw',
      |    'connector.properties.zookeeper.connect' = '172.22.11.36:2181',
      |    'connector.properties.bootstrap.servers' = '172.22.11.36:9092',
      |    'connector.properties.group.id' = 'wufc-local',
      |    'connector.startup-mode' = 'latest-offset',
      |    'update-mode' = 'append',
      |    'format.type' = 'json'
      |)
      |""".stripMargin

  val createEventTest =
    """
      |CREATE TABLE ods_event_raw (
      |    eventTime BIGINT,
      |    distinct_id STRING,
      |    event STRING,
      |    rt AS TO_TIMESTAMP(FROM_UNIXTIME(eventTime / 1000, 'yyyy-MM-dd HH:mm:ss')),
      |    watermark for rt as rt - interval '1' second
      |) WITH (
      |    'connector.type' = 'kafka',
      |    'connector.version' = 'universal',
      |    'connector.topic' = 'ods_event_test',
      |    'connector.properties.zookeeper.connect' = '172.22.11.36:2181',
      |    'connector.properties.bootstrap.servers' = '172.22.11.36:9092',
      |    'connector.properties.group.id' = 'wufc-local',
      |    'connector.startup-mode' = 'earliest-offset',
      |    'update-mode' = 'append',
      |    'format.type' = 'json'
      |)
      |""".stripMargin

}
