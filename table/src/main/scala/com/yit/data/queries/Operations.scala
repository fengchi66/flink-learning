package com.yit.data.queries

import com.yit.data.common.SqlQuery
import com.yit.data.util.FlinkUtil
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row

/**
 * created by wufc 
 * on 2020/9/8
 */
object Operations {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val tableEnv = FlinkUtil.createTableEnv(env)

    // 1.创建kafka流表 ods_event_raw[(eventTime,distinct_id,event)]
    tableEnv.sqlUpdate(SqlQuery.createEventTest)

    // 2.简单查询表
    val table = tableEnv.sqlQuery("SELECT distinct_id, event FROM ods_event_raw")

    /**
     * 3. GroupBy 聚合
     * 注意： GroupBy 在流处理表中会产生更新结果（updating result）
     * 使用了集合后转换为流需要toRetractStream, 结果如下:
     * (true,brandflashsale_productlist_exposure,267)
     * (false,brandflashsale_productlist_exposure,267)
     * (true,brandflashsale_productlist_exposure,268)
     */
    val table1 = tableEnv.sqlQuery(
      """
        |SELECT event, COUNT(1) as counter
        |FROM ods_event_raw
        |GROUP BY event
        |""".stripMargin)

    /**
     * 4. GroupBy 窗口聚合: 使用分组窗口对每个组(每个key)进行计算并得到一个结果行
     */
    val table2 = tableEnv.sqlQuery(
      """
        |SELECT event, COUNT(1)
        |FROM ods_event_raw
        |GROUP BY TUMBLE(rt, INTERVAL '5' SECOND), event
        |""".stripMargin)


      table2
      .toRetractStream[Row]
      .print()

    env.execute("Job")

  }

}
