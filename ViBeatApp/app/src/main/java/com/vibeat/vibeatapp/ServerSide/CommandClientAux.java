package com.vibeat.vibeatapp.ServerSide;
import android.util.Log;

import com.vibeat.vibeatapp.Objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandClientAux {

	public static JSONArray getPartyArray(Command cmd) throws JSONException {
		if(cmd.cmd_type != CommandType.SEARCH_RESULT)
			return null;
		JSONArray partyArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.RESULT);
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject partyJsonObject = objectArray.getJSONObject(i);

			String name = partyJsonObject.getString(jsonKey.NAME.name());
			String image = partyJsonObject.getString(jsonKey.IMAGE.name());
			int id = partyJsonObject.getInt(jsonKey.PARTY_ID.name());

			partyArray.put(new partyInfo(name,image,id));
		}
		return partyArray;
	}

	public static JSONArray getSyncPartyAttribute(Command cmd, jsonKey key) throws JSONException {
		if(cmd.cmd_type != CommandType.SYNC_PARTY)
			return null;
		JSONArray result = null;
		switch(key) {
			case USERS:
				result = createUserArray(cmd);
				break;
			case IMAGE:
				result = cmd.getSyncPartyAttribute(jsonKey.IMAGE);
				break;
			case LOCATION:
				result = cmd.getSyncPartyAttribute(jsonKey.LOCATION);
				break;
			case REQUESTS:
				result = createRequestUserArray(cmd);
				break;
			case SONGS:
				result = createSongsArray(cmd);
				break;
			case NAME:
				result = cmd.getSyncPartyAttribute(jsonKey.NAME);
				break;
			case IS_PRIVATE:
				result = cmd.getSyncPartyAttribute(jsonKey.IS_PRIVATE);
				break;
			case CURRENT_TRACK_ID:
				result = cmd.getSyncPartyAttribute(jsonKey.CURRENT_TRACK_ID);
				break;
			case PARTY_PLAYING:
				result = cmd.getSyncPartyAttribute(jsonKey.PARTY_PLAYING);
				break;
			case CHANGES:
				result = cmd.getSyncPartyAttribute(jsonKey.CHANGES);
				break;
			default:
				break;
		}
		return result;
	}

	private static JSONArray createUserArray(Command cmd) throws JSONException {
		JSONArray userArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.USERS);
		if(objectArray == null)
			return null;
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject userJsonObject = objectArray.getJSONObject(i);

			String name = userJsonObject.getString(jsonKey.NAME.name());
			String path = userJsonObject.getString(jsonKey.IMAGE.name());
			int id = userJsonObject.getInt(jsonKey.USER_ID.name());
			boolean is_admin = userJsonObject.getBoolean(jsonKey.IS_ADMIN.name());

			userArray.put(new User(name,path,id,is_admin));
		}
		return userArray;
	}

	private static JSONArray createSongsArray(Command cmd) throws JSONException {
		JSONArray songArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.SONGS);
		if(objectArray == null)
			return null;
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject songJsonObject = objectArray.getJSONObject(i);
			int trackId = songJsonObject.getInt(jsonKey.TRACK_ID.name());
			String db_id = songJsonObject.getString(jsonKey.DB_ID.name());
			songArray.put(new trackInfo(trackId,db_id));
		}
		return songArray;
	}

	private static JSONArray createRequestUserArray(Command cmd) throws JSONException {
		JSONArray userArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.REQUESTS);
		if(objectArray == null)
			return null;
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject userJsonObject = objectArray.getJSONObject(i);
			String name = userJsonObject.getString(jsonKey.NAME.name());
			String path = userJsonObject.getString(jsonKey.IMAGE.name());
			int id = userJsonObject.getInt(jsonKey.USER_ID.name());
			userArray.put(new User(name,path,id,false));
		}
		return userArray;
	}
}