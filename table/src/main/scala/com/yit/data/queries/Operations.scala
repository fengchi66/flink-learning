package com.yit.data.queries

import com.yit.data.common.SqlQuery
import com.yit.data.util.FlinkUtil
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row

/**
 * created by wufc 
 * on 2020/9/8
 * 对一条流进行查询
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

    /**
     * 5. Over Window aggregation:
     * 注意： 所有的聚合必须定义到同一个窗口中，即相同的分区、排序和区间。当前仅支持 PRECEDING (无界或有界) 到 CURRENT ROW
     * 范围内的窗口、FOLLOWING 所描述的区间并未支持，ORDER BY 必须指定于单个的时间属性。
     * 每来一条数据发出一次结果
     */
//    val table3 = tableEnv.sqlQuery(
//      """
//        |SELECT COUNT(1) OVER (
//        |  PARTITION BY event
//        |  ORDER BY rt
//        |  ROWS BETWEEN 2 PRECEDING AND CURRENT ROW)
//        |FROM ods_event_raw
//        |
//        |""".stripMargin)
//
//    tableEnv.sqlQuery(
//      """
//        |SELECT COUNT(amount) OVER w, SUM(amount) OVER w
//        |FROM Orders
//        |WINDOW w AS (
//        |  PARTITION BY user
//        |  ORDER BY proctime
//        |  ROWS BETWEEN 2 PRECEDING AND CURRENT ROW)
//        |""".stripMargin)

    /**
     * 6. Distinct
     * 对于流处理查询，根据不同字段的数量，计算查询结果所需的状态可能会无限增长
     */
    val table4 = tableEnv.sqlQuery("SELECT DISTINCT event FROM ods_event_raw")

    /**
     * 7. Grouping sets, Rollup, Cube
     * 如果您经常需要对数据进行多维度聚合分析（例如既需要按照a列聚合，也要按照b列聚合，同时要按照a和b两列聚合），
     * 您可以使用GROUPING SETS语句进行多维度聚合分析，避免多次使用UNION ALL影响性能。
     */
    val grouping_sql =
      """
        |SELECT
        |    `month`,
        |    `day`,
        |    count(distinct `username`) as uv
        |FROM tmall_item
        |group by
        |grouping sets((`month`),(`month`,`day`));
        |""".stripMargin

    /**
     * 8. Having
     */
    val table5 = tableEnv.sqlQuery(
      """
        |SELECT event, COUNT(1)
        |FROM ods_event_raw
        |GROUP BY event
        |HAVING COUNT(1) > 200
        |""".stripMargin)

    table5
      .toRetractStream[Row]
      .print()

    env.execute("Job")

  }

}
