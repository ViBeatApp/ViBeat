package server_module;

import java.net.Socket;

public class Person {
	Socket sock;
	boolean isAdmin;
	partyThread party_thread;
	
	public Person(Socket sock) {
		super();
		this.sock = sock;
	}
}

