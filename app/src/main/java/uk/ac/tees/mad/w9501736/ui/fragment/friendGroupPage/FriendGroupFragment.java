package uk.ac.tees.mad.w9501736.ui.fragment.friendGroupPage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import uk.ac.tees.mad.w9501736.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendGroupFragment extends Fragment {


    public FriendGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_group, container, false);
    }

}
