package dsanchez.ssii.psi5;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
//			String checkTable = String.format("SELECT * FROM sqlite_master WHERE name ='{0}' and type='table';",TABLE_NAME);
			
			LoggerUtil.getLogger().info("Check table statement: "/* + checkTable*/);

//			cursor = stmt.executeQuery(checkTable);
			DatabaseMetaData md = conn.getMetaData();
			cursor = md.getTables(null, null, TABLE_NAME, null);//We check if our table exists
			
			LoggerUtil.getLogger().info(String.format("Checking if the table %s exists...", TABLE_NAME));
			
			//If cursor.first() is false means that the table desn't exist, so we have to create it.
			if(!cursor.next()){
				LoggerUtil.getLogger().warn(String.format("The table %s doesn't exists. Creating table...", TABLE_NAME));
				String createTable = String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, insert_date DATE, message TEXT, signed_message TEXT, encoded_key TEXT, integrity NUMERIC);", TABLE_NAME);
				
				LoggerUtil.getLogger().info(String.format("CREATE statement: %s",createTable));
				
				stmt.execute(createTable);
				
				//conn.commit(); The database is in auto-commit mode
				LoggerUtil.getLogger().info(String.format("Table '%s' created correctly\n", TABLE_NAME));
			}else{
				LoggerUtil.getLogger().warn(String.format("The table {%s already exists\n", TABLE_NAME));
			}
			
		}catch(Throwable oops){
			LoggerUtil.getLogger().error("Error while connecting to the database.",oops);
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
			
			String insert = String.format("INSERT INTO {%s (insert_date, message, signed_message, encoded_key, integrity) VALUES (?,'%s','%s','%s','%s');", TABLE_NAME,message,signedMessage,encodedKey,integrity);
			System.out.println(insert);//TODO: Remove
			
			LoggerUtil.getLogger().info("INSERT statement: "+insert);
			
			stmt = conn.prepareStatement(insert);//We prepare the statement to include parameters after this
			stmt.setDate(1, new Date(System.currentTimeMillis()));//The date is from java.sql.Date
			result = stmt.executeUpdate();
			
		}catch(Throwable oops){
			oops.printStackTrace();//TODO: remove
			LoggerUtil.getLogger().error("Error while trying to insert the data in the Data Base\n",oops);
		}
		return result;
	}

}
