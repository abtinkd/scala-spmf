package com.tahlilafzar.sample

import scala.collection.mutable
import java.io.FileWriter


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
  protected var timeCount = 0L
  protected var transactionCount = 0

  // reads from buffer and then clears it for new data
  def read_Clear_Buffer() {
    timeCount += 1
    if (timeCount > window_size){
      nWinData -= nWinData.head
      transactionCount -= nWinData.head.size
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
    read_Clear_Buffer()

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
        for (size <- 2 to intersectVls.size)
          for (foundVal <- intersectVls.subsets(size))
            if (FSet.contains(foundVal))
              FSet.countItem(foundVal)
            else FSet.addCompItem(foundVal, timeCount)
      }
    }

    // 1: Update items support -> 2: update ATF of the remained items 3: Eliminate weak rules
    FSet.updateItemsSupport((Sup: Float, Cnt: Int, nWin: Int) => (Sup * (nWin-1) + Cnt.toFloat / transactionCount) / nWin.toFloat)
    FSet.updateItemsATF(timeCount)
    FSet.checkMinBound(minSup, timeCount, useRegression)
    save_results("\n" + timeCount + " -Considered Regression:" + useRegression + " -WINDATA:\n" + nWinData,
      addressStr + "_RawData")
    save_results("\n" + timeCount + " -Considered Regression:" + useRegression + " -FSET:\n" + FSet,
      addressStr + "_FSet")
  }

  def save_results(res: String, addr: String) {
    val title = "Min-Support = " + minSup + "   Window-Size = " + window_size + "\n*************************************\n"

    if (addr == "")
      print(title + res)
    else {
      val writer = new FileWriter(addr+"_"+minSup+"_"+window_size+".txt", true)
      writer.write(title + res)
      writer.close()
    }
  }
}