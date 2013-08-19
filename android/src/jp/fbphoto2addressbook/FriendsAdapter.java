package jp.fbphoto2addressbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.*;

/**
 * @author Kazuki Nishiura
 */
public class FriendsAdapter extends ArrayAdapter<FbFriend> {
    private LayoutInflater inflater;
    private Set<FbFriend> selectedFriends;
    private RequestQueue queue;
    private ImageLoader imageLoader;

    public FriendsAdapter(Context context) {
        super(context, R.layout.list_item_friend, new LinkedList<FbFriend>());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedFriends = new HashSet<FbFriend>();
        queue = Volley.newRequestQueue(getContext());
        imageLoader = new ImageLoader(queue, new BitmapCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item_friend, null);
        final FbFriend friend = getItem(position);
        ((TextView) view.findViewById(R.id.friend_name)).setText(friend.getName());
        final CheckBox checkBox = ((CheckBox) view.findViewById(R.id.check_box));
        checkBox.setChecked(selectedFriends.contains(friend));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toBeChecked = !checkBox.isChecked();
                checkBox.setChecked(toBeChecked);
                if (toBeChecked) {
                    selectedFriends.add(friend);
                } else {
                    selectedFriends.remove(friend);
                }
            }
        });

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(
                ((ImageView) view.findViewById(R.id.friend_picture)),
                R.drawable.ic_contact_picture,
                android.R.drawable.ic_delete);
        imageLoader.get(friend.getPictureUrl(), listener);

        return view;
    }

    // copied from http://dev.classmethod.jp/smartphone/android/android-tips-51-volley/
    private static class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
}