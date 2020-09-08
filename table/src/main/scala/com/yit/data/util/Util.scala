package com.yit.data.util

import com.google.gson.Gson

/**
 * created by wufc 
 * on 2020/9/7
 */
object Util {
  private[this] val gson = new Gson

  def getGson() = gson

}
