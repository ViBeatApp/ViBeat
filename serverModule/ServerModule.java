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

import org.json.JSONArray;
import org.json.JSONException;

public class ServerModule {
	static List<Party> current_parties = new ArrayList<>();
	static List<User>  disconnected_users = new ArrayList<>();
	static List<User>  comeback_users = new ArrayList<>();
	static int partyID = 0;
	static Selector selector;

	public static void main(String[] args) throws IOException, JSONException{

		Selector selector = Selector.open();

		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress("192.168.43.238",2000));

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
			iter.remove();
		}
	}

	protected static void handleReadCommands(Selector selector, SelectionKey key) throws IOException, JSONException {
		SocketChannel client = (SocketChannel) key.channel();
		Command cmd = readWriteAux.readSocket(client);
		cmd.printCommand();
		switch(cmd.cmd_type){

		case AUTHENTICATION:
			int userId = cmd.getIntAttribute(jsonKey.USER_ID.name());		
			User disconnectedUser = isDisconnectedUser(userId);
			if(disconnectedUser != null) {
				disconnectedUser.channel = client;
				int partyID = disconnectedUser.currentPartyId;
				Party party = FindPartyByID(partyID);
				if (party != null) {
					key.cancel();
					join_party(disconnectedUser,partyID);
					break;
				}
			}
			
			String name = cmd.getStringAttribute(jsonKey.NAME.name());
			String image = cmd.getStringAttribute(jsonKey.IMAGE.name());
			User newUser = new User(name,userId,image, client);
			key.attach(newUser);					///check this
			break;

		case NEARBY_PARTIES:
			send_nearby_parties((User)key.attachment(),cmd);
			break;

		case JOIN:
			int partyID = cmd.getIntAttribute(jsonKey.PARTY_ID.name());
			Party party = FindPartyByID(partyID);
			if (party == null)
				break;
			key.cancel();	
			join_party((User)key.attachment(),partyID);
			break;

		case CREATE:
			key.cancel();
			create_party((User)key.attachment(),cmd,selector);
			break;

		case DISCONNECTED:	
			client.close();
			break;

		case SEARCH_PARTY:
			Command answer = getPartiesByName(cmd.getStringAttribute(jsonKey.NAME.name()));
			readWriteAux.writeSocket(((User)key.attachment()).get_channel(), answer);
		default:
			System.out.println("error cmdType not join/create/disconnected.");
			break;
		}
	}

	private static User isDisconnectedUser(int id) {
		Iterator<User> iter = disconnected_users.iterator();
		while (iter.hasNext()){
			User user = iter.next();
			if(user.id == id) {	
				iter.remove();
				return user;
			}
		}
		return null;
	}

	private static Command getPartiesByName(String name) throws JSONException {
		JSONArray resultArray = new JSONArray();
		for(Party party : current_parties) {
			if (party.party_name.contains(name)) {
				resultArray.put(party.getPublicJson());
			}
		}
		return Command.get_searchResult_command(resultArray);
	}

	//TODO
	public static void send_nearby_parties(User client,Command cmd) {

	}

	/* creating a new party
	 * making admin the client who created the party */
	public static void create_party(User party_creator, Command cmd, Selector selector) throws JSONException, IOException {
		String name = cmd.getStringAttribute(jsonKey.NAME.name());
		boolean is_private = cmd.getBoolAttribute(jsonKey.IS_PRIVATE.name());

		Party party = new Party(name,partyID++,party_creator,is_private);
		System.out.println("serverModule - party.party_id = " + party.party_id);
		current_parties.add(party);
		(new Thread(new Party_thread(party,selector))).start();

	}

	private static boolean join_party(User client, int partyId) throws JSONException {
		Party party = FindPartyByID(partyId);
		if (party == null)
			return false;
		System.out.println("serverModule - looked for partyID: " + "Tomer - I've changed this. serverModule.join_party()");
		System.out.println("serverModule - party: " + party);
		party.addNewClient(client);
		party.selector.wakeup();
		return true;
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
	static void addComebackUser(User user) throws ClosedChannelException {
		comeback_users.add(user);
		user.channel.register(selector, SelectionKey.OP_READ);
	}

	//mini thread and locks.
	//TODO
	static void addDisconenctedUser(User user) {
		disconnected_users.add(user);
	}

}

