package demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import demo.R;
import demo.adapter.CardAdapter;
import demo.info.FindLoveInfo;
import demo.info.SpaceInfo;
import demo.model.Model;
import demo.utils.HttpGetThread;
import demo.utils.HttpPostThread;
import demo.utils.LoadImg;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;
import demo.widget.CardView;

/**
 * Created by moon9 on 2016/3/21.
 */
public class FindLoveActivity extends FragmentActivity implements CardView.OnCardClickListener {
    List<String> list;
    private TestFragment frag;

    private LoadImg loadImg=new LoadImg(this);
    private View view;

    private static final String TAG = "FindLoveActivity";

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_love);
        initUI();
    }

    private void initUI() {
        CardView cardView = (CardView) findViewById(R.id.peoplePhotosCard);
        cardView.setOnCardClickListener(this);
        cardView.setItemSpace(convertDpToPixelInt(this, 20));

        MyCardAdapter adapter = new MyCardAdapter(this);
        for (int i = 0; i < LoadingActivity.photoList.size(); i++) {
            String key = LoadingActivity.photoList.get(i).getPhotoName();
            Bitmap bitmap = LoadingActivity.photoList.get(i).getBitmap();
            addBitmapToMemoryCache(key, bitmap);
        }
        cardView.setAdapter(adapter);
        FragmentManager manager = getSupportFragmentManager();
        frag = new TestFragment();
        manager.beginTransaction().add(R.id.contentView, frag).commit();
    }

    @Override
    public void onCardClick(final View view, final int position) {
        String photoUrl = LoadingActivity.photoList.get(position).getPhotoName();
        int index = photoUrl.lastIndexOf(".");
        String substring = photoUrl.substring(0, index);
        int index1 = substring.lastIndexOf(".");
        String uid = substring.substring(index1 + 1, index);
        Intent intent = new Intent(FindLoveActivity.this, OtherProfileActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);

    }

    public class MyCardAdapter extends CardAdapter<String> {

        public MyCardAdapter(Context context) {
            super(context);
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 8;
            // 设置图片缓存大小为程序最大可用内存的1/8
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }

        @Override
        public int getCount() {
            return LoadingActivity.photoList.size();
        }

        @Override
        protected View getCardView(int position, View convertView, ViewGroup parent) {
            final String url = LoadingActivity.photoList.get(position).getPhotoName();
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(FindLoveActivity.this);
                view = inflater.inflate(R.layout.item_find_love, parent, false);
            } else {
                view = convertView;
            }
            final ImageView photo = (ImageView) view.findViewById(R.id.item_people_photo);
            photo.setTag(url);
            setImageView(url, photo);

            return view;
        }
    }

    private int convertDpToPixelInt(Context context, float dp) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int px = (int) (dp * (metrics.densityDpi / 160f));
        return px;
    }

    private void setImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.default_useravatar);
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @param bitmap
     *            LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key
     *            LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }


    private void postInfo() {
        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        String uid = sp.getString("uId", "");

        String url = Model.DOWNLOADPHOTOS + "uid=" + uid;
        ThreadPoolUtils.execute(new HttpGetThread(hand, url));
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                String result = (String) msg.obj;
                List<FindLoveInfo> newList = MyJson.getPhotosList(result);

                for (FindLoveInfo info : newList) {
                    final String url = Model.HTTPURL+"UserPhotos/"+info.getUid()+"/"+info.getPhotoName();
                    info.setPhotoName(url);
                }
            }
        }
    };


}
