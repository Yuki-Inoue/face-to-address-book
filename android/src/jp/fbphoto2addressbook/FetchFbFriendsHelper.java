package jp.fbphoto2addressbook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.common.collect.ImmutableSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * @author Kazuki Nishiura
 */
public class FetchFbFriendsHelper {
    private static final String MY_FRIENDS_API = "me/friends";
    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String PICTURE_FIELD = "picture";

    public Request createRequestForMyFriendsWithPicture(Request.Callback callback) {
        return createRequestForMyFriendsWithPicture(-1, -1, callback);
    }

    public Request createRequestForMyFriendsWithPicture(
            int width, int height, Request.Callback callback) {
        Session session = Session.getActiveSession();
        Request request = Request.newGraphPathRequest(session, MY_FRIENDS_API, callback);

        StringBuilder pictureFieldBuilder = new StringBuilder().append(PICTURE_FIELD);
        if (height > 0) {
            pictureFieldBuilder.append(".height(").append(height).append(')');
        }
        if (width > 0) {
            pictureFieldBuilder.append(".width(").append(width).append(')');
        }
        Set<String> fields = ImmutableSet.of(ID_FIELD, NAME_FIELD, pictureFieldBuilder.toString());

        Bundle parameters = request.getParameters();
        parameters.putString("fields", TextUtils.join(",", fields));
        request.setParameters(parameters);

        return request;
    }

    public static JSONArray parseData(GraphObject result) {
        return (JSONArray) result.getProperty("data");
    }

    public static String parseNextUrl(GraphObject result) {
        JSONObject paging = (JSONObject) result.getProperty("paging");
        if (!paging.has("next")) {
            return null;
        }
        try {
            return paging.getString("next");
        } catch (JSONException o) {
            Log.w("FB2ADD", "failed to get next paging");
            return null;
        }
    }
}
