package com.yit.data.util

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.EnvironmentSettings
import org.apache.flink.table.api.scala._

/**
 * created by wufc 
 * on 2020/9/7
 */
object FlinkUtil {

  def createTableEnv(env: StreamExecutionEnvironment): StreamTableEnvironment ={
    val settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build()
    StreamTableEnvironment.create(env, settings)
  }

}
