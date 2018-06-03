package serverObjects;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

public class Playlist {
	public List<Track> songs;
	public int nextTrackID = 50;
	public int currentTrack = -1;

	public Playlist() {
		super();
		songs = new ArrayList<>();
		nextTrackID = new Random().nextInt(123456789);
	}
	//TODO
	public Track addSong(String url){
		Track track = new Track(url,nextTrackID++);
		songs.add(track);
		if(currentTrack == -1)
			setCurrentTrack(track.trackId);
		return track;
	}

	public void setCurrentTrack(int trackID) {
		int currentIndex = -1;
		for(int i = 0;i < get_list_size(); ++i){
			Track track = songs.get(i);
			if(track.trackId == trackID) {
				currentTrack = trackID;
				return;
			}
			if(track.trackId == currentTrack){
				currentIndex = i;
			}
		}
		currentTrack = (currentIndex + 1) % get_list_size();
	}

	public int get_current_track_id() {
		return currentTrack;
	}

	public int get_list_size() {
		return songs.size();
	}

	public int deleteSong(int trackID){
		Iterator<Track> iter = songs.iterator();
		while (iter.hasNext()){
			Track track = iter.next();
			if(track.trackId == trackID) {
				//int index = songs.indexOf(track);
				if(trackID == currentTrack){
					System.out.println("You need to send playNextSong before deleting the current song!!!!!!!!!!!!!!!");
					//int nextIndex = (index+1) % get_list_size();
					//currentTrack = songs.get(nextIndex).trackId;
					return -1;
				}
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
