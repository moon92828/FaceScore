package demo.utils;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import demo.info.PhotoInfo;
import demo.ui.BaseActivity;
import demo.ui.MyPhotoActivity;

/**
 * Created by moon9 on 2016/3/15.
 */
public class PhotoAct extends BaseActivity {

    private Bitmap bm = null;
    private String Tag = "ImgAct";
    private Intent date = null;
    Uri uri =null;
    String sdStatus=null;
    boolean isstate=true;
    private static IMGCallBack1 mIMGCallBack;
    private FileUtils fileUtils = new FileUtils(this);
    private PhotoInfo photoInfo;
    private String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (requestCode == 0) {
            date = data;
            if (date != null) {
                Uri originalUri = data.getData(); // 获得图片的uri
                if (originalUri.getPath().toString() != null) {
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 1;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inPurgeable = true;
                        options.inInputShareable = true;

                        if (null != bm && bm.isRecycled() == false) {
                            bm.recycle();
                        }
                        bm = BitmapFactory.decodeStream(resolver
                                .openInputStream(Uri.parse(originalUri
                                        .toString())), null, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } // 显得到bitmap图片
                    if (bm != null) {
                        fileName = String.valueOf(System.currentTimeMillis());
                        fileUtils.saveBitmap(fileName,bm );

                        new MyCamaralThread().start();
                    } else {
                        Toast.makeText(PhotoAct.this, "图片获取失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhotoAct.this, "图片获取失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                PhotoAct.this.finish();
            }
        }
    }

    /******************解决图片旋转问题****************************/
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**********************************************/
    File fial=null;

    class MyCamaralThread extends Thread{

        public void run(){
            float mWeight = 480f;
            float mHight = 854f;
            float scaleWidth;
            float scaleHeight;
            scaleWidth = ((float)mWeight)/bm.getWidth();
            scaleHeight = ((float)mHight)/bm.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap mbit = null;
            mbit = Bitmap.createBitmap(bm, 0, 0,
                    bm.getWidth(), bm.getHeight(), matrix, true);

            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            mbit.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            String data = Base64.encodeToString(bytes, Base64.DEFAULT);

            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setBitmap(mbit);
            photoInfo.setPhotoName(fileName);

            MyPhotoActivity.tempSelectBitmap.add(photoInfo);
            mIMGCallBack.callback(data, mbit);
            PhotoAct.this.finish();
        }
    }

    public static void setIMGcallback(IMGCallBack1 myIMGCallBack){
        mIMGCallBack = myIMGCallBack;
    }

    public interface IMGCallBack1{
        public void callback(String data,Bitmap bitmap);
    }
}
