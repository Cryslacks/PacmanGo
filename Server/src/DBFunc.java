import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * Provides a connection between the Java server and the SQL database.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */

public class DBFunc {
	private String url = "jdbc:mysql://cryslacks.win:3306/pacmanGO";
	private String uName = "racketkungen";
	private String pWord = "racketkungen";
	private Connection con;
	private Statement state;
	/**
	 * Creates a new connection to the database.
	 */
	public DBFunc(){
		try{
			Class.forName("com.mysql.jdbc.Driver");  
			this.con = DriverManager.getConnection(url,uName,pWord);
			state=con.createStatement();
			System.out.println("DBFunc: We are connected to the database located at cryslacks.win:3306");
		}catch(Exception e){
			
			System.out.println("Error connecting to DB");
			System.out.println(e);
		}
	}

	/**
	 * Queries the provided SQL command against the SQL database.
	 * @param query The string which contains the SQL query.
	 * @return A resultset containing information gathered by the provided SQL query.
	 */
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

