package jp.fbphoto2addressbook;

/**
 * Data class that represents a friend. It has similar member as
 * {@link com.facebook.model.GraphUser} and also contain local contact data.
 *
 * @author Kazuki Nishiura
 */
public class Friend implements Comparable<Friend> {
    private final String fbId;
    private int contactId;
    private final String name;
    private final String pictureUrl;

    public Friend(String fbId, String name, String pictureUrl) {
        this.fbId = fbId;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    public Friend setContactId(int contactId) {
        this.contactId = contactId;
        return this;
    }

    public int getContactId() {
        return contactId;
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
    public int compareTo(Friend another) {
        return name.compareTo(another.name);
    }
}
