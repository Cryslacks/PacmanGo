import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountHandler{
	private static DBFunc db = new DBFunc();
	
	public static boolean loginAccount(String username, String password, String hwid){
		ResultSet rs = db.query("SELECT * FROM users WHERE username = '"+username+"' AND password='"+password+"'");
		
		try {
			if(rs.next()){
				AccountHandler.updateHwid(username, hwid);
				return true;
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR");
		}
		
			return false;
	
	}
	
	public static void createAccount(String username, String password, String hwid){
		ResultSet rs = db.query("SELECT * FROM users WHERE username = '"+username+"' AND password='"+password+"'");

		try {
			if(rs.next())
				System.out.println("USER ALREADY EXIST");
			else
				db.query("INSERT INTO users (username, password, hwid) VALUES ('"+username+"', '"+password+"', '"+hwid+"')");
		} catch (SQLException e) {
			System.out.println("SQL ERROR");
		}
	}
	public static void updateHwid(String username, String hwid){
		db.query("INSERT INTO users (hwid) VALUES ('"+hwid+"') WHERE username='"+username+"'");
	}
	
	
	public static void main(String[] args){
	
	
		AccountHandler.createAccount("kiss", "hej", "hhhhhhh");
		
	}
	

}
