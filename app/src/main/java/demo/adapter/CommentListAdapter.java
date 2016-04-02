package demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import demo.R;
import demo.info.CommentsInfo;
import demo.model.Model;

/**
 * Created by moon9 on 2016/3/11.
 */
public class CommentListAdapter extends BaseAdapter {

    private List<CommentsInfo> list;
    private Context ctx;

    public CommentListAdapter(Context ctx, List<CommentsInfo> list) {
        this.ctx = ctx;
        this.list = list;
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
        Holder hold;
        if (arg1 == null) {
            hold = new Holder();
            arg1 = View.inflate(ctx, R.layout.comment_detail_item, null);
            hold.UserName = (TextView) arg1
                    .findViewById(R.id.Detail_Item_UserName);
            hold.Num = (TextView) arg1.findViewById(R.id.Detail_Item_Num);
            hold.Value = (TextView) arg1.findViewById(R.id.Detail_Item_Value);
            arg1.setTag(hold);
        } else {
            hold = (Holder) arg1.getTag();
        }
        hold.UserName.setText(list.get(arg0).getUname());
        hold.Num.setText("" + (arg0 + 1));
        hold.Value.setText(list.get(arg0).getCvalue());
        hold.UserName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              //Not finish

            }
        });
        return arg1;
    }

    static class Holder {
        TextView UserName;
        TextView Num;
        TextView Value;
    }

}
