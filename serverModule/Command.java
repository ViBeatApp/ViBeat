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
	
	public void setAttribute(String key, JSONArray resultArray) throws JSONException {
		this.cmd_info.put(key,resultArray);
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
	
	public JSONArray getSyncPartyAttribute(String key) throws JSONException {
		return cmd_info.getJSONArray(key);
	}
	public static Command get_authentication_command(String name,int userID,byte[] image) throws JSONException {
		Command cmd = new Command(CommandType.AUTHENTICATION);
		cmd.setAttribute(jsonKey.NAME.name(), name);
		cmd.setAttribute(jsonKey.USER_ID.name(), userID);
		cmd.setAttribute(jsonKey.IMAGE.name(), new String(image));
		return cmd;
	}
	public static Command get_nearbyParties_Command(int Location) throws JSONException {
		Command cmd = new Command(CommandType.NEARBY_PARTIES);
		cmd.setAttribute(jsonKey.LOCATION.name(), Location);
		return cmd;
	}
	public static Command get_searchParty_Command(String name) throws JSONException {
		Command cmd = new Command(CommandType.SEARCH_PARTY);
		cmd.setAttribute(jsonKey.NAME.name(), name);
		return cmd;
	}
	
	public static Command get_join_Command(int partyID) throws JSONException {
		Command cmd = new Command(CommandType.JOIN);
		cmd.setAttribute(jsonKey.PARTY_ID.name(), partyID);
		return cmd;
	}
	
	public static Command get_create_Command(String name,boolean isPrivate) throws JSONException {
		Command cmd = new Command(CommandType.CREATE);
		cmd.setAttribute(jsonKey.NAME.name(), name);
		cmd.setAttribute(jsonKey.IS_PRIVATE.name(), isPrivate);
		return cmd;
	}
	
	public static Command get_addSons_Command(String url) throws JSONException {
		Command cmd = new Command(CommandType.ADD_SONG);
		cmd.setAttribute(jsonKey.URL.name(), url);
		return cmd;
	}
	public static Command get_deleteSong_Command(int trackId) throws JSONException {
		Command cmd = new Command(CommandType.DELETE_SONG);
		cmd.setAttribute(jsonKey.TRACK_ID.name(), trackId);
		return cmd;
	}
	public static Command get_swapSongs_Command(int trackId_1,int trackId_2) throws JSONException {
		Command cmd = new Command(CommandType.SWAP_SONGS);
		cmd.setAttribute(jsonKey.TRACK_ID_1.name(), trackId_1);
		cmd.setAttribute(jsonKey.TRACK_ID_2.name(), trackId_2);
		return cmd;
	}
	public static Command get_playSong_Command(int trackId,int offset) throws JSONException {
		Command cmd = new Command(CommandType.PLAY_SONG);
		cmd.setAttribute(jsonKey.TRACK_ID.name(), trackId);
		cmd.setAttribute(jsonKey.OFFSET.name(), offset);
		return cmd;
	}
	public static Command get_imReady_Command(int trackId) throws JSONException {
		Command cmd = new Command(CommandType.IM_READY);
		cmd.setAttribute(jsonKey.TRACK_ID.name(), trackId);
		return cmd;
	}
	public static Command get_pause_Command(int trackId) throws JSONException {
		Command cmd = new Command(CommandType.PAUSE);
		cmd.setAttribute(jsonKey.TRACK_ID.name(), trackId);
		return cmd;
	}
	
	public static Command get_makePrivate_Command(boolean isPrivate) throws JSONException {
		Command cmd = new Command(CommandType.MAKE_PRIVATE);
		cmd.setAttribute(jsonKey.IS_PRIVATE.name(), isPrivate);
		return cmd;
	}
	
	public static Command get_renameParty_Command(String name) throws JSONException {
		Command cmd = new Command(CommandType.RENAME_PARTY);
		cmd.setAttribute(jsonKey.NAME.name(), name);
		return cmd;
	}
	public static Command get_confirmRequest_Command(int userId,boolean confirmed) throws JSONException {
		Command cmd = new Command(CommandType.CONFIRM_REQUEST);
		cmd.setAttribute(jsonKey.USER_ID.name(), userId);
		cmd.setAttribute(jsonKey.CONFIRMED.name(), confirmed);
		return cmd;
	}
	public static Command get_makeAdmin_Command(int userId) throws JSONException {
		Command cmd = new Command(CommandType.MAKE_ADMIN);
		cmd.setAttribute(jsonKey.USER_ID.name(), userId);
		return cmd;
	}
	public static Command get_updateLocation_Command(int Location) throws JSONException {
		Command cmd = new Command(CommandType.UPDATE_LOCATION);
		cmd.setAttribute(jsonKey.LOCATION.name(), Location);
		return cmd;
	}
	public static Command get_closeParty_Command(int Location) throws JSONException {
		Command cmd = new Command(CommandType.CLOSE_PARTY);
		return cmd;
	}
	public static Command get_leaveParty_Command(int Location) throws JSONException {
		Command cmd = new Command(CommandType.LEAVE_PARTY);
		return cmd;
	}
	
	public static Command get_syncParty_Command(JSONObject partyInfo) throws JSONException {
		Command cmd = new Command(CommandType.SYNC_PARTY,partyInfo);
		return cmd;
	}
	
	public static Command get_rejected_Command() throws JSONException {
		Command cmd = new Command(CommandType.REJECTED);
		return cmd;
	}

	public static Command get_searchResult_command(JSONArray resultArray) throws JSONException {
		Command cmd = new Command(CommandType.SEARCH_RESULT);
		cmd.setAttribute(jsonKey.RESULT.name(), resultArray);
		return cmd;
	}
}
