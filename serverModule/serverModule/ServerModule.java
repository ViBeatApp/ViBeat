package serverModule;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;

import serverObjects.Command;
import serverObjects.Location;
import serverObjects.Party;
import serverObjects.ReadWriteAux;
import serverObjects.User;
import serverObjects.jsonKey;

public class ServerModule {
	static List<Party> current_parties = Collections.synchronizedList(new ArrayList<Party>());
	static List<User>  disconnected_users = Collections.synchronizedList(new ArrayList<User>());
	static List<User>  comeback_users = Collections.synchronizedList(new ArrayList<User>());
	static int partyID = 0;
	static Selector selector;

	public static void main(String[] args) throws IOException, JSONException{

		selector = Selector.open();

		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress("172.16.0.196",2000));

		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		while(true){

			System.out.println("Waiting for select...");
			int readyChannels = selector.select();
			System.out.println("Number of selected keys: " + readyChannels);
			handleComeBackUser();

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

	private static void handleComeBackUser() {
		synchronized (comeback_users) {
			Iterator<User> iter = comeback_users.iterator();
			while (iter.hasNext()){
				User user = iter.next();
				SelectionKey key = user.get_channel().keyFor(selector);
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				iter.remove();
			}
		}
		
	}

	protected static void handleReadCommands(Selector selector, SelectionKey key) throws IOException, JSONException {
		SocketChannel client = (SocketChannel) key.channel();
		Command cmd = ReadWriteAux.readSocket(client);
		cmd.printCommand();
		switch(cmd.cmd_type){

		case AUTHENTICATION:
			int userId = cmd.getIntAttribute(jsonKey.USER_ID);		
			User disconnectedUser = isDisconnectedUser(userId);
			if(disconnectedUser != null) {
				disconnectedUser.setChannel(client);
				if(join_party(disconnectedUser,disconnectedUser.currentPartyId)) 
					break;
			}

			String name = cmd.getStringAttribute(jsonKey.NAME);
			String image = cmd.getStringAttribute(jsonKey.IMAGE);
			User newUser = new User(name,userId,image, client);
			key.attach(newUser);					///check this
			break;

		case NEARBY_PARTIES:
			send_nearby_parties((User)key.attachment(),cmd);
			break;

		case JOIN:
			int partyID = cmd.getIntAttribute(jsonKey.PARTY_ID);
			join_party((User)key.attachment(),partyID);
			break;

		case CREATE:
			create_party((User)key.attachment(),cmd,selector);
			break;

		case DISCONNECTED:	
			if(key.attachment() != null)
				addDisconenctedUser((User)key.attachment());
			System.out.println(client);
			client.close();
			break;

		case SEARCH_PARTY:
			Command answer = getPartiesByName(cmd.getStringAttribute(jsonKey.NAME));
			ReadWriteAux.writeSocket(((User)key.attachment()).get_channel(), answer);
		default:
			System.out.println("error cmdType not join/create/disconnected.");
			break;
		}
	}

	private static User isDisconnectedUser(int id) {
		synchronized (disconnected_users) {
			Iterator<User> iter = disconnected_users.iterator();
			while (iter.hasNext()){
				User user = iter.next();
				if(user.id == id) {	
					iter.remove();
					return user;
				}
			}
		}
		return null;
	}

	private static Command getPartiesByName(String name) throws JSONException {
		JSONArray resultArray = new JSONArray();
		
		synchronized (current_parties) {
			for (Party party : current_parties){
				if (party.party_name.contains(name)) {
					resultArray.put(party.getPublicJson());
				}
			}		
		}
		return Command.create_searchResult_command(resultArray);
	}


	public static void send_nearby_parties(User user,Command cmd) throws JSONException {
		Location location = new Location(cmd);
		if(user == null) {
			System.out.println("error send nearbyParties.!!!!!!!!!!!!!!!!!!!!!!!");
			return;
		}
		user.setLocation(location);
		JSONArray partyArray = new JSONArray();
		for (Party party : current_parties){
			if(distance(party.get_Location(), location) < 100) {
				partyArray.put(party.getPublicJson());
				System.out.println(party.getPublicJson());
			}
		}
		
		Command result = Command.create_searchResult_command(partyArray);
		System.out.print("send to " +user.name + " command: ");
		ReadWriteAux.writeSocket(user.get_channel(), result);
	}

	public static double distance(Location loc1, Location loc2) {
		double lat1 = loc1.latitude; 
		double lat2 = loc2.latitude;
		double lon1 = loc1.longitude;
        double lon2 = loc2.longitude;
        double el1 = loc1.altitude;
        double el2 = loc2.altitude;
	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
	
	
	/* creating a new party
	 * making admin the client who created the party */
	public static void create_party(User party_creator, Command cmd, Selector selector) throws JSONException, IOException {
		String name = cmd.getStringAttribute(jsonKey.NAME);
		boolean is_private = cmd.getBoolAttribute(jsonKey.IS_PRIVATE);
		
		SelectionKey key = party_creator.get_channel().keyFor(selector);
		key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);

		Party party = new Party(name,partyID++,party_creator,is_private);
		System.out.println("serverModule - party.party_id = " + party.party_id);
		current_parties.add(party);
		(new Thread(new Party_thread(party,selector))).start();

	}

	private static boolean join_party(User client, int partyId) throws JSONException {
		Party party = FindPartyByID(partyId);
		if (party == null)
			return false;
		SelectionKey key = client.get_channel().keyFor(selector);
		
		System.out.println("serverModule - looked for partyID: " + partyId);
		
		synchronized (party.waitingClients) {
			if (party.keep_on) {
				key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
				party.addWaitingClient(client);
				party.selector.wakeup();
				return true;
			}
		}
		return false;
	}

	private static Party FindPartyByID(int id) {
		synchronized (current_parties) {

			for(Party party : current_parties){
				if(party.party_id == id){
					return party;
				}
			}		
		}
		return null;
	}

	//mini thread and locks.
	static void addDisconenctedUser(User user) throws IOException {
		synchronized (disconnected_users) {	
			for(User disUser : disconnected_users){
				if(disUser.id == user.id){
					disUser.currentPartyId = user.currentPartyId;
					return;
				}
			}	
			disconnected_users.add(user);
		}
		user.closeChannel();
	}

	static void backToServer(User user) {
		comeback_users.add(user);
		selector.wakeup();
	}
	
	static void deleteParty(Party party) {
		boolean remove = current_parties.remove(party);
		System.out.println("removing party: " + party.party_name + " result: " + remove);
	}

}

