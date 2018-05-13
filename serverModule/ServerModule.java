import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerModule {
	static List<Party> current_parties = new ArrayList<>();
	static List<User>  authenticated_users = new ArrayList<>();
	static int partyID = 0;
	public static void main(String[] args) throws IOException, JSONException{


		Selector selector = Selector.open();

		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(9999));

		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while(true){

			System.out.println("Waiting for select...");
			int readyChannels = selector.select();
			if(readyChannels == 0) continue;
			System.out.println("Number of selected keys: " + readyChannels);

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

			while(keyIterator.hasNext()) {

				SelectionKey key = keyIterator.next();

				// a connection was accepted by a ServerSocketChannel.
				if(key.isAcceptable()) {

					SocketChannel client = serverSocketChannel.accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out.println("Accepted new connection from client: " + client);

				} 
				// a channel is ready for reading
				else if (key.isReadable()) {
					handleReadCommands(selector, key);
				} 

				keyIterator.remove();
			}
		}
	}

	protected static void handleReadCommands(Selector selector, SelectionKey key) throws IOException, JSONException {
		SocketChannel client = (SocketChannel) key.channel();
		byte[] messageArray = readWriteAux.readSocket(client);
		Command cmd = new Command(messageArray);
		switch(cmd.cmd_type){

		case Authentication:
			String name = cmd.cmd_info.getString("Name");
			int id = cmd.cmd_info.getInt("Id");
			byte[] image = (byte[]) cmd.cmd_info.get("Image");
			User newUser = new User(name,id,image);
			authenticated_users.add(newUser);
			key.attach(newUser);
			
		case Nearby_Parties:
			sent_nearby_parties((User)key.attachment(),cmd.cmd_info);
			
		case Join:
			join_party((User)key.attachment(),cmd.cmd_info);
			break;
			
		case Create:
			create_party((User)key.attachment(),cmd.cmd_info,selector);
			break;
			
		case Disconnected:	
			removeIfAuthenticated(key);
			client.close();
			break;
		default:
			System.out.println("error cmdType not join/create/disconnected.");
			break;
		}
	}


	private static void removeIfAuthenticated(SelectionKey key) {
		User user = (User) key.attachment();
		if(user == null)
			return;
		authenticated_users.remove(user);

	}


	public static void sent_nearby_parties(User client,JSONObject info) {

	}


	/* creating a new party
	 * making admin the client who created the party */
	public static void create_party(User party_creator, JSONObject info, Selector selector) throws JSONException {
		String name = info.getString("Name");
		boolean is_private = info.getBoolean("private");
		Party party = new Party(name,partyID++,party_creator,is_private);
		new Thread(new Party_thread(party,selector)).start();

	}

	private static void join_party(User client, JSONObject cmd_info) throws JSONException {
		Party party = FindPartyByID(cmd_info.getInt("Id"));
		party.addRequest(client);
		party.selector.wakeup();	
	}

	private static Party FindPartyByID(int id) {
		for(Party party : current_parties){
			if(party.party_id == id){
				return party;
			}
		}
		return null;
	}
}
