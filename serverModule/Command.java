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
			this.cmd_type = CommandType.Disconnected;
			return;
		}
		JSONObject json = byteToJson(message);
		this.cmd_type = CommandType.valueOf(json.getString(jsonKey.COMMAND_TYPE.getCommandString()));
		this.cmd_info = json.getJSONObject(jsonKey.COMMAND_INFO.getCommandString());
	}

	public JSONObject byteToJson(byte[] message) throws JSONException {
		return new JSONObject(new String(message));
	}
	
	public byte[] commandTobyte() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("command", this.cmd_type);
		json.put("info", this.cmd_info);
		return json.toString().getBytes();
	}
	

}
