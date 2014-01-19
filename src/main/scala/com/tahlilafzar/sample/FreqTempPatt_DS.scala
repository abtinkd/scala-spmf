package com.tahlilafzar.sample

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.{Itemset, Itemsets}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/15/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */

class FreqTempPatt_DS(minSup: Float, winSizeN: Int, bufferStr: mutable.Buffer[String]) {
  protected var frqPattrn = List[ItemSet[String]]()
  protected var nWinData = ListBuffer[transactionList[String]]()
  protected val FSet = new ItemSet[String]()
  protected var winCount = 0
  protected var transactionCount = 0

  // reads from buffer and then clears it for new data
  def readBuffer() {
    if (winCount > winSizeN)
      nWinData -= nWinData.head
    else
      winCount += 1

    val transList = new transactionList[String]
    for (trans <- bufferStr) {
      val itemsStr = trans.split(" +")
      val itmSet = new ItemSet[String]()
      for (itm <- itemsStr) {
        itmSet.addSingItem(new Item[String](itm))
      }
      transList.addTransaction(itmSet)
    }
    nWinData += transList
    transactionCount = transList.size
    bufferStr.clear()
  }

  def runAlgorithm() = {
    for (curWinData <- nWinData) {
      val curTransactions = curWinData.getTransactions()
      for (curTrans <- curTransactions) {
        val curItmSetVals = curTrans.getItemsValSet()
        var foundValSet = Set[String]()

        //counting singleton items
        for (curItmValue <- curItmSetVals)
          if (FSet.contains(curItmValue)) {
            foundValSet += curItmValue
            FSet.countItem(curItmValue)
          }
          else
            FSet.addSingItem(new Item[String](curItmValue))

        //counting compound items
        for (size <- 2 to foundValSet.size)
          for (foundVal <- foundValSet.subsets(size))
            if (FSet.contains(foundVal))
              FSet.countItem(foundVal)
      }
    }

    FSet.uppdateItemsSupport((Sup: Float, Cnt: Int, nWin: Int) => (Sup + Cnt.toFloat / transactionCount) / nWin.toFloat)
    FSet.checkMinBound(minSup)
    FSet.updateCompounds()
  }
}

class Item[T](value: T) {
  protected var (count, nWindow) = (0, 1)
  protected var support = 0.0F

  def getValue() = value

  def getSupport() = support

  def getCount() = count

  def resetCount() {
    count = 0
  }

  def countOne() {
    count += 1
  }

  def countOneWin() {
    nWindow += 1
  }

  def updateSupport(func: (Float, Int, Int) => Float): Float = {
    support = func(support, count, nWindow)
    support
  }
}

class ItemSet[T] {
  protected val singletons = mutable.Set[Item[T]]()
  protected val compounds = mutable.Set[Item[Set[T]]]()

  def addSingItem(itm: Item[T]) {
    if (!this.contains(itm.getValue()))
      singletons += itm
  }

  def addCompItem(itm: Item[Set[T]]) {
    if (!this.contains(itm.getValue()))
      compounds += itm
  }

  def updateCompounds() {
    val singletonsStr = getItemsValSet()
    for (s <- 2 to singletonsStr.size)
      for (itmVl <- singletonsStr.subsets(s))
        if(!this.contains(itmVl))
          compounds += new Item[Set[T]](itmVl)
  }

  def countItem(itmVal: T): Int = {
    var res = 0
    for (itm <- singletons)
      if (itm.getValue() == itmVal) {
        itm.countOne()
        res = itm.getCount()
      }
    res
  }

  def countItem(itmVal: Set[T]): Int = {
    var res = 0
    for (itm <- compounds)
      if (itm.getValue() == itmVal) {
        itm.countOne()
        res = itm.getCount()
      }
    res
  }

  def contains(itmVal: T): Boolean = {
    var result = false
    for (i <- singletons)
      if (i.getValue() == itmVal)
        result = true
    result
  }

  def contains(itmVal: Set[T]): Boolean = {
    var result = false
    for (i <- compounds)
      if (i.getValue() == itmVal)
        result = true
    result
  }

  //  def hasSubSet(subSet: Set[T]) = {
  //    val itmVlSet = getItemsValSet()
  //    subSet subsetOf itmVlSet
  //  }

  //  def getItem(itm: T): Item[T] = {
  //    for (i <- singletons)
  //      if (i.getValue() == itm)
  //        return i
  //    null
  //  }
  //
  //  def getItem(itm: Set[T]): Item[Set[T]] = {
  //    for (i <- compounds)
  //      if (i.getValue() == itm)
  //        return i
  //    null
  //  }

  def getItemSet(): Set[Item[T]] = Set.empty[Item[T]] ++ singletons

  // Returns all values in the set
  def getItemsValSet(): Set[T] = {
    var result = Set[T]()
    for (i <- singletons)
      result += i.getValue()
    result
  }

  def uppdateItemsSupport(func: (Float, Int, Int) => Float) {
    singletons.foreach(x => x.updateSupport(func))
    compounds.foreach(x => x.updateSupport(func))
  }

  def checkMinBound(minSup: Float) {
    singletons.foreach(x => {
      if (x.getSupport() < minSup) singletons -= x
    })
    compounds.foreach(x => {
      if (x.getSupport() < minSup) compounds -= x
    })
  }
}

class transactionList[T] {
  protected var transactions = List[ItemSet[T]]()

  def addTransaction(trans: ItemSet[T]) {
    transactions = trans :: transactions
  }

  def getTransactions() = List.empty[ItemSet[T]] ++ transactions

  def size = transactions.size
}