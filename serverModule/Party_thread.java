

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public class Party_thread implements Runnable {
	
	long time_out;
	boolean keep_on;
	Party party;
	int number_of_participents;
	int ready_for_next_song; /* number of people ready for next sone */
	public boolean woke_up;
	Command get_ready_command;
	Command play_command;
	public boolean start_song;
	List<User> ready_for_song;
	Selector server_selector;
	
	
	public Party_thread(Party party, Selector server_selector) {
		this.party = party;
		this.server_selector = server_selector;
		
	}
	@Override
	public void run() {
		
	}
	
	/* the main function   */
	public void listen() throws IOException {
		while (keep_on) {
			party.selector.select(time_out);
			if (woke_up) {
				WakeUp_handler();
			}
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
			if (start_song) {
				SendCommandToList(play_command, ready_for_song, true);
			}
		}
	}
	
	/* to handle */
	public void WakeUp_handler() {
		
	}
	
	public void register_for_selection(User user) throws IOException {
		SocketChannel channel = user.get_channel();
		channel.configureBlocking(false); /* maybe redundant? */
		channel.register(party.selector, SelectionKey.OP_READ, user);
	}
	
	/* we should decide about the command format */
	public void do_command(Command cmd, User user) {
		switch (cmd.cmd_type) {
			case AddSong:
				break;
			case SwapSongs:
				break;
			case DeleteSong:
				break;
			case GetReady:
				
			case PlaySong:
				break;
			case Resume:
				break;
		default:
			break;
		}
	}
	
	public void PlaySong(Command cmd) throws IOException {
		update_get_ready_command();
		ready_for_next_song = 0;
		ready_for_song = new ArrayList<User>();
		SendCommandToAll(get_ready_command);
	}
	
	public void GetReady(Command cmd, User user) {
		user.is_prepared = User.Song_preparation.prepared;
		ready_for_next_song++;
		ready_for_song.add(user);
		if (ready_for_next_song > 0.5*number_of_participents) {
			start_song = true;
		}
		
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
	
	public void Resume(Command cmd) {
		// ToDo
	}
	
	public void SendCommandToAdmins(Command cmd) throws IOException {
		SendCommandToList(cmd, party.admins, false);
	}
	
	public void SendCommandToAll(Command cmd) throws IOException {
		SendCommandToList(cmd, party.connected, false);
	}
	
	public void SendCommandToList(Command cmd, List<User> receivers,
			boolean remove_from_list) throws IOException {
		for (User receiver: receivers) {
			SocketChannel channel = receiver.get_channel();
			SendCommandToChannel(channel, null); /* for now */
			if (remove_from_list) {
				receivers.remove(receiver);
			}
		}
	}
	
	public void SendCommandToChannel(SocketChannel channel, JSONObject obj) throws IOException {
		byte[] Data = obj.toString().getBytes();
		readWriteAux.writeSocket(channel, Data);
	}
	
	public void update_get_ready_command() {
		// ToDo
		
	}
}
