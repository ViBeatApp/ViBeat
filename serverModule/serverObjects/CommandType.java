package serverObjects;
public enum CommandType {
	
	//from client to server
	AUTHENTICATION,
	NEARBY_PARTIES,
	SEARCH_PARTY,
	JOIN, 
	CREATE, 
	ADD_SONG, 
	DELETE_SONG, 
	SWAP_SONGS,
	IM_READY,
	RENAME_PARTY,
	MAKE_PRIVATE,
	CONFIRM_REQUEST,
	MAKE_ADMIN,
	LEAVE_PARTY,
	UPDATE_LOCATION,
	
	//both
	PLAY_SONG, 
	PAUSE,
	
	//from server to client
	GET_READY, 
	REJECTED,
	UPDATE_PARTY,
	SYNC_PARTY, 
	SEARCH_RESULT,
	
	//get if the other side fall
	DISCONNECTED, 
	SYNC_MUSIC, 
	SEEK_MUSIC;
	
}
