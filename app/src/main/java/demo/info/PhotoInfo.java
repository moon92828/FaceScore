package demo.info;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import demo.R;
import demo.ui.LoadingActivity;

/**
 * #作者：Kincai
 * #时间：2015/11/24 16:18
 * #描述：TODO
 */
public class PhotoInfo {
    private String photoName;
    private String text;
    private Bitmap bitmap;
    private String uid;

    private static final String JSON_NAME = "photoName";
    private static final String JSON_TEXT = "text";

    public Bitmap getBitmap() {
        return bitmap;
    }

    public PhotoInfo() {
    }

    public PhotoInfo(int id) {
        bitmap = LoadingActivity.photoList.get(0).getBitmap();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_NAME, photoName);
        json.put(JSON_TEXT, text);
        return json;
    }

    public PhotoInfo(JSONObject json) throws JSONException {
        photoName = json.getString(JSON_NAME);
        text = json.getString(JSON_TEXT);
    }

}
