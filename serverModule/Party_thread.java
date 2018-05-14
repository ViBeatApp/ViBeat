

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

import org.json.JSONException;
import org.json.JSONObject;

public class Party_thread implements Runnable {
	
	public Party party;
	public Command get_ready_command;
	public Command play_command;
	public Command pause_command;
	public int number_of_participents;
	public int ready_for_next_song; /* number of people ready for next sone */
	public boolean woke_up;
	public boolean keep_on;
	public boolean play_song;
	public boolean fresh_start;
	public boolean first_wave;
	public List<User> ready_for_play;
	public List<User> unhandled_requests;
	public List<User> pending_requests;
	public Selector server_selector;
	public Instant last_play_time;
	public long total_offset;
	public long current_song_duration;
	
	public Party_thread(Party party, Selector server_selector) {
		this.party = party;
		this.server_selector = server_selector;
		pending_requests = new ArrayList<User>();
		
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
			total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
			long sleeping_reminder = current_song_duration - total_offset;
			if (sleeping_reminder <= 0) {
				System.out.println("it's the end of the world");
			}
			party.selector.select(sleeping_reminder - 100);
			WakeUp_handler(); /* lost wake-up-call? */
			/*
			if (woke_up) {
				WakeUp_handler();
				woke_up = false;
			} */
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
			if (play_song) {
				if (first_wave) {
					if (fresh_start) {
						total_offset = 0; /* we've start a new track */
						fresh_start = false;
						party.next_song();
						current_song_duration = party.current_song_duartion();
					}
					last_play_time = Instant.now();
					update_play_command(0); /* no offset in the first wave */
					first_wave = false;
				} else {
					Duration timeElapsed = Duration.between(last_play_time, Instant.now());
					update_play_command(timeElapsed.toMillis());
				}
				SendCommandToList(play_command, ready_for_play, true);
			}
		}
	}
	
	/* we should decide about the command format */
	public void do_command(Command cmd, User user) throws Exception {
		switch (cmd.cmd_type) {
			case AddSong:
				AddSong(cmd);
				break;
			case SwapSongs:
				DeleteSong(cmd);
				break;
			case DeleteSong:
				DeleteSong(cmd);
				break;
			case GetReady:
				GetReady(cmd, user);
				break;
			case PlaySong:
				fresh_start = true;
				PlayProtocol(cmd);
				break;
			case Resume:
				PlayProtocol(cmd);
				break;
			case Pause:
				pause_song();
				break;
			case Disconnected:	
				disconnect_user(user);
				break;
		    default:
		    	break;
		}
	}
	
	/* handling the locks */
	public void get_requests() {
		
	}
	
	/* to handle */
	public void WakeUp_handler() throws Exception {
		get_requests();
		
		if (!party.is_private) { /* the party is public */
			update_get_ready_command();
		}
		Iterator<User> iter = unhandled_requests.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			register_for_selection(user);
			if (party.is_private) { /* party is private, send request to join the party*/
				Command cmd = new Command(null); /* need to figure out the command structcure */
				SendCommandToAdmins(cmd);
			} else { /* the party is public, tell the user to get ready */
				SendCommandToUser(user, get_ready_command.cmd_info);
			}
			pending_requests.add(user); /* waiting for approval */
			iter.remove();
		}
	}
	
	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		channel.configureBlocking(false); /* redundant? */
		channel.register(party.selector, SelectionKey.OP_READ, user);
	}
	
	/* we wait for half of the party participants to be ready before we actually start playin */
	public void PlayProtocol(Command cmd) throws IOException {
		update_get_ready_command();
		ready_for_next_song = 0;
		first_wave = true;
		play_song = false; 
		ready_for_play = new ArrayList<User>();
		SendCommandToAll(get_ready_command);
	}
	
	public void GetReady(Command cmd, User user) {
		user.is_prepared = User.Song_preparation.prepared;
		ready_for_next_song++;
		ready_for_play.add(user);
		if (ready_for_next_song > 0.5*number_of_participents) {
			play_song = true;
		}
	}
	
	public void pause_song() throws IOException {
		SendCommandToAll(pause_command);
		total_offset += Duration.between(last_play_time, Instant.now()).toMillis();
	}

	public void DeleteSong(Command cmd) {
		// ToDo
	}
	
	public void SwapSongs(Command cmd) {
		// ToDo
	}
	
	public void AddSong(Command cmd) {
		// ToDo
	}
	
	public void disconnect_user(User user) throws JSONException, Exception {
		boolean removed_admin = party.admins.remove(user);
		boolean removed_participent = party.connected.remove(user);
		party.request.remove(user);
		if (removed_participent || removed_admin) {
			number_of_participents--;
			Command cmd = new Command(null); /* upadting the party-participents */
			SendCommandToAll(cmd);
			if (ready_for_play.contains(user)) {
				ready_for_play.remove(user);
				ready_for_next_song--;
			}
			if (ready_for_next_song > 0.5*number_of_participents) {
				play_song = true;
			}
		}
		user.client.close();
	}
	
	public void SendCommandToAdmins(Command cmd) throws IOException {
		SendCommandToList(cmd, party.admins, false);
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
	
	public void update_get_ready_command() {
		// ToDo
	}
	
	/* updates the play-command */
	public void update_play_command(long song_offset) {
		// ToDo
		
	}
}
