import java.nio.channels.SocketChannel;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	String name;
	int id;
	byte[] image;
	boolean is_admin;
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
