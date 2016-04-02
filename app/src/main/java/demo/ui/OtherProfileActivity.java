package demo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo.R;
import demo.adapter.MyPhotoListAdapter;
import demo.adapter.OtherPhotoListAdapter;
import demo.info.FindLoveInfo;
import demo.info.OtherProfileInfo;
import demo.info.PhotoInfo;
import demo.model.Model;
import demo.net.DownBitmap;
import demo.utils.HttpGetThread;
import demo.utils.LoadImg;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;
import demo.widget.HandyTextView;
import demo.widget.RoundImageView;
import demo.widget.UserPhotosView;

/**
 * Created by moon9 on 2016/3/29.
 */
public class OtherProfileActivity extends BaseActivity implements OnClickListener,OtherPhotoListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "OtherProfileActivity";

    private LinearLayout mLayoutChat;// 对话
    private LinearLayout mLayoutUnfollow;// 取消收藏
    private LinearLayout mLayoutFollow;// 收藏
    private LinearLayout mLayoutReport;// 拉黑/举报
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout mLayoutSign;// 签名

    private LinearLayout mLayoutGender;// 性别根布局
    private ImageView mIvGender;// 性别
    private HandyTextView mHtvName;//账户名
    private HandyTextView mHtvNickname;//昵称
    private HandyTextView mHtvAge;// 年龄
    private HandyTextView mHtvSign;//签名
    private HandyTextView mHtvConstellation;// 星座
    private HandyTextView mHtvCity;// 城市
    private HandyTextView mHtvTime;// 时间
    private RelativeLayout mLayoutFeed;// 状态根布局
    private LinearLayout mLayoutFeedPicture;// 状态图片布局
    private RoundImageView mRivFeedPicture;// 状态图片
    private HandyTextView mHtvFeedSignature;// 状态签名
    private UserPhotosView mUpvPhotos;// 照片


    private String mUid;// ID
    private String mName;// 姓名

    private OtherProfileInfo mProfile;
    private String url;

    private LoadImg loadImg;
    public static List<PhotoInfo> photoList = new ArrayList<PhotoInfo>();
    // 下载图片最大并行线程数
    private static final int Max = 5;

    // android 提供给我们的一个线程池,使用方便
    private ExecutorService threadPools = null;

    private RecyclerView mRecyclerView;
    OtherPhotoListAdapter adapter;

    public static int size;//图片个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        photoList = new ArrayList<PhotoInfo>();
        initViews();
        initListener();
        init();
    }

    private void initViews() {
        mLayoutChat = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_chat);
        mLayoutUnfollow = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_unfollow);
        mLayoutFollow = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_follow);
        mLayoutReport = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_report);

        mUpvPhotos = (UserPhotosView) findViewById(R.id.otherprofile_upv_photos);

        mLayoutGender = (LinearLayout) findViewById(R.id.otherprofile_layout_gender);
        mIvGender = (ImageView) findViewById(R.id.otherprofile_iv_gender);
        mHtvAge = (HandyTextView) findViewById(R.id.otherprofile_htv_age);
        mHtvConstellation = (HandyTextView) findViewById(R.id.otherprofile_htv_constellation);
        mHtvCity = (HandyTextView) findViewById(R.id.otherprofile_htv_city);
        mHtvName = (HandyTextView) findViewById(R.id.otherprofile_account_id);
        mHtvNickname = (HandyTextView) findViewById(R.id.otherprofile_account_nickname);
        mHtvSign = (HandyTextView) findViewById(R.id.otherprofile_info_htv_sign);
        mLayoutFeed = (RelativeLayout) findViewById(R.id.otherprofile_layout_feed);
        mLayoutFeedPicture = (LinearLayout) findViewById(R.id.otherprofile_layout_feed_pic);
        mRivFeedPicture = (RoundImageView) findViewById(R.id.otherprofile_riv_feed_pic);
        mHtvFeedSignature = (HandyTextView) findViewById(R.id.otherprofile_htv_feed_sign);

        mLayoutSign = (LinearLayout) findViewById(R.id.otherprofile_info_layout_sign);
    }

    private void initListener() {
        mLayoutChat.setOnClickListener(this);
        mLayoutFollow.setOnClickListener(this);
        mLayoutUnfollow.setOnClickListener(this);
        mLayoutReport.setOnClickListener(this);
        mLayoutFeed.setOnClickListener(this);
    }

    private void init() {
        mUid = getIntent().getStringExtra("uid");
        getProfile();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {

            case R.id.otherprofile_bottom_layout_unfollow:
                System.out.println("取消关注");
                break;

            case R.id.otherprofile_bottom_layout_follow:
                System.out.println("关注");
                break;

            case R.id.otherprofile_bottom_layout_report:
                System.out.println("拉黑/举报");
                break;
            case R.id.otherprofile_layout_feed:

                break;
        }
    }

    private void getProfile() {
        new getProfileTask().execute();
    }


    private class getProfileTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(OtherProfileActivity.this, "正在加载,请稍后...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mProfile = new OtherProfileInfo();
            url = Model.DOWNLOADOTHERPROFILE + "uid=" + mUid;
            ThreadPoolUtils.execute(new HttpGetThread(hand, url));
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(OtherProfileActivity.this, "找不到地址", Toast.LENGTH_LONG).show();
            } else if (msg.what == 100) {
                Toast.makeText(OtherProfileActivity.this, "传输失败", Toast.LENGTH_LONG).show();
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null) {
                    mProfile = MyJson.getOtherProfile(result);
                    initProfile();
                }
            }
        }
    };

    private void initProfile() {
        mHtvName.setText(mProfile.getName());
        mHtvSign.setText(mProfile.getSign());
        EaseUserUtils.setUserNick(mProfile.getName(), mHtvNickname);
        mLayoutGender.setBackgroundResource(mProfile.getGenderBgId());
        mIvGender.setImageResource(mProfile.getGenderId());
        mHtvAge.setText(mProfile.getAge() + "");
        mHtvConstellation.setText(mProfile.getConstellation());
        mHtvCity.setText(mProfile.getCity());
        mHtvFeedSignature.setText(mProfile.getSign());
        if (mProfile.getSignPicture() != null) {
            mLayoutFeedPicture.setVisibility(View.VISIBLE);
        } else {
            mLayoutFeedPicture.setVisibility(View.GONE);
        }
        setPhotos();
    }

    private void setPhotos() {
        String url = Model.DOWNLOADPHOTOS + "uid=" + mUid;
        ThreadPoolUtils.execute(new HttpGetThread(hand2, url));
    }

    Handler hand2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                String result = (String) msg.obj;
                List<PhotoInfo> newList = MyJson.getPhotosUrl(result);
                size = newList.size();

                for (PhotoInfo info : newList) {
                    final String url = Model.HTTPURL + "UserPhotos/" + mUid + "/" + info.getPhotoName();
                    downloadPhoto(url);
                }
            }
        }
    };

    public void downloadPhoto(final String imageUrl) {
        // 网络加载
        if (!imageUrl.equals("")) {
            if (threadPools == null) {
                // 实例化我们的线程池
                threadPools = Executors.newFixedThreadPool(Max);
            }
            // 下载回图片回调Handler
            final Handler hand2 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 如果图片下载成功，并且回调对象不为空时
                    if (msg.what == 111) {
                        Bitmap bit = (Bitmap) msg.obj;
                        PhotoInfo info = new PhotoInfo();
                        info.setBitmap(bit);
                        info.setPhotoName(imageUrl);
                        photoList.add(info);
                        Log.i(TAG, "add bitmap to photoList");
                    }
                    if (photoList.size() == size) {
                        mUpvPhotos.setPhotos(photoList);
                        Log.i(TAG, "download otherProfile photos success!");
                    }
                    super.handleMessage(msg);
                }
            };

            // 下载图片线程
            Thread thread = new Thread() {
                public void run() {
                    // 网络下载时的字节流
                    InputStream inputStream = DownBitmap.getInstance()
                            .getInputStream(imageUrl);
                    // 图片压缩为原来的一半
                    BitmapFactory.Options op = new BitmapFactory.Options();
                    op.inSampleSize = 6;
                    if(Model.IMGFLAG)
                        op.inSampleSize = 1;
                    Bitmap bit = BitmapFactory.decodeStream(inputStream, null,
                            op);
                    if (bit != null) {
                        // 传递给Handler
                        Message msg = hand2.obtainMessage();
                        msg.what = 111;
                        msg.obj = bit;
                        hand2.sendMessage(msg);
                    }
                }
            };

            threadPools.execute(thread);
        }
    }

    private void setAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setReverseLayout(false);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View itemView, int position) {
        Toast.makeText(this, "第" + (position + 1) + "条", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View itemView, int position) {
        Toast.makeText(this, "第"+(position+1)+"条", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {

    }
}
