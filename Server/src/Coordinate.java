
public class Coordinate {
<<<<<<< refs/remotes/origin/Efiila
	private static final double RADIUS = 6378137.0;
=======
>>>>>>> Player object and value handling
	private double lon;
	private double lat;

	public Coordinate(double lon, double lat){
		this.lon = lon;
		this.lat = lat;
	}
	
	public double[] getCoord(){
		double[] temp = {this.lat, this.lon};
		return temp;
	}
<<<<<<< refs/remotes/origin/Efiila
	public void setCoord(double lon, double lat){
		this.lon = lon;
		this.lat = lat;
	}
	
	public double[] toMeters() {
		double[] temp = {Math.toRadians(this.lon) * RADIUS, Math.log(Math.tan(Math.PI / 4 + Math.toRadians(this.lat) / 2)) * RADIUS};
		return temp;
	}
=======
	public void setLon(double lon, double lat){
		this.lon = lon;
		this.lat = lat;
	}
>>>>>>> Player object and value handling
}
