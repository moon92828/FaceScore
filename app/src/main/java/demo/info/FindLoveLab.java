package demo.info;

import android.content.Context;

import java.util.List;

/**
 * Created by moon9 on 2016/3/26.
 */
public class FindLoveLab {
    private List<FindLoveInfo> mList;

    private static FindLoveLab sFindLoveLab;
    private Context mAppContext;

    private FindLoveLab(Context appContext) {
        mAppContext = appContext;
    }

    public static FindLoveLab get(Context c) {
        if (sFindLoveLab == null) {
            sFindLoveLab = new FindLoveLab(c.getApplicationContext());
        }
        return sFindLoveLab;
    }

    public List<FindLoveInfo> getList() {
        return mList;
    }

}
