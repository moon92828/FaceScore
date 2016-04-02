package demo.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.List;

import demo.DemoApplication;
import demo.DemoHelper;
import demo.R;
import demo.db.DemoDBManager;
import demo.info.UserInfo;
import demo.model.Model;
import demo.utils.HttpPostThread;
import demo.utils.MyJson;
import demo.utils.ThreadPoolUtils;

/**
 * Created by moon9 on 2016/3/1.
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean progressShow;
    private boolean autoLogin = false;

    private String currentUsername;
    private String currentPassword;
    private ProgressDialog pd;

    private UserInfo userInfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果登录成功过，直接进入主页面
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            return;
        }
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
        }
    }

    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = usernameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;

        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });

        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();


        // 调用sdk登陆方法登陆聊天服务器
        Log.d(TAG, "EMClient.getInstance().login");

        loginFromDb();
    }


    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    private void loginFromDb() {
        String url = Model.LOGIN;
        String value = "{\"uname\":\"" + currentUsername + "\",\"upassword\":\""
                + currentPassword + "\"}";
        ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "请求失败，服务器故障",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (msg.what == 100) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "服务器无响应",
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result.equalsIgnoreCase("NOUSER")) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "用户名不存在",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (result.equalsIgnoreCase("NOPASS")) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "密码错误",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (result != null) {
                    Log.e(TAG, "get result from server: " + result);
                    SharedPreferences sp = LoginActivity.this
                            .getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    List<UserInfo> newList = MyJson.getUserList(result);
                    if (newList != null) {
                        userInfo = newList.get(0);
                    }
                    if (userInfo != null) {
                        editor.putString("uName", userInfo.getuName());
                        editor.putString("uId", userInfo.getUserId());
                    }
                    // 提交保存
                    editor.commit();
                    loginInIM();
                }
            }
        }
    };

    private void loginInIM() {
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");

                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                //异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                // 进入主页面
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
        }
        if (autoLogin) {
            return;
        }
    }
}
