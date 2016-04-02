package demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo.R;
import demo.info.FindLoveInfo;
import demo.info.PhotoInfo;
import demo.model.Model;
import demo.net.DownBitmap;
import demo.util.BitmapCache;
import demo.utils.FileUtils;
import demo.utils.HttpGetThread;
import demo.utils.LoadImg;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;
import demo.widget.HeartProgressBar;

/**
 * Created by moon9 on 2016/3/26.
 */
public class LoadingActivity extends BaseActivity {
    private static final String TAG = "LoadingActivity";

    HeartProgressBar heartProgressBar;
    private LoadImg loadImg;

    public static List<PhotoInfo> photoList;

    // 下载图片最大并行线程数
    private static final int Max = 5;

    // android 提供给我们的一个线程池,使用方便
    private ExecutorService threadPools = null;


    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_loading);

        heartProgressBar = (HeartProgressBar) findViewById(R.id.progressBar);

        photoList = new ArrayList<PhotoInfo>();
        getPhotosUrl();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(heartProgressBar.isStopped()) {
            heartProgressBar.start();
        } else {
            heartProgressBar.dismiss();
        }
    }


    private void getPhotosUrl() {
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
                List<PhotoInfo> newList = MyJson.getPhotosUrl(result);

                loadImg = new LoadImg(LoadingActivity.this);
                loadImg.setPhotoDownloadCallBack(new LoadImg.PhotoDownloadCallBack() {
                    @Override
                    public void onPhotoDownload(PhotoInfo info) {
                        photoList.add(info);
                    }
                });

                for (PhotoInfo info : newList) {
                    final String url = Model.HTTPURL+"UserPhotos/"+info.getUid()+"/"+info.getPhotoName();
                    downloadPhoto(url,newList.size());
                }
            }
        }
    };

    public void downloadPhoto(final String imageUrl,final int size) {
        // 网络加载
        if (!imageUrl.equals("")) {
            if (threadPools == null) {
                // 实例化我们的线程池
                threadPools = Executors.newFixedThreadPool(Max);
            }
            // 下载回图片回调Handler
            final Handler hand = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 如果图片下载成功，并且回调对象不为空时
                    if (msg.what == 111) {
                        Bitmap bit = (Bitmap) msg.obj;
                        PhotoInfo info = new PhotoInfo();
                        info.setBitmap(bit);
                        info.setPhotoName(imageUrl);
                        LoadingActivity.photoList.add(info);
                    }
                    if (LoadingActivity.photoList.size() == size) {
                        Intent intent = new Intent(LoadingActivity.this, FindLoveActivity.class);
                        startActivity(intent);
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
                        Message msg = hand.obtainMessage();
                        msg.what = 111;
                        msg.obj = bit;
                        hand.sendMessage(msg);
                    }
                }
            };

            threadPools.execute(thread);
        }
    }
}
