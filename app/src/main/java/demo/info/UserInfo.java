package demo.info;

/**
 * Created by moon9 on 2016/3/9.
 */
public class UserInfo {
    private String userId;
    private String uName;
    private String uSex;
    private String uAge;
    private String uTime;
    private String uPlace;
    private String uHobbies;
    private String uExplain;

    public String getuExplain() {
        return uExplain;
    }

    public void setuExplain(String uExplain) {
        this.uExplain = uExplain;
    }

    public String getuHobbies() {
        return uHobbies;
    }

    public void setuHobbies(String uHobbies) {
        this.uHobbies = uHobbies;
    }

    public String getuPlace() {
        return uPlace;
    }

    public void setuPlace(String uPlace) {
        this.uPlace = uPlace;
    }

    public String getuTime() {
        return uTime;
    }

    public void setuTime(String uTime) {
        this.uTime = uTime;
    }

    public String getuAge() {
        return uAge;
    }

    public void setuAge(String uAge) {
        this.uAge = uAge;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
