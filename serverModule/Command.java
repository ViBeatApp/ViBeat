public class Command {
	CommandType cmd_type;
	String cmd_info;


	public Command(byte[] message) {
		if(message == null){
			this.cmd_type = CommandType.disconnected;
			return;
		}
		JSONObject json = CommandToJson(message);
		this.cmd_type = CommandType.valueOf(json['command']);
	}

	public JSONObject CommandToJson(byte[] message) {
		return cmd_info;

	}

}
