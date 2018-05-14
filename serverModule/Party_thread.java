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

	public boolean in_play_protocol;

	public List<User> ready_for_play;
	public List<User> newClients;

	public Instant last_play_time;
	public long total_offset;
	public long current_song_duration;

	public Party_thread(Party party, Selector server_selector) throws JSONException {
		this.party = party;
		this.server_selector = server_selector;
		pause_command = new Command(CommandType.Pause);
		play_command = new Command(CommandType.PlaySong);
		get_ready_command = new Command(CommandType.GetReady);
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
			party.selector.select();
			update_party = new Command(CommandType.updateParty);
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
				byte[] received_data = readWriteAux.readSocket(channel);
				Command cmd = new Command(received_data);
				do_command(cmd, (User) key.attachment());
			}
			keyIterator.remove();
		}
		if (play_condition()) { /* we start playing the song */
			party.is_playing = true;
			last_play_time = Instant.now();
			sendPlayToList();
		}
		SendCommandToAll(update_party);
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
				updateMsg = jsonKey.REQUESTS.getCommandString();
			} 
			
			/* the party is public, tell the user to get ready */
			else { 
				party.addClient(user);
				update_get_ready_command();
				SendCommandToUser(user, get_ready_command.cmd_info);
				updateMsg = jsonKey.USERS.getCommandString();
			}
			addToJSONArray(updateMsg,user.get_JSON());
			iter.remove();
		}
	}
	
	public boolean play_condition() {
		return (0.5*party.connected.size() < ready_for_play.size()) && !party.is_playing;
	}

	/* we should decide about the command format */

	public void do_command(Command cmd, User user) throws Exception {
		switch (cmd.cmd_type) {
		case PlaySong:
			startPlayProtocol(cmd);
			break;
		case Pause:
			pause_song();
			break;
		case GetReady:
			GetReady(cmd, user);
			break;

			//add to lists.
		case SwapSongs:
			SwapSongs(cmd);
			break;
		case DeleteSong:
			DeleteSong(cmd);
			break;

		case AddSong:
			AddSong(cmd);
			break;
		case Disconnected:	
			disconnect_user(user);
			break;
		case Location:	
			disconnect_user(user);
			break;
		default:
			break;
		}
	}

	/* handling the locks */
	//TODO
	public void get_newClients() {
		newClients = new ArrayList<>();
	}

	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		channel.configureBlocking(false); /* redundant? */
		channel.register(party.selector, SelectionKey.OP_READ, user);
	}

	/* we wait for half of the party participants to be ready before we actually start playin */
	public void startPlayProtocol(Command cmd) throws IOException, JSONException {
		if (!(in_play_protocol && cmd.cmd_info.getInt("TrackID") == party.get_current_track_id())) {
			ready_for_play = new ArrayList<>();
			party.is_playing = false;
			in_play_protocol = true;
			party.next_song();
			total_offset = cmd.cmd_info.getLong("offset");
			update_get_ready_command();
			SendCommandToAll(get_ready_command);
		}
	}

	public void GetReady(Command cmd, User user) throws IOException, JSONException {
		if (party.is_playing) {
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
			update_play_command();
			SendCommandToUser(user, play_command.cmd_info);
		} else {
			ready_for_play.add(user);
		}
	}

	public void pause_song() throws IOException, JSONException {
		party.is_playing = false;
		SendCommandToAll(pause_command);
	}
	
	public void DeleteSong(Command cmd) throws JSONException {
		party.deleteSong(cmd.cmd_info.getInt("TrackID"));
		addToJSONArray(jsonKey.DELETE_SONGS.getCommandString(),cmd.cmd_info);
	}

	public void SwapSongs(Command cmd) throws JSONException {
		party.changeSongsOrder(cmd.cmd_info.getInt("TrackID_1"),cmd.cmd_info.getInt("TrackID_2"));
		addToJSONArray(jsonKey.SWAP_SONGS.getCommandString(),cmd.cmd_info);
	}

	public void AddSong(Command cmd) throws JSONException {
		Track newTrack = party.addSong(cmd.cmd_info.getString("url"));
		JSONObject trackJSON = newTrack.get_JSON();
		addToJSONArray(jsonKey.NEW_SONGS.getCommandString(),trackJSON);
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
		user.client.close();
	}

	public void SendCommandToAll(Command cmd) throws IOException {
		SendCommandToList(cmd, party.connected, false);
	}

	public void SendCommandToList(Command cmd, List<User> receivers,
			boolean remove_from_list) throws IOException {
		Iterator<User> iter = receivers.iterator();
		while (iter.hasNext()){
			SendCommandToUser(iter.next(), cmd.cmd_info); /* for now */
			if (remove_from_list) {
				iter.remove();
			}
		}
	}

	public void SendCommandToUser(User user, JSONObject obj) throws IOException {
		byte[] Data = obj.toString().getBytes();
		readWriteAux.writeSocket(user.get_channel(), Data);
	}
	
	private void sendPlayToList() throws IOException, JSONException {
		update_play_command();
		SendCommandToList(play_command, ready_for_play, true);
	}
	
	/* updates the GetReady command */
	public void update_get_ready_command() throws JSONException {
		get_ready_command.cmd_info.put("offset", total_offset);
		get_ready_command.cmd_info.put("TrackID", party.get_current_track_id());
	}

	/* updates the play command */
	public void update_play_command() throws JSONException {
		play_command.cmd_info.put("offset", total_offset);
		play_command.cmd_info.put("TrackID", party.get_current_track_id());

	}
}
