package serverObjects;

public enum userIntention {
    PLAY_BUTTON(0),
    NEXT_BUTTON(1),
    ON_COMPLETION(2);

    private int type;

    userIntention(int type) {
        this.type = type;
    }

    public int getInt() {
        return type;
    }
    public static userIntention getEnum(int type){
    	switch(type){
    	case 0:
    		return PLAY_BUTTON;
    	case 1:
    		return NEXT_BUTTON;
    	case 2:
    		return ON_COMPLETION;
    	}
		return null;
    }
}

