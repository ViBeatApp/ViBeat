import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
		userJson.put(jsonKey.NAME.name(), this.name);
		userJson.put(jsonKey.USER_ID.name(), this.id);
		userJson.put(jsonKey.IMAGE.name(), this.image);
		return userJson;
	}
	
	public static JSONArray getUserArray(List<User> connected) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for(User user : connected) {
			jsonArray.put(user.get_JSON());
		}
		return jsonArray;
	}
}
