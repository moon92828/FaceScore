package demo.db;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import demo.info.PhotoInfo;

/**
 * Created by moon9 on 2016/3/18.
 */
public class MyPhotoJSONSerializer {

    private Context mContext;
    private String mFilename;

    private static final String TAG = "MyPhotoJSONSerializer";

    public MyPhotoJSONSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    public void savePhotoInfo(ArrayList<PhotoInfo> photos)
            throws JSONException, IOException {
        JSONArray array = new JSONArray();
        for (PhotoInfo p : photos)
            array.put(p.toJSON());

            Writer writer = null;
            try {
                OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(array.toString());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
    }

    public ArrayList<PhotoInfo> loadPhotoInfo() throws IOException,JSONException {
        ArrayList<PhotoInfo> photoInfos = new ArrayList<PhotoInfo>();
        BufferedReader reader = null;
        try {
            //Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            //Parse th JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the array of photoInfo from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                photoInfos.add(new PhotoInfo(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found: " + e);
        }finally {
            if (reader != null) {
                reader.close();
            }
        }
        return photoInfos;
    }
}
