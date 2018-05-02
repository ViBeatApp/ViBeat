package server_module;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class partyThread extends Thread {
	Party party;
	
	partyThread(Socket adminSocket,String partyName) {
		super();
		this.party = new Party(adminSocket, partyName);
	}
	public void run() {}
	
	public void sendPlayAll() {}
	public void sendPauseAll() {}
	public void sendNextSongAll() {}
	
	public static void sendFileToClient(DataOutputStream toClient, String pathName) throws IOException {
		//send size()
		int size = (int) new File(pathName).length();
		toClient.writeUTF("size");
		toClient.writeInt(size);          
		System.out.println("send size : " + size + "\n");

		//send file
		toClient.writeUTF("file");
		DataInputStream file_reader = new DataInputStream(new FileInputStream(pathName));
		copyInputToOutput(file_reader,toClient,size);
		toClient.flush();

	}

	public static void copyInputToOutput( DataInputStream input, DataOutputStream output,int size ) {
		try {
			byte[] buf = new byte[65536];
			int len;
			int written = 0;
			while(written < size && (len=input.read(buf,written,size-written))>0){
				written += len;
				output.write(buf,0,len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	
	