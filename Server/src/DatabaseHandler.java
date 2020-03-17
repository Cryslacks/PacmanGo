import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import javafx.util.Pair;
/**
 * Provides functions for easier use of SQL commands.
 * @author David Eriksson
 * @author Fredrik Lindahl
 */
public class DatabaseHandler{
	public static DBFunc db;

	/**
	 * Checks if the specified user credentials is valid.
	 * @param username The username of the specified account.
	 * @param password The password of the specified account.
	 * @param hwid The hardware id of the specified account. 
	 * @return Boolean which if the provided credentials are valid.
	 */
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
	
	/**
	 * Creates an account with the specified credentials.
	 * @param username The username of the new account.
	 * @param password The password of the new account.
	 * @param hwid The hardware id of the new account. 
	 * @return Boolean if the specified credentials doesn't already exist.
	 */
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
	
	/**
	 * Updates the current hardware id of a specified user.
	 * @param username The username of the specified account.
	 * @param hwid The new hardware id which the specified account will be changed to.
	 */
	public static void updateHwid(String username, String hwid){
		db.query("UPDATE users SET hwid='null' WHERE hwid='"+hwid+"'");
		db.query("UPDATE users SET hwid='"+hwid+"' WHERE username='"+username+"'");
	}
	
	/**
	 * Gets the name from a provided hardware id.
	 * @param hwid The hardware id which the name is to be checked.
	 * @return The name of the account which had the specified hardware id.
	 */
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
	/**
	 * Gets all the maps which are located in the database.
	 * @return A pair list containing pairs of map id and map name.
	 */
	public static Pair<Integer, String>[] getMaps(){
		ResultSet rs = db.query("SELECT COUNT(*) as size FROM maps");
		int size = 0;

		try {
			rs.next();
			size = rs.getInt("size");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
				
		rs = db.query("SELECT * FROM maps");
		Pair<Integer, String>[] pIS = null;

		try {
			pIS = new Pair[size];
			System.out.println(pIS.length);
			int i = 0;
			while(rs.next() && i < size){	
				String name = rs.getString("map_name");
				int id = rs.getInt("map_id");
				
				pIS[i] = new Pair<Integer, String>(id, name);
				i++;
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pIS;
	}
	
	/**
	 * Fetches the mapdata located on the database.
	 * @param id The id of the map which is to be fetched.
	 * @return MapData containing the map which is fetched.
	 */
	public static MapData loadMap(int id){
		ResultSet rs = db.query("SELECT * FROM maps WHERE map_id = '"+id+"'");
		MapData mapData = null;

		try {
			while(rs.next()){
				
				String mapName = rs.getString("map_name");
				JSONObject data = new JSONObject(rs.getString("map_data"));
				
				mapData = new MapData(data, mapName);
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapData;
	}
	
	/**
	 * Saves a new map onto the database.
	 * @param j A JSONObject which contains data regarding the new map.
	 * @return A boolean if the map was successfully saved.
	 */
	public static boolean saveMap(JSONObject j){
		ResultSet rs = db.query("SELECT * FROM maps WHERE map_name = '"+j.getString("MapName")+"'");
			
			try {
				if(rs.next())
					return false;
				else{
					String tempName = j.getString("MapName");
					j.remove("MapName");
					db.query("INSERT INTO maps (map_name, map_data) VALUES ('"+tempName+"', '"+j.toString()+"')");
					return true;
				}
			} catch (SQLException e) {
				return false;
			}
		
		
	}
	
	
		
}










