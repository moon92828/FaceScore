package demo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import demo.DemoHelper;
import demo.R;
import demo.model.Model;
import demo.utils.HttpPostThread;
import demo.utils.ThreadPoolUtils;

/**
 * Created by moon9 on 2016/3/1.
 */
public class RegisterActivity extends BaseActivity {
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    private String username = null;
    private String password = null;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
    }

    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {
        username = userNameEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMClient.getInstance().createAccount(username, password);
                        registeInDb();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    pd.dismiss();
                                // 保存用户名
                                DemoHelper.getInstance().setCurrentUserName(username);
                                Log.d(TAG, "注册成功");
                                finish();
                            }
                        });
                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    pd.dismiss();
                                int errorCode=e.getErrorCode();
                                if(errorCode== EMError.NETWORK_ERROR){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void registeInDb() {
        String url = Model.REGISTET;
        String value = "{\"uname\":\"" + username + "\",\"upassword\":\"" + password
                + "\"}";

        ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
    }

    Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 404) {
                Toast.makeText(RegisterActivity.this, "请求失败，服务器故障", Toast.LENGTH_LONG).show();
            } else if (msg.what == 100) {
                Toast.makeText(RegisterActivity.this, "服务器无响应", Toast.LENGTH_LONG).show();
            } else if (msg.what == 200) {
                String result = (String) msg.obj;
                if (result.equals("ok")) {
                    Toast.makeText(RegisterActivity.this, "注册成功,请登陆", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("NameValue", username);
                    intent.putExtra("PasswordValue", password);
                    setResult(2, intent);
                    return;
                } else if (result.trim().equals("no")) {
                    userNameEditText.setText("");
                    passwordEditText.setText("");
                    confirmPwdEditText.setText("");
                    // Toast.makeText(RegistetActivity.this, "用户名以存在,请重新注册", 1)
                    // .show();
                    return;
                } else {
                    userNameEditText.setText("");
                    passwordEditText.setText("");
                    confirmPwdEditText.setText("");
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        }
    };


    public void back(View view) {
        finish();
    }

}
