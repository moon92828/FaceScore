package demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo.info.FindLoveInfo;
import demo.info.FindLoveLab;
import demo.info.PhotoInfo;
import demo.model.Model;
import demo.net.DownBitmap;
import demo.ui.FindLoveActivity;
import demo.ui.LoadingActivity;

/**
 * 控制图片的加载类
 * 列表在滑动过程时,没有图片会进行下载,并保存到sdcard与 imageCaches 当中去,使用软引用进行封装，如果内存不够时
 * 我们的imageCaches 当中的Bitmap对象会被清理掉,图片被释放掉 再次需要加载的时候，先从1级缓存当中获取，如果没有的话，去
 * 本地获取，本地也获取不到的话，去网络下载。 一级缓存作用：对于listview当中刚刚滑动过的item显示的图片进行保存
 * 二级缓存作用：对于listview当中很久前查看的图片或已经被释放掉图片 进行保存
 */
public class LoadImg extends Activity{
    // 下载图片最大并行线程数
    private static final int Max = 5;
    // 图片的一级缓存,保存在我们程序内部
    private Map<String, SoftReference<Bitmap>> imageCaches = null;

    private Context context;

    // 查看本地缓存工具类
    private FileUtils fileUtiles;
    // android 提供给我们的一个线程池,使用方便
    private ExecutorService threadPools = null;

    private static PhotoDownloadCallBack photoDownloadCallBack;

    // 初始化上面的相关的变量
    public LoadImg(Context ctx) {
        imageCaches = new HashMap<String, SoftReference<Bitmap>>();
        fileUtiles = new FileUtils(ctx);
    }

    // 加载图片时，入口
    public Bitmap loadImage(final String imageUrl) {
        // imageUrl 由于其唯一型，把他作为我们map当中的key
        // 图片名称
        final String filename = imageUrl.substring(
                imageUrl.lastIndexOf("/") + 1, imageUrl.length());
        // 图片保存到本地时的地址
        String filepath = fileUtiles.getAbsolutePath() + "/" + filename;
        // 查找一级缓存，看看是否有这张图片
        // 如果map当中有这个key返回一个true
        if (imageCaches.containsKey(imageUrl)) {
            // 找到对应图片软引用的封装
            SoftReference<Bitmap> soft = imageCaches.get(imageUrl);
            // 从软引用当中获取图片
            Bitmap bit = soft.get();
            if (bit != null)
                return bit;
            // 从我们的一级缓存（程序内部获取图片）
        }
        // 从二级缓存当中获取图片
        if (fileUtiles.isBitmap(filename)) {
            Bitmap bit = BitmapFactory.decodeFile(filepath);
            // 在二级缓存读取的时候直接添加到一级缓存当中
            imageCaches.put(imageUrl, new SoftReference<Bitmap>(bit));
            return bit;
        }

        // 一级缓存，二级缓存都不存在，直接到网络加载
        if (imageUrl != null && !imageUrl.equals("")) {
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
                        // 添加到一级缓存当中
                        imageCaches.put(imageUrl,
                                new SoftReference<Bitmap>(bit));
                        // 添加到二级缓存
                        fileUtiles.saveBitmap(filename, bit);
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

        return null;
    }
    public interface PhotoDownloadCallBack {

        void onPhotoDownload(PhotoInfo info);
    }

    public static void setPhotoDownloadCallBack(PhotoDownloadCallBack callBack) {
        photoDownloadCallBack = callBack;
    }

    /**
     * 建立HTTP请求，并获取Bitmap对象。
     *
     * @param imageUrl
     *            图片的URL地址
     * @return 解析后的Bitmap对象
     */
    public Bitmap downloadBitmap(String imageUrl) {
        Bitmap bitmap = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(10 * 1000);
            con.setDoInput(true);
            con.setDoOutput(true);
            bitmap = BitmapFactory.decodeStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitmap;
    }

}
