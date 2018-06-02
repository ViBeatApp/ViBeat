package serverObjects;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Party {
	
	public enum Party_Status {
		not_started, preparing, playing, pause,
	}
	
	public String party_name;
	public int party_id;
	public Location location;
	public Party_Status status;
	public Playlist playlist;
	public int numOfAdmins = 0;
	public List<User> connected;
	public List<User> waitingClients;
	public List<User> request; //only if private
	public Selector selector; //for wakeUp
	public boolean is_private;
	public boolean keep_on;
	
	public Party(String party_name, int party_id, User admin, boolean is_private) throws IOException {
		super();
		this.party_name = party_name;
		this.party_id = party_id;
		this.status = Party_Status.not_started;
		this.playlist = new Playlist();
		this.connected = new ArrayList<>();
		this.request = new ArrayList<>();
		this.waitingClients = Collections.synchronizedList(new ArrayList<User>());
		this.is_private = is_private;
		this.selector = Selector.open();
		this.keep_on = true;
		this.location = admin.location;
		addClient(admin);
		makeAdmin(admin);
	}

	public String getPartyImage(){
		return connected.get(0).get_image();
	}
	//TODO
	public void UpdateLocation(Location location){ 
		synchronized(this.location) {
			this.location = location;
		}
	}
	
	public Location get_Location(){ 
		synchronized(this.location) {
			return this.location;
		}
	}
	
	public User getAdmin() {
		if (this.connected.size() > 0)
			return this.connected.get(0);
		System.out.println("error getAdmin(0) - party.");
		return null;
	}

	public void makeAdmin(User user){
		if(!user.is_admin) {
			user.is_admin = true;
			++numOfAdmins;
		}	
	}
	
	public void disableAdmin(User user){
		if(user.is_admin) {
			user.is_admin = false;
			--numOfAdmins;
		}	
	}
	
	public boolean nonEmptyPlaylist(){
		return this.get_current_track_id() != -1;
	}
	
	public void addClient(User user){
		user.currentPartyId = this.party_id;
		connected.add(user);
		user.is_admin = false;
	}
	
	public void removeClient(User user, boolean disconnected){
		if(!disconnected) {
			user.currentPartyId = -1;
		}
		
		disableAdmin(user);
		connected.remove(user);
		
		if(numOfAdmins == 0 && numOfClients() != 0) {
			makeAdmin(connected.get(0));
		}
	}

	public void addRequest(User user){
		request.add(user);
	}

	public boolean removeRequest(User user){
		return request.remove(user);
	}
	
	// new_clients is a synchronized object
	public void addWaitingClient(User user) {
		waitingClients.add(user);
	}
	
	public int numOfClients() {
		return connected.size();
	}

	public Track addSong(String url){ 		
		return playlist.addSong(url);
	}
	
	public int deleteSong(int trackID){
		return playlist.deleteSong(trackID);
	}
	
	public int changeSongsOrder(int trackID_1, int trackID_2){	
		return playlist.changeSongsOrder(trackID_1,trackID_2);
	}
	
	public int get_playlist_size() {
		return playlist.get_list_size();
	}
	
	public int get_current_track_id() {
		return playlist.get_current_track_id();
	}

	public void setCurrentTrack(int trackId) {
		playlist.setCurrentTrack(trackId);
		
	}

	public JSONObject getPublicJson() throws JSONException{
		JSONObject publicJson = new JSONObject();
		publicJson.put(jsonKey.NAME.name(), this.party_name);
		publicJson.put(jsonKey.PARTY_ID.name(), this.party_id);
		publicJson.put(jsonKey.IMAGE.name(), new String(this.getPartyImage()));
		return publicJson;
	}
	
	public JSONObject getFullJson() throws JSONException{
		JSONObject fullJson = new JSONObject();
		fullJson.put(jsonKey.NAME.name(), new JSONArray().put(party_name));
		fullJson.put(jsonKey.IMAGE.name(), new JSONArray().put(this.getPartyImage()));
		fullJson.put(jsonKey.SONGS.name(), playlist.getTrackArray());
		fullJson.put(jsonKey.IS_PRIVATE.name(), new JSONArray().put(is_private));
		fullJson.put(jsonKey.USERS.name(), User.getUserArray(connected));
		fullJson.put(jsonKey.REQUESTS.name(), User.getUserArray(request));
		fullJson.put(jsonKey.CURRENT_TRACK_ID.name(), new JSONArray().put(this.get_current_track_id()));
		return fullJson;
	}

	

}
