package jp.fbphoto2addressbook;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Helper class to retrieve contact data
 *
 * @author Kazuki Nishiura
 */
public class ContactQueryHelper {
    public static final int DISPLAY_NAME_INDEX = 0;
    public static final int THUMBNAIL_URI_INDEX = 1;

    private final Context context;

    public ContactQueryHelper(Context context) {
        this.context = context;
    }

    /**
     * @return cursor that point the first element or null if no element found.
     */
    public Cursor getCursorForSortedContacts() {
        Cursor c = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                },
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME);
        if (c.moveToFirst()) {
            return c;
        }
        return null;
    }
}
