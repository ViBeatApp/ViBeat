package server_module;
import server_module.ServerModule;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



public class Client_Handler implements Runnable {
	Person client;

	Client_Handler(Socket sock) {
		this.client = new Person(sock);
	}

	public void run() {
		try {
			DataOutputStream toClient = new DataOutputStream(this.client.sock.getOutputStream());
			DataInputStream fromClient = new DataInputStream(this.client.sock.getInputStream());
			System.out.println("Accepted connection : " + this.client.sock);

			identification(fromClient,toClient);					//join to some party.
			communicateWithServer(fromClient,toClient);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {}
	}
	
	public void identification(DataInputStream fromClient, DataOutputStream toClient) throws IOException {
		String msg;
		boolean done = false;
		while (!done) {
			done = true;
			toClient.writeUTF("create or join?");
			msg = fromClient.readUTF();
			switch(msg) {

			case "create":
				toClient.writeUTF("enter name:");
				ServerModule.addParty(new partyThread(this.client.sock, fromClient.readUTF()));
				break;

			case "join":
				toClient.writeUTF("enter name:");	
				partyThread party_thread;
				if((party_thread = ServerModule.findPartyByName(fromClient.readUTF())) == null) { 	//name;
					toClient.writeUTF("there is no such party! loser");
					done = true;
				}
				else sendSignalJoinPerson(party_thread);
				break;
			}
		}
	}
	
	public void communicateWithServer(DataInputStream fromClient,DataOutputStream toClient) throws IOException {
		String msg;
		boolean done = false;
		while (!done) {
			toClient.writeUTF("add song/start/play/pause/downloadNextSong/move to");
			msg = fromClient.readUTF();
			switch(msg) {
			
			case "add song":
				//if(!client.isAdmin)
				handleAddSong(fromClient,toClient);
				break;

			case "start":
				done = handleStartSong(fromClient,toClient);
				break;

			case "play":
				done = handlePlaySong(fromClient,toClient);
				break;

			case "pause":
				done = handlePauseSong(fromClient,toClient);
				break;

			case "downloadNextSong":
				done = handle_downloadNextSong(fromClient,toClient);
				break;

			case "move to song":
				done = handle_moveSong(fromClient,toClient);
				break;
			}
		}
	}

	private void handleAddSong(DataInputStream fromClient, DataOutputStream toClient) throws IOException {
		toClient.writeUTF("enter song name:");
		this.client.party_thread.party.addSong(fromClient.readUTF()); //need to send signal

	}

	private boolean handleStartSong(DataInputStream fromClient, DataOutputStream toClient) throws IOException {		

		if(this.client.party_thread.party.getSong() == null) { 							//name;
			toClient.writeUTF("songs list is empty, tell the admin");
		}
		else {
			toClient.writeUTF("done");
			sendSignalToSendFile();
			return true;
		}
		return false;

	}

	private boolean handlePlaySong(DataInputStream fromAdmin, DataOutputStream toAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean handlePauseSong(DataInputStream fromAdmin, DataOutputStream toAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean handle_downloadNextSong(DataInputStream fromAdmin, DataOutputStream toAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean handle_moveSong(DataInputStream fromAdmin, DataOutputStream toAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	public void sendSignalToSendFile() {}

	private void sendSignalJoinPerson(partyThread party_thread) {
		// TODO Auto-generated method stub

	}


}