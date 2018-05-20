import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.json.JSONException;

public class test {
	public static void main(String[] args) throws Exception {
		//check_enum();
		SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
		System.out.println("create new party");
		
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.cmd_info.put("NAME", "Ido");
		auth.cmd_info.put("USER_ID", 0);
		auth.cmd_info.put("IMAGE", "abcd");
		readWriteAux.writeSocket(socket, auth);
		
		Command create = new Command(CommandType.CREATE);
		create.cmd_info.put("NAME", "Ido's party");
		create.cmd_info.put("IS_PRIVATE", false);
		readWriteAux.writeSocket(socket, create);
		//System.out.println(readWriteAux.readSocket(socket));
		check_manage_songs(socket);
	}
	
	public static void check_manage_songs(SocketChannel socket) throws Exception {
		Command add_song = new Command(CommandType.ADD_SONG);
		Command reply;
		System.out.println("sending new_command");
		add_song.cmd_info.put("URL", "www.youtube1");
		readWriteAux.writeSocket(socket, add_song);
		System.out.println("send URL1");
		reply = readWriteAux.readSocket(socket);
		System.out.println("got reply");
		System.out.println(reply);
		
		add_song.cmd_info.put("URL", "www.youtube2");
		readWriteAux.writeSocket(socket, add_song);
		reply = readWriteAux.readSocket(socket);
		System.out.println("got reply");
		System.out.println(reply);
		
		add_song.cmd_info.put("URL", "www.youtube3");
		readWriteAux.writeSocket(socket, add_song);
		reply = readWriteAux.readSocket(socket);
		System.out.println("got reply");
		System.out.println(reply);
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
			System.out.println("	" + playlist.songs.get(i).name);	
		}
	}

}
