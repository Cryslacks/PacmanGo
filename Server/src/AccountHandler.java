import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountHandler{
	public static DBFunc db;
	
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
	
	public static boolean createAccount(String username, String password, String hwid){
		ResultSet rs = db.query("SELECT * FROM users WHERE username = '"+username+"' AND password='"+password+"'");

		try {
			if(rs.next())
				return false;
//				System.out.println("USER ALREADY EXIST");
			else {
				db.query("INSERT INTO users (username, password, hwid) VALUES ('"+username+"', '"+password+"', '"+hwid+"')");
				return true;
			}
		} catch (SQLException e) {
			System.out.println("SQL ERROR");
			return false;
		}
	}
	public static void updateHwid(String username, String hwid){
		db.query("UPDATE users SET hwid='"+hwid+"' WHERE username='"+username+"'");
	}
}
