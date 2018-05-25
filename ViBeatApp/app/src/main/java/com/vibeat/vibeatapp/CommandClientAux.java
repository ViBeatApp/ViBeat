package com.vibeat.vibeatapp;

import com.vibeat.vibeatapp.Objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CommandClientAux {

	public static JSONArray getSyncPartyAttribute(Command cmd, jsonKey key) throws JSONException {
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
		default:
			break;
		}
		return result;
	}
	
	private static JSONArray createUserArray(Command cmd) throws JSONException {
		JSONArray userArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.REQUESTS);
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
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject songJsonObject = objectArray.getJSONObject(i);
			int trackId = songJsonObject.getInt(jsonKey.TRACK_ID.name());
			String path = songJsonObject.getString(jsonKey.URL.name());	
			songArray.put(new song(trackId,path));
		}
		return songArray;
	}
	
	private static JSONArray createRequestUserArray(Command cmd) throws JSONException {
		JSONArray userArray = new JSONArray();
		JSONArray objectArray = cmd.getSyncPartyAttribute(jsonKey.REQUESTS);
		for(int i = 0; i < objectArray.length(); ++i) {
			JSONObject userJsonObject = objectArray.getJSONObject(i);
			String name = userJsonObject.getString(jsonKey.NAME.name());
			String path = userJsonObject.getString(jsonKey.IMAGE.name());
			int id = userJsonObject.getInt(jsonKey.USER_ID.name());		
			userArray.put(new User(name,path,id, false));
		}
		return userArray;
	}
}

