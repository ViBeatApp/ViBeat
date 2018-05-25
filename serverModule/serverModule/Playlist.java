package serverModule;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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
	public Track addSong(String url){
		Track track = new Track(url,nextTrackID++);
		songs.add(track);
		return track;
	}
	
	public int get_current_track_id() {
		if(songs.size() == 0) 
			return -1;
		return songs.get(0).trackId;
	}
	
	public int deleteSong(int trackID){
		Iterator<Track> iter = songs.iterator();
		while (iter.hasNext()){
			Track track = iter.next();
			if(track.trackId == trackID) {
				iter.remove();
				return 1;
			}
		}
		return -1;
	}
	
	public int changeSongsOrder(int trackID_1, int trackID_2){
		int firstIndex = -1;
		int secondIndex = -1;
		for(int i = 0; i < songs.size(); ++i) {
			if(songs.get(i).trackId == trackID_1) {
				firstIndex = i;
			}
			if(songs.get(i).trackId == trackID_2) {
				secondIndex = i;
			}
		}
		if(firstIndex == -1 || secondIndex == -1) return -1;
		Track tmpTrack = songs.get(firstIndex);
		songs.set(firstIndex, songs.get(secondIndex));
		songs.set(secondIndex, tmpTrack);
		return 0;
	}
	
	public JSONArray getTrackArray() throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for(Track track : songs) {
			jsonArray.put(track.get_JSON());
		}
		return jsonArray;
	}
	
}
