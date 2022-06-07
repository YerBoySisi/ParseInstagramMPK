package com.sisiame.parseinstagrammpk;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESC = "description", KEY_IMG = "image", KEY_USER = "user";

    public String getDescription() {
        return getString(KEY_DESC);
    }

    public void setDescription(String description) {
        put(KEY_DESC, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMG);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMG, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

}
