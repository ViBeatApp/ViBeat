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
	public void addSong(String DB_ID){
		Track track = new Track(DB_ID,nextTrackID++);
		songs.add(track);
		if(currentTrack == -1)
			setCurrentTrack(track.trackId);
	}

	public void setCurrentTrack(int trackID) {
		
		if(trackID == -1){
			for(int i = 0; i < songs.size(); ++i){
				if(songs.get(i).trackId == currentTrack){
					System.out.println("error setCurrentTrack !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					currentTrack = songs.get((i+1)%songs.size()).trackId;
					return;
				}
			}
		}
		
		for(int i = 0;i < get_list_size(); ++i){
			Track track = songs.get(i);
			if(track.trackId == trackID) {
				currentTrack = trackID;
				return;
			}
		}
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
				int index = songs.indexOf(track);
				if(trackID == currentTrack){
					System.out.println("deleting current song");
					int nextIndex = (index+1) % get_list_size();
					currentTrack = songs.get(nextIndex).trackId;
					iter.remove();
					return 1;
				}
				iter.remove();
				return 0;
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
		System.out.println("first index: " + firstIndex + " second index: " + secondIndex);
		if(firstIndex == -1 || secondIndex == -1) 
			return 0;
		if(secondIndex < firstIndex) {
			Track end = songs.get(firstIndex); 
			for(int i = firstIndex; i > secondIndex; i--)
			{
				songs.set(i,songs.get(i-1)); 
			}
			songs.set(secondIndex, end);    
		}
		else if(firstIndex < secondIndex) {
			Track start = songs.get(firstIndex); 
			for(int i = firstIndex; i < secondIndex; i++)
			{
				songs.set(i,songs.get(i+1)); 
			}
			songs.set(secondIndex, start);  
		}
		return 1;
	}

	public JSONArray getTrackArray() throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for(Track track : songs) {
			jsonArray.put(track.get_JSON());
		}
		return jsonArray;
	}

}
