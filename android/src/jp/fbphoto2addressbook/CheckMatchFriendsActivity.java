package jp.fbphoto2addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Activity to query contact data, fetch facebook's friends and check for matching item.
 *
 * @author Kazuki Nishiura
 */
public class CheckMatchFriendsActivity extends Activity {
    private ContactQueryHelper contactQueryHelper;
    private TextView numberOfContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_match_friends);
        numberOfContacts = (TextView) findViewById(R.id.number_of_contacts);

        contactQueryHelper = new ContactQueryHelper(this);
        Cursor c = contactQueryHelper.getCursorForSortedContacts();
        numberOfContacts.setText(Integer.toString(c.getCount()));
        while (c.moveToNext()) {
            // TODO : check match with FB data.
            Log.d("CONTACT", c.getString(ContactQueryHelper.DISPLAY_NAME_INDEX));
        }
    }
}
