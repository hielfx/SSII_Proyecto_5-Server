package dsanchez.ssii.psi5;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLiteUtil {
	
	private static final String TABLE_NAME = "pedido";
	
	private static final String CONECTION = "jdbc:sqlite:ssii-psi5.db";
	
	public static void checkTable(){
		Connection conn;
		Statement stmt;
		ResultSet cursor;
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(CONECTION);
			
			//We check if the table exist. If the table doesn't exist we create it.
			stmt = conn.createStatement();
			String checkTable = String.format("SELECT * FROM sqlite_master WHERE name ='{0}' and type='table';",TABLE_NAME);
			LoggerUtil.getLogger().info("Check table statement: " + checkTable);

			cursor = stmt.executeQuery(checkTable);
			LoggerUtil.getLogger().info(String.format("Checking if the table {0} exists...", TABLE_NAME));
			
			//If cursor.first() is false means that the table desn't exist, so we have to create it.
			if(!cursor.first()){
				LoggerUtil.getLogger().warning(String.format("The table {0} doesn't exists. Creating table...", TABLE_NAME));
				String createTable = String.format("CREATE TABLE {0} (id INTEGER PRIMARY KEY AUTOINCREMENT, insert_date DATE, message TEXT, signed_message TEXT, encoded_key TEXT, integrity NUMERIC);", TABLE_NAME);
				
				stmt.execute(createTable);
				
				conn.commit();//We commit the changes
				LoggerUtil.getLogger().info(String.format("Table '{0}' created correctly\n", TABLE_NAME));
			}else{
				LoggerUtil.getLogger().warning(String.format("The table {0} already exists\n", TABLE_NAME));
			}
			
		}catch(Throwable oops){
			LoggerUtil.getLogger().log(Level.SEVERE, "Error while connecting to the database.");
			oops.printStackTrace();
		}
		
	}
	
	public static Integer insertIntoTable(String message, String signedMessage,String encodedKey,Integer integrity){
		Connection conn;
		PreparedStatement stmt;
		Integer result = null;
		try{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(CONECTION);
			LoggerUtil.getLogger().info("Inserting data in the Data Base...");
			
			String insert = String.format("INSERT INTO {0} (insert_date, message, signed_message, encoded_key, integrity) VALUES (?,'{1}','{2}','{3}','{4}');", TABLE_NAME,message,signedMessage,encodedKey,integrity);
			System.out.println(insert);//TODO: Remove
			
			LoggerUtil.getLogger().info("INSERT statement: "+insert);
			
			stmt = conn.prepareStatement(insert);//We prepare the statement to include parameters after this
			stmt.setDate(1, new Date(System.currentTimeMillis()));//The date is from java.sql.Date
			result = stmt.executeUpdate();
			
		}catch(Throwable oops){
			oops.printStackTrace();
			LoggerUtil.getLogger().log(Level.SEVERE,"Error while trying to insert the data in the Data Base\n");
		}
		return result;
	}

}
