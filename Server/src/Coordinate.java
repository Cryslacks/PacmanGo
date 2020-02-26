
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
	
	public double distanceTo(Coordinate c) {
		double[] a = this.toMeters();
		double[] b = c.toMeters();
		
		
		return Math.sqrt(Math.pow(a[0]-b[0],2) + Math.pow(a[1]-b[1],2));
	}
	
	public boolean collideArea(Coordinate a, Coordinate b, int radius) {
		// TAKEN FROM: http://mathworld.wolfram.com/Circle-LineIntersection.html
		
		double d_r = Math.sqrt(Math.pow(a.getCoord()[0]-b.getCoord()[0],2) + Math.pow(a.getCoord()[1]-b.getCoord()[1],2));
		double D = a.getCoord()[0]*b.getCoord()[1]-b.getCoord()[0]*a.getCoord()[1];
		
		double discr = Math.pow(radius, 2)*Math.pow(d_r, 2)-Math.pow(D, 2);
		
		if(discr >= 0)
			return true;

		return false;
	}
	
}
