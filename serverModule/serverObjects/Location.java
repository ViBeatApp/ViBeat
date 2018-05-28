package serverObjects;

import org.json.JSONException;

public class Location {
	public double longitude;
	public double latitude;
	public double altitude;
	
	public Location(double longitude,double latitude,double altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.latitude = altitude;
	}
	public Location(Command cmd) throws JSONException {
		this.longitude = cmd.getDoubleAttribute(jsonKey.LONGTITUDE);
		this.latitude = cmd.getDoubleAttribute(jsonKey.LATITUDE);
		this.altitude = cmd.getDoubleAttribute(jsonKey.ALTITUDE);
	}
	
}
