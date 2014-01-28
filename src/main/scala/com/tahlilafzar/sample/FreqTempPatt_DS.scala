package com.tahlilafzar.sample

import scala.collection.mutable
import java.io._
import scala.collection.mutable.ListBuffer


/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/15/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
class FreqTempPatt_DS(minSup: Float, window_size: Int, bufferStr: mutable.Buffer[String], addressStr: String = "") {
  protected val nWinData = mutable.ListBuffer[transactionList[String]]()
  protected val FSet = new ItemSet[String]()
  protected val patterns = mutable.Map[String, ListBuffer[(Long, Float)]]()
  protected var timeCount = 0L
  protected var transactionCount = 0
  protected val writerFSet = new OutputStreamWriter(new FileOutputStream(
    addressStr + "_FSet" + "_"+minSup+"_"+window_size+".txt", true), "UTF-8")
  protected val writerData = new OutputStreamWriter(new FileOutputStream(
    addressStr + "_RawData" + "_"+minSup+"_"+window_size+".txt", true), "UTF-8")
  protected val writerPattern = new OutputStreamWriter(new FileOutputStream(
    addressStr + "_Patterns" + "_"+minSup+"_"+window_size+".txt", true), "UTF-8")
//  protected val writerPattern = new PrintWriter(new File(addressStr + "_Patterns" + "_"+minSup+"_"+window_size+".txt"),"UTF-8")
  //      val writer = new FileWriter(addr+"_"+minSup+"_"+window_size+".txt", true)

  // reads from buffer and then clears it for new data
  def read_buffer_then_clear() {
    timeCount += 1
    if (timeCount > window_size){
      transactionCount -= nWinData.head.size
      nWinData -= nWinData.head
    }

    val transList = new transactionList[String]
    for (trans <- bufferStr) {
      val itemsStr = trans.split("\\s+")
      val itmSet = new ItemSet[String]()
      for (itm <- itemsStr) {
        itmSet.addSingItem(itm, timeCount)
      }
      transList.addTransaction(itmSet)
    }
    nWinData += transList
    transactionCount += transList.size
    bufferStr.clear()
  }

  def runAlgorithm(useRegression: Boolean) = {
    read_buffer_then_clear()

    FSet.resetCount()
    FSet.countOneWin(window_size)

    for (curWinData <- nWinData) {
      val curTransactions = curWinData.getTransactions
      for (curTrans <- curTransactions) {
        val curItmSetVals = curTrans.getItemsValSet
        var foundValSet = Set[String]()

        //counting singleton items
        for (curItmValue <- curItmSetVals)
          if (FSet.contains(curItmValue)) {
            foundValSet += curItmValue
            FSet.countItem(curItmValue)
          }
          else
            FSet.addSingItem(curItmValue, timeCount)

        //counting compound items
        val prevWinItmVls = FSet.getPrevWinItems
        val intersectVls = prevWinItmVls.intersect(foundValSet)
        if (intersectVls.size > 1)
          FSet.addCompItem(intersectVls, timeCount)
      }
    }

    // 1: Update items support -> 2: update ATF of the remained items 3: Eliminate weak rules
    FSet.updateItemsSupport((Sup: Float, Cnt: Int, nWin: Int) => (Sup * (nWin-1) + Cnt.toFloat / transactionCount) / nWin.toFloat)
    FSet.updateItemsATF(timeCount)
    FSet.checkMinBound(minSup, timeCount, useRegression)
    update_Patterns(timeCount)

    save_results("\n" + timeCount + " -Considered Regression:" + useRegression + " -WINDATA:\n" + nWinData,
      writerData)
    save_results("\n" + timeCount + " -Considered Regression:" + useRegression + " -FSET:\n" + FSet,
      writerFSet)
  }

  def save_results(res: String, outStream: OutputStreamWriter) {
    if (addressStr == "")
      print(res)
    else  {
      outStream.write(res)
      outStream.flush()
    }
  }

  def save_results(res: String, writer: PrintWriter) {
    if (addressStr == "")
      print(res)
    else  {
      writer.write(res)
      writer.flush()
    }
  }

  def update_Patterns(curTime: Long) {
    for (itm <- FSet.getSingletons)
      if (!patterns.contains(itm.getValue))
        patterns += (itm.getValue -> ListBuffer[(Long, Float)]((curTime, itm.getSupport)))
      else
        patterns(itm.getValue) += Tuple2(curTime, itm.getSupport)

    for (itm <- FSet.getCompounds)
      if (!patterns.contains(itm.getValue.toString()))
        patterns += (itm.getValue.toString() -> ListBuffer[(Long, Float)]((curTime, itm.getSupport)))
      else
        patterns(itm.getValue.toString()) += Tuple2(curTime, itm.getSupport)
  }

  def close() {
    val patSortedList = patterns.toList.sortBy(-_._2.size)
    for(patt <- patSortedList) {
      save_results(patt.toString() + "\n", writerPattern)
      save_results(patt._2.size + " times present.\n\n", writerPattern)
    }
    writerFSet.close()
    writerData.close()
    writerPattern.close()
  }
}