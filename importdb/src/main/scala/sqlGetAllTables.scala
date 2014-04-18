/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/8/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.{File, PrintWriter}
import java.sql.{ResultSetMetaData, DriverManager, Connection}
import scala.collection.mutable

object sqlGetAllTables extends App{
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://vm11.cluster.dev.tahlilafzar.com:3306/information_schema"
  val username = "abtin"
  val password = "123456"
  var connection:Connection = null
  val writer = new PrintWriter(new File("DBDATA" ))

  try {
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT DISTINCT TABLE_NAME, TABLE_SCHEMA FROM COLUMNS")
    while (resultSet.next()) {
      val dbName = resultSet.getString("TABLE_SCHEMA")
      val tbName = resultSet.getString("TABLE_NAME")
      val statement2 = connection.createStatement()
      val tableRecords = statement2.executeQuery("SELECT * FROM " + dbName +
        "." + tbName)
//      writer.write("\n\nDatabase: "+ dbName + ", Table: " + tbName + "\n\n")
      val metaData: ResultSetMetaData = tableRecords.getMetaData()
      while(tableRecords.next()){
        var i = 1
        while(i <= metaData.getColumnCount()){
          val columnName = metaData.getColumnName(i)
          writer.write(dbName + ", " + tbName + ",\t\"" + columnName + "\": " + tableRecords.getString(columnName) + ", ")
          i+=1
        }
        writer.write("\n")
      }

    }
    connection.close()
    writer.close()
  } catch {
    case e => e.printStackTrace
  }
}
