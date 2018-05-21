import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Party {
	
	public enum Party_Status {
		not_started, preparing, playing, pause,
	}
	
	String party_name;
	int party_id;
	//Location location;
	Party_Status status;
	Playlist playlist;
	int numOfAdmins = 0;
	List<User> connected;
	List<User> new_clients;
	List<User> request; //only if private
	Selector selector; //for wakeUp
	boolean is_private;

	public Party(String party_name, int party_id, User admin, boolean is_private) throws IOException {
		super();
		this.party_name = party_name;
		this.party_id = party_id;
		this.status = Party_Status.not_started;
		this.playlist = new Playlist();
		connected = new ArrayList<>();
		request = new ArrayList<>();
		new_clients = new ArrayList<>();
		this.is_private = is_private;
		this.selector = Selector.open();
		addClient(admin);
		makeAdmin(admin);
	}

	public byte[] getPartyImage(){
		return connected.get(0).get_image();
	}
	//TODO
	public void UpdateLocation(){  //pings

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
	
	public void addClient(User user){
		user.currentPartyId = this.party_id;
		connected.add(user);
		user.is_admin = false;
	}
	
	public boolean removeClient(User user,boolean disconnected){
		if(!disconnected)
			user.currentPartyId = -1;
		
		disableAdmin(user);
		boolean response =  connected.remove(user);
		
		if(numOfAdmins == 0 && numOfClients() != 0) 
			makeAdmin(connected.get(0));
		return response;
	}

	public void addRequest(User user){
		request.add(user);
	}

	public boolean removeRequest(User user){
		return request.remove(user);
	}
	
	//handle locks.
	public void addNewClient(User user) {
		new_clients.add(user);
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

	public int get_current_track_id() {
		return playlist.get_current_track_id();
	}

	public void next_song() {
		// TODO Auto-generated method stub
		
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
		fullJson.put(jsonKey.NAME.name(), this.party_name);
		fullJson.put(jsonKey.IMAGE.name(), new String(this.getPartyImage()));
		fullJson.put(jsonKey.LOCATION.name(), 0);
		fullJson.put(jsonKey.SONGS.name(), playlist.getTrackArray());
		fullJson.put(jsonKey.IS_PRIVATE.name(), is_private);
		fullJson.put(jsonKey.USERS.name(), User.getUserArray(connected));
		fullJson.put(jsonKey.REQUESTS.name(), User.getUserArray(request));
		return fullJson;
	}
	
	

}
