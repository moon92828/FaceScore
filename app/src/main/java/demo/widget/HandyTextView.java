package demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by moon9 on 2016/3/29.
 */
public class HandyTextView extends TextView {

    public HandyTextView(Context context) {
        super(context);
    }

    public HandyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HandyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null) {
            text = "";
        }
        super.setText(text, type);
    }
}
