package serverModule;
import org.json.JSONException;
import org.json.JSONObject;

public class Track {
	String url;
	int trackId;					//unique to track, not to name !
	
	public Track(String url,int trackID) {
		super();
		this.trackId = trackID;
		this.url = url;
	}
	
	public JSONObject get_JSON() throws JSONException {
		JSONObject trackJson = new JSONObject();
		trackJson.put(jsonKey.URL.name(), this.url);
		trackJson.put(jsonKey.URL.name(), this.trackId);
		return trackJson;
	}
}

