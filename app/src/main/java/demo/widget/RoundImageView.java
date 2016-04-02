package demo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by moon9 on 2016/3/17.
 */
public class RoundImageView extends ImageView {

    public RoundImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);


        Rect rec = canvas.getClipBounds();
        rec.top = rec.top+2;
        rec.bottom = rec.bottom -2;
        rec.left = rec.left +2;
        rec.right = rec.right -2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Shader mShader = new LinearGradient(0, 0, 0, 400,
                new int[] { 0xff000000,0xffeaeaea,0xffd6d6d6}, new float[]{0 ,0.5f,1.0f}, Shader.TileMode.CLAMP);
        paint.setShader(mShader);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        RectF rf2 = new RectF(rec);
        canvas.drawOval(rf2, paint);

    }

    @SuppressWarnings("deprecation")
    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#000000"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
                sbmp.getHeight() / 2 + 0.1f, sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        bm = toRoundCorner(bm, 3);
        super.setImageBitmap(bm);
    }

    public void setImageBitmap(Bitmap bm, int pixels) {
        bm = toRoundCorner(bm, pixels);
        super.setImageBitmap(bm);
    }
}
