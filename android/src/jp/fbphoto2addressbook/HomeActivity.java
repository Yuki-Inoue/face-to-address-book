package jp.fbphoto2addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Home activity which serves the first view of the app and process log in.
 *
 * @author Kazuki Nishiura
 */
public class HomeActivity extends Activity {

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
