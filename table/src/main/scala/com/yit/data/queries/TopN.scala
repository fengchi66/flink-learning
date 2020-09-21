package com.yit.data.queries

import com.yit.data.common.SqlQuery
import com.yit.data.util.FlinkUtil
import org.apache.flink.streaming.api.scala._

object TopN {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val tableEnv = FlinkUtil.createTableEnv(env)
  
    // 1.创建kafka流表 ods_event_raw[(eventTime,distinct_id,event)]
    tableEnv.sqlUpdate(SqlQuery.createEventTest)
  
    /**
     * 分组Top-N: 目前仅 Blink 计划器支持 Top-N 。
     */
  }
  
}
