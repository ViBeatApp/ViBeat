import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class test implements Runnable {
	
	public int user_id;
	public String user_name;
	
	public test(int user_id, String user_name) {
		this.user_id = user_id;
		this.user_name = user_name;
	}
	
	public void play_protocol_user(SocketChannel socket) throws Exception {
		get_command(socket, CommandType.GET_READY, "user" + user_id);		
		send_ready_command(socket, 0);
		
		get_command(socket, CommandType.PLAY_SONG, "user" + user_id);

		// next cycle
		if (user_id == 1) {
			Thread.sleep(500);	
			get_command(socket, CommandType.PAUSE, "user" + user_id);
			System.out.println("------------------------ user1 -------------------");
			get_command(socket, CommandType.GET_READY, "user" + user_id);
			send_ready_command(socket, 0);
			get_command(socket, CommandType.PLAY_SONG, "user" + user_id);
		}
	}

	public void join_party(SocketChannel socket) throws Exception {
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.cmd_info.put("NAME", user_name);
		auth.cmd_info.put("USER_ID", user_id);
		auth.cmd_info.put("IMAGE", "aaa");
		readWriteAux.writeSocket(socket, auth);
		//Thread.sleep(1000);
		
		Command join_party = new Command (CommandType.JOIN);
		join_party.setAttribute(jsonKey.PARTY_ID.name(), 0);
		readWriteAux.writeSocket(socket, join_party);
		//Thread.sleep(1000);
		
		System.out.println("user"+user_id + "- asked to join");
		get_command(socket, CommandType.GET_READY, "user" + user_id);
	}
	@Override
	public void run() {
		try {
			System.out.println("----------- new user-id = " + user_id + " -------------");
			//SocketChannel socket = SocketChannel.open(new InetSocketAddress("192.168.43.238", 2000));
			SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 2000));
			join_party(socket);
			play_protocol_user(socket);
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	public static void launch_user(int id, String name) {
		(new Thread(new test(id, name))).start();
	}
	
	public static void main(String[] args) throws Exception {
		//check_enum();
		//SocketChannel socket = SocketChannel.open(new InetSocketAddress("192.168.43.238", 2000));
		SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 2000));
		System.out.println("create new party");
		
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.cmd_info.put("NAME", "Ido");
		auth.cmd_info.put("USER_ID", 0);
		auth.cmd_info.put("IMAGE", "abcd");
		readWriteAux.writeSocket(socket, auth);
		Command create = new Command(CommandType.CREATE);
		create.cmd_info.put("NAME", "Ido's party");
		create.cmd_info.put("IS_PRIVATE", true);
		readWriteAux.writeSocket(socket, create);
		
		manage_songs(socket);
		
		launch_user(1, "Tomer");
		
		Thread.sleep(1000);
		accept_new_participent(socket, 1);
		Thread.sleep(1000);
		play_protocol_admin(socket);
		Thread.sleep(2000);
	}
	
	public static void accept_a_lot(SocketChannel socket, int first_guest_id, int new_guests_n) throws Exception {
		for (int i = 0; i < new_guests_n; i++) {
			int current_guest_id = first_guest_id+i;
			launch_user(current_guest_id, "Idan" + Integer.toString(current_guest_id));
			Thread.sleep(20);
		}
		int accept_counter = 0;
		while (accept_counter <= new_guests_n) {
			Command reply = get_command(socket, CommandType.SYNC_PARTY, "Ido-admin");
			JSONArray req = reply.getSyncPartyAttribute(jsonKey.REQUESTS.name());
			for (int j = 0; j < req.length(); j++) {
				JSONObject usr = req.getJSONObject(j);
				int new_req = (int) usr.get(jsonKey.USER_ID.name());
				send_conf_command(socket, new_req);
				accept_counter++;
				System.out.println("accept user:" +  Integer.toString(new_req));
			}
		}
	}

	private static void accept_new_participent(SocketChannel socket, int user_id) throws Exception {
		System.out.println("admin1 - in accept_new_participent");
		Thread.sleep(10);
		Command reply = readWriteAux.readSocket(socket);
		System.out.println("admin2 - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		send_conf_command(socket, user_id);
		//Thread.sleep(1000);
		
		reply = readWriteAux.readSocket(socket);
		System.out.println("admin3 - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		
	}
	
	public static void manage_songs(SocketChannel socket) throws Exception {
		Command add_song = new Command(CommandType.ADD_SONG);
		Command reply;
		System.out.println("admin - sending new_command");
		add_song.cmd_info.put("URL", "www.youtube1");
		readWriteAux.writeSocket(socket, add_song);
		
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
	
	
	public static Command get_command(SocketChannel socket, CommandType type,String user_name) throws Exception {
		Command reply;
		while (true) {
			reply = readWriteAux.readSocket(socket);
			System.out.println(user_name + " - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
			if (reply.cmd_type == type) {
				break;
			}
		}
		return reply;
	}
	
	public static void send_conf_command(SocketChannel socket, int user_id) throws Exception {
		Command confirm_req = new Command(CommandType.CONFIRM_REQUEST);
		confirm_req.setAttribute(jsonKey.USER_ID.name(), user_id);
		confirm_req.setAttribute(jsonKey.CONFIRMED.name(), true);
		readWriteAux.writeSocket(socket, confirm_req);
	}
	
	public static void send_play_command(SocketChannel socket, int offset, int track_id) throws Exception {
		Command play_cmd = new Command(CommandType.PLAY_SONG);
		play_cmd.setAttribute(jsonKey.TRACK_ID.name(), track_id);
		play_cmd.setAttribute(jsonKey.OFFSET.name(), offset);
		readWriteAux.writeSocket(socket, play_cmd);
	}
	
	public static void send_ready_command(SocketChannel socket, int track_id) throws Exception {
		Command ready_command = new Command(CommandType.IM_READY);
		ready_command.setAttribute(jsonKey.TRACK_ID.name(), track_id);
		readWriteAux.writeSocket(socket, ready_command);
	}
	
	public static void send_pause_command(SocketChannel socket, int track_id) throws Exception {
		Command pause_cmd = new Command(CommandType.PAUSE);
		pause_cmd.setAttribute(jsonKey.TRACK_ID.name(), 0);
		readWriteAux.writeSocket(socket, pause_cmd);
	}
	
	public static void play_protocol_admin(SocketChannel socket) throws Exception {
		send_play_command(socket, 0, 0);
		get_command(socket, CommandType.GET_READY, "admin");
		
		send_ready_command(socket, 0);
		get_command(socket, CommandType.PLAY_SONG, "admin");
		
		Thread.sleep(1500);
		
		// some users entered the party
		accept_a_lot(socket, 2, 3);
		
		send_pause_command(socket, 0);
		get_command(socket, CommandType.PAUSE, "admin");
		Thread.sleep(1000);
		launch_user(5, "Dana");
		accept_new_participent(socket, 2);
		Thread.sleep(1000);
		
		// next cycle
		System.out.println("------------------------ admin -------------------");
		send_play_command(socket, 0, 1);
		get_command(socket, CommandType.GET_READY, "admin");
		send_ready_command(socket, 0);
		get_command(socket, CommandType.PLAY_SONG, "admin");
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
