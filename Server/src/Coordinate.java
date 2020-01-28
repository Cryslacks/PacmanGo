
public class Coordinate {
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
	public void setLon(double lon, double lat){
		this.lon = lon;
		this.lat = lat;
	}
}
