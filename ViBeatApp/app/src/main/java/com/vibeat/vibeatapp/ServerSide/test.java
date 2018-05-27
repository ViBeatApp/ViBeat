package com.vibeat.vibeatapp.ServerSide;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.json.JSONArray;
import org.json.JSONObject;

public class test implements Runnable {
	
	public int user_id;
	public String user_name;
	
	public test(int user_id, String user_name) {
		this.user_id = user_id;
		this.user_name = user_name;
	}
	
	public void play_protocol_user(SocketChannel socket) throws Exception {
		wait_for_command(socket, CommandType.GET_READY, "user" + user_id);		
		send_ready_command(socket, 0);
		
		wait_for_command(socket, CommandType.PLAY_SONG, "user" + user_id);

		// next cycle
		if (user_id == 1) {
			Thread.sleep(500);	
			wait_for_command(socket, CommandType.PAUSE, "user" + user_id);
			System.out.println("------------------------ user1 -------------------");
			wait_for_command(socket, CommandType.GET_READY, "user" + user_id);
			send_ready_command(socket, 0);
			wait_for_command(socket, CommandType.PLAY_SONG, "user" + user_id);
		}
	}
	
	public void move_to_next_song_client(SocketChannel socket) throws Exception {
		Command cmd = wait_for_command(socket, CommandType.GET_READY, "user" + user_id);
		int track_id = cmd.getIntAttribute(jsonKey.TRACK_ID);
		Thread.sleep(500);
		send_ready_command(socket, track_id);
		wait_for_command(socket, CommandType.PLAY_SONG, "user" + user_id);
	}

	public void join_party(SocketChannel socket) throws Exception {
		Command auth = new Command(CommandType.AUTHENTICATION);
		auth.setAttribute(jsonKey.NAME, user_name);
		auth.setAttribute(jsonKey.USER_ID, user_id);
		auth.setAttribute(jsonKey.IMAGE, "aaa");
		ReadWriteAux.writeSocket(socket, auth);
		//Thread.sleep(1000);
		
		Command join_party = new Command (CommandType.JOIN);
		join_party.setAttribute(jsonKey.PARTY_ID, 0);
		ReadWriteAux.writeSocket(socket, join_party);
		//Thread.sleep(1000);
		
		System.out.println("user"+user_id + "- asked to join");
		wait_for_command(socket, CommandType.GET_READY, "user" + user_id);
	}
	@Override
	public void run() {
		try {
			System.out.println("----------- new user-id = " + user_id + " -------------");
			//SocketChannel socket = SocketChannel.open(new InetSocketAddress("192.168.43.238", 2000));
			SocketChannel socket = SocketChannel.open(new InetSocketAddress("localhost", 2000));
			join_party(socket);
			play_protocol_user(socket);
			move_to_next_song_client(socket);
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
		auth.setAttribute(jsonKey.NAME, "Ido");
		auth.setAttribute(jsonKey.USER_ID, 0);
		auth.setAttribute(jsonKey.IMAGE, "abcd");
		ReadWriteAux.writeSocket(socket, auth);
		Command create = new Command(CommandType.CREATE);
		create.setAttribute(jsonKey.NAME, "Ido's party");
		create.setAttribute(jsonKey.IS_PRIVATE, true);
		ReadWriteAux.writeSocket(socket, create);
		
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
		while (accept_counter < new_guests_n) {
			Command reply = wait_for_command(socket, CommandType.SYNC_PARTY, "Ido-admin");
			JSONArray req = reply.getSyncPartyAttribute(jsonKey.REQUESTS);
			for (int j = 0; j < req.length(); j++) {
				JSONObject usr = req.getJSONObject(j);
				int new_req = (int) usr.get(jsonKey.USER_ID.name());
				send_conf_command(socket, new_req);
				accept_counter++;
				System.out.println("accepting user:" +  Integer.toString(new_req));
			}
		}
		System.out.println("----- admin finished accepting, accepted: " + accept_counter);
	}

	private static void accept_new_participent(SocketChannel socket, int user_id) throws Exception {
		System.out.println("admin1 - in accept_new_participent");
		Thread.sleep(10);
		Command reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin2 - command: " + reply.cmd_type + " info:" + reply.cmd_info);
		send_conf_command(socket, user_id);
		//Thread.sleep(1000);
		
		reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin3 - command: " + reply.cmd_type + " info:" + reply.cmd_info);
		
	}
	
	public static void manage_songs(SocketChannel socket) throws Exception {
		Command add_song = new Command(CommandType.ADD_SONG);
		Command reply;
		System.out.println("admin - sending new_command");
		add_song.setAttribute(jsonKey.URL, "www.youtube1");
		ReadWriteAux.writeSocket(socket, add_song);
		
		System.out.println("admin - send URL1");
		reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("admin - command: " + reply.cmd_type + " info:" + reply.cmd_info);
		
		add_song.setAttribute(jsonKey.URL, "www.youtube2");
		ReadWriteAux.writeSocket(socket, add_song);
		reply = ReadWriteAux.readSocket(socket);
		System.out.println("admin - got reply");
		System.out.println("command: " + reply.cmd_type + " info:" + reply.cmd_info);
	}
	
	
	public static Command wait_for_command(SocketChannel socket, CommandType type,String user_name) throws Exception {
		Command reply;
		while (true) {
			reply = ReadWriteAux.readSocket(socket);
			System.out.println(user_name + " - command: " + reply.cmd_type + " info:" + reply.cmd_info);
			if (reply.cmd_type == type) {
				break;
			}
		}
		return reply;
	}
	
	public static void send_conf_command(SocketChannel socket, int user_id) throws Exception {
		Command confirm_req = new Command(CommandType.CONFIRM_REQUEST);
		confirm_req.setAttribute(jsonKey.USER_ID, user_id);
		confirm_req.setAttribute(jsonKey.CONFIRMED, true);
		ReadWriteAux.writeSocket(socket, confirm_req);
	}
	
	public static void send_play_command(SocketChannel socket, int offset, int track_id) throws Exception {
		Command play_cmd = new Command(CommandType.PLAY_SONG);
		play_cmd.setAttribute(jsonKey.TRACK_ID, track_id);
		play_cmd.setAttribute(jsonKey.OFFSET, offset);
		ReadWriteAux.writeSocket(socket, play_cmd);
	}
	
	public static void send_ready_command(SocketChannel socket, int track_id) throws Exception {
		Command ready_command = new Command(CommandType.IM_READY);
		ready_command.setAttribute(jsonKey.TRACK_ID, track_id);
		ReadWriteAux.writeSocket(socket, ready_command);
	}
	
	public static void send_pause_command(SocketChannel socket, int track_id) throws Exception {
		Command pause_cmd = new Command(CommandType.PAUSE);
		pause_cmd.setAttribute(jsonKey.TRACK_ID, 0);
		ReadWriteAux.writeSocket(socket, pause_cmd);
	}
	
	public static void initiate_song(SocketChannel socket, int track_id) throws Exception {
		send_play_command(socket, 0, track_id);
		wait_for_command(socket, CommandType.GET_READY, "admin");
		Thread.sleep(1500);
		send_ready_command(socket, 0);
		wait_for_command(socket, CommandType.PLAY_SONG, "admin");
	}
	
	public static void play_protocol_admin(SocketChannel socket) throws Exception {
		System.out.println("------ admin initiate song #1 ------");
		initiate_song(socket, 0);
		Thread.sleep(1500);
		
		// some users entered the party
		accept_a_lot(socket, 2, 3);
		Thread.sleep(1000);
		System.out.println("------ admin initiate song #2 ------");
		initiate_song(socket, 1);
	}
	
	public static void check_pause(SocketChannel socket) throws Exception {
		send_pause_command(socket, 0);
		wait_for_command(socket, CommandType.PAUSE, "admin");
		Thread.sleep(1000);
		launch_user(5, "Dana");
		accept_new_participent(socket, 2);
		Thread.sleep(1000);
		
		// next cycle
		System.out.println("------------------------ admin -------------------");
		send_play_command(socket, 0, 1);
		wait_for_command(socket, CommandType.GET_READY, "admin");
		send_ready_command(socket, 0);
		wait_for_command(socket, CommandType.PLAY_SONG, "admin");
	}
}
