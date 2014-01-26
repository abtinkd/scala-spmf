package com.tahlilafzar.sample

/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/22/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
class Item[T](val value: T, timeAdded: Long) {
  protected var (count, nWindow) = (1, 1)
  protected var support = 0.0F
  protected val ATF = new ATF_Form(timeAdded)


  def getValue  = value

  def getSupport = support

  def getCount = count

  def getATF = ATF

  override def toString = "  [v(" + value.toString + ")c(" + count.toString + ")w(" +
    nWindow.toString + ")su(" + support.toString + ")]"

  def resetCount() {
    count = 0
  }

  def countOne() {
    count += 1
  }

  def countOneWin(winSize: Int) {
    if (nWindow < winSize)
      nWindow += 1
  }

  def updateSupport(func: (Float, Int, Int) => Float): Float = {
    support = func(support, count, nWindow)
    support
  }

  def updateATF(curTime: Long) {
    ATF.sum_t_f += (ATF.t_s * support)
    ATF.sum_f += support
    ATF.sum_f_2 += (support * support)
    ATF.sum_t += curTime
    ATF.sum_t_2 += (curTime * curTime)
  }

  def runRegression : (Double, Double, Double) = {
    val s_t_t = ATF.sum_t_2 - ((ATF.sum_t * ATF.sum_t).toDouble / nWindow)
    val s_f_f  = ATF.sum_f_2 - ((ATF.sum_f * ATF.sum_f) / nWindow)
    val s_t_f = ATF.sum_t_f - ((ATF.sum_t * ATF.sum_f) / nWindow)
    val beta = s_t_f/s_t_t
    val alpha = ATF.sum_f/nWindow - (beta * (ATF.sum_t/nWindow))
    val r_2 = (s_t_f * s_t_f) / (s_t_t * s_f_f)
    (alpha, beta, r_2)
  }
}

class ATF_Form(val t_s: Long){
  var sum_t_f, sum_f, sum_f_2 = 0.0
  var sum_t, sum_t_2 = 0L
}
