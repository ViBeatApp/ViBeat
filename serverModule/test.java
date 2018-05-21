import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class test implements Runnable {
	
	public void play_protocol_user(SocketChannel socket) throws Exception {
		get_command(socket, CommandType.GET_READY, "user");
		
		Command ready_command = new Command(CommandType.IM_READY);
		ready_command.setAttribute(jsonKey.TRACK_ID.name(), 0);
		readWriteAux.writeSocket(socket, ready_command);
		
		get_command(socket, CommandType.PLAY_SONG, "user");
		
		Thread.sleep(1000);
		Command pause_cmd = new Command(CommandType.PAUSE);
		pause_cmd.setAttribute(jsonKey.TRACK_ID.name(), 0);
		readWriteAux.writeSocket(socket, pause_cmd);
		
		get_command(socket, CommandType.PAUSE, "user");
	}

	public void join_party(SocketChannel socket) throws Exception {
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.cmd_info.put("NAME", "Tomer");
		auth.cmd_info.put("USER_ID", 1);
		auth.cmd_info.put("IMAGE", "aaa");
		readWriteAux.writeSocket(socket, auth);
		//Thread.sleep(1000);
		
		Command join_party = new Command (CommandType.JOIN);
		join_party.setAttribute(jsonKey.PARTY_ID.name(), 0);
		readWriteAux.writeSocket(socket, join_party);
		//Thread.sleep(1000);
		
		System.out.println("client - asked to join");
		Command reply = readWriteAux.readSocket(socket);
		System.out.println("client - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		
		reply = readWriteAux.readSocket(socket);
		System.out.println("client get-ready: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
	}
	@Override
	public void run() {
		try {
			SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
			join_party(socket);
			play_protocol_user(socket);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		//check_enum();
		SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
		System.out.println("create new party");
		
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.cmd_info.put("NAME", "Ido");
		auth.cmd_info.put("USER_ID", 0);
		auth.cmd_info.put("IMAGE", "abcd");
		readWriteAux.writeSocket(socket, auth);
		//Thread.sleep(1000);
		Command create = new Command(CommandType.CREATE);
		create.cmd_info.put("NAME", "Ido's party");
		create.cmd_info.put("IS_PRIVATE", true);
		readWriteAux.writeSocket(socket, create);
		//Thread.sleep(1000);
		//System.out.println(readWriteAux.readSocket(socket));
		manage_songs(socket);
		
		(new Thread(new test())).start();
		accept_new_participent(socket);
		Thread.sleep(1000);
		play_protocol_admin(socket);
		Thread.sleep(1000);
	}
	
	private static void accept_new_participent(SocketChannel socket) throws Exception {
		System.out.println("admin - in accept_new_participent");
		Command reply = readWriteAux.readSocket(socket);
		System.out.println("admin - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		Command confirm_req = new Command(CommandType.CONFIRM_REQUEST);
		confirm_req.setAttribute(jsonKey.USER_ID.name(), 1);
		confirm_req.setAttribute(jsonKey.CONFIRMED.name(), true);
		readWriteAux.writeSocket(socket, confirm_req);
		//Thread.sleep(1000);
		
		reply = readWriteAux.readSocket(socket);
		System.out.println("admin - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
	}
	
	public static void manage_songs(SocketChannel socket) throws Exception {
		Command add_song = new Command(CommandType.ADD_SONG);
		Command reply;
		System.out.println("admin - sending new_command");
		add_song.cmd_info.put("URL", "www.youtube1");
		readWriteAux.writeSocket(socket, add_song);
		//Thread.sleep(1000);
		System.out.println("admin - send URL1");
		reply = readWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("admin - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		
		add_song.cmd_info.put("URL", "www.youtube2");
		readWriteAux.writeSocket(socket, add_song);
		reply = readWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
	}
	
	
	public static void get_command(SocketChannel socket, CommandType type,String user_name) throws Exception {
		while (true) {
			Command reply = readWriteAux.readSocket(socket);
			System.out.println("command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
			if (reply.cmd_type == type) {
				break;
			}
		}
	}
	
	public static void play_protocol_admin(SocketChannel socket) throws Exception {
		Command play_cmd = new Command(CommandType.PLAY_SONG);
		play_cmd.setAttribute(jsonKey.TRACK_ID.name(), 0);
		play_cmd.setAttribute(jsonKey.OFFSET.name(), 0);
		readWriteAux.writeSocket(socket, play_cmd);
		get_command(socket, CommandType.GET_READY, "admin");
		
		Command ready_command = new Command(CommandType.IM_READY);
		ready_command.setAttribute(jsonKey.TRACK_ID.name(), 0);
		readWriteAux.writeSocket(socket, ready_command);
		
		get_command(socket, CommandType.PLAY_SONG, "admin");
		
		Thread.sleep(1000);
		Command pause_cmd = new Command(CommandType.PAUSE);
		pause_cmd.setAttribute(jsonKey.TRACK_ID.name(), 0);
		readWriteAux.writeSocket(socket, pause_cmd);
		
		get_command(socket, CommandType.PAUSE, "admin");
	}

	public static void printInfo(Party party){
		System.out.println("party name: " + party.party_name);
		System.out.println("party id: " + party.party_id);
		System.out.println("party's admins: ");
		//printUserList(party.admins);
		System.out.println("party's clients: ");
		printUserList(party.connected);
		System.out.println("party's requests: ");
		printUserList(party.request);
		System.out.println("party's playlist: ");
		printPlayList(party.playlist);
		System.out.println("\n\n");
	}

	private static void printUserList(List<User> list) {
		for(int i = 0; i < list.size(); ++i){
			System.out.println("	" + list.get(i).name);	
		}
	}
	
	private static void printPlayList(Playlist playlist) {
		for(int i = 0; i < playlist.songs.size(); ++i){
			System.out.println("	" + playlist.songs.get(i).url);	
		}
	}
}
