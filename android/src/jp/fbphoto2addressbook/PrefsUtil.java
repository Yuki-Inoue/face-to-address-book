package jp.fbphoto2addressbook;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Wrapper methods for {@link SharedPreferences}.
 *
 * @author Kazuki Nishiura
 */
public class PrefsUtil {
    private static final String PREFERENCES_NAME = "Facebook2AddressBook";

    public static SharedPreferences getDefault(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getDefaultEditor(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
    }

    public static String getPreferencesName() {
        return PREFERENCES_NAME;
    }

    public final static class Keys {
        private Keys() {}
        public static final String IMPORTED_FRIEND_IDS = "imported_friend_ids";
    }
}
