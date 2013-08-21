package jp.fbphoto2addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Helper class to retrieve contact data
 *
 * @author Kazuki Nishiura
 */
public class ContactQueryHelper {
    public static final int ID_INDEX = 0;
    public static final int DISPLAY_NAME_INDEX = 1;
    public static final int THUMBNAIL_URI_INDEX = 2;

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
                        ContactsContract.Contacts._ID,
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

    /**
     * Update user's contact photo
     * @param contactId ID for user who's photo will be updated
     * @param photo blob of photo
     */
    public void updateContactPhoto(int contactId, byte[] photo) {
        ContentValues newValues = new ContentValues();
        newValues.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo);
        newValues.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

        // TODO: check what happens if there is no element to update, and do proper care.
        context.getContentResolver().update(
                ContactsContract.Data.CONTENT_URI,
                newValues,
                ContactsContract.Data.CONTACT_ID + " == " + contactId + " AND "
                        + ContactsContract.Data.MIMETYPE + "=='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                /* Selection args */ null);
    }
}
