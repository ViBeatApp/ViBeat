import java.nio.channels.SocketChannel;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	public enum Party_Status {
		in_party, havnt_decided, waiting_for_approval
	}
	public enum Song_preparation {
		prepared, not_prepared
	}
	String name;
	int id;
	byte[] image;
	boolean is_admin;
	Party_Status party_status; /* waiting for approval or in party */
	Song_preparation is_prepared;
	SocketChannel channel;
	
	public User(String name, int id, byte[] image,SocketChannel channel) {
		super();
		this.name = name;
		this.image = image;
		this.id = id;
		this.channel = channel;
	}
	
	
	public byte[] get_image() {
		return image;
	}
	
	public SocketChannel get_channel() {
		return channel;
	}
	
	public JSONObject get_JSON() throws JSONException {
		JSONObject userJson = new JSONObject();
		userJson.put("name", this.name);
		userJson.put("id", this.id);
		userJson.put("image", this.image);
		return userJson;
	}

}
