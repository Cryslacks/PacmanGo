
public class Coordinate {
	private static final double RADIUS = 6378137.0;
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
	public void setCoord(double lon, double lat){
		this.lon = lon;
		this.lat = lat;
	}
	
	public double[] toMeters() {
		double[] temp = {Math.toRadians(this.lon) * RADIUS, Math.log(Math.tan(Math.PI / 4 + Math.toRadians(this.lat) / 2)) * RADIUS};
		return temp;
	}
}
