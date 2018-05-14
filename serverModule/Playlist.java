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
	//TODO
	public Track addSong(String name){
		if(!songIsExist(name))
			return null;
		Track track = new Track(name);
		songs.add(track);
		return track;
	}
	
	public int deleteSong(int trackID){

		return 0;
	}
	
	public int changeSongsOrder(int trackID_1, int trackID_2){

		return 0;
	}
	
	private boolean songIsExist(String name) {
		// TODO Auto-generated method stub
		return true;
	}
}
