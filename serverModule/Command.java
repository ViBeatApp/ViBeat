import org.json.JSONException;
import org.json.JSONObject;

public class Command {
	CommandType cmd_type;
	JSONObject cmd_info;


	public Command(byte[] message) throws JSONException {
		if(message == null){
			this.cmd_type = CommandType.Disconnected;
			return;
		}
		JSONObject json = CommandToJson(message);
		this.cmd_type = CommandType.valueOf(json.getString("Command"));
		this.cmd_info = json.getJSONObject("Info");
	}

	public JSONObject CommandToJson(byte[] message) throws JSONException {
		return new JSONObject(new String(message));

	}

}
