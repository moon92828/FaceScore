package demo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import demo.db.MyPhotoJSONSerializer;
import demo.info.PhotoInfo;
import demo.R;
import demo.adapter.MyPhotoListAdapter;
import demo.model.Model;
import demo.utils.FileUtils;
import demo.utils.HttpPostThread;
import demo.utils.LoadImg;
import demo.utils.PhotoAct;
import demo.utils.ThreadPoolUtils;


/**
 * Created by moon9 on 2016/3/17.
 */
public class MyPhotoActivity extends BaseActivity implements View.OnClickListener,MyPhotoListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    MyPhotoListAdapter adapter;
    private ImageView addPhoto;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private View parentView;
    private String data = "";
    private String[] photoData=new String[5];
    private Bitmap bitmap;
    private FileUtils fileUtils = new FileUtils(this);
    public static ArrayList<PhotoInfo> tempSelectBitmap = new ArrayList<PhotoInfo>();

    private MyPhotoJSONSerializer mSerializer;

    private static final String FILENAME = "myPhotoInfo.json";
    private static final int TAKE_PICTURE = 0x000001;
    private static final String TAG = "MyPhotoActivity";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_my_photo, null);
        setContentView(parentView);
        mSerializer = new MyPhotoJSONSerializer(this, FILENAME);
        try {
            tempSelectBitmap = mSerializer.loadPhotoInfo();
            for (int i = 0; i < tempSelectBitmap.size(); i++) {
                String PhotoName = tempSelectBitmap.get(i).getPhotoName();
                LoadImg loadImg = new LoadImg(this);
                tempSelectBitmap.get(i).setBitmap(loadImg.loadImage(PhotoName));
            }
        } catch (Exception e) {
            tempSelectBitmap = new ArrayList<PhotoInfo>();
            Log.e(TAG, "Error loading photoInfos: ", e);
        }
        initView();
    }

    private void initView() {
        addPhoto =(ImageView)findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        setAdapter();
        pop = new PopupWindow(MyPhotoActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button btn_camera = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button btn_photo = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button btn_cancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        PhotoAct.setIMGcallback(new PhotoAct.IMGCallBack1() {

            @Override
            public void callback(String data, Bitmap bitmap) {
                MyPhotoActivity.this.data = data;
                MyPhotoActivity.this.bitmap = bitmap;
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        int mID = arg0.getId();
        switch (mID) {
            case R.id.addPhoto:
                ll_popup.startAnimation(AnimationUtils.loadAnimation(MyPhotoActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.parent:
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_camera:
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_Photo:
                Intent open_album = new Intent(MyPhotoActivity.this, PhotoAct.class);
                startActivity(open_album);
                break;
            case R.id.item_popupwindows_cancel:
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
        }
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
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    fileUtils.saveBitmap(fileName, bm);
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setBitmap(bm);
                    photoInfo.setText("test");
                    photoInfo.setPhotoName(fileName);
                    tempSelectBitmap.add(photoInfo);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setReverseLayout(false);
        layoutManager.setOrientation( GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter = new MyPhotoListAdapter(this, tempSelectBitmap));
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePhotos();
    }

    public boolean savePhotos() {
        Bitmap bitmap;
        for (int i = 0; i < tempSelectBitmap.size(); i++) {
            bitmap = tempSelectBitmap.get(i).getBitmap();
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            photoData[i] = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        String uid = sp.getString("uId", "");
        String[] simg = new String[5];
        simg[0] = "";
        if (photoData[0]!=null) {
            int img_length = 0;
            for (int i = 0; i < photoData.length; i++) {
                if (photoData[i] != null) {
                    simg[i] = System.currentTimeMillis()+i+"."+uid+".png";// 图片
                    img_length++;
                }
            }
            String json_img = "";
            for (int i = 0; i < img_length; i++) {
                json_img += "\"simg"+i+"\":\"" + simg[i] + "\",";
            }
            String Json = "{"+json_img + "\"uid\":\"" + uid+"\"}";
            ThreadPoolUtils.execute(new HttpPostThread(hand, Model.UPLOADPHOTOS, Json, photoData));
        }
        try {
            mSerializer = new MyPhotoJSONSerializer(this, FILENAME);
            mSerializer.savePhotoInfo(tempSelectBitmap);
            Log.i(TAG, "photos saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving photos: ", e);
            return false;
        }
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null && result.equals("ok")) {
                    Toast.makeText(MyPhotoActivity.this, "糗事发送成功", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

}
