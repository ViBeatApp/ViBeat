import org.json.JSONException;
import org.json.JSONObject;

public class Track {
	String name;
	String image_url;
	String Url;
	double duration;
	int trackId;					//unique to track, not to name !
	
	public Track(String name) {
		super();
		this.name = name;
	}
	
	public JSONObject get_JSON() throws JSONException {
		JSONObject trackJson = new JSONObject();
		trackJson.put("url", this.Url);
		trackJson.put("trackId", this.trackId);
		return trackJson;
	}
}

