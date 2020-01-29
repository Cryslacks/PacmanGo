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
			System.out.println("AccountHandler: ERR_SQL");
		}
		
			return false;
	
	}
	
	public static boolean createAccount(String username, String password, String hwid){
		ResultSet rs = db.query("SELECT * FROM users WHERE username = '"+username+"' AND password='"+password+"'");

		try {
			if(rs.next())
				return false;
			else {
				db.query("INSERT INTO users (username, password, hwid) VALUES ('"+username+"', '"+password+"', '"+hwid+"')");
				return true;
			}
		} catch (SQLException e) {
			System.out.println("AccountHandler: ERR_SQL");
			return false;
		}
	}
	public static void updateHwid(String username, String hwid){
		db.query("UPDATE users SET hwid='"+hwid+"' WHERE username='"+username+"'");
	}
	
	public static String getNameFromHWID(String hwid) {
		ResultSet rs = db.query("SELECT username FROM users WHERE hwid = '"+hwid+"'");		
		
		try {
			return rs.getString(0);
		} catch (SQLException e) {
			System.out.println("AccountHandler: ERR_SQL");
			return "ERR_NO_USER";
		}
	}
}
