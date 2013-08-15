package jp.fbphoto2addressbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.*;

/**
 * @author Kazuki Nishiura
 */
public class FriendsAdapter extends ArrayAdapter<FbFriend> {
    private LayoutInflater inflater;
    private Set<FbFriend> selectedFriends;

    public FriendsAdapter(Context context) {
        super(context, R.layout.list_item_friend, new LinkedList<FbFriend>());
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedFriends = new HashSet<FbFriend>();
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
// TODO:        ((ImageView) view.findViewById(R.id.friend_picture)).setImageURI(item.getPhotoUri());
        return view;
    }
}