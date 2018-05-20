import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerModule {
	static List<Party> current_parties = new ArrayList<>();
	static List<User>  authenticated_users = new ArrayList<>();
	static List<User>  comeback_users = new ArrayList<>();
	static int partyID = 0;
	static Selector selector;

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
			handle_comeback_users();
			System.out.println("Number of selected keys: " + readyChannels);

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

			while(keyIterator.hasNext()) {

				SelectionKey key = keyIterator.next();

				// a connection was accepted by a ServerSocketChannel.
				if(key.isAcceptable()) {

					SocketChannel socket = serverSocketChannel.accept();
					socket.configureBlocking(false);
					socket.register(selector, SelectionKey.OP_READ);
					System.out.println("Accepted new connection from client: " + socket);

				} 
				// a channel is ready for reading
				else if (key.isReadable()) {
					handleReadCommands(selector, key);
				} 
				keyIterator.remove();
			}
		}
	}

	private static void handle_comeback_users() throws ClosedChannelException {
		Iterator<User> iter = comeback_users.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			user.channel.register(selector, SelectionKey.OP_READ);
			authenticated_users.add(user);
			iter.remove();
		}
		
	}

	protected static void handleReadCommands(Selector selector, SelectionKey key) throws IOException, JSONException {
		SocketChannel client = (SocketChannel) key.channel();
		Command cmd = readWriteAux.readSocket(client);
		System.out.println("command " + cmd.cmd_type + ", info" + cmd.cmd_info);
		switch(cmd.cmd_type){
		
		case AUTHENTICATION:
			String name = cmd.cmd_info.getString(jsonKey.NAME.name());
			int id = cmd.cmd_info.getInt(jsonKey.USER_ID.name());
			byte[] image = cmd.cmd_info.getString(jsonKey.IMAGE.name()).getBytes();
			
			User newUser = new User(name,id,image, client);
			authenticated_users.add(newUser);
			key.attach(newUser);					///check this
			break;

		case NEARBY_PARTIES:
			sent_nearby_parties((User)key.attachment(),cmd.cmd_info);
			break;
			
		case JOIN:
			key.cancel();
			join_party((User)key.attachment(),cmd.cmd_info);		
			break;

		case CREATE:
			key.cancel();
			create_party((User)key.attachment(),cmd.cmd_info,selector);
			break;

		case DISCONNECTED:	
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
	//TODO
	public static void sent_nearby_parties(User client,JSONObject info) {

	}
	
	/* creating a new party
	 * making admin the client who created the party */
	public static void create_party(User party_creator, JSONObject info, Selector selector) throws JSONException, IOException {
		String name = info.getString(jsonKey.NAME.name());
		boolean is_private = info.getBoolean(jsonKey.IS_PRIVATE.name());
		
		Party party = new Party(name,partyID++,party_creator,is_private);
		(new Thread(new Party_thread(party,selector))).start();

	}

	private static void join_party(User client, JSONObject cmd_info) throws JSONException {
		Party party = FindPartyByID(cmd_info.getInt(jsonKey.PARTY_ID.name()));
		party.addNewClient(client);
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
	
	//mini thread and locks.
	//TODO
	private static void addComebackUser(User user) {
		comeback_users.add(user);
	}

}

