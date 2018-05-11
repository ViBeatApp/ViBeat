package server;

public class Track {
	String name;
	String image_url;
	String Url;
	double duration;
	int id;					//unique to track, not to name !
	
	public Track(String name) {
		super();
		this.name = name;
	}
}

