package demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import demo.R;
import demo.info.SpaceInfo;
import demo.utils.LoadImg;

/**
 * Created by moon9 on 2016/3/10.
 */
public class SpaceListAdapter extends BaseAdapter {
    private List<SpaceInfo> list;
    private Context ctx;
    private LoadImg loadImgHeadImg;
    private LoadImg loadImgMainImg;
    private boolean upFlag = false;
    private boolean downFlag = false;

    public SpaceListAdapter(Context ctx, List<SpaceInfo> list) {
        this.list = list;
        this.ctx = ctx;
        // 加载图片对象
        loadImgHeadImg = new LoadImg(ctx);
        loadImgMainImg = new LoadImg(ctx);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        final Holder hold;
        if (arg1 == null) {
            hold = new Holder();
            arg1 = View.inflate(ctx, R.layout.space_listview_item, null);
            hold.UserHead = (ImageView) arg1.findViewById(R.id.Item_UserHead);
            hold.UserName = (TextView) arg1.findViewById(R.id.Item_UserName);
            hold.MainText = (TextView) arg1.findViewById(R.id.Item_MainText);
            hold.MainImg = (ImageView) arg1.findViewById(R.id.Item_MainImg);
            hold.Like = (LinearLayout) arg1.findViewById(R.id.Item_Like);
            hold.Like_Img = (ImageView) arg1.findViewById(R.id.Item_Like_img);
            hold.Like_text = (TextView) arg1.findViewById(R.id.Item_Like_text);
            hold.CommentNum = (TextView) arg1.findViewById(R.id.Item_CommentNum);
            hold.Comment = (LinearLayout) arg1.findViewById(R.id.Item_Comment);
            arg1.setTag(hold);
        }else {
            hold = (Holder) arg1.getTag();
        }
        hold.UserName.setText(list.get(arg0).getuName());
        hold.MainText.setText(list.get(arg0).getsValue());
        hold.Like_text.setText(list.get(arg0).getsLike());
        hold.CommentNum.setText(list.get(arg0).getsComment());

        // 设置监听
        hold.Like.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String num = String.valueOf(Integer.parseInt(list.get(arg0)
                        .getsLike()));
                if (upFlag) {// 判断是否提交过
                    if (!upFlag) {// 判断提交的是不是顶 如果不是则操作
                        list.get(arg0).setsLike(num + 1);
                        hold.Like_text.setText(num + 1);
                        hold.Like.setBackgroundResource(R.drawable.button_vote_active);
                        hold.Like_Img.setImageResource(R.drawable.icon_for_active);
                        hold.Like_text.setTextColor(Color.RED);
                        upFlag = true;
                        hold.Like.setTag("0");
                    }
                } else {
                    hold.Like_text.setText(num);
                    list.get(arg0).setsLike(num);
                }
            }
        });
        hold.UserHead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ctx, "未完成", Toast.LENGTH_SHORT).show();
            }
        });
        hold.MainImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(ctx, "查看大图被点击", Toast.LENGTH_SHORT).show();
            }
        });
        String userName = hold.UserName.getText().toString();
        EaseUserUtils.setUserAvatar(ctx, userName, hold.UserHead);

        return arg1;
    }

    static class Holder {
        ImageView UserHead;
        TextView UserName;
        TextView MainText;
        ImageView MainImg;
        LinearLayout Like;
        ImageView Like_Img;
        TextView Like_text;
        TextView CommentNum;
        LinearLayout Comment;
    }
}
