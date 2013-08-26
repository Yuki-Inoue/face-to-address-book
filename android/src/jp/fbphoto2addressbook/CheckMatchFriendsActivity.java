package jp.fbphoto2addressbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.google.common.base.Joiner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Activity to query contact data, fetch facebook's friends and check for matching item.
 *
 * @author Kazuki Nishiura
 */
public class CheckMatchFriendsActivity extends Activity {
    public static final String EXTRA_KEY_IF_IGNORE_CONTACT_WITH_PHOTO
            = "if_ignore_contact_with_photo";
    private static final boolean SHOULD_IGNORE_CONTACT_WITH_PHOTO_DEFAULT = true;

    private ContactQueryHelper contactQueryHelper;
    private boolean pickFriendsWhenSessionOpened;
    private Cursor cursorForContacts;
    private RequestAsyncTask fetchFriendsRequest;
    private List<Friend> friends = new ArrayList<Friend>();
    private FriendsAdapter friendsAdapter;
    private boolean shouldIgnoreContactWithPhoto;

    private TextView numberOfContactsView;
    private TextView numberOfFbFriendsView;
    private TextView numberOfMatchedFriendsView;
    private ViewGroup progressViewWrapper;
    private TextView progressTextView;
    private ListView matchFriendsListView;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_match_friends);
        numberOfContactsView = (TextView) findViewById(R.id.number_of_contacts);
        numberOfFbFriendsView = (TextView) findViewById(R.id.number_of_fb_friends);
        numberOfMatchedFriendsView = (TextView) findViewById(R.id.number_of_match_friends);
        progressViewWrapper = (ViewGroup) findViewById(R.id.progress_bar_container);
        progressTextView = (TextView) findViewById(R.id.progress_text);
        matchFriendsListView = (ListView) findViewById(R.id.match_friends_list);
        confirmButton = (Button) findViewById(R.id.match_friends_ok_button);

        Intent intent = getIntent();
        shouldIgnoreContactWithPhoto = intent.getBooleanExtra(
                EXTRA_KEY_IF_IGNORE_CONTACT_WITH_PHOTO,
                SHOULD_IGNORE_CONTACT_WITH_PHOTO_DEFAULT);

        friendsAdapter = new FriendsAdapter(this);
        matchFriendsListView.setAdapter(friendsAdapter);
        contactQueryHelper = new ContactQueryHelper(this);
        startFetchingFriends();
        showIndicatorWithText("Fetching friends data from Facebook");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureInitCursorForContacts();
    }

    private void ensureInitCursorForContacts() {
        if (cursorForContacts == null) {
            cursorForContacts = contactQueryHelper.getCursorForSortedContacts();
            numberOfContactsView.setText(Integer.toString(cursorForContacts.getCount()));
        }
    }

    @Override
    protected void onStop() {
        if (fetchFriendsRequest != null) {
            fetchFriendsRequest.cancel(false);
        }

        super.onStop();
    }

    private void showIndicatorWithText(CharSequence text) {
        progressViewWrapper.setVisibility(View.VISIBLE);
        progressTextView.setText(text);
    }

    private void hideIndicator() {
        progressViewWrapper.setVisibility(View.GONE);
    }

    private boolean ensureOpenSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(this, true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (pickFriendsWhenSessionOpened && state.isOpened()) {
                        pickFriendsWhenSessionOpened = false;
                        startFetchingFriends();
                    }
                }
            });
            return false;
        }
        return true;
    }

    private void startFetchingFriends() {
        if (ensureOpenSession()) {
            FetchFbFriendsHelper fetchHelper = new FetchFbFriendsHelper();
            Request request = fetchHelper.createRequestForMyFriendsWithPicture(new Callback());
            fetchFriendsRequest = request.executeAsync();
        } else {
            pickFriendsWhenSessionOpened = true;
        }
    }

    private void transitToResultActivity(ArrayList<Integer> importedFriendIds) {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putIntegerArrayListExtra(
                ResultActivity.EXTRA_KEY_IMPORTED_FRIEND_IDS, importedFriendIds);
        startActivity(intent);
    }

    private class Callback implements Request.Callback {

        @Override
        public void onCompleted(Response response) {
            if (handleErrorIfExists(response.getError())) {
                return;
            }
            GraphObject result = response.getGraphObject();
            JSONArray data = FetchFbFriendsHelper.parseData(result);
            updateNumberOfFbFriends(data.length());
            storeFriendsData(data);
            String nextUrl = FetchFbFriendsHelper.parseNextUrl(result);
            boolean isNextRequestSent = false;
            if (nextUrl != null) {
                String[] splits = nextUrl.split("facebook.com/", 2);
                if (splits.length == 2) {
                    Request nextRequest = new Request(Session.getActiveSession(), splits[1]);
                    nextRequest.setCallback(new Callback());
                    fetchFriendsRequest = nextRequest.executeAsync();
                    isNextRequestSent = true;
                }
            }
            if (!isNextRequestSent) {
                lookForMatchingFriends();
            }
        }

        private boolean handleErrorIfExists(FacebookRequestError requestError) {
            if (requestError == null) {
                return false;
            }
            // TODO: proper error handling
            Log.e("FB2ADD", requestError.getErrorCode() + requestError.getErrorMessage());
            return true;
        }

        private void updateNumberOfFbFriends(final int numberOfFbFriends) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CharSequence currentNumCh = numberOfFbFriendsView.getText();
                    int currentNum = Integer.parseInt(currentNumCh.toString());
                    numberOfFbFriendsView.setText(Integer.toString(currentNum + numberOfFbFriends));
                }
            });
        }

        private void storeFriendsData(JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = null;
                try {
                    item = data.getJSONObject(i);
                } catch (JSONException e) {
                    Log.e("FB2Add", "failed to get " + i + "-th element from " + data);
                    continue;
                }
                String url = FetchFbFriendsHelper.parseValidPictureUrlFromUserItem(item);
                if (url != null) {
                    String id = FetchFbFriendsHelper.parseIdFromUserItem(item);
                    String name = FetchFbFriendsHelper.parseNameFromUserItem(item);
                    friends.add(new Friend(id, name.replaceAll("\\s+", " ").trim(), url));
                    Log.d("FB2ADD", name.replaceAll("\\s+", " "));
                }
            }
        }

        private void lookForMatchingFriends() {
            showIndicatorWithText("Check for matching friends data");
            matchFriendsListView.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setEnabled(false);

            ensureInitCursorForContacts();
            // Memo: we do O(nm) comparison, this can be reduced to O(n + m)
            while (cursorForContacts.moveToNext()) {
                for (int i = 0; i < friends.size(); i++) {
                    if (shouldIgnoreContactWithPhoto
                            && cursorForContacts.getString(ContactQueryHelper.THUMBNAIL_URI_INDEX) != null) {
                        continue;
                    }
                    if (friends.get(i).getName().equals(
                            cursorForContacts.getString(ContactQueryHelper.DISPLAY_NAME_INDEX))) {
                        int contactId = cursorForContacts.getInt(ContactQueryHelper.ID_INDEX);
                        friendsAdapter.add(friends.get(i).setContactId(contactId));
                    }
                }
            }
            numberOfMatchedFriendsView.setText(Integer.toString(friendsAdapter.getCount()));

            hideIndicator();
            confirmButton.setEnabled(true);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<Friend> selectedFriends = friendsAdapter.getSelectedFriends();
                    if (selectedFriends.size() == 0) {
                        Toast.makeText(
                                CheckMatchFriendsActivity.this,
                                "No friends are selected",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showIndicatorWithText("Importing selected friends' photo..");
                    matchFriendsListView.setVisibility(View.GONE);
                    ArrayList<Integer> importedFriendIds = new ArrayList<Integer>();
                    for (Friend friend: selectedFriends){
                        Bitmap icon = friendsAdapter.getDisplayedImage(friend.getFbId());
                        contactQueryHelper.updateContactPhoto(
                                friend.getContactId(),
                                Util.getBytesFromBitmap(icon));
                        importedFriendIds.add(friend.getContactId());
                        Log.d("FB2ADD", "Import photo for " + friend.getName() + "("
                                + friend.getContactId() + ")");
                    }
                    PrefsUtil.getDefaultEditor(CheckMatchFriendsActivity.this)
                            .putString(
                                PrefsUtil.Keys.IMPORTED_FRIEND_IDS,
                                Joiner.on(',').join(importedFriendIds))
                            .commit();
                    transitToResultActivity(importedFriendIds);
                }
            });
        }
    }
}
