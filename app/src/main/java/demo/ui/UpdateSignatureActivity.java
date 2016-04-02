package demo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import demo.R;

/**
 * Created by moon9 on 2016/3/28.
 */
public class UpdateSignatureActivity extends BaseActivity implements OnClickListener {

    private TextView editdetail_back;
    private TextView editdetail_finish;
    private TextView edit_title;
    private TextView edit_num;
    private EditText signature_et;

    private int num;
    private int len;
    private String resultcontent;
    private  String myContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_signature);

        Intent intent;
        Bundle bundle;
        intent = this.getIntent();
        bundle = intent.getExtras();
        myContent = bundle.getString("myContent");

        num = 20;
        len = 0;
        initView();
        initLstener();
    }

    private void initView() {
        edit_num=(TextView)findViewById(R.id.edit_num);
        signature_et = (EditText) findViewById(R.id.signature_et);
        edit_title = (TextView) findViewById(R.id.edit_title);

        edit_num.setText(len + "/" + num);

        editdetail_finish = (TextView) findViewById(R.id.editdetail_finish);
        editdetail_back=(TextView)findViewById(R.id.editdetail_back);
        signature_et.setText(myContent);
        resultcontent = signature_et.getText().toString();
        len = resultcontent.length();
        edit_num.setText(len+"/"+num);
    }

    private void initLstener() {
        editdetail_finish.setOnClickListener(this);
        editdetail_back.setOnClickListener(this);

        signature_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                resultcontent = signature_et.getText().toString();
                len = resultcontent.length();
                if (len <= num) {
                    edit_num.setTextColor(getResources().getColor(R.color.gray));
                    edit_num.setText(String.valueOf(len) + "/" + num);
                } else {
                    edit_num.setTextColor(Color.RED);
                    edit_num.setText(String.valueOf(len) + "/" + num);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.editdetail_finish:
                resultcontent=signature_et.getText().toString().trim();
                int length = resultcontent.length();
                if (length > num) {
                    Toast.makeText(UpdateSignatureActivity.this, "字数超过限制", Toast.LENGTH_SHORT).show();
                    return;
                   }

                SharedPreferences sp = getSharedPreferences(
                        "UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("uExplain", resultcontent);
                editor.commit();

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", resultcontent);
                resultIntent.putExtras(bundle);
                setResult(RESULT_OK,resultIntent);
                this.finish();
                break;
            case R.id.editdetail_back:
                UpdateSignatureActivity.this.finish();
                break;
            default:
                break;
        }
        }


}
