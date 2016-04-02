package demo.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.ArrayList;
import java.util.List;

import demo.R;
import demo.adapter.CommentListAdapter;
import demo.info.CommentsInfo;
import demo.info.SpaceInfo;
import demo.model.Model;
import demo.utils.HttpGetThread;
import demo.utils.LoadImg;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;
import demo.widget.DetailsListView;

/**
 * Created by moon9 on 2016/3/11.
 */
public class CommentDetailActivity extends BaseActivity implements OnClickListener{

    private SpaceInfo info = null;
    private LoadImg loadImg;
    private CommentListAdapter mAdapter = null;
    private List<CommentsInfo> list = new ArrayList<CommentsInfo>();
    private Button ListBottem = null;
    private String url = null;
    private boolean flag = true;
    private boolean listBottemFlag = true;
    private int mStart = 0;
    private int mEnd = 5;
    private DetailsListView Detail_List;

    private ImageView Detail_Back, Detail_SendComment;
    private ImageView Detail_UserHead;
    private TextView Detail_UserName;
    private TextView Detail_MainText;
    private ImageView Detail_MainImg;
    private LinearLayout Detail_Like;
    private ImageView Detail_Like_Img;
    private TextView Detail_Like_text;
    private LinearLayout Detail_Comments;
    private LinearLayout Detail__progressBar;
    private TextView Detail_CommentsNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment_detail);
        Intent intent = getIntent();
        Bundle bund = intent.getBundleExtra("value");
        info = (SpaceInfo) bund.getSerializable("SpaceInfo");
        loadImg = new LoadImg(CommentDetailActivity.this);
        initView();
        addInformation();

        mAdapter = new CommentListAdapter(CommentDetailActivity.this, list);
        ListBottem = new Button(CommentDetailActivity.this);
        ListBottem.setText("点击加载更多");
        ListBottem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag && listBottemFlag) {
                    url = Model.COMMENTS + "sid=" + info.getSid() + "&start="
                            + mStart + "&end=" + mEnd;
                    ThreadPoolUtils.execute(new HttpGetThread(hand, url));
                    listBottemFlag = false;
                } else if (!listBottemFlag)
                    Toast.makeText(CommentDetailActivity.this, "正在加载中...", Toast.LENGTH_LONG)
                            .show();
            }
        });
        Detail_List.addFooterView(ListBottem, null, false);
        ListBottem.setVisibility(View.GONE);
        Detail_List.setAdapter(mAdapter);
        String endParames = Model.COMMENTS + "sid=" + info.getSid() + "&start="
                + mStart + "&end=" + mEnd;
        ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
        EaseUserUtils.setUserAvatar(this, info.getuName(),Detail_UserHead);
    }

    private void initView() {
        Detail_Back = (ImageView) findViewById(R.id.Detail_Back);
        Detail_SendComment = (ImageView) findViewById(R.id.Detail_SendComment);
        Detail_UserHead = (ImageView) findViewById(R.id.Detail_UserHead);
        Detail_UserName = (TextView) findViewById(R.id.Detail_UserName);
        Detail_MainText = (TextView) findViewById(R.id.Detail_MainText);
        Detail_MainImg = (ImageView) findViewById(R.id.Detail_MainImg);
        Detail_Like = (LinearLayout) findViewById(R.id.Detail_Like);
        Detail_Like_Img = (ImageView) findViewById(R.id.Detail_Like_Img);
        Detail_Like_text = (TextView) findViewById(R.id.Detail_Like_text);
        Detail_CommentsNum = (TextView) findViewById(R.id.Detail_CommentsNum);
        Detail_Comments = (LinearLayout) findViewById(R.id.Comment_Item);
        Detail_List = (DetailsListView) findViewById(R.id.Detail_List);
        Detail__progressBar = (LinearLayout) findViewById(R.id.Detail_progressBar);
        Detail_CommentsNum = (TextView) findViewById(R.id.Detail_CommentsNum);
        Detail_Back.setOnClickListener(this);
        Detail_SendComment.setOnClickListener(this);
        Detail_UserHead.setOnClickListener(this);
        Detail_MainImg.setOnClickListener(this);
        Detail_Like.setOnClickListener(this);
        Detail_Comments.setOnClickListener(this);

    }

    public void onClick(View arg0){
        int mID = arg0.getId();
        switch (mID) {
            case R.id.Detail_Back:
                CommentDetailActivity.this.finish();
                break;
            case R.id.Detail_SendComment:
                    Intent intent = new Intent(CommentDetailActivity.this,
                            SendCommentActivity.class);
                    Bundle bund = new Bundle();
                    bund.putSerializable("CommentInfo", info);
                    intent.putExtra("value", bund);
                    startActivity(intent);
                break;
            case R.id.Detail_UserHead:
                //Not finish
                break;
            case R.id.Detail_MainImg:
                //Not finish
                break;
            case R.id.Detail_Like:

                break;
            default:
                break;
        }
    }

    Handler hand = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(CommentDetailActivity.this, "请求失败，服务器故障", Toast.LENGTH_LONG)
                        .show();
                listBottemFlag = true;
            } else if (msg.what == 100) {
                Toast.makeText(CommentDetailActivity.this, "服务器无响应", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null) {
                    List<CommentsInfo> newList = MyJson.getCommentsList(result);
                    if (newList != null) {
                        if (newList.size() == 5) {
                            Detail_List.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.VISIBLE);
                            mStart += 5;
                            mEnd += 5;
                        } else if (newList.size() == 0) {
                            if (list.size() == 0)
                                Detail_CommentsNum.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.GONE);
                            Toast.makeText(CommentDetailActivity.this,
                                    "已经没有了...", Toast.LENGTH_LONG).show();
                        } else {
                            Detail_List.setVisibility(View.VISIBLE);
                            ListBottem.setVisibility(View.GONE);
                        }
                        for (CommentsInfo info : newList) {
                            list.add(info);
                        }
                        listBottemFlag = true;
                    } else {
                        Detail_CommentsNum.setVisibility(View.VISIBLE);
                    }
                }
                Detail__progressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void addInformation() {
        Detail_UserName.setText(info.getuName());
        Detail_MainText.setText(info.getsValue());
        Detail_Like_text.setText(info.getsLike());
    }
}
