package demo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demo.R;
import demo.adapter.SpaceListAdapter;
import demo.info.SpaceInfo;
import demo.model.Model;
import demo.utils.HttpGetThread;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;
import demo.widget.HeadInfo;

/**
 * Created by moon9 on 2016/3/10.
 */
public class ChatSpaceActivity extends BaseActivity implements OnClickListener {

    private String spaceInfoUrl = Model.NEWEST;
    private int topMeunFlag = 1;
    private ImageView mSendInfo;
    private TextView mTopMenuOne, mTopMenuTwo, mTopMenuThree;
    private HeadInfo headInfo;
    private LinearLayout mLinearLayout, load_progressBar;
    private TextView HomeNoValue;
    private SpaceListAdapter mAdapter = null;
    private List<SpaceInfo> list = new ArrayList<SpaceInfo>();
    private InfoFragmentCallBack mInfoFragmentCallback;
    private Button ListBottom = null;
    private boolean flag = true;
    private boolean loadflag = false;
    private boolean listBottemFlag = true;
    private String url = null;
    private int mStart = 0;
    private int mEnd = 5;

    private final static String TAG = "chatSpaceActivity";

    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_chat_space);
        headInfo = new HeadInfo(this);
        initView();
        initListener();
    }

    private void initView() {
        load_progressBar = (LinearLayout) findViewById(R.id.load_progressBar);
        mLinearLayout = (LinearLayout) findViewById(R.id.HomeGroup);
        headInfo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        headInfo.setDivider(null);
        mLinearLayout.addView(headInfo);
        mSendInfo = (ImageView) findViewById(R.id.SendInfo);
        mTopMenuOne = (TextView) findViewById(R.id.TopMenuOne);
        mTopMenuTwo = (TextView) findViewById(R.id.TopMenuTwo);
        mTopMenuThree = (TextView) findViewById(R.id.TopMenuThree);
        HomeNoValue = (TextView) findViewById(R.id.HomeNoValue);
    }

    private void initListener() {
        mSendInfo.setOnClickListener(this);
        mTopMenuOne.setOnClickListener(this);
        mTopMenuTwo.setOnClickListener(this);
        mTopMenuThree.setOnClickListener(this);
        createTextColor();
        switch (topMeunFlag) {
            case 1:
                mTopMenuOne.setTextColor(Color.WHITE);
                mTopMenuOne.setBackgroundResource(R.drawable.top_tab_active);
                break;
            case 2:
                mTopMenuTwo.setTextColor(Color.WHITE);
                mTopMenuTwo.setBackgroundResource(R.drawable.top_tab_active);
                break;
            case 3:
                mTopMenuThree.setTextColor(Color.WHITE);
                mTopMenuThree.setBackgroundResource(R.drawable.top_tab_active);
                break;
        }
        mAdapter = new SpaceListAdapter(this, list);
        ListBottom = new Button(this);
        ListBottom.setText("点击加载更多");
        ListBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag && listBottemFlag) {
                    url = spaceInfoUrl + "start=" + mStart + "&end=" + mEnd;
                    ThreadPoolUtils.execute(new HttpGetThread(hand, url));
                    listBottemFlag = false;
                } else if (!listBottemFlag)
                    Toast.makeText(ChatSpaceActivity.this, "正在加载中...", Toast.LENGTH_LONG).show();
            }
        });
        headInfo.addFooterView(ListBottom, null, false);
        headInfo.setAdapter(mAdapter);
        headInfo.setOnItemClickListener(new MainListOnItemClickListener());
        url = Model.NEWEST + "start=" + mStart + "&end=" + mEnd;
        ThreadPoolUtils.execute(new HttpGetThread(hand, url));
        headInfo.setonRefreshListener(new HeadInfo.OnRefreshListener() {

            @Override
            public void onRefresh() {

                if (loadflag == true) {
                    mStart = 0;
                    mEnd = 5;
                    url = spaceInfoUrl + "start=" + mStart + "&end=" + mEnd;
                    ListBottom.setVisibility(View.GONE);
                    ThreadPoolUtils.execute(new HttpGetThread(hand, url));
                    loadflag = false;
                } else {
                    Toast.makeText(ChatSpaceActivity.this, "正在加载中，请勿重复刷新", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onClick(View arg0) {
        int mID = arg0.getId();
        switch (mID) {
            case R.id.SendInfo:
                Intent intent = new Intent(ChatSpaceActivity.this, SendSharedActivity.class);
                startActivity(intent);
                break;
            case R.id.TopMenuOne:
                createTextColor();
                mTopMenuOne.setTextColor(Color.WHITE);
                mTopMenuOne.setBackgroundResource(R.drawable.top_tab_active);
                if (topMeunFlag != 1) {
                    spaceInfoUrl = Model.NEWEST;
                    topMeunFlag = 1;
                    createListModel();
                }
                break;
            case R.id.TopMenuTwo:
                createTextColor();
                mTopMenuTwo.setTextColor(Color.WHITE);
                mTopMenuTwo.setBackgroundResource(R.drawable.top_tab_active);
                if (topMeunFlag != 2) {
                    spaceInfoUrl = Model.GUANZHU;
                    topMeunFlag = 2;
                    createListModel();
                }
                break;
            case R.id.TopMenuThree:
                createTextColor();
                mTopMenuThree.setTextColor(Color.WHITE);
                mTopMenuThree.setBackgroundResource(R.drawable.top_tab_active);
                if (topMeunFlag != 3) {
                    spaceInfoUrl = Model.HOT;
                    topMeunFlag = 3;
                    createListModel();
                }
                break;
            default:
                break;
        }
    }

    private void createListModel() {
        ListBottom.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        load_progressBar.setVisibility(View.VISIBLE);
        loadflag = false;
        mStart = 0;
        mEnd = 5;
        url = spaceInfoUrl + "start=" + mStart + "&end=" + mEnd;
        ThreadPoolUtils.execute(new HttpGetThread(hand, url));
    }

    private class MainListOnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            Intent intent = new Intent(ChatSpaceActivity.this, CommentDetailActivity.class);
            Bundle bund = new Bundle();
            bund.putSerializable("SpaceInfo", list.get(arg2 - 1));
            intent.putExtra("value", bund);
            startActivity(intent);
        }
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(ChatSpaceActivity.this, "找不到地址", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 100) {
                Toast.makeText(ChatSpaceActivity.this, "传输失败", Toast.LENGTH_LONG).show();
                listBottemFlag = true;
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null) {
                    List<SpaceInfo> newList = MyJson.getSpaceInfoList(result);
                    Log.e(TAG, "get spaceInfo: " + result);
                    if (newList != null) {
                        if (newList.size() == 5) {
                            ListBottom.setVisibility(View.VISIBLE);
                            mStart += 5;
                            mEnd += 5;
                        } else if (newList.size() == 0) {
                            if (list.size() == 0)
                                HomeNoValue.setVisibility(View.VISIBLE);
                            ListBottom.setVisibility(View.GONE);
                            Toast.makeText(ChatSpaceActivity.this, "已经没有了...", Toast.LENGTH_LONG).show();
                        } else {
                            ListBottom.setVisibility(View.GONE);
                        }
                        if (!loadflag) {
                            list.removeAll(list);
                        }
                        for (SpaceInfo info : newList) {
                            list.add(info);
                        }
                        listBottemFlag = true;
                    } else {
                        if (list.size() == 0)
                            HomeNoValue.setVisibility(View.VISIBLE);
                    }
                }
                mLinearLayout.setVisibility(View.VISIBLE);
                load_progressBar.setVisibility(View.GONE);
                headInfo.onRefreshComplete();
                mAdapter.notifyDataSetChanged();
                loadflag = true;
            }
            mAdapter.notifyDataSetChanged();
        };
    };



    @SuppressWarnings("deprecation")
    private void createTextColor() {
        Drawable background = new BitmapDrawable();
        mTopMenuOne.setTextColor(Color.parseColor("#815F3D"));
        mTopMenuTwo.setTextColor(Color.parseColor("#815F3D"));
        mTopMenuThree.setTextColor(Color.parseColor("#815F3D"));
        mTopMenuOne.setBackgroundDrawable(background);
        mTopMenuTwo.setBackgroundDrawable(background);
        mTopMenuThree.setBackgroundDrawable(background);
        HomeNoValue.setVisibility(View.GONE);
    }

    public interface InfoFragmentCallBack {
        public void callback(int flag);
    }

}
