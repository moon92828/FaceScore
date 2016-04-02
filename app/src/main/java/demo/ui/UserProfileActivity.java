package demo.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import demo.DemoHelper;
import demo.R;
import demo.model.Model;
import demo.utils.HttpPostThread;
import demo.utils.ThreadPoolUtils;
import demo.widget.SexSlipButton;


/**
 * Created by moon9 on 2016/3/8.
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private ImageView headAvatar;
    private ImageView headPhotoUpdate;
    private ImageView editButton;
    private TextView tvNickName;
    private TextView tvUserId;
    private TextView tvUserSex;
    private TextView tvUserAge;
    private TextView tvUserRegion;
    private TextView tvUserSign;
    private ProgressDialog dialog;
    private RelativeLayout rlNickName,rlUserId,rlUserAge, rlUserSex,rlUserSign;
    private SexSlipButton sexButton;


    private String uName,uAge,uPlace,uExplain,uSex, uHobbies;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String permissionInfo;
    private final int SDK_PERMISSION_REQUEST = 127;

    private final int MYSIGNATURE=20;


    private static final String TAG = "UserProfileActivity";


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_user_profile);

        mLocationClient = new LocationClient(this);     //声明LocationClient类();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        uName = sp.getString("uName", "");
        uAge = sp.getString("uAge", "");
        uPlace = sp.getString("uPlace", "");
        uExplain = sp.getString("uExplain", "");
        uSex = sp.getString("uSex", "");
        uHobbies = sp.getString("uHobbies", "");

        initView();
        initListener();
        initLocation();
        mLocationClient.start();
        //Log.e(TAG, "get user name: " + userInfoJson);
        createUserInfo();
    }


    private void initView() {
        headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
        headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
        editButton = (ImageView) findViewById(R.id.edit_my_info);
        tvUserId = (TextView) findViewById(R.id.user_id);
        tvNickName = (TextView) findViewById(R.id.user_nickname);
        tvUserSex = (TextView) findViewById(R.id.user_sex);
        tvUserRegion = (TextView) findViewById(R.id.user_region);
        rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
        rlUserId = (RelativeLayout) findViewById(R.id.rl_user_id);
        rlUserAge = (RelativeLayout) findViewById(R.id.rl_user_age);
        tvUserAge = (TextView) findViewById(R.id.user_age);
        rlUserSex = (RelativeLayout) findViewById(R.id.rl_user_sex);
        sexButton = (SexSlipButton) findViewById(R.id.show_sex);
        rlUserSign = (RelativeLayout) findViewById(R.id.re_sign);
        tvUserSign = (TextView) findViewById(R.id.user_sign);
        tvUserAge.setText(uAge);
        tvUserSign.setText(uExplain);
        if (editButton.getVisibility() == View.GONE) {
            rlUserAge.setVisibility(View.GONE);
            rlUserSex.setVisibility(View.GONE);

        }
    }

    private void initListener() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        rlNickName.setOnClickListener(this);
        headAvatar.setOnClickListener(this);
        editButton.setOnClickListener(this);
        rlUserAge.setOnClickListener(this);
        rlUserSign.setOnClickListener(this);
        sexButton.setCheck(true);
        sexButton.SetOnChangedListener(new SexSlipButton.OnChangedListener() {
            @Override
            public void OnChanged(boolean CheckState) {
                if(CheckState){
                    tvUserSex.setText("男");
                }
                else{
                    tvUserSex.setText("女");
                }
                updateUserInfo();
            }
        });
        if(username != null){
            if (username.equals(EMClient.getInstance().getCurrentUser())) {
                tvUserId.setText(EMClient.getInstance().getCurrentUser());
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
            } else {
                tvUserId.setText(username);
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
                asyncFetchUserInfo(username);
            }
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null){
                tvUserRegion.setText(uPlace);
                return;
            }
            tvUserRegion.setText(location.getCity());
            uPlace = location.getCity();
         }
        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_avatar:
                uploadHeadPhoto();
                break;
            case R.id.rl_nickname:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.rl_user_age:
                final Calendar c;
                DatePickerDialog  dialog = null;
                c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                final int nowYear = c.get(Calendar.YEAR);
                dialog = new DatePickerDialog(UserProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                tvUserAge.setText((nowYear - year) + "岁");
                                uAge =String.valueOf(nowYear - year);
                            }
                        }, c.get(Calendar.YEAR), // 传入年份
                        c.get(Calendar.MONTH), // 传入月份
                        c.get(Calendar.DAY_OF_MONTH)); // 传入天数
                dialog.getDatePicker().setMaxDate((new Date()).getTime());
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case R.id.re_sign:
                Intent intent = new Intent(UserProfileActivity.this, UpdateSignatureActivity.class);
                intent.putExtra("myContent", tvUserSign.getText());
                startActivityForResult(intent,MYSIGNATURE);
            default:
                break;
        }

    }

    public void asyncFetchUserInfo(String username){
        DemoHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    DemoHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    tvNickName.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_useravatar).into(headAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.default_useravatar).into(headAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }

    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }



    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean updatenick = DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            tvNickName.setText(nickName);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case MYSIGNATURE:
                if (data != null) {
                    String result=data.getStringExtra("result");
                    tvUserSign.setText(result);
                    uExplain = result;
                }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            headAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = DemoHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void createUserInfo() {
        if (uName == null) {
            Intent intent = new Intent(UserProfileActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        } else {
            if (uSex.equals("0")) {
                tvUserSex.setText("男");
                sexButton.setCheck(true);
            } else {
                tvUserSex.setText("女");
                sexButton.setCheck(false);
            }
        }
    }

    private void updateUserInfo() {
        SharedPreferences sp = getSharedPreferences(
                "UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (tvUserSex.getText().toString().equals("男")) {
            editor.putString("uSex", "0");
            uSex = "0";
        } else {
            editor.putString("uSex", "1");
            uSex = "1";
        }
        editor.putString("uAge", tvUserAge.getText().toString());
        editor.putString("uPlace", tvUserRegion.getText().toString());
        Log.i(TAG, "get user info: " + tvUserAge.getText().toString() + " " + tvUserSign.getText().toString() + " " + tvUserRegion.getText().toString());
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationClient.stop();
        updateUserInfo();

        String url = Model.POSTUSERINFO;
        String value = "{\"uname\":\"" + uName + "\",\"uage\":\"" + uAge + "\",\"usex\":\"" + uSex
                + "\",\"uplace\":\"" + uPlace+ "\",\"uexplain\":\"" + uExplain
                + "\"}";

        ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(UserProfileActivity.this, "保存信息失败，服务器故障", Toast.LENGTH_LONG).show();
            } else if (msg.what == 100) {
                Toast.makeText(UserProfileActivity.this, "服务器无响应", Toast.LENGTH_LONG).show();
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result.equals("success")) {
                    //Log.e(TAG, "post userInfo success");
                    return;
                } else if (result.trim().equals("error")) {
                    //Log.e(TAG, "post userInfo fail");
                    return;
                } else {
                    return;
                }
            }
        }
    };

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

}
