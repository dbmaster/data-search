/*
 *  File Version:  $Id: free_text_search.groovy 153 2013-06-08 20:31:25Z schristin $
 */

import groovy.sql.Sql
import com.branegy.dbmaster.model.*
import com.branegy.service.connection.api.ConnectionService
import com.branegy.dbmaster.connection.ConnectionProvider
import com.branegy.dbmaster.connection.JDBCDialect
import org.apache.commons.io.IOUtils

connectionSrv = dbm.getService(ConnectionService.class)

def search_server    = p_database.split("\\.")[0]
def search_database  = p_database.split("\\.")[1]

RevEngineeringOptions options = new RevEngineeringOptions();
options.database = search_database
options.importIndexes = false
options.importViews = false
options.importProcedures = false

connectionInfo = connectionSrv.findByName(search_server)
connector = ConnectionProvider.getConnector(connectionInfo)

logger.info("Connecting to server")
dialect = connector.connect()
logger.info("Retrieving list of tables")
model = dialect.getModel(search_server, options)

logger.info("Searching...")

connection = connector.getJdbcConnection(search_database)
dbm.closeResourceOnExit(connection)

def sql = new Sql(connection)

def rsToString = { data ->
    if (data==null) {
        return null
    } else if (data instanceof java.sql.Clob) {
        Reader reader = data.getCharacterStream();
        return IOUtils.toString(reader);
    } else {
        return data.toString()
    }
}


search_result = []

model.tables.each { table  ->
    logger.info("Searching in table ${table.name}")

    query = generateSql(table, dialect);

    logger.debug("Query = ${query}")

    int columnCount
    def columnNames = []
    try {
        def rows = sql.rows(query, [search_terms: p_search_terms] ) {
            metadata -> columnCount = metadata.columnCount
            columnNames = (1..metadata.columnCount).collect { metadata.getColumnName(it) }
        }

        if (rows.size()>0) {
            println "Found value(s) in table ${table.name}\n"

            println """<table cellspacing="0" class="simple-table" border="1">
                        <tr style=\"background-color:#EEE\">"""
            colNames = (1..columnCount).each {
                println "<td>${columnNames[it-1]}</td>"
            }
            println "</tr>"

            rows.each {  row ->
                print "<tr>"
                (1..columnCount).each {
                    println "<td>${ rsToString(row.getAt(it-1)) }</td>"
                }
                print "</tr>"
            }
            println "</table>"
        /* println """${tool_linker.all().set("id","db-query")
                               .set("p_server",search_server)
                               .set("p_database",search_database)
                               .set("p_query",query)
                               .render("view data")}""".toString()
        */
            println "<br/><br/>"
            search_result << table.name
        }
    } catch (Exception e) {
        logger.error("Error searching in table ${table}", e)
    }
}

connection.close()
println "\nSearch Completed. Found value ${p_search_terms} in tables ${search_result}</pre>"

logger.info("Search completed")

def generateSql(Table table, JDBCDialect dialect){
   def tableName = table.name
   def columns=    table.columns

   switch (dialect.getDialectName().toLowerCase()){
   case "oracle":
       columnNames = columns.findAll { !it.type.matches("BFILE|LONG|BLOB|LONG BINARY|LONG RAW|XMLTYPE")  }.collect { "\"${it.name}\" like :search_terms" }
       return  "select * from \"${tableName}\" where (".toString() + columnNames.join (" or \n")+") and ROWNUM <= ${p_max_rows}"
   case "sqlserver":
       columnNames = columns.findAll { !it.type.matches("timestamp|image|xml|sql_variant")  }
                            .collect { "["+it.name+"] like :search_terms" }
       tableName = "["+table.getSchema()+"].["+ table.getSimpleName()+"]"
       return  "select top ${p_max_rows} * from ${tableName} with (NOLOCK) \nwhere ".toString() +
                columnNames.join (" or \n      ")
   case "mysql":
       columnNames = columns.findAll { !it.type.matches("image|xml")  }
                            .collect { it.name+" like :search_terms" }
       return  "select * from ${tableName} \nwhere ".toString() + columnNames.join (" or \n      ") + " limit 0,${p_max_rows}"
   case "nuodb":
       columnNames = columns.findAll { !it.type.matches("image|xml")  }.collect { it.name+ " like :search_terms" }
       return  "select * from ${tableName} \nwhere ".toString() + columnNames.join (" or \n      ") + " limit 0,${p_max_rows}"
   default:
       columnNames = columns.findAll { !it.type.matches("image|xml")  }.collect { it.name+" like :search_terms" }
       return  "select * from ${tableName} \nwhere ".toString() + columnNames.join (" or \n      ") + " limit 0,${p_max_rows}"
   }
}
