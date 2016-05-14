package dsanchez.ssii.psi5;

public class SQLiteTest {
	
	public static void main(String[] args){
		
		try{
			
			SQLiteUtil.checkTable();
			
		}catch(Throwable oops){
			oops.printStackTrace();
		}
		
	}

}
