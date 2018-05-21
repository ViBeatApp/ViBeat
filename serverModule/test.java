import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.json.JSONException;

public class test implements Runnable {

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
		System.out.println("got reply - joined party?");
		System.out.println("command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
	}
	@Override
	public void run() {
		try {
			SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
			join_party(socket);
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
	}
	
	private static void accept_new_participent(SocketChannel socket) throws Exception {
		System.out.println("admin - in accept_new_participent");
		Command reply = readWriteAux.readSocket(socket);
		System.out.println("admin - command: " + reply.cmd_type.name() + " info:" + reply.cmd_info);
		Command confirm_req = new Command(CommandType.CONFIRM_REQUEST);
		confirm_req.setAttribute(jsonKey.USER_ID.name(), 1);
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
		Thread.sleep(1000);
	}

	public static void printInfo(Party party){
		System.out.println("party name: " + party.party_name);
		System.out.println("party id: " + party.party_id);
		System.out.println("party's admins: ");
		printUserList(party.admins);
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
