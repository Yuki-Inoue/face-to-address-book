package jp.fbphoto2addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.Collections;
import java.util.List;

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
        return getCursorForSortedContacts(Collections.EMPTY_LIST);
    }

    /**
     * @return cursor that point the first element or null if no element found.
     */
    public Cursor getCursorForSortedContacts(List<Integer> idsToPick) {
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };
        String selection = null;
        if (idsToPick != null && idsToPick.size() > 0) {
            StringBuilder selectionBuilder = new StringBuilder();
            selectionBuilder
                    .append(ContactsContract.Contacts._ID)
                    .append(" IN (");
            for (int i = 0; i < idsToPick.size(); i++) {
                selectionBuilder.append(idsToPick.get(i));
                if (i == idsToPick.size() - 1) {
                    selectionBuilder.append(')');
                } else {
                    selectionBuilder.append(',');
                }
            }
            selection = selectionBuilder.toString();
        }

        Cursor c = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                /* selection args */ null,
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
        if (photo != null) {
            newValues.put(ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        }
        // TODO: check what happens if there is no element to update, and do proper care.
        context.getContentResolver().update(
                ContactsContract.Data.CONTENT_URI,
                newValues,
                ContactsContract.Data.CONTACT_ID + " == " + contactId + " AND "
                        + ContactsContract.Data.MIMETYPE + "=='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                /* Selection args */ null);
    }

    public void setNullToContactPhoto(String[] ids) {
        for (String id: ids) {
            updateContactPhoto(Integer.parseInt(id), null);
        }
    }
}
