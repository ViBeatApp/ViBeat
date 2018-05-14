import java.util.ArrayList;
import java.util.List;

public class Playlist {
	List<Track> songs;
	int nextTrackID = 0;
	int songCounter; //played so far - for sync when song ends.
	
	public Playlist() {
		super();
		songs = new ArrayList<>();
		songCounter = 0;
	}

	public int addSong(String name){
		if(!songIsExist(name))
			return -1;
		Track track = new Track(name);
		songs.add(track);
		return 0;
	}
	
	public int removeSong(int index){
		if(songs.size() <= index){
			return -1;
		}
		songs.remove(index);
		return 0;
	}
	
	public int changeSongsOrder(int i, int j){
		if(songs.size() <= i || songs.size() <= j || i == j){
			return -1;
		}
		Track track_i = songs.get(i);
		songs.set(i, songs.get(j));
		songs.set(j, track_i);
		return 0;
	}
	
	private boolean songIsExist(String name) {
		// TODO Auto-generated method stub
		return true;
	}
}
