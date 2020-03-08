
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
		// https://www.codeproject.com/Questions/626899/Converting-Latitude-And-Longitude-to-an-X-Y-Coordi
		double[] temp = {	RADIUS * Math.cos(this.lat) * Math.cos(this.lon),
							RADIUS * Math.cos(this.lat) * Math.sin(this.lon),
							RADIUS * Math.sin(this.lat) };
		return temp;
	}

	public double distanceTo(Coordinate c) {
		double[] a = this.getCoord();
		double[] b = c.getCoord();
	
		
		return Math.sqrt(Math.pow(a[0]-b[0],2) + Math.pow(a[1]-b[1],2));
	}
	
	public double distanceToM(Coordinate c) {
		//Borrowed from https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
		
		double lat1 = this.getCoord()[1];
		double lng1 = this.getCoord()[0];
		double lat2 = c.getCoord()[1];
		double lng2 = c.getCoord()[0];
		
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLng/2) * Math.sin(dLng/2);
		double cg = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		float dist = (float) (RADIUS * cg);
		return dist;
	}
	
	public boolean collideArea(Coordinate a, Coordinate b, int radius) {
		// Borrowed from: http://mathworld.wolfram.com/Circle-LineIntersection.html
		
		// FIXA DETTA SEN FÖR I HELVETE
		
		
		double d_r = Math.sqrt(Math.pow(a.getCoord()[0]-b.getCoord()[0],2) + Math.pow(a.getCoord()[1]-b.getCoord()[1],2));
		double D = a.getCoord()[0]*b.getCoord()[1]-b.getCoord()[0]*a.getCoord()[1];
		
		double discr = Math.pow(radius, 2)*Math.pow(d_r, 2)-Math.pow(D, 2);
		
		if(discr >= 0)
			return true;

		return false;
	}
	
}
