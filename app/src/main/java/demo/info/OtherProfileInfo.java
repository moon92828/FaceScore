package demo.info;

import java.util.List;

/**
 * Created by moon9 on 2016/3/31.
 */
public class OtherProfileInfo {

    private String uid;// ID
    private String name;// 姓名
    private String gender;// 性别 0-女，1-男
    private int genderId;// 性别对应的图片资源ResId
    private int genderBgId;// 性别对应的背景资源ResId
    private String age;// 年龄
    private String constellation;// 星座
    private String city;// 城市
    private String sign;// 签名
    private String signPicture;// 签名图片

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public int getGenderBgId() {
        return genderBgId;
    }

    public void setGenderBgId(int genderBgId) {
        this.genderBgId = genderBgId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignPicture() {
        return signPicture;
    }

    public void setSignPicture(String signPicture) {
        this.signPicture = signPicture;
    }
}
