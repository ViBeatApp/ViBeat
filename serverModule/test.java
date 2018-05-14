import java.util.List;

public class test {

	public static void main(String[] args) {
		
//		System.out.println("create new party");
//		User ido = new User("ido");
//		Party party = new Party("ido's party", 3, ido, true);
//		System.out.println(party.addSong("ido's song") == 0 ? "adding success\n" : "adding failed\n");
//		printInfo(party);
//		
//		System.out.println("adding tomer to clients");
//		User tomer = new User("tomer");
//		party.addClient(tomer);
//		party.addSong("tomer's song");
//		printInfo(party);
//		
//		System.out.println("adding tomer to admins");
//		party.addAdmin(tomer);
//		printInfo(party);
//		
//		System.out.println("adding idan to requests");
//		User idan = new User("idan");
//		party.addRequest(idan);
//		printInfo(party);
//		
//		System.out.println("replace the songs");
//		party.changeSongsOrder(0,1);
//		printInfo(party);
//		
//		System.out.println("remove tomer's song");
//		party.removeSong(0);
//		printInfo(party);
		
	}
	
	public static void printInfo(Party party){
		System.out.println("party name: " + party.party_name);
		System.out.println("party id: " + party.party_id);
		System.out.println("party's admins: ");
		printUserList(party.admins);
		System.out.println("party's clients: ");
		printUserList(party.connected);
		System.out.println("party's requests: ");
		printUserList(party.request);
		System.out.println("party's playlist: ");
		printPlayList(party.playlist);
		System.out.println("\n\n");
	}

	private static void printUserList(List<User> list) {
		for(int i = 0; i < list.size(); ++i){
			System.out.println("	" + list.get(i).name);	
		}
	}
	
	private static void printPlayList(Playlist playlist) {
		for(int i = 0; i < playlist.songs.size(); ++i){
			System.out.println("	" + playlist.songs.get(i).name);	
		}
	}

}
