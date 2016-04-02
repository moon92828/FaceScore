package demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by moon9 on 2016/3/11.
 *  计算listview的高度加载到scrollview中
 */
public class DetailsListView extends ListView {
    public DetailsListView(Context context) {
        super(context);
    }

    public DetailsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
