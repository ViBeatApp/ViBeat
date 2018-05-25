package serverModule;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	String name;
	int id;
	String image;
	boolean is_admin;
	SocketChannel channel;
	int currentPartyId = -1;				//if isn't part of some party so value = -1
	
	public User(String name, int id, String image,SocketChannel channel) {
		super();
		this.name = name;
		this.image = image;
		this.id = id;
		this.channel = channel;
		this.is_admin = false;
	}
	
	
	public String get_image() {
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
		userJson.put(jsonKey.IS_ADMIN.name(), this.is_admin);
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
