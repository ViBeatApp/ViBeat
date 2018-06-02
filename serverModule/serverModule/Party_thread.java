package serverModule;
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
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverObjects.Command;
import serverObjects.CommandType;
import serverObjects.Location;
import serverObjects.Party;
import serverObjects.ReadWriteAux;
import serverObjects.Track;
import serverObjects.User;
import serverObjects.jsonKey;

public class Party_thread implements Runnable {

	public Party party;
	public Selector server_selector;

	public Command get_ready_command;
	public Command play_command;
	public Command pause_command;
	public Command update_party;

	public List<User> ready_for_play;
	public List<User> unHandledClients;

	public Instant last_play_time;
	public int total_offset;
	public int current_song_duration;

	public Party_thread(Party party, Selector server_selector) throws JSONException {
		this.party = party;
		this.server_selector = server_selector;
		pause_command = new Command(CommandType.PAUSE);
		play_command = new Command(CommandType.PLAY_SONG);
		get_ready_command = new Command(CommandType.GET_READY);
		total_offset = 0;
		ready_for_play = new ArrayList<>();
		unHandledClients = new ArrayList<>();
		last_play_time = null;
	}

	@Override
	public void run() {
		try {
			register_for_selection(party.getAdmin());
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* the main function   */
	public void listen() throws IOException, Exception {
		while (party.keep_on) {
			int readyChannels = party.selector.select();
			if(readyChannels == 0 ) {
				System.out.println("lost wake up");
			}
			update_party = new Command(CommandType.UPDATE_PARTY);
			handler_new_clients();
			handle_current_clients();
		}
	}

	private void handle_current_clients() throws IOException, JSONException, Exception {
		Set<SelectionKey> selectedKeys = party.selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		while(keyIterator.hasNext() && party.keep_on) {
			SelectionKey key = keyIterator.next();
			if (key.isReadable()) {
				SocketChannel channel  = (SocketChannel) key.channel();
				Command cmd = ReadWriteAux.readSocket(channel);
				System.out.print("from user " + ((User)key.attachment()).name + " to server: ");
				cmd.printCommand();
				do_command(cmd, key);
			}
			keyIterator.remove();
		}

		if (play_condition()) { /* we start playing the song */
			party.status = Party.Party_Status.playing;
			last_play_time = Instant.now();
			sendPlayToList();
		}
		syncPartyToAll();
		if ((party.status == Party.Party_Status.not_started)
				&& (party.get_current_track_id() != -1)) {
			update_get_ready_command();
			SendCommandToAll(get_ready_command);
		}
		//SendCommandToAll(update_party);
	}

	/* to handle */
	public void handler_new_clients() throws Exception {
		getNewClients();
		jsonKey updateMsg;
		Iterator<User> iter = unHandledClients.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			register_for_selection(user);
			if (party.is_private) { 
				party.addRequest(user);				
				updateMsg = jsonKey.REQUESTS;
			} 
			/* the party is public, tell the user to get ready */
			else { 
				addClientToParty(user);
				updateMsg = jsonKey.USERS;
			}
			addToJSONArray(updateMsg,user.get_JSON());
			iter.remove();
		}
	}

	/* send to the user the entire party info */
	public void addClientToParty(User user) throws JSONException, IOException {
		party.addClient(user);
		JSONObject party_info = party.getFullJson();
		Command sync_command = Command.create_syncParty_Command(party_info);
		SendCommandToUser(user, sync_command);
		if (party.get_current_track_id() != -1) {
			update_get_ready_command();
			SendCommandToUser(user, get_ready_command);
		}
	}

	public boolean play_condition() {
		return (0.5*party.numOfClients() < ready_for_play.size()) && party.status == Party.Party_Status.preparing;
	}

	public void do_command(Command cmd, SelectionKey key) throws Exception {
		User user = (User) key.attachment();
		switch (cmd.cmd_type) {
		case PLAY_SONG:
			startPlayProtocol(cmd);
			break;
		case PAUSE:
			pause_song();
			break;
		case IM_READY:
			GetReady(cmd, user);
			break;
		case CONFIRM_REQUEST:
			confirmRequest(key,cmd);							
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
		case UPDATE_LOCATION:	
			updateLocation(cmd);
			break;
		case RENAME_PARTY:
			party.party_name = cmd.getStringAttribute(jsonKey.NAME);
			break;
		case MAKE_PRIVATE:
			party.is_private = cmd.getBoolAttribute(jsonKey.IS_PRIVATE);
			if(party.is_private == false) 
				makePublicHandle();
			break;
		case DISCONNECTED:	
			returnToServerModule(user,true);
			break;
		case LEAVE_PARTY:	
			returnToServerModule(user,false);
			break;
		case CLOSE_PARTY:	
			destroyParty();
			break;
		default:
			break;
		}
	}

	private void makePublicHandle() throws JSONException, IOException {		
		Iterator<User> iter = party.request.iterator();

		while (iter.hasNext()){
			User userReq = iter.next();
			iter.remove();
			addClientToParty(userReq);
		}		
	}


	private void makeAdmin(Command cmd) throws JSONException {
		int userId = cmd.getIntAttribute(jsonKey.USER_ID);
		User user = find_user(userId,party.connected);
		System.out.println("user: " + user.name);
		party.makeAdmin(user);	
	}

	private void confirmRequest(SelectionKey key, Command cmd) throws JSONException, IOException {
		User confirmed_user = find_user(cmd.getIntAttribute(jsonKey.USER_ID),party.request);
		if(confirmed_user == null) return;				//no such user / other admin confirmed.
		party.removeRequest(confirmed_user);
		if(cmd.getBoolAttribute(jsonKey.CONFIRMED)) {
			addClientToParty(confirmed_user);
		}
		else {
			SendCommandToUser(confirmed_user, Command.create_rejected_Command());
			returnToServerModule(confirmed_user,false);
		}

	}

	private User find_user(int USER_ID, List<User> userList) {
		for (User user: userList) {
			if (user.id == USER_ID) {
				return user;
			}
		}

		return null; /* no such user */
	}

	/*TODO handling the locks */
	public void getNewClients() {
		unHandledClients = new ArrayList<>();
		clone_User_list(party.waitingClients, unHandledClients);
	}

	// importing the new users from the the party syncronized list to local list
	public void clone_User_list(List<User> oldList, List<User> newList) {
		synchronized(oldList) {
			Iterator<User> iter = oldList.iterator();
			while (iter.hasNext()){
				User user = iter.next();
				newList.add(user);
				iter.remove();
			}	
		}
	}

	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		channel.register(party.selector, SelectionKey.OP_READ, user);
	}

	/* we wait for half of the party participants to be ready before we actually start playing */
	public void startPlayProtocol(Command cmd) throws IOException, JSONException {
		System.out.println("--- party-thread: party-songs-#: " + party.get_playlist_size());
		if ((party.status == Party.Party_Status.preparing || party.status == Party.Party_Status.playing)
				&& cmd.getIntAttribute(jsonKey.TRACK_ID) == party.get_current_track_id()) {
			return;
		}
		ready_for_play = new ArrayList<>();
		party.status = Party.Party_Status.preparing;
		party.setCurrentTrack(cmd.getIntAttribute(jsonKey.TRACK_ID));
		total_offset = cmd.getIntAttribute(jsonKey.OFFSET);
		System.out.println("party-thread: startPlayProtocol: update total offset = " + total_offset);
		update_get_ready_command(); // not updating the offset
		SendCommandToAll(get_ready_command);
	}

	public void GetReady(Command cmd, User user) throws IOException, JSONException {
		switch(party.status) {
		case playing:
			System.out.println("party-thread: GetReady: update total offset = " + total_offset);
			update_play_command(true);
			SendCommandToUser(user, play_command);
			break;
		case preparing:
			ready_for_play.add(user);
			break;
		default:
			break;
		}
	}

	private void updateLocation(Command cmd) throws JSONException {
		Location location = new Location(cmd);		
		party.UpdateLocation(location);						//TODO should be sync?

	}

	public void pause_song() throws IOException, JSONException {
		if (party.status == Party.Party_Status.playing) {
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
			System.out.println("party-thread: pause_song: update total offset = " + total_offset);
		} else if ((party.status == Party.Party_Status.pause) ||  (party.status == Party.Party_Status.not_started)) {
			return;
		}
		party.status = Party.Party_Status.pause;
		SendCommandToAll(pause_command);
	}

	public void DeleteSong(Command cmd) throws JSONException {
		party.deleteSong(cmd.getIntAttribute(jsonKey.TRACK_ID));
		addToJSONArray(jsonKey.DELETE_SONGS,cmd.cmd_info);
	}

	public void SwapSongs(Command cmd) throws JSONException {
		party.changeSongsOrder(cmd.getIntAttribute(jsonKey.TRACK_ID_1),cmd.getIntAttribute(jsonKey.TRACK_ID_2));
		addToJSONArray(jsonKey.SWAP_SONGS,cmd.cmd_info);
	}

	public void AddSong(Command cmd) throws JSONException {
		Track newTrack = party.addSong(cmd.getStringAttribute(jsonKey.URL));
		JSONObject trackJSON = newTrack.get_JSON();
		addToJSONArray(jsonKey.NEW_SONGS,trackJSON);
	}

	private void addToJSONArray(jsonKey classifier, JSONObject JsonObject) throws JSONException {
		if(!update_party.cmd_info.has(classifier.name())) {
			update_party.cmd_info.put(classifier.name(), new JSONArray());
		}
		update_party.cmd_info.getJSONArray(classifier.name()).put(JsonObject);
	}

	public void returnToServerModule(final User user, boolean disconnected) throws IOException, JSONException {
		party.removeClient(user, disconnected);
		party.removeRequest(user);
		ready_for_play.remove(user);		
		unHandledClients.remove(user);

		if(party.numOfClients() == 0 && party.keep_on) {
			System.out.println("party should be destroied");
			destroyParty();
			return;
		}

		user.closeChannel();

		if(disconnected) {
			CompletableFuture.runAsync(new Runnable() {
				@Override
				public void run() {
					System.out.println("party-thread - user " + user.name + " has disconnected");
					ServerModule.addDisconenctedUser(user);
				}
			});
		}
	}

	private void destroyParty() throws IOException, JSONException {
		synchronized (party.waitingClients) {
			if(!party.keep_on){
				System.out.println("destroy party");
				return;
			}
			party.keep_on = false;
			Command close = new Command(CommandType.CLOSE_PARTY);
			ServerModule.deleteParty(party);
			SendCommandToList(close, party.connected, true);
			SendCommandToList(close, party.waitingClients, true);
			SendCommandToList(close, party.request, true);
			SendCommandToList(close, unHandledClients, true);
			
		}

	}

	public void SendCommandToAll(Command cmd) throws IOException, JSONException {
		SendCommandToList(cmd, party.connected, false);
	}

	public void SendCommandToList(Command cmd, List<User> recievers, boolean remove_from_list) throws IOException, JSONException {
		
		if(!party.keep_on)
			return;
		
		List<User> disconnectedUsers = new ArrayList<>();
		
		Iterator<User> iter = recievers.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			if(SendCommandToUser(user, cmd) == -1) {
				disconnectedUsers.add(user);
			}
			if (remove_from_list) {
				iter.remove();
			}
		}

		iter = disconnectedUsers.iterator();
		while (iter.hasNext()){
			returnToServerModule(iter.next(), true);
		}
	}

	public int SendCommandToUser(User user, Command cmd) throws IOException, JSONException {
		System.out.print("send to " +user.name + " command: ");
		cmd.printCommand();
		int result =  ReadWriteAux.writeSocket(user.get_channel(), cmd);
		if(cmd.cmd_type == CommandType.CLOSE_PARTY) 
			user.closeChannel();
		return result;
	}

	private void sendPlayToList() throws IOException, JSONException {
		update_play_command(false); // not updating the offset
		SendCommandToList(play_command, ready_for_play, true);
	}

	private void syncPartyToAll() throws JSONException, IOException {
		if(party.numOfClients() == 0) {
			return;
		}
		JSONObject party_info = party.getFullJson();

		Command sync_command = Command.create_syncParty_Command(party_info);
		SendCommandToAll(sync_command);
	}

	/* updates the GetReady command */
	public void update_get_ready_command() throws JSONException {	
		if (party.status == Party.Party_Status.playing) {
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
		}
		get_ready_command.setAttribute(jsonKey.OFFSET, total_offset);
		get_ready_command.setAttribute(jsonKey.TRACK_ID, party.get_current_track_id());
	}

	/* updates the play command */
	public void update_play_command(boolean update_offset) throws JSONException {
		if (update_offset) {
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
		}
		play_command.setAttribute(jsonKey.OFFSET, total_offset);
		play_command.setAttribute(jsonKey.TRACK_ID, party.get_current_track_id());
	}
}
