import org.json.JSONException;
import org.json.JSONObject;

public class Command {
	CommandType cmd_type;
	JSONObject cmd_info;
	
	public Command(CommandType cmd_type) throws JSONException {
		
		this.cmd_type = cmd_type;
		this.cmd_info = new JSONObject();
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
	
	private JSONObject byteToJson(byte[] message) throws JSONException {
		return new JSONObject(new String(message));
	}
	

}
