package serverObjects;
import org.json.JSONException;
import org.json.JSONObject;

public class Track {
	String database_id;
	int trackId;					//unique to track, not to name !
	
	public Track(String database_id,int trackID) {
		super();
		this.trackId = trackID;
		this.database_id = database_id;
	}
	
	public JSONObject get_JSON() throws JSONException {
		JSONObject trackJson = new JSONObject();
		trackJson.put(jsonKey.DB_ID.name(), this.database_id);
		trackJson.put(jsonKey.TRACK_ID.name(), this.trackId);
		return trackJson;
	}
}

