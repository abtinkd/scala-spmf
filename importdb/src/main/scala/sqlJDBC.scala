import java.sql.{DriverManager, Connection}
import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: GHC
 * Date: 1/7/14
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * A Scala JDBC connection example by Alvin Alexander,
 * http://alvinalexander.com
 */

object sqlJDBC {
  // connect to the database named "mysql" on the localhost
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://vm11.cluster.dev.tahlilafzar.com:3306/information_schema"
  val username = "abtin"
  val password = "123456"
  var connection:Connection = null

  try {
    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    // create the statement, and run the select query
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME FROM COLUMNS")
    var allTables = Map[String, mutable.Set[String]]()
    while ( resultSet.next() ) {
      val tableName = resultSet.getString("TABLE_NAME")
      val dbName = resultSet.getString("TABLE_SCHEMA")
      val dbTbl = dbName + "." + tableName
      val columName = resultSet getString "Column_NAME"
      if (!allTables.contains(dbTbl))
        allTables = allTables+(dbTbl -> mutable.Set(columName))
      else
        allTables(dbTbl)+=columName
    }
    println(allTables)
    //allTables.keys.foreach(i => println("Table: " + i + "\n\tColumns: " + allTables(i)))
    allTables.foreach(mp => println("Table: " + mp._1 + "   Fields: " + mp._2))
    connection.close()
  } catch {
    case e => e.printStackTrace
  }
}
