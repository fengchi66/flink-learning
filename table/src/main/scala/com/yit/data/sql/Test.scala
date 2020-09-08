package com.yit.data.sql

import com.yit.data.common.SqlQuery
import com.yit.data.util.FlinkUtil
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row


/**
 * created by wufc 
 * on 2020/9/7
 */
object Test {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = FlinkUtil.createTableEnv(env)

    tableEnv.sqlUpdate(SqlQuery.createKafkaTable)

    tableEnv.sqlQuery("SELECT * FROM ods_flow_sc_event_raw")
      .toAppendStream[Row]
      .print()

    env.execute("Job")
  }

}
