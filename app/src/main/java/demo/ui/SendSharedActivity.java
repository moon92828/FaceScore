package demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import demo.R;
import demo.info.ImageInfo;
import demo.model.Model;
import demo.util.Bimp;
import demo.util.Res;
import demo.utils.FileUtils;
import demo.utils.HttpPostThread;
import demo.utils.ThreadPoolUtils;

/**
 * Created by moon9 on 2016/3/13.
 */
public class SendSharedActivity extends BaseActivity implements OnClickListener {
    private ImageView mClose, mUpLoadEdit, mCamera, mAlbum;
    private EditText mNeirongEdit;
    private String[] data=new String[4];
    private PopupWindow pop = null;
    private View parentView;
    private LinearLayout ll_popup;
    private GridView noScrollgridview;
    private GridAdapter adapter;
    public static Bitmap bimap,bm ;
    private FileUtils fileUtils = new FileUtils(this);

    private static final int TAKE_PICTURE = 0x000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        parentView = getLayoutInflater().inflate(R.layout.activity_send_shared, null);
        setContentView(parentView);
        initView();
        Res.init(this);
    }

    private void initView(){
        // 获取关闭按钮id
        mClose = (ImageView) findViewById(R.id.close);
        // 发表按钮
        mUpLoadEdit = (ImageView) findViewById(R.id.UpLoadEdit);
        mNeirongEdit = (EditText) findViewById(R.id.input_content);
        mClose.setOnClickListener(this);
        mUpLoadEdit.setOnClickListener(this);

        pop = new PopupWindow(SendSharedActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button btn_camera = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button btn_photo = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button btn_cancel = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        noScrollgridview = (GridView) findViewById(R.id.input_image);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(SendSharedActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(SendSharedActivity.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        int ID = v.getId();
        switch (ID) {
            case R.id.close:
                SendSharedActivity.this.finish();
                break;
            case R.id.UpLoadEdit:
                    sendMeth();
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
                Intent intent = new Intent(SendSharedActivity.this,
                        AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
                break;
            case R.id.item_popupwindows_cancel:
                pop.dismiss();
                ll_popup.clearAnimation();
                break;

        }
    }

    private void sendMeth() {
        if (mNeirongEdit.getText().toString().equals("")) {
            Toast.makeText(SendSharedActivity.this, "请先填写糗事文字再提交", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        String uid = sp.getString("uId", "");
        String svalue = mNeirongEdit.getText().toString();// 内容
        String[] simg = new String[4];
        simg[0] = "";
        if (data[0]==null) {
            String Json = "{\"uid\":\"" + uid + "\","
                    + "\"simg\":\"" + simg[0] + "\"," + "\"svalue\":\"" + svalue
                    + "\"," + "\"slike\":\"0\"}";
            ThreadPoolUtils.execute(new HttpPostThread(hand, Model.SENDSHARED, Json));
            SendSharedActivity.this.finish();

        }else {
            int img_length = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i] != null) {
                    simg[i] = System.currentTimeMillis()+i + ".png";// 图片
                    img_length++;
                }
            }
            String json_img = "";
            for (int i = 0; i < img_length; i++) {
                json_img += "\"simg"+i+"\":\"" + simg[i] + "\",";
            }
            String Json = "{\"uid\":\"" + uid + "\","
                    + json_img + "\"svalue\":\"" + svalue
                    + "\"," + "\"slike\":\"0\"}";
            ThreadPoolUtils.execute(new HttpPostThread(hand, Model.SENDSHARED, Json, data));
            SendSharedActivity.this.finish();
        }

    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result != null && result.equals("ok")) {
                    Toast.makeText(SendSharedActivity.this, "糗事发送成功", Toast.LENGTH_LONG).show();
                    SendSharedActivity.this.finish();
                }
            }
        }
    };

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 4){
                return 4;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 4) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                bm = Bimp.tempSelectBitmap.get(position).getBitmap();
                holder.image.setImageBitmap(bm);
                new Thread() {
                    public void run() {
                        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                        byte[] bytes = bStream.toByteArray();
                        data[position] = Base64.encodeToString(bytes, Base64.DEFAULT);
                    }
                }.start();
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 4 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    fileUtils.saveBitmap(fileName,bm);

                    ImageInfo takePhoto = new ImageInfo();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }
}
