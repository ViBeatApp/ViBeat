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

import org.json.JSONException;
import org.json.JSONObject;

import serverObjects.Command;
import serverObjects.CommandType;
import serverObjects.Location;
import serverObjects.Party;
import serverObjects.Party.Party_Status;
import serverObjects.ReadWriteAux;
//import serverObjects.Track;
import serverObjects.User;
import serverObjects.jsonKey;


public class Party_thread implements Runnable {

	public Party party;
	public Selector server_selector;
	public Command pause_command;

	public List<User> ready_for_play;
	public List<User> unHandledClients;

	public Instant last_offset_update_time;
	public int total_offset;
	public boolean touchCurrentSong;

	public Party_thread(Party party, Selector server_selector) throws JSONException {
		this.party = party;
		this.server_selector = server_selector;
		pause_command = new Command(CommandType.PAUSE);
		total_offset = 0;
		ready_for_play = new ArrayList<>();
		unHandledClients = new ArrayList<>();
		last_offset_update_time = null;
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
			touchCurrentSong = false;
			party.update_party = new Command(CommandType.SYNC_PARTY);
			handle_comeBack_clients();
			handle_new_clients();
			handle_current_clients();
		}
		System.out.println("party has stopped");
	}

	private void handle_comeBack_clients() throws Exception {
		List<User> comeBackUsers = getComeBackClients();
		Iterator<User> iter = comeBackUsers.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			removeDisconnectedUser(user);
			register_for_selection(user);
			boolean isAdmin = user.is_admin;
			party.removeClient(user, false);
			addClientToParty(user,isAdmin);
			iter.remove();
		}

	}

	private void removeDisconnectedUser(final User user) {
		CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				ServerModule.isDisconnectedUser(user.id);
			}
		});

	}

	private List<User> getComeBackClients() {
		List<User> comeBackUser = new ArrayList<>();
		clone_User_list(party.comeBackUsers, comeBackUser,true);
		return comeBackUser;
	}

	private void handle_current_clients() throws IOException, JSONException, Exception {
		Set<SelectionKey> selectedKeys = party.selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		while(keyIterator.hasNext() && party.keep_on) {
			SelectionKey key = keyIterator.next();
			if (key.isValid() && key.isReadable()) {
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
			sendPlayToList();
			last_offset_update_time = Instant.now();
		}

		updatePartyToAll();

		//optimization.
		if ((party.status == Party.Party_Status.not_started) && touchCurrentSong) {
			Command get_ready_command = create_get_ready_command(false);
			SendCommandToAll(get_ready_command);
		}
	}

	/* to handle */
	public void handle_new_clients() throws Exception {
		getNewClients();
		Iterator<User> iter = unHandledClients.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			register_for_selection(user);

			if(user.currentPartyId != party.party_id && user.currentPartyId != -1)
				System.out.println("error !!! handler_new_clients");

			if(party.connected.isEmpty()){					//last client exits and new client has just arrived. nasty bug.
				addClientToParty(user,true);
			}

			else if(!party.is_private || user.currentPartyId == party.party_id) { 
				addClientToParty(user,user.is_admin);
			}

			else { 
				party.addRequest(user);	
			} 

			iter.remove();
		}
	}

	/* send to the user the entire party info */
	public void addClientToParty(User user,boolean makeAdmin) throws JSONException, IOException {
		party.addClient(user);
		if(makeAdmin)
			party.makeAdmin(user);
		Command sync_command = Command.create_syncParty_Command(party.getFullJson());
		SendCommandToUser(user, sync_command);
		if (party.nonEmptyPlaylist()) {
//			if (party.status == Party_Status.playing) {
//				pause_song(Command.create_pause_Command(party.get_current_track_id(), total_offset));
//				startPlayProtocol(Command.create_playSong_Command(party.get_current_track_id(), total_offset));
//			}
			Command get_ready_command = create_get_ready_command(party.status == Party_Status.playing);
			SendCommandToUser(user, get_ready_command);
		}
	}

	public boolean play_condition() {
		return (0.8*party.numOfClients() < ready_for_play.size()) && party.status == Party.Party_Status.preparing;
	}

	public void do_command(Command cmd, SelectionKey key) throws Exception {
		User user = (User) key.attachment();
		switch (cmd.cmd_type) {
		case PLAY_SONG:
			startPlayProtocol(cmd);
			return;
		case PAUSE:
			pause_song(cmd);
			return;
		case IM_READY:
			handleReady(cmd, user);
			return;
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
			return;
		case RENAME_PARTY:
			party.setName(cmd.getStringAttribute(jsonKey.NAME));
			break;
		case MAKE_PRIVATE:
			party.changePrivacy(cmd.getBoolAttribute(jsonKey.IS_PRIVATE));
			if(party.is_private == false) 
				makePublicHandle();
			break;
		case DISCONNECTED:	
			synchronized (party.comeBackUsers) {
				if(isComeBackUser(party.comeBackUsers,user.id))
					break;
				returnToServerModule(user,true);
			}
			break;
		case LEAVE_PARTY:
			if(SendCommandToUser(user, Command.create_leaveParty_Command()))
				returnToServerModule(user,false);
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
			addClientToParty(userReq,false);
		}		
	}


	private void makeAdmin(Command cmd) throws JSONException {
		int userId = cmd.getIntAttribute(jsonKey.USER_ID);
		User user = find_user(userId,party.connected);
		party.makeAdmin(user);	
	}

	private void confirmRequest(SelectionKey key, Command cmd) throws JSONException, IOException {
		User confirmed_user = find_user(cmd.getIntAttribute(jsonKey.USER_ID),party.request);
		if(confirmed_user == null) 
			return;				//no such user / other admin confirmed.
		party.removeRequest(confirmed_user);
		if(cmd.getBoolAttribute(jsonKey.CONFIRMED)) {
			addClientToParty(confirmed_user,false);
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


	public void getNewClients() {
		unHandledClients = new ArrayList<>();
		clone_User_list(party.waitingClients, unHandledClients,true);
	}

	// importing the new users from the the party syncronized list to local list
	public void clone_User_list(List<User> oldList, List<User> newList,boolean remove) {
		synchronized(oldList) {
			Iterator<User> iter = oldList.iterator();
			while (iter.hasNext()){
				User user = iter.next();
				newList.add(user);
				if(remove)
					iter.remove();
			}	
		}
	}

	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		SelectionKey key = channel.keyFor(party.selector);
		if(key == null)
			channel.register(party.selector, SelectionKey.OP_READ, user);
		else
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
	}

	/* we wait for half of the party participants to be ready before we actually start playing */
	public void startPlayProtocol(Command cmd) throws IOException, JSONException {
		System.out.println("--- party-thread: party-songs-#: " + party.get_playlist_size());
		int trackId = cmd.getIntAttribute(jsonKey.TRACK_ID);
		if(trackId == party.get_current_track_id()) {			
			if(party.status == Party.Party_Status.preparing || (party.status == Party.Party_Status.playing && party.get_playlist_size() != 1)) {
				return;
			}	
		}
		
		else if ((party.status == Party.Party_Status.pause || (party.status == Party.Party_Status.not_started && trackId != -1))) { //next song but there's no need to start playing. 
			party.setCurrentTrack(trackId);  
			updatePartyToAll();		//for checking if we're at the current track.
			return;
		}
		
		total_offset = cmd.getIntAttribute(jsonKey.OFFSET);
		party.setCurrentTrack(cmd.getIntAttribute(jsonKey.TRACK_ID));
		ready_for_play = new ArrayList<>();
		party.status = Party.Party_Status.preparing;
		System.out.println("party-thread: startPlayProtocol: update total offset = " + total_offset);
		System.out.println("startPlayProtocol thread - currentTrackId: " + party.get_current_track_id());
		Command get_ready_command = create_get_ready_command(false); // not updating the offset
		updatePartyToAll();		//for checking if we're at the current track.
		SendCommandToAll(get_ready_command);
	}

	public void handleReady(Command cmd, User user) throws IOException, JSONException, InterruptedException {
		if(party.get_current_track_id() != cmd.getIntAttribute(jsonKey.TRACK_ID))
			return;
		switch(party.status) {
		case playing:
			System.out.println("handle-ready - party is playing");
			if(ready_for_play.contains(user))
				return;
			pause_song(Command.create_pause_Command(-1, total_offset));
			updateTime();
			startPlayProtocol(Command.create_playSong_Command(party.get_current_track_id(), total_offset));
			break;
		case preparing:
			if(!ready_for_play.contains(user))
				ready_for_play.add(user);
			break;
		default:
			break;
		}
	}

	private void updateLocation(Command cmd) throws JSONException {
		Location location = new Location(cmd);		
		party.UpdateLocation(location);

	}

	public void pause_song(Command cmd) throws IOException, JSONException {
		if (party.status == Party.Party_Status.playing) {
			total_offset = cmd.getIntAttribute(jsonKey.OFFSET);
			System.out.println("party-thread: pause_song: update total offset = " + total_offset);
		} else if ((party.status == Party.Party_Status.pause) ||  (party.status == Party.Party_Status.not_started)) {
			return;
		}
		party.status = Party.Party_Status.pause;
		SendCommandToAll(pause_command);
	}

	public void DeleteSong(Command cmd) throws JSONException, IOException {
		int trackID = cmd.getIntAttribute(jsonKey.TRACK_ID);

		int deleteCurrentSong = party.deleteSong(trackID);

		if(deleteCurrentSong == 1){
			touchCurrentSong = true;
			if(party.status == Party_Status.playing || party.status == Party_Status.preparing){
				party.status = Party_Status.not_started;
				startPlayProtocol(Command.create_playSong_Command(party.get_current_track_id(), 0));
			}
		}
	}

	public void SwapSongs(Command cmd) throws JSONException {
		int trackID_1 = cmd.getIntAttribute(jsonKey.TRACK_ID_1);
		int trackID_2 = cmd.getIntAttribute(jsonKey.TRACK_ID_2);
		party.changeSongsOrder(trackID_1,trackID_2);
	}

	public void AddSong(Command cmd) throws JSONException {
		if(!party.nonEmptyPlaylist())
			touchCurrentSong = true;
		party.addSong(cmd.getStringAttribute(jsonKey.DB_ID));
	}

	public void returnToServerModule(final User user, boolean disconnected) throws IOException, JSONException {
		party.removeClient(user, disconnected);
		party.removeRequest(user);
		ready_for_play.remove(user);		
		unHandledClients.remove(user);
		SelectionKey key = user.get_channel().keyFor(party.selector);

		if(!disconnected){
			key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
			ServerModule.backToServer(user);
		}

		if(party.numOfClients() == 0 && party.keep_on) {
			System.out.println("party should be destroied");
			destroyParty();
			return;
		}

		if(disconnected) {
			user.closeChannel();
			addDisconenctedUser(user);
		}
	}

	private void addDisconenctedUser(final User user) {
		CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				System.out.println("party-thread addDisconenctedUser - user " + user.name + " has disconnected");
				try {
					ServerModule.addDisconenctedUser(user);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void destroyParty() throws IOException, JSONException {
		synchronized (party.waitingClients) {
			if(party.waitingClients.size() != 0){
				System.out.println("destroy party shouldn't happen.");
				return;
			}
			party.keep_on = false;
			ServerModule.deleteParty(party);		
		}

	}

	public void SendCommandToAll(Command cmd) throws IOException, JSONException {
		SendCommandToList(cmd, party.connected, false);
	}

	public void SendCommandToList(Command cmd, List<User> recievers, boolean remove_from_list) throws IOException, JSONException {

		List<User> new_recievers = new ArrayList<>();
		clone_User_list(recievers, new_recievers,remove_from_list);

		for (User user : new_recievers){
			SendCommandToUser(user, cmd);
		}
	}

	public boolean SendCommandToUser(User user, Command cmd) throws IOException, JSONException {
		System.out.print("send to " +user.name + " command: ");
		cmd.printCommand();
		if(ReadWriteAux.writeSocket(user.get_channel(), cmd) == -1){
			returnToServerModule(user, true);
			return false;
		}
		return true;
	}

	private void sendPlayToList() throws IOException, JSONException {
		Command play_command = create_play_command(); // not updating the offset
		SendCommandToList(play_command, ready_for_play, false);
	}


	private void updatePartyToAll() throws IOException, JSONException {
		if(party.update_party.cmd_info.length() == 0) {
			return;
		}
		SendCommandToAll(party.update_party);
		party.update_party = new Command(CommandType.SYNC_PARTY);
		//syncPartyToAll();
	}

	/*private void syncPartyToAll() throws JSONException, IOException {
		JSONObject party_info = party.getFullJson();
		Command sync_command = Command.create_syncParty_Command(party_info);
		SendCommandToAll(sync_command);
	}*/

	/* updates the GetReady command */
	public Command create_get_ready_command(boolean middleOfPlaying) throws JSONException {
		Command get_ready_command = new Command(CommandType.GET_READY);
		if (party.status == Party.Party_Status.playing) {
			updateTime();
		}
		get_ready_command.setAttribute(jsonKey.OFFSET, total_offset);
		get_ready_command.setAttribute(jsonKey.TRACK_ID, party.get_current_track_id());
		get_ready_command.setAttribute(jsonKey.WAIT_FOR_TO_SEEK, middleOfPlaying);
		return get_ready_command;
	}

	/* updates the play command */
	public Command create_play_command() throws JSONException {
		System.out.println("create_play_command - before update");
		Command play_command = new Command(CommandType.PLAY_SONG);
		play_command.setAttribute(jsonKey.OFFSET, total_offset);
		play_command.setAttribute(jsonKey.TRACK_ID, party.get_current_track_id());
		return play_command;
	}

	protected void updateTime() {
		Instant current_time = Instant.now();
		total_offset += Duration.between(last_offset_update_time, current_time).toMillis();
		last_offset_update_time = current_time;
	}

	private static boolean isComeBackUser(List<User> comeBackUser, int id) {
		synchronized (comeBackUser) {
			Iterator<User> iter = comeBackUser.iterator();
			while (iter.hasNext()){
				User user = iter.next();
				if(user.id == id) {	
					return true;
				}
			}
		}
		return false;
	}
}
