package dsanchez.ssii.psi5;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SQLiteUtil {
	
	private static final String TABLE_NAME = "pedido";
	
	private static final String CONECTION = "jdbc:sqlite:ssii-psi5.db";
	
	private static final int RETRIEVE_MONTHS = 3; //The number of month (with the actual one) to get the percentages
	
	private static Connection conn = null;
	
	private static Connection getConnection() throws ClassNotFoundException, SQLException{
		if(conn==null){
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(CONECTION);
		}
		return conn;
	}
	
	public static void checkTable(){
		Connection conn;
		Statement stmt;
		ResultSet cursor;
		try{
			conn = getConnection();
			
			//We check if the table exist. If the table doesn't exist we create it.
			stmt = conn.createStatement();
			DatabaseMetaData md = conn.getMetaData();
			cursor = md.getTables(null, null, TABLE_NAME, null);//We check if our table exists
			
			LoggerUtil.getLogger().info(String.format("Checking if the table %s exists...", TABLE_NAME));
			
			//If cursor doesn't have next at 1st iteration means that the table desn't exist, so we have to create it.
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
	
	public static Integer insertIntoTable(String message, String signedMessage,byte[] encoded,Integer integrity){
		checkTable();
		
		Connection conn;
		PreparedStatement stmt;
		Integer result = null;
		try{
			conn = getConnection();
			LoggerUtil.getLogger().info("Inserting data in the Data Base...");
			
			String insert = String.format("INSERT INTO %s (insert_date, message, signed_message, encoded_key, integrity) VALUES (?,'%s','%s','%s','%s');", TABLE_NAME,message,signedMessage,encoded,integrity);
			System.out.println(insert);//TODO: Remove
			
			LoggerUtil.getLogger().debug("INSERT statement: "+insert);
			
			stmt = conn.prepareStatement(insert);//We prepare the statement to include parameters after this
			
			Date now = new Date(Calendar.getInstance().getTimeInMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sqlDate = sdf.format(now);
			stmt.setString(1, sqlDate);//The date is from java.sql.Date
			result = stmt.executeUpdate();
			
		}catch(Throwable oops){
			oops.printStackTrace();//TODO: remove
			LoggerUtil.getLogger().error("Error while trying to insert the data in the Data Base\n",oops);
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	public static Object[] getPercentages(){
		Connection conn;
		Statement stmt;
		Object[] result = new Object[3];
		ResultSet rs;
		
		//We retrieve the percentage and the date from the current, the past on and before the past one
		String query;
		
		try{
			LoggerUtil.getMailLogger().info("Atempting to retrieve the percentages from the Data Base");
			
			conn = getConnection();
			stmt = conn.createStatement();
			
			result[1]=0.0; //Initialice the percentages
			
			for(int i=0;i<RETRIEVE_MONTHS;i++){
				query = "select strftime('%Y-%m',insert_date), round(avg(integrity),4)*100 from pedido where strftime('%Y-%m',insert_date)=strftime('%Y-%m',date('now','start of month','-" + i + " month')) group by strftime('%Y-%m',insert_date);";
				
				//The percentage from the month
				rs = stmt.executeQuery(query);
				
				while(rs.next()){
					Double percentage = rs.getDouble(2);
					if(i==0){
						result[0] = percentage;
					}else{
						result[1]= (double) result[1]+percentage;
					}
				}
			}
			
			if(RETRIEVE_MONTHS>1){
				result[1] = (double) result[1]/(RETRIEVE_MONTHS-1*1.0); //Average percentage from previous months
				result[2] = RETRIEVE_MONTHS-1;
			}else{
				result[2] = 0;
			}
			
			LoggerUtil.getMailLogger().info("Percentages retrieved successfully.");
		}catch(Throwable oops){
			LoggerUtil.getMailLogger().error("There was a problem retrieving the percentages from the Data Base",oops);
		}
		
		return result;
	}

}
