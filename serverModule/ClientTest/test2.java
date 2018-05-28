package ClientTest;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import serverObjects.Command;
import serverObjects.CommandType;
import serverObjects.ReadWriteAux;
import serverObjects.jsonKey;

public class test2 implements Runnable {
	SocketChannel socket;
	String name;
	public test2(SocketChannel socket, String string) {
		this.socket = socket;
		this.name = string;
	}

	public void run() {
		try {
			while(true) {
				Command rep = ReadWriteAux.readSocket(socket);
				if(rep.cmd_type == CommandType.DISCONNECTED)
					break;
				System.out.println("reply to " + name + ": " + rep.cmd_type + " info:" + rep.cmd_info);
			}
		} catch (Exception e) {	
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		//check_enum();
		System.out.println("ido");
		SocketChannel ido_socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
		System.out.println("first connection");
		
		(new Thread(new test2(ido_socket,"ido"))).start();
		
		Command auth_ido = new Command(CommandType.AUTHENTICATION);
		auth_ido.cmd_info.put("NAME", "Ido");
		auth_ido.cmd_info.put("USER_ID", 0);
		auth_ido.cmd_info.put("IMAGE", "abcd");
		ReadWriteAux.writeSocket(ido_socket, auth_ido);
		
		Command create = new Command(CommandType.CREATE);
		create.cmd_info.put("NAME", "Ido's party");
		create.cmd_info.put("IS_PRIVATE", false);
		ReadWriteAux.writeSocket(ido_socket, create);
		
		System.out.println("tomer");
		SocketChannel tomer_socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
		(new Thread(new test2(tomer_socket,"tomer"))).start();
		Command auth_tomer = new Command(CommandType.AUTHENTICATION);
		auth_tomer.cmd_info.put("NAME", "Tomer");
		auth_tomer.cmd_info.put("USER_ID", 1);
		auth_tomer.cmd_info.put("IMAGE", "aaa");
		ReadWriteAux.writeSocket(tomer_socket, auth_tomer);
		//Thread.sleep(1000);
		
		Command search_party = new Command (CommandType.SEARCH_PARTY);
		search_party.setAttribute(jsonKey.NAME, "Ido");
		ReadWriteAux.writeSocket(tomer_socket, search_party);
		Thread.sleep(2000);
		
		Command join_party = new Command (CommandType.JOIN);
		join_party.setAttribute(jsonKey.PARTY_ID, 0);
		ReadWriteAux.writeSocket(tomer_socket, join_party);
		
		Command add_song = new Command(CommandType.ADD_SONG);
		System.out.println("ido - sending add_song");
		add_song.cmd_info.put("URL", "www.youtube1");
		ReadWriteAux.writeSocket(ido_socket, add_song);	
		//rep = readWriteAux.readSocket(ido_socket);
		//System.out.println("reply:" + rep.cmd_type + " info:" + rep.cmd_info);
		
		ido_socket.close();
		
		System.out.println("ido closed his socket and go to sleep");
		Thread.sleep(1000);
		
		System.out.println("ido is opening new connection");
		ido_socket = SocketChannel.open(new InetSocketAddress("localhost", 9999));
		(new Thread(new test2(ido_socket,"ido2"))).start();
		System.out.println("second connection");
		ReadWriteAux.writeSocket(ido_socket, auth_ido);
				
		Thread.sleep(5000);
		

	}
	
//	private static void accept_new_participent(SocketChannel socket) throws Exception {
//		System.out.println("admin1 - in accept_new_participent");
//		Thread.sleep(1000);
//		Command reply = readWriteAux.readSocket(socket);
//		System.out.println("admin2 - command: " + reply.cmd_type + " info:" + reply.cmd_info);
//		Command confirm_req = new Command(CommandType.CONFIRM_REQUEST);
//		confirm_req.setAttribute(jsonKey.USER_ID, 1);
//		confirm_req.setAttribute(jsonKey.CONFIRMED, true);
//		readWriteAux.writeSocket(socket, confirm_req);
//		//Thread.sleep(1000);
//		
//		reply = readWriteAux.readSocket(socket);
//		System.out.println("admin3 - command: " + reply.cmd_type + " info:" + reply.cmd_info);
//	}
//	
	public static void manage_songs(SocketChannel socket) throws Exception {
		Command add_song = new Command(CommandType.ADD_SONG);
		Command reply;
		System.out.println("admin - sending new_command");
		add_song.cmd_info.put("URL", "www.youtube1");
		ReadWriteAux.writeSocket(socket, add_song);
		//Thread.sleep(1000);
		System.out.println("admin - send URL1");
		reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("admin - command: " + reply.cmd_type + " info:" + reply.cmd_info);
		
		add_song.cmd_info.put("URL", "www.youtube2");
		ReadWriteAux.writeSocket(socket, add_song);
		reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("command: " + reply.cmd_type + " info:" + reply.cmd_info);
	}
	
	
	public static void get_command(SocketChannel socket, CommandType type,String user_name) throws Exception {
		while (true) {
			Command reply = ReadWriteAux.readSocket(socket);
			System.out.println(user_name + " - command: " + reply.cmd_type + " info:" + reply.cmd_info);
			if (reply.cmd_type == type) {
				break;
			}
		}
	}
	
	public static void play_protocol_admin(SocketChannel socket) throws Exception {
		Command play_cmd = new Command(CommandType.PLAY_SONG);
		play_cmd.setAttribute(jsonKey.TRACK_ID, 0);
		play_cmd.setAttribute(jsonKey.OFFSET, 0);
		ReadWriteAux.writeSocket(socket, play_cmd);
		get_command(socket, CommandType.GET_READY, "admin");
		
		Command ready_command = new Command(CommandType.IM_READY);
		ready_command.setAttribute(jsonKey.TRACK_ID, 0);
		ReadWriteAux.writeSocket(socket, ready_command);
		
		get_command(socket, CommandType.PLAY_SONG, "admin");
		
		Thread.sleep(1000);
		Command pause_cmd = new Command(CommandType.PAUSE);
		pause_cmd.setAttribute(jsonKey.TRACK_ID, 0);
		ReadWriteAux.writeSocket(socket, pause_cmd);
		
		get_command(socket, CommandType.PAUSE, "admin");
	}
}
