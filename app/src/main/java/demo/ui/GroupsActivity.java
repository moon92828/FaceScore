package demo.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.hyphenate.chat.EMGroup;

import java.util.List;

import demo.adapter.GroupAdapter;

/**
 * Created by moon9 on 2016/3/3.
 */
public class GroupsActivity extends BaseActivity {
    public static final String TAG = "GroupsActivity";
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private GroupAdapter groupAdapter;
    private InputMethodManager inputMethodManager;
    public static GroupsActivity instance;
    private View progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;


}
