import java.nio.channels.SocketChannel;

public class User {
	public enum Party_Status {
		in_party, havnt_decided, waiting_for_approval
	}
	public enum Song_preparation {
		prepared, not_prepared
	}
	String name;
	int id;
	Image image;
	boolean is_admin;
	Party_Status party_status; /* waiting for approval or in party */
	Song_preparation is_prepared;
	SocketChannel client;
	
	public Image get_image() {
		return image;
	}
	
	public SocketChannel get_channel() {
		return client;
	}
	

}
