package demo.utils;

import com.baidu.platform.comapi.map.F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import demo.R;
import demo.info.CommentsInfo;
import demo.info.FindLoveInfo;
import demo.info.OtherProfileInfo;
import demo.info.PhotoInfo;
import demo.info.SpaceInfo;
import demo.info.UserInfo;

/**
 * Created by moon9 on 2016/3/9.
 */
public class MyJson {

    public static List<UserInfo> getUserList(String result) {
        List<UserInfo> list = null;
        try {
            JSONArray jay = new JSONArray(result);
            list = new ArrayList<UserInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                UserInfo info = new UserInfo();
                info.setUserId(job.getString("USERID"));
                info.setuName(job.getString("UNAME"));
                info.setuAge(job.getString("UAGE"));
                info.setuHobbies(job.getString("UHOBBIES"));
                info.setuPlace(job.getString("UPLACE"));
                info.setuExplain(job.getString("UEXPLAIN"));
                info.setuTime(job.getString("UTIME"));
                info.setuSex(job.getString("USEX"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }

    // 解析空间消息
    public static List<SpaceInfo> getSpaceInfoList(String value) {
        List<SpaceInfo> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<SpaceInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                SpaceInfo info = new SpaceInfo();
                info.setSid(job.getString("SID"));
                info.setUid(job.getString("UID"));
                info.setsValue(job.getString("SVALUE"));
                info.setsLike(job.getString("SLIKE"));
                info.setsComment(job.getString("SCOMMENT"));
                info.setCreateTime(job.getString("CREATE_TIME"));
                info.setuName(job.getString("UNAME"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }

    // 解析空间评论
    public static List<CommentsInfo> getCommentsList(String value) {
        List<CommentsInfo> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<CommentsInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                CommentsInfo info = new CommentsInfo();
                info.setCid(job.getString("CID"));
                info.setCvalue(job.getString("CVALUE"));
                info.setSid(job.getString("SID"));
                info.setUid(job.getString("UID"));
                info.setCtime(job.getString("CTIME"));
                info.setUname(job.getString("UNAME"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }

    //解析图片地址
    public static List<FindLoveInfo> getPhotosList(String value) {
        List<FindLoveInfo> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<FindLoveInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                FindLoveInfo info = new FindLoveInfo();
                info.setPhotoName(job.getString("PHOTO_NAME"));
                info.setUid(job.getString("UID"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }

    public static OtherProfileInfo getOtherProfile(String value) {
        OtherProfileInfo profileInfo = new OtherProfileInfo();
        try {
            JSONObject obj = new JSONObject(value);
            profileInfo.setName(obj.getString("UNAME"));
            profileInfo.setAge(obj.getString("UAGE"));
            profileInfo.setCity(obj.getString("UPLACE"));
            profileInfo.setGender(obj.getString("USEX"));
            profileInfo.setSign(obj.getString("UEXPLAIN"));
            if (profileInfo.getGender().equals("0")) {
                profileInfo.setGenderId(R.drawable.ic_user_male);
                profileInfo.setGenderBgId(R.drawable.bg_gender_male);
            } else {
                profileInfo.setGenderId(R.drawable.ic_user_famale);
                profileInfo.setGenderBgId(R.drawable.bg_gender_famal);
            }
        } catch (JSONException e) {

        }
        return profileInfo;
    }

    //解析图片地址
    public static List<PhotoInfo> getPhotosUrl(String value) {
        List<PhotoInfo> list = null;
        try {
            JSONArray jay = new JSONArray(value);
            list = new ArrayList<PhotoInfo>();
            for (int i = 0; i < jay.length(); i++) {
                JSONObject job = jay.getJSONObject(i);
                PhotoInfo info = new PhotoInfo();
                info.setPhotoName(job.getString("PHOTO_NAME"));
                info.setUid(job.getString("UID"));
                list.add(info);
            }
        } catch (JSONException e) {
        }
        return list;
    }
}
