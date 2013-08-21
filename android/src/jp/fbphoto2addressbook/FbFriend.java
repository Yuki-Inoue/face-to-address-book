package jp.fbphoto2addressbook;

/**
 * Data class that represents a friend in facebook. Similar to {@link com.facebook.model.GraphUser},
 * but it contains photoURL, while doesn't contain other property like birthday.
 *
 * @author Kazuki Nishiura
 */
public class FbFriend implements Comparable<FbFriend> {
    private final String fbId;
    private final String name;
    private final String pictureUrl;

    public FbFriend(String fbId, String name, String pictureUrl) {
        this.fbId = fbId;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    public String getFbId() {
        return fbId;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public int compareTo(FbFriend another) {
        return name.compareTo(another.name);
    }
}
