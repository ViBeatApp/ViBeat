package server_module;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerModule {

	public final static int SOCKET_PORT = 8000;  // you may change this
	public final static List<partyThread> parties_Threads = new ArrayList<partyThread>();
	public final static List<Thread> sockets_Threads = new ArrayList<Thread>();
	public final static List<Socket> Sockets = new ArrayList<Socket>();
	@SuppressWarnings("resource")
	
	public static void main (String [] args ) throws IOException {
		
		ServerSocket servsock = new ServerSocket(SOCKET_PORT);
		while (true) {
			System.out.println("Waiting...");
			//get connection
			Socket sock = servsock.accept();
			Sockets.add(sock);
			sockets_Threads.add(new Thread(new Client_Handler(sock)));
			sockets_Threads.get(sockets_Threads.size()-1).start();
		}

	}
	
	public static void addParty(partyThread partyThread) {
		parties_Threads.add(partyThread);
	}
	
	public static partyThread findPartyByName(String msg) {
		 for (partyThread party_thread : parties_Threads) {
			 	
		        if (party_thread.party.partyName.equals(msg)) {
		            return party_thread;
		        }
		 }
		 return null;
	}
}
