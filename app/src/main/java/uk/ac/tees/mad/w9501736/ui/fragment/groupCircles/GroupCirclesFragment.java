package uk.ac.tees.mad.w9501736.ui.fragment.groupCircles;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import butterknife.BindView;
import uk.ac.tees.mad.w9501736.R;
import uk.ac.tees.mad.w9501736.adapters.UserListAdapter;
import uk.ac.tees.mad.w9501736.models.AvailableUserList;
import uk.ac.tees.mad.w9501736.ui.BaseFragment;
import uk.ac.tees.mad.w9501736.ui.helper.AdapterInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupCirclesFragment extends BaseFragment implements AdapterInterface {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.rvUser)
    RecyclerView rvUser;

    AdapterInterface adapterInterface;


    public GroupCirclesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_circles, container, false);
    }


    @Override
    protected int layoutRes() {
        return R.layout.fragment_group_circles;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterInterface = this;

        ArrayList data = new ArrayList<>();
        data.add(new AvailableUserList("User 1", false));
        data.add(new AvailableUserList("User 2", false));
        data.add(new AvailableUserList("User 3", false));
        data.add(new AvailableUserList("User 4", false));
        rvUser.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvUser.setAdapter(new UserListAdapter(data, adapterInterface));
        btnClick(view);
    }

    public void btnClick(View view) {
        fab.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_groupCirclesFragment_to_findNewUserFragment));
    }


    @Override
    public void onItemClicked(String title) {

    }

    @Override
    public void onDeleteCtaClicked(String id) {

    }

    @Override
    public void setEditableText(String id, String name) {

    }
}
