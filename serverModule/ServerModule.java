import java.nio.channels.SocketChannel;
import java.util.List;

public class ServerModule {
	List<Party> current_parties;
	List<User>  non_party_users;
	
	
	public void HandleNewConnection(SocketChannel new_connection) {
		
	}
	
	public void display_nearby_parties(SocketChannel new_connection) {
		
	}
	
	/* creating a new party
	 * making admin the client who created the party */
	public void create_party(User party_creator) {
		
	}
	
	public void request_join(User client, Party requested_party) {
		
	}
	
	public void actual_join(User client, Party requested_party) {
		
	}
	
	public Party FindPartyByName(String party_name) {
		
	}

}
