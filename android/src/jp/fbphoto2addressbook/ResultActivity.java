package jp.fbphoto2addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that shows importing result.
 *
 * @author Kazuki Nishiura
 */
public class ResultActivity extends Activity {
    public static final String EXTRA_KEY_IMPORTED_FRIEND_IDS = "imported_friend_ids";
    private ListView importedFriendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView numberOfImportedFriends
                = (TextView) findViewById(R.id.number_of_imported_friends_text);
        importedFriendsListView = (ListView) findViewById(R.id.imported_friend_list);
        List<Integer> importedFriendIds
                = getIntent().getIntegerArrayListExtra(EXTRA_KEY_IMPORTED_FRIEND_IDS);
        numberOfImportedFriends.setText(Integer.toString(importedFriendIds.size()));
        showImportedFriendsName(importedFriendIds);
    }

    private void showImportedFriendsName(List<Integer> importedFriendIds) {
        ContactQueryHelper contactQueryHelper = new ContactQueryHelper(this);
        Cursor cursor = contactQueryHelper.getCursorForSortedContacts(importedFriendIds);
        List<String> names = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(ContactQueryHelper.DISPLAY_NAME_INDEX));
            } while (cursor.moveToNext());
        }
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        importedFriendsListView.setAdapter(adapter);
    }
}
