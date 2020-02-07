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
	public static MapValues loadMap(int id){
		ResultSet rs = db.query("SELECT * FROM maps WHERE map_id = '"+id+"'");
		MapValues mapInfo = null;
		int[][] sendAdj;
		try {
			while(rs.next()){
				
				String mapName = rs.getString("name");
				JSONObject j = new JSONObject(rs.getString("pos_mat"));   //>>>>>>ÄNDRA TIL MAP INFO<<<<<
				Coordinate[] coords = new Coordinate[j.getJSONArray("pos_mat").length()];
				for(int i = 0; i < j.getJSONArray("pos_mat").length(); i++) {
		            coords[i] = new Coordinate(j.getJSONArray("pos_mat").getJSONObject(i).getJSONArray("coord").getDouble(0),j.getJSONArray("pos_mat").getJSONObject(i).getJSONArray("coord").getDouble(0));
		            if(ServerFunc.debugMode){
		            	System.out.println("LoadMap: coord_nr "+i+":");
		            	System.out.println("\t x="+coords[i].getCoord()[0]);            
			            System.out.println("\t y="+coords[i].getCoord()[1]);
					
		            }
		            
				}
				sendAdj = new int[j.getJSONArray("pos_mat").length()][j.getJSONArray("pos_mat").length()];
				for(int i = 0; i < j.getJSONArray("adj_mat").length(); i++) {
					for(int q = 0; q < j.getJSONArray("adj_mat").getJSONArray(i).length(); q++) {
						sendAdj[i][q] = j.getJSONArray("adj_mat").getJSONArray(i).getInt(q);
					}
					
					
		            if(ServerFunc.debugMode){
		            	System.out.println("LoadMap: coord_nr "+i+":");
		            	System.out.println("\t x="+coords[i].getCoord()[0]);            
			            System.out.println("\t y="+coords[i].getCoord()[1]);
					
		            }
		            
				}
				mapInfo = new MapValues(mapName, sendAdj, coords);
				
				
		
			}
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapInfo;
	
	}
	public static void loadCoin(){
		
	}
	public static void saveMap(String name, double[] adj_mat, double[] adj_pos){
		//query(INSERT .. .. .. INTO maps) pro tip arrays.toString();
	}
	
	
		
}










