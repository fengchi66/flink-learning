package com.yit.data.sql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala._

/**
 * created by wufc 
 * on 2020/9/7
 */
object KafkaSourceDemo {
  def main(args: Array[String]): Unit = {
    // Table执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    val tableEnv = StreamTableEnvironment.create(env, settings)

    // 1.kafka source表
    tableEnv.sqlUpdate(
      """
        |CREATE TABLE ods_flow_sc_event_raw (
        |    eventTime TIMESTAMP(3),
        |    _track_id BIGINT,
        |    distinct_id STRING,
        |    login_id STRING,
        |    anonymous_id STRING,
        |    original_id STRING,
        |    type STRING,
        |    event STRING,
        |    lib STRING,
        |    properties STRING
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
        |""".stripMargin)

    tableEnv.sqlUpdate(
      """
        |CREATE TABLE ods_event_test (
        |    eventTime TIMESTAMP(3),
        |    distinct_id STRING,
        |    event STRING
        |) WITH (
        |    'connector.type' = 'kafka',
        |    'connector.version' = 'universal',
        |    'connector.topic' = 'ods_event_test',
        |    'connector.properties.zookeeper.connect' = '172.22.11.36:2181',
        |    'connector.properties.bootstrap.servers' = '172.22.11.36:9092',
        |    'update-mode' = 'append',
        |    'format.type' = 'json'
        |)
        |""".stripMargin)

    tableEnv.sqlUpdate("INSERT INTO ods_event_test SELECT eventTime, distinct_id, event FROM ods_flow_sc_event_raw")

    env.execute("Job")
  }

}
