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
	static List<User>  non_party_users = new ArrayList<>();
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

				if(key.isAcceptable()) {

					// a connection was accepted by a ServerSocketChannel.
					SocketChannel client = serverSocketChannel.accept();
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out.println("Accepted new connection from client: " + client);

				} else if (key.isConnectable()) {
					// a connection was established with a remote server.

				} else if (key.isReadable()) {

					// a channel is ready for reading
					SocketChannel client = (SocketChannel) key.channel();
					byte[] messageArray = readWriteAux.readSocket(client);
					Command cmd = new Command(messageArray);
					switch(cmd.cmd_type){
					
					case Authentication:
						System.out.println("Client message: authentication.");
						String name = cmd.cmd_info.getString("Name");
						int id = cmd.cmd_info.getInt("Id");
						byte[] image = (byte[]) cmd.cmd_info.get("Image");
						User newUser = new User(name,id,image);
						non_party_users.add(newUser);
						key.attach(newUser);
						
					case Join:
						System.out.println("Client message: join.");
						break;
					case Create:
						System.out.println("Client message: create");
						create_party((User)key.attachment(),cmd.info);
						break;
					case Disconnected:	
						System.out.println("Client messages are complete; close.");
						removeIfAuthenticated(key);
						client.close();
						break;
					default:
						System.out.println("error cmdType not join/create/disconnected.");
						break;
					}

				} else if (key.isWritable()) {
					// a channel is ready for writing
				}

				keyIterator.remove();
			}
		}
	}
	private static void removeIfAuthenticated(SelectionKey key) {
		User user = (User) key.attachment();
		if(user == null)
			return;
		non_party_users.remove(user);
		
	}
	public void HandleNewConnection(SocketChannel new_connection) {

	}

	public void displaNamey_nearby_parties(SocketChannel new_connection) {

	}

	/* creating a new party
	 * making admin the client who created the party */
	public static void create_party(User party_creator, JSONObject info) throws JSONException {
		String name = info.getString("Name");
		boolean is_private = info.getBoolean("private");
		Party party = new Party(name,partyID++,party_creator,is_private);
		new Thread(new Party_thread(party)).start();
		
	}

	public void request_join(User client, Party requested_party) {

	}

	public void actual_join(User client, Party requested_party) {

	}

	public Party FindPartyByName(String party_name) {
		return null;
	}
}
