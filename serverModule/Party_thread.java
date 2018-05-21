import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Party_thread implements Runnable {

	public Party party;
	public Selector server_selector;

	public Command get_ready_command;
	public Command play_command;
	public Command pause_command;
	public Command update_party;

	public boolean keep_on;

	public List<User> ready_for_play;
	public List<User> newClients;

	public Instant last_play_time;
	public long total_offset;
	public long current_song_duration;

	public Party_thread(Party party, Selector server_selector) throws JSONException {
		this.party = party;
		this.server_selector = server_selector;
		pause_command = new Command(CommandType.PAUSE);
		play_command = new Command(CommandType.PLAY_SONG);
		get_ready_command = new Command(CommandType.GET_READY);
		keep_on = true;
		ready_for_play = new ArrayList<>();
	}

	@Override
	public void run() {
		try {
			register_for_selection(party.admins.get(0));
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* the main function   */
	public void listen() throws IOException, Exception {
		while (keep_on) {
			int readyChannels = party.selector.select();
			if(readyChannels == 0) {
				System.out.println("lost wake up");
				continue;
			}
			update_party = new Command(CommandType.UPDATE_PARTY);
			handler_new_clients();
			handle_current_clients();
		}
	}

	private void handle_current_clients() throws IOException, JSONException, Exception {
		Set<SelectionKey> selectedKeys = party.selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		while(keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();
			if (key.isReadable()) {
				SocketChannel channel  = (SocketChannel) key.channel();
				Command cmd = readWriteAux.readSocket(channel);
				cmd.printCommand();
				do_command(cmd, (User) key.attachment());
			}
			keyIterator.remove();
		}
		if (play_condition()) { /* we start playing the song */
			party.status = Party.Party_Status.playing;
			last_play_time = Instant.now();
			sendPlayToList();
		}
		syncPartyToAll();
		//SendCommandToAll(update_party);
	}

	/* to handle */
	public void handler_new_clients() throws Exception {
		get_newClients();
		String updateMsg;
		Iterator<User> iter = newClients.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			register_for_selection(user);
			if (party.is_private) { 
				party.addRequest(user);				
				updateMsg = jsonKey.REQUESTS.name();
			} 
			/* the party is public, tell the user to get ready */
			else { 
				addClientToParty(user);
				updateMsg = jsonKey.USERS.name();
			}
			addToJSONArray(updateMsg,user.get_JSON());
			iter.remove();
		}
	}

	/* send to the user the entire party info */
	public void addClientToParty(User user) throws JSONException, IOException {
		party.addClient(user);
		JSONObject party_info = party.getFullJson();
		Command sync_command = new Command(CommandType.SYNC_PARTY, party_info);
		SendCommandToUser(user, sync_command);

		update_get_ready_command();
		SendCommandToUser(user, get_ready_command);
	}

	public boolean play_condition() {
		return (0.5*party.connected.size() < ready_for_play.size()) && party.status == Party.Party_Status.preparing;
	}

	/* we should decide about the command format */

	public void do_command(Command cmd, User user) throws Exception {
		switch (cmd.cmd_type) {
		case PLAY_SONG:
			startPlayProtocol(cmd);
			break;
		case PAUSE:
			pause_song();
			break;
		case GET_READY:
			GetReady(cmd, user);
			break;
		case CONFIRM_REQUEST:
			confirmRequest(cmd);							
			break;												//adding_and_removing_from_updateParty_json();
			//add to lists.
		case SWAP_SONGS:
			SwapSongs(cmd);
			break;
		case DELETE_SONG:
			DeleteSong(cmd);
			break;
		case ADD_SONG:
			AddSong(cmd);
			break;
		case MAKE_ADMIN:	
			makeAdmin(cmd);
			break;
		case LOCATION:	
			updateLocation(cmd);
			break;
		case RENAME_PARTY:
			party.party_name = cmd.cmd_info.getString(jsonKey.NAME.name());
		case DISCONNECTED:	
			disconnect_user(user);
			break;
		default:
			break;
		}
	}

	private void makeAdmin(Command cmd) throws JSONException {
		int userId = cmd.cmd_info.getInt(jsonKey.USER_ID.name());
		User user = find_user(userId);
		party.addAdmin(user);	
	}

	private void confirmRequest(Command cmd) throws JSONException, IOException {
		User confirmed_user = find_user(cmd.getIntAttribute(jsonKey.USER_ID.name()));
		if(confirmed_user == null) return;				//no such user / other admin confirmed.
		addClientToParty(confirmed_user);
		party.removeRequest(confirmed_user);
	}

	private User find_user(int USER_ID) {
		for (User user: party.request) {
			if (user.id == USER_ID) {
				return user;
			}
		}
		return null; /* no such user */
	}

	/* handling the locks */
	//TODO
	public void get_newClients() {
		newClients = new ArrayList<>();
	}

	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		channel.register(party.selector, SelectionKey.OP_READ, user);
	}

	/* we wait for half of the party participants to be ready before we actually start playing */
	public void startPlayProtocol(Command cmd) throws IOException, JSONException {
		if((party.status == Party.Party_Status.preparing || party.status == Party.Party_Status.playing) && cmd.cmd_info.getInt("TrackID") == party.get_current_track_id()) {
			return;
		}
		ready_for_play = new ArrayList<>();
		party.status = Party.Party_Status.preparing;
		party.next_song();
		total_offset = cmd.cmd_info.getLong("offset");
		update_get_ready_command();
		SendCommandToAll(get_ready_command);
	}

	public void GetReady(Command cmd, User user) throws IOException, JSONException {
		switch(party.status) {
		case playing:
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
			update_play_command();
			SendCommandToUser(user, play_command);
			break;
		case preparing:
			ready_for_play.add(user);
			break;
		default:
			break;
		}
	}
	
	private void updateLocation(Command cmd) {
		// TODO Auto-generated method stub
		
	}

	public void pause_song() throws IOException, JSONException {
		if(party.status != Party.Party_Status.playing) 
			return;
		party.status = Party.Party_Status.notPlaying;
		SendCommandToAll(pause_command);
	}

	public void DeleteSong(Command cmd) throws JSONException {
		party.deleteSong(cmd.cmd_info.getInt(jsonKey.TRACK_ID.name()));
		addToJSONArray(jsonKey.DELETE_SONGS.name(),cmd.cmd_info);
	}

	public void SwapSongs(Command cmd) throws JSONException {
		party.changeSongsOrder(cmd.cmd_info.getInt(jsonKey.TRACK_ID_1.name()),cmd.cmd_info.getInt(jsonKey.TRACK_ID_2.name()));
		addToJSONArray(jsonKey.SWAP_SONGS.name(),cmd.cmd_info);
	}

	public void AddSong(Command cmd) throws JSONException {
		Track newTrack = party.addSong(cmd.cmd_info.getString(jsonKey.URL.name()));
		JSONObject trackJSON = newTrack.get_JSON();
		addToJSONArray(jsonKey.NEW_SONGS.name(),trackJSON);
	}

	private void addToJSONArray(String classifier, JSONObject JsonObject) throws JSONException {
		if(!update_party.cmd_info.has(classifier)) {
			update_party.cmd_info.put(classifier, new JSONArray());
		}
		update_party.cmd_info.getJSONArray(classifier).put(JsonObject);

	}

	//TODO
	public void disconnect_user(User user) throws JSONException, Exception {
		boolean removed_admin = party.admins.remove(user);
		boolean removed_participent = party.connected.remove(user);
		party.request.remove(user);
		if (removed_participent || removed_admin) {
			//number_of_participents--;
			//Command cmd = new Command(null); /* upadting the party-participents */
			//SendCommandToAll(cmd);
			//if (ready_for_play.contains(user)) {
			//ready_for_play.remove(user);
			//ready_for_next_song--;
			//}
			//if (ready_for_next_song > 0.5*number_of_participents) {
			//play_song = true;
			//}
		}
		user.channel.close();
	}

	public void SendCommandToAll(Command cmd) throws IOException, JSONException {
		SendCommandToList(cmd, party.connected, false);
	}
	//TODO
	public void SendCommandToList(Command cmd, List<User> receivers, boolean remove_from_list) throws IOException, JSONException {
		Iterator<User> iter = receivers.iterator();
		while (iter.hasNext()){
			SendCommandToUser(iter.next(), cmd); /* for now */
			if (remove_from_list) {
				iter.remove();
			}
		}
	}

	public void SendCommandToUser(User user, Command cmd) throws IOException, JSONException {
		readWriteAux.writeSocket(user.get_channel(), cmd);
	}

	private void sendPlayToList() throws IOException, JSONException {
		update_play_command();
		SendCommandToList(play_command, ready_for_play, true);
	}

	private void syncPartyToAll() throws JSONException, IOException {
		// TODO Auto-generated method stub
		JSONObject party_info = party.getFullJson();
		Command sync_command = new Command(CommandType.SYNC_PARTY, party_info);
		SendCommandToAll(sync_command);
	}

	/* updates the GetReady command */
	public void update_get_ready_command() throws JSONException {
		get_ready_command.cmd_info.put(jsonKey.OFFSET.name(), total_offset);
		get_ready_command.cmd_info.put(jsonKey.TRACK_ID.name(), party.get_current_track_id());
	}

	/* updates the play command */
	public void update_play_command() throws JSONException {
		play_command.cmd_info.put(jsonKey.OFFSET.name(), total_offset);
		play_command.cmd_info.put(jsonKey.TRACK_ID.name(), party.get_current_track_id());
	}
}
