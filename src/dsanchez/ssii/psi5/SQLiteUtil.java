package dsanchez.ssii.psi5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

public class SQLiteUtil {
	
	private static final String TABLE_NAME = "pedido";
	
	private static final Logger LOGGER = Logger.getLogger(SQLiteUtil.class.getName());
	
	public void checkTable(){
		Connection conn;
		Statement stmt;
		ResultSet cursor;
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:ssii-psi5.db");
			
			//We check if the table exist. If the table doesn't exist we create it.
			stmt = conn.createStatement();
			String checkTable = String.format("SELECT * FROM sqlite_master WHERE name ='{0}' and type='table';",TABLE_NAME);
			LOGGER.info("Check table statement: " + checkTable);

			cursor = stmt.executeQuery(checkTable);
			LOGGER.info(String.format("Checking if the table {0} exists...", TABLE_NAME));
			
			//If cursor.first() is false means that the table desn't exist, so we have to create it.
			if(!cursor.first()){
				LOGGER.info(String.format("The table {0} doesn't exists. Creating table...", TABLE_NAME));
				String createTable = String.format("CREATE TABLE {0} (id INTEGER PRIMARY KEY AUTOINCREMENT, insert_date DATE, message TEXT, integrity NUMERIC);", TABLE_NAME);
				//TODO:Add public key to the database
			}
			
		}catch(Throwable oops){
			oops.printStackTrace();
		}
		
	}

}
