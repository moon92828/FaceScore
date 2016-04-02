package demo.info;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.Serializable;

import demo.util.Bimp;

/**
 * Created by moon9 on 2016/3/13.
 */
public class ImageInfo implements Serializable {
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    private Bitmap bitmap;
    public boolean isSelected = false;

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public Bitmap getBitmap() {
        if(bitmap == null){
            try {
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
