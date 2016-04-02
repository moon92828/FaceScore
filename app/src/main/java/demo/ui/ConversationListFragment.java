package demo.ui;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.util.NetUtils;

import demo.Constant;
import demo.R;

/**
 * Created by moon9 on 2016/3/3.
 */
public class ConversationListFragment extends EaseConversationListFragment {
    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // 注册上下文菜单
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if(conversation.isGroup()){
                        if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        }else{
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }

                    }
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
    }


}
