import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Command {
	CommandType cmd_type;
	JSONObject cmd_info;
	
	public Command(CommandType cmd_type) throws JSONException {
		this.cmd_type = cmd_type;
		this.cmd_info = new JSONObject();
	}
	
	public Command(CommandType cmd_type, JSONObject obj) {
		this.cmd_type = cmd_type;
		this.cmd_info = obj;
	}
	
	public Command(byte[] message) throws JSONException {
		if(message == null){
			this.cmd_type = CommandType.DISCONNECTED;
			return;
		}
		JSONObject json = byteToJson(message);
		this.cmd_type = CommandType.valueOf(json.getString(jsonKey.COMMAND_TYPE.name()));
		this.cmd_info = json.getJSONObject(jsonKey.COMMAND_INFO.name());
	}

	public byte[] commandTobyte() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(jsonKey.COMMAND_TYPE.name(), this.cmd_type);
		json.put(jsonKey.COMMAND_INFO.name(), this.cmd_info);
		return json.toString().getBytes();
	}
	
	public void printCommand() {
		System.out.println("command " + this.cmd_type + ", info" + this.cmd_info);
	}
	
	private JSONObject byteToJson(byte[] message) throws JSONException {
		return new JSONObject(new String(message));
	}
	
	public void setAttribute(String key, String string) throws JSONException {
		this.cmd_info.put(key,string);
	}
	
	public void setAttribute(String key, int num) throws JSONException {
		this.cmd_info.put(key,num);
	}
	
	public void setAttribute(String key, boolean bool) throws JSONException {
		this.cmd_info.put(key,bool);
	}
	
	public String getStringAttribute(String key) throws JSONException {
		return this.cmd_info.getString(key);
	}
	
	public int getIntAttribute(String key) throws JSONException {
		return this.cmd_info.getInt(key);
	}
	
	public boolean getBoolAttribute(String key) throws JSONException {
		return this.cmd_info.getBoolean(key);
	}
	
	public JSONArray getUpdatePartyAttribute(String key) throws JSONException {
		return cmd_info.getJSONArray(key);
	}
}
