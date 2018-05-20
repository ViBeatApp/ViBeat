import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.nio.channels.Selector;

public class Party {
	
	public enum Party_Status {
		notPlaying, preparing, isPlaying
	}
	
	String party_name;
	int party_id;
	//Location location;
	Party_Status status;
	Playlist playlist;
	List<User> admins;
	List<User> connected;
	List<User> new_clients;
	List<User> request; //only if private
	Selector selector; //for wakeUp
	boolean is_private;

	public Party(String party_name, int party_id, User admin, boolean is_private) throws IOException {
		super();
		this.party_name = party_name;
		this.party_id = party_id;
		this.status = Party_Status.notPlaying;
		this.playlist = new Playlist();
		admins = new ArrayList<>();
		connected = new ArrayList<>();
		request = new ArrayList<>();
		this.is_private = is_private;
		this.selector = Selector.open();
		this.admins.add(admin);
		this.connected.add(admin);
	}

	public void getPartyImage(){
		admins.get(0).get_image();
	}
	//TODO
	public void UpdateLocation(){  //pings

	}

	public void addAdmin(User user){
		admins.add(user);
	}

	public void addClient(User user){
		connected.add(user);
	}
	
	//handle locks.
	public void addNewClient(User user) {
		new_clients.add(user);
	}
	
	public void addRequest(User user){
		request.add(user);
	}

	public int comfirmedRequest(User user){
		int index = request.indexOf(user);
		if(index == -1){
			return -1;
		}
		connected.add(user);
		request.remove(index);
		return 0;
	}

	public int rejectedRequest(User user){
		int index = request.indexOf(user);
		if(index == -1){
			return -1;
		}
		request.remove(index);
		return 0;
	}

	public Track addSong(String name){ 		
		return playlist.addSong(name);
	}
	
	public int deleteSong(int trackID){
		return playlist.deleteSong(trackID);
	}
	
	public int changeSongsOrder(int trackID_1, int trackID_2){	
		return playlist.changeSongsOrder(trackID_1,trackID_2);
	}
	

	public int get_current_track_id() {
		return playlist.songs.get(0).trackId;
	}

	public void next_song() {
		// TODO Auto-generated method stub
		
	}
}
