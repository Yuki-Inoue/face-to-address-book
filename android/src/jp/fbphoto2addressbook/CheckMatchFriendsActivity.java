package jp.fbphoto2addressbook;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import org.json.JSONArray;

/**
 * Activity to query contact data, fetch facebook's friends and check for matching item.
 *
 * @author Kazuki Nishiura
 */
public class CheckMatchFriendsActivity extends Activity {
    private ContactQueryHelper contactQueryHelper;
    private boolean pickFriendsWhenSessionOpened;
    private Cursor cursorForContacts;
    private RequestAsyncTask fetchFriendsRequest;

    private TextView numberOfContactsView;
    private TextView numberOfFbFriendsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_match_friends);
        numberOfContactsView = (TextView) findViewById(R.id.number_of_contacts);
        numberOfFbFriendsView = (TextView) findViewById(R.id.number_of_fb_friends);

        contactQueryHelper = new ContactQueryHelper(this);
        startFetchingFriends();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private class Callback implements Request.Callback {

        @Override
        public void onCompleted(Response response) {
            if (handleErrorIfExists(response.getError())) {
                return;
            }
            GraphObject result = response.getGraphObject();
            JSONArray data = FetchFbFriendsHelper.parseData(result);
            updateNumberOfFbFriends(data.length());
            String nextUrl = FetchFbFriendsHelper.parseNextUrl(result);
            if (nextUrl != null) {
                // TODO: implement this
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
    }
}
