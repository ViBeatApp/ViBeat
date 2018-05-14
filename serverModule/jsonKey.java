
enum jsonKey {
	NAME("NAME"),
	
	IMAGE("IMAGE"),
	URL("URL"),
	
	CLIENT_LOCATION("CLIENT_LOCATION"),
	LOCATION("LOCATION"),
	PUBLIC_PARTY_INFO("PUBLIC_PARTY_INFO"),
	PARTY_INFO("PARTY_INFO"),
	CONFIRMED("CONFIRMED"),
	
	OFFSET("OFFSET"),
	USERS("USERS"),
	REQUESTS("REQUESTS"),
	DELETE_SONGS("DELETE_SONGS"),
	NEW_SONGS("NEW_SONGS"),
	SWAP_SONGS("SWAP_SONGS"),
	PARTY_RENAME("PARTY_RENAME"),
	
	IS_PRIVATE("IS_PRIVATE"),
	IS_ADMIN("IS_ADMIN"),
	
	TRACK_ID_1("TRACK_ID_1"),
	TRACK_ID_2("TRACK_ID_2"),
	
	TRACK_ID("TRACK_ID"),
	PARTY_ID("PARTY_ID"),
	USER_ID("USER_ID");


	private String text;
	jsonKey (String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
}
