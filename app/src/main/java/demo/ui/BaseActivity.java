package demo.ui;

import android.os.Bundle;

import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by moon9 on 2016/3/1.
 */
public class BaseActivity extends EaseBaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // umeng
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng
        MobclickAgent.onPause(this);
    }


}
