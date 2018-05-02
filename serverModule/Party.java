package server_module;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Party {
	List<Socket> participant = new ArrayList<Socket>();
	List<Socket> adminSocket = new ArrayList<Socket>();
	List<String> songs_list = new ArrayList<String>();
	String partyName;
	
	public Party(Socket adminSocket,String partyName) {
		super();
		this.adminSocket.add(adminSocket);
		this.participant.add(adminSocket);
		this.partyName = partyName;
	}
	
	public void addPerson(Socket newOne) {
		this.participant.add(newOne);
	}
	
	public void addSong(String song) {
		this.songs_list.add(song);
	}
	
	public String getSong() {
		if(this.songs_list.size() == 0) return null;
		return this.songs_list.get(0);
	}
}
