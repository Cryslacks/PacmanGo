import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class DatabaseHandler{
	public static DBFunc db;
	
	public static boolean loginAccount(String username, String password, String hwid){
		ResultSet rs = db.query("SELECT * FROM users WHERE username = '"+username+"' AND password='"+password+"'");
		
		try {
			if(rs.next()){
				DatabaseHandler.updateHwid(username, hwid);
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
			rs.absolute(1);
			return rs.getString("username");
		} catch (SQLException e) {
			System.out.println("AccountHandler: ERR_SQL");
			return "ERR_NO_USER";
		}
	}
	public static MapData loadMap(int id){
		ResultSet rs = db.query("SELECT * FROM maps WHERE map_id = '"+id+"'");
		MapData mapData = null;

		try {
			while(rs.next()){
				
				String mapName = rs.getString("name");
				JSONObject data = new JSONObject(rs.getString("map_data"));
				
				mapData = new MapData(data, mapName);
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapData;
	}
	
	public static void loadCoin(){
		//DO we even NEED?
	}
	public static boolean saveMap(JSONObject j){
		ResultSet rs = db.query("SELECT * FROM maps WHERE name = '"+j.getString("MapName")+"'");
			
			try {
				if(rs.next())
					return false;
				else{
					String tempName = j.getString("MapName");
					j.remove("MapName");
					db.query("INSERT INTO maps (mapname, data) VALUES ('"+tempName+"', '"+j.toString()+"')");
					return true;
				}
			} catch (SQLException e) {
				return false;
			}
		
		
	}
	
	
		
}










