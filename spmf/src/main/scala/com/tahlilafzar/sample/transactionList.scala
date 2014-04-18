package com.tahlilafzar.sample

/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/22/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
class transactionList[T] {
  protected var transactions = List[ItemSet[T]]()

  def addTransaction(trans: ItemSet[T]) {
    transactions = trans :: transactions
  }

  override def toString = transactions.toString()

  def getTransactions = List.empty[ItemSet[T]] ++ transactions

  def size = transactions.size
}
