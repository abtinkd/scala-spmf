package com.tahlilafzar.sample

import scala.io.Source
import scala.collection.mutable
import com.tahlilafzar.sample.FreqTempPatt_DS
/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/19/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
object MainTest_FreqTempPatt_DS {
  def main(args: Array[String]){
    val (minSup: Float, winSize: Int)  = (0.9F, 2)
    val fileName = "DbDatEncoded"
    val fileStr = mutable.Buffer[String]()
    val bufferStr = mutable.Buffer[String]()

    val assocRuleAlg = new FreqTempPatt_DS(minSup, winSize, bufferStr)

    for (line <- Source.fromFile(fileName).getLines())
      fileStr += line

    var i = 1
    for(trans <- fileStr) {
      if(i > 5){
        println(bufferStr)
        assocRuleAlg.runAlgorithm()
        i = 1
      }
      else {
        bufferStr += trans
        i +=1
      }
    }
  }
}
