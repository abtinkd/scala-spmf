package com.tahlilafzar.sample

import scala.collection.mutable
import java.lang.IllegalArgumentException
import scala.IllegalArgumentException

/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/22/14
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
class ItemSet[T] {
  protected val singletons = mutable.Set[Item[T]]()
  protected val compounds = mutable.Set[Item[Set[T]]]()
  protected var prevWinItemVals = Set[T]()

  def addSingItem(itmVal: T, time: Long) {
    if (!this.contains(itmVal))
      singletons += new Item[T](itmVal, time)
  }

  def addCompItem(itmVal: Set[T], time: Long) {
    if(this.contains(itmVal))
      compounds.foreach(itm => if(itm.getValue.subsetOf(itmVal)) itm.countOne())
    else if(itmVal.size > 1){
      var extraNewComp = Set[Item[Set[T]]]()
      compounds.foreach(itm => {
        if (itm.getValue.subsetOf(itmVal))
          itm.countOne()
        else {
          val intersect = itmVal.intersect(itm.getValue)
          if (intersect.size > 1 && !this.contains(intersect)) {
            val newCompItem = new Item[Set[T]](intersect, itm.getATF.t_s)
            newCompItem.copyFrom(itm)
            newCompItem.countOne()
            if (extraNewComp.forall(x => x.getValue != intersect))
              extraNewComp += newCompItem
          }
        }
      })
      compounds ++= extraNewComp
      if(extraNewComp.forall(x => x.getValue!= itmVal))
        compounds += new Item[Set[T]](itmVal, time)
    }
  }

  def getSingletons = singletons

  def getCompounds = compounds

  override def toString = {
    var result = "{"
    singletons.foreach(x => result += x.toString + ",")
    compounds.foreach(x => result += x.toString + ",")
    result += "}\n(" + singletons.size + "," + compounds.size + ")\n"
    result
  }

  def resetCount() {
    for(itm <- singletons)
      itm.resetCount()

    for(itm <- compounds)
      itm.resetCount()
  }

  def countOneWin(winSize: Int) {
    for(itm <- singletons)
      itm.countOneWin(winSize)

    for(itm <- compounds)
      itm.countOneWin(winSize)
  }

  def countItem(itmVal: T): Int = {
    var res = 0
    for (itm <- singletons)
      if (itm.getValue == itmVal) {
        itm.countOne()
        res = itm.getCount
      }
    res
  }

  def countItem(itmVal: Set[T]): Int = {
    var res = 0
    for (itm <- compounds)
      if (itm.getValue == itmVal) {
        itm.countOne()
        res = itm.getCount
      }
    res
  }

  def contains(itmVal: T): Boolean = {
    var result = false
    for (i <- singletons if !result)
      if (i.getValue == itmVal)
        result = true
    result
  }

  def contains(itmVal: Set[T]): Boolean = {
    var result = false
    for (i <- compounds.toList if !result)
      if (i.getValue == itmVal)
        result = true
    result
  }

  def getItemSet: Set[Item[T]] = Set.empty[Item[T]] ++ singletons

  // Returns all values in the set
  def getItemsValSet: Set[T] = {
    val result = mutable.Set[T]()
    for (i <- singletons)
      result += i.getValue
    Set() ++ result
  }

  def updateItemsSupport(func: (Float, Int, Int) => Float) {
    singletons.foreach(x => x.updateSupport(func))
    compounds.foreach(x => x.updateSupport(func))
  }

  def updateItemsATF(curTime: Long) {
    singletons.foreach(x => x.updateATF(curTime))
    compounds.foreach(x => x.updateATF(curTime))
  }

  def isSingCompCompatible = {
    compounds.forall(x => x.getValue.forall(y => this.contains(y)))
  }

  def isCompoundCompatible = {
    var result = true
    val cmpArr = compounds.toArray
    for(s<-0 until cmpArr.size if result)
      for(i<-s+1 until cmpArr.size if result)
        if(!this.contains(cmpArr(s).getValue & cmpArr(i).getValue)) // check if intersect is already in the ItemSet
          result = false
    result
  }

  def isSupportCompatible = {
    var result = true
    val cmpArr = compounds.toArray
    for(singItm <- singletons if result)
      if(!compounds.forall(cmpItm => !(cmpItm.getValue.contains(singItm.getValue) && singItm.getSupport < cmpItm.getSupport))) // none of the compounds have a singleton with smaller support
        result = false
    for(i <- 0 until cmpArr.size if result)
      for(j <- i+1 until cmpArr.size if result)
        if(cmpArr(i).getValue.subsetOf(cmpArr(j).getValue) && cmpArr(i).getSupport < cmpArr(j).getSupport)
          result = false
    result
  }

  def makeCompatible() {
    val notCompatible = mutable.Set[Item[Set[T]]]()
    val cmpArr = compounds.toArray
    for(singItm <- singletons)
      compounds.foreach(cmpItm =>
        if(cmpItm.getValue.contains(singItm.getValue) && singItm.getSupport < cmpItm.getSupport)
          notCompatible += cmpItm
      )
    for(i <- 0 until cmpArr.size)
      for(j <- i+1 until cmpArr.size)
        if(cmpArr(i).getValue.subsetOf(cmpArr(j).getValue) && cmpArr(i).getSupport < cmpArr(j).getSupport)
          notCompatible += cmpArr(i)
    compounds --= notCompatible
  }

  def deleteLowSupportItems(minSup: Float, curTime: Long, considerATF: Boolean) {
    val notInBoundSing = mutable.Set[Item[T]]()
    val notInBoundComp = mutable.Set[Item[Set[T]]]()
    
    if (considerATF) {
      singletons.foreach(x => if (x.getSupport < minSup) {
        val regLine = x.runRegression
        if ((regLine._1 + regLine._2 * curTime) < minSup)
          notInBoundSing += x
      })
      compounds.foreach(x => if (x.getSupport < minSup) {
        val regLine = x.runRegression
        if ((regLine._1 + regLine._2 * curTime) < minSup)
          notInBoundComp += x
      })
    }
    else {
      singletons.foreach(x => if (x.getSupport < minSup) notInBoundSing += x)
      compounds.foreach(x => if (x.getSupport < minSup) notInBoundComp += x)
    }
    
    singletons --= notInBoundSing
    compounds --= notInBoundComp

    val notCompatible = mutable.Set[Item[Set[T]]]()
    val replaceWith = mutable.Set[Item[Set[T]]]()
    for(badSingItm <- notInBoundSing)
      compounds.foreach(x =>
        if(x.getValue.contains(badSingItm.getValue)) {
          notCompatible += x

        }
      )
    for(badCmpItm <- notInBoundComp)
      compounds.foreach(x =>
        if(badCmpItm.getValue.subsetOf(x.getValue))
          notCompatible += x
      )    
    compounds --= notCompatible
    prevWinItemVals = getItemsValSet
  }

  def getPrevWinItems  = prevWinItemVals
}
