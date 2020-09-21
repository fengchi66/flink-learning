package com.yit.data.common

import java.util.Properties

import com.alibaba.fastjson.JSON
import com.yit.data.enties.EventLog
import com.yit.data.util.Util
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}

/**
 * created by wufc 
 * on 2020/9/6
 */
object KafkaSourceFactory {

  def createKafkaConsumer(): FlinkKafkaConsumer[String] = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "172.22.14.41:9092")
    properties.setProperty("group.id", "wufc-local")

    val kafkaConsumer = new FlinkKafkaConsumer[String]("yit_stage_supplychain.yitiao_scm_sub_order_sku", new SimpleStringSchema(), properties)
    kafkaConsumer.setStartFromEarliest()

    kafkaConsumer
  }

  def createKafkaProducer(): FlinkKafkaProducer[String] = {
    new FlinkKafkaProducer[String]("172.22.11.36:9092", "ods_event_test", new SimpleStringSchema())
  }

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
//
    env.addSource(createKafkaConsumer())
//        .map(line => {
//          val jSONObject = JSON.parseObject(line)
//          val eventTime = jSONObject.getLong("time")
//          val distinct_id = jSONObject.getString("distinct_id")
//          val event = jSONObject.getString("event")
//          EventLog(eventTime, distinct_id, event)
//        })
//        .map(r => {
//          Util.getGson().toJson(r)
//        })
//        .addSink(createKafkaProducer()
        .print()
    

    env.execute("Job")
  }

}
