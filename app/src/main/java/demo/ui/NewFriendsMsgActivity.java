package demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import demo.R;
import demo.adapter.NewFriendsMsgAdapter;
import demo.db.InviteMessgeDao;
import demo.domain.InviteMessage;

/**
 * Created by moon9 on 2016/3/3.
 */
public class NewFriendsMsgActivity extends BaseActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        //设置adapter
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

    }

    public void back(View view) {
        finish();
    }
}
