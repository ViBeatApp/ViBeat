import java.nio.channels.SocketChannel;

public class Party_thread implements Runnable {
	
	long time_out;
	boolean keep_on;
	Party party;
	int number_of_participents;
	int ready_for_next_song; /* number of people ready for next sone */
	public boolean woke_up;
	int WakeUpReason;
	
	public void listen() {
		while (keep_on) {
			party.selector.select(time_out);
			if (woke_up) {
				WakeUp_handler();
			}
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
			while(keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				if (key.isReadable()) {
					SocketChannel channel  = selectionKey.channel();
					byte[] received_data = ReadWriteOx.read();
					Command cmd = new Command(received_data);
					do_command(cmd, key.attachment(););
				}
				keyIterator.remove();
			}
		}
	}
	
	/* to handle */
	public void WakeUp_handler() {
		
	}
	
	public void register_for_selection(User user) {
		SocketChannel channel = user.get_channel();
		channel.configureBlocking(false); /* maybe redundant? */
		SelectionKey key = channel.register(party.selector,
				SelectionKey.OP_READ, user);
	}
	
	/* we should decide about the command format */
	public void do_command(Command cmd, User user) {
		switch (cmd.get_type()) {
			case AddSong:
				break;
			case SwapSongs:
				break;
			case DeleteSong:
				break;
			case GetReady:
				break;
			case PlaySong:
				break;
			case Resume:
				break;
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
	
	public void GetReady(Command cmd) {
		// ToDo
	}
	
	public void PlaySong(Command cmd) {
		// ToDo
	}
	
	public void Resume(Command cmd) {
		// ToDo
	}
	
	public void SendCommandToAdmins(Command cmd) {
		SendCommandToList(cmd, party.admins);
	}
	
	public void SendCommandToAll(Command cmd) {
		SendCommandToList(cmd, party.connected);
	}
	
	public void SendCommandToList(Command cmd, List<User> receivers) {
		for (User receiver: receivers) {
			SocketChannel channel = receiver.get_channel();
			SendCommandToChannel(channel, cmd.get_command());
		}
	}
	
	public void SendCommandToChannel(SocketChannel channel, JSONObject obj) {
		byte[] Data = obj.toString();
		send_channel(channel, Data);
	}
	@Override
	public void run() {
		
		
	}

}
