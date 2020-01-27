import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DBFunc {
	private String url = "jdbc:mysql://cryslacks.win:3306/pacmanGO";
	private String uName = "racketkungen";
	private String pWord = "racketkungen";
	private Connection con;
	private Statement state;
	public DBFunc(){
		try{
			Class.forName("com.mysql.jdbc.Driver");  
			this.con = DriverManager.getConnection(url,uName,pWord);
			state=con.createStatement();
			System.out.println("we are connected");
		}catch(Exception e){
			
			System.out.println("Error connecting to DB");
			System.out.println(e);
		}
	}
	
	public ResultSet query(String query) {
		try {
			if(query.toUpperCase().split("SELECT").length > 1){
				ResultSet rs = this.state.executeQuery(query);
				return rs;
			}else{
				
				this.state.executeUpdate(query);
				return null;
			}
		}catch(Exception e) {
		
			System.out.println(e);
			return null;
		}
	}
	

}

