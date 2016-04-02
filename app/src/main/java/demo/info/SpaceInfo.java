package demo.info;

import java.io.Serializable;

/**
 * Created by moon9 on 2016/3/10.
 */
public class SpaceInfo implements Serializable{
    private String sid;
    private String uid;
    private String uName;
    private String sValue;
    private String sLike;
    private String sComment;
    private String createTime;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getsValue() {
        return sValue;
    }

    public void setsValue(String sValue) {
        this.sValue = sValue;
    }

    public String getsLike() {
        return sLike;
    }

    public void setsLike(String sLike) {
        this.sLike = sLike;
    }

    public String getsComment() {
        return sComment;
    }

    public void setsComment(String sComment) {
        this.sComment = sComment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
