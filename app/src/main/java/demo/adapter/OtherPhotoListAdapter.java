package demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import demo.R;
import demo.info.PhotoInfo;
import demo.ui.OtherProfileActivity;

/**
 * Created by moon9 on 2016/4/1.
 */
public class OtherPhotoListAdapter extends RecyclerView.Adapter<OtherPhotoListAdapter.ListViewHolder> {
    private Context context;
    private PhotoInfo data;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(View itemView, int position);

        void onLongClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OtherPhotoListAdapter(Context context, PhotoInfo info) {
        this.context = context;
        this.data = info;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView descTv;
        private ImageView iv;

        public ListViewHolder(View itemView) {
            super(itemView);

            descTv = (TextView) itemView.findViewById(R.id.item_list_tv_name);
            iv = (ImageView) itemView.findViewById(R.id.item_list_iv_icon);

        }
    }

    //当viewholder和数据绑定时回调
    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        PhotoInfo dataInfo = data;

        holder.descTv.setText(dataInfo.getText());
        holder.iv.setImageBitmap(dataInfo.getBitmap());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(holder.itemView, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (listener != null) {
                    listener.onLongClick(holder.itemView, position);
                }
                return false;
            }
        });
    }

    //当viewholder创建的时候回调
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        view = View.inflate(context, R.layout.item_my_photo_grid, null);

        return new ListViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return OtherProfileActivity.size;
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_VIEW_TYPE_ITEM;
    }
}
