package jp.fbphoto2addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Home activity which serves the first view of the app and process log in.
 *
 * @author Kazuki Nishiura
 */
public class HomeActivity extends Activity {
    public final static int DEBUG_MENU_REMOVE_IMPORTED_PHOTO_ID = 123456;
    private View loginButton;
    private View startButton;
    private View startButtonWrapper;
    private CheckBox optionIfIgnoreContactWithPhoto;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    handleLoggedInStatus(session);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loginButton = findViewById(R.id.setup_login_button);
        startButton = findViewById(R.id.start_fetch_data_button);
        startButtonWrapper = findViewById(R.id.start_fetch_data_button_wrapper);
        optionIfIgnoreContactWithPhoto =
                (CheckBox) findViewById(R.id.if_ignore_contact_data_with_photo);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CheckMatchFriendsActivity.class);
                setOptions(intent);
                startActivity(intent);
            }
        });

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        handleLoggedInStatus(Session.getActiveSession());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean displayed = super.onCreateOptionsMenu(menu);
        if (Constants.IS_DEBUG) {
            menu.add(
                    /* group id */ Menu.NONE,
                    DEBUG_MENU_REMOVE_IMPORTED_PHOTO_ID,
                    /* order */ Menu.NONE,
                    "Remove imported data (debug only)");
            return true;
        } else {
            return displayed;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DEBUG_MENU_REMOVE_IMPORTED_PHOTO_ID:
                if (!Constants.IS_DEBUG) {
                    break;
                }
                String[] ids = PrefsUtil.getDefault(this)
                        .getString(PrefsUtil.Keys.IMPORTED_FRIEND_IDS, "").split(",");
                if (ids.length > 0 && ids[0].length() > 0) {
                    new ContactQueryHelper(this).setNullToContactPhoto(ids);
                    Toast.makeText(this, ids.length + " photos are deleted.", Toast.LENGTH_SHORT).show();
                    PrefsUtil.getDefaultEditor(this).remove(PrefsUtil.Keys.IMPORTED_FRIEND_IDS);
                } else {
                    Toast.makeText(this, "No photos are imported by this app.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOptions(Intent intent) {
        intent.putExtra(
                CheckMatchFriendsActivity.EXTRA_KEY_IF_IGNORE_CONTACT_WITH_PHOTO,
                optionIfIgnoreContactWithPhoto.isChecked());
    }

    private void handleLoggedInStatus(Session session) {
        boolean loggedIn = session != null && session.isOpened();
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        startButtonWrapper.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        uiHelper.onPause();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        uiHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
