package demo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import demo.R;
import demo.info.CommentsInfo;
import demo.info.SpaceInfo;
import demo.model.Model;
import demo.utils.HttpPostThread;
import demo.utils.ThreadPoolUtils;

/**
 * Created by moon9 on 2016/3/11.
 */
public class SendCommentActivity extends BaseActivity {

    private SpaceInfo info = null;
    private ImageView Comment_Back, Comment_Send;
    private EditText Comment_Edit;

    private static final String TAG = "sendCommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_send_comment);
        initView();
        Intent intent = getIntent();
        Bundle bund = intent.getBundleExtra("value");
        info = (SpaceInfo) bund.getSerializable("CommentInfo");
    }

    private void initView(){
        Comment_Back = (ImageView) findViewById(R.id.Comment_Back);
        Comment_Send = (ImageView) findViewById(R.id.Comment_Send);
        Comment_Edit = (EditText) findViewById(R.id.Comment_Edit);
        Comment_Back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendCommentActivity.this.finish();
            }
        });
        Comment_Send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    sendMessage();
            }
        });
    }

    private void sendMessage() {
        if (Comment_Edit.getText().toString().equals("")) {
            Toast.makeText(SendCommentActivity.this, "请先填写糗事文字再提交", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        String uid = sp.getString("uId", "");
        String sid = info.getSid();
        String cvalue = Comment_Edit.getText().toString();
        String Json = "{\"uid\":\"" + uid + "\"," + "\"sid\":\"" + sid + "\","
                + "\"cvalue\":\"" + cvalue + "\"}";
        ThreadPoolUtils
                .execute(new HttpPostThread(hand, Model.SENDCOMMENT, Json));
        SendCommentActivity.this.finish();
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null && result.equals("ok")) {
                    Toast.makeText(SendCommentActivity.this, "评论发送成功", Toast.LENGTH_LONG)
                            .show();
                    SendCommentActivity.this.finish();
                }
            }else {
                Log.e(TAG, msg.what + " " + msg.obj);
            }
        }
    };

}
