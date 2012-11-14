package jp.fbphoto2addressbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

/**
 * Home activity which serves the first view of the app.
 *
 * @author Kazuki Nishiura
 */
public class HomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
}
