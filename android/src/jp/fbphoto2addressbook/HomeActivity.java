package jp.fbphoto2addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CheckMatchFriendsActivity.class);
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
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    private void handleLoggedInStatus(Session session) {
        boolean loggedIn = session != null && session.isOpened();
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        startButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
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
