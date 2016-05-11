package domain;

/**
 * Created by Administrator on 2016/1/11.
 */
public class User {
    private String userName;//环信id
    private String pinyinNick;//名字的拼音
    private String headImage;//头像名字
    private String sex;
    private String age;
    private String headChar;//名字首字母
    private String usernick;//用户昵称
    private String tel;
    private String fxid;//微信ID
    private String region;
    private String sign;
    private String beizhu;

    /**
     * constructor
     * @param userName
     * @param headImage
     */
    public User(String userName,String headImage){
        this.userName =  userName;
        this.headImage = headImage;
    }
    public User(){

    }
    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getFxid() {
        return fxid;
    }

    public void setFxid(String fxid) {
        this.fxid = fxid;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public String getPinyinNick() {
        return pinyinNick;
    }

    public void setPinyinNick(String pinyinNick) {
        this.pinyinNick = pinyinNick;
    }

    public String getHeadChar() {
        return headChar;
    }

    public void setHeadChar(String headChar) {
        this.headChar = headChar;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String toString()
    {
        return "headChar="+headChar;
    }

//    @Override
//    public int compareTo(Object another) {
//        User user = (User)another;
//        int result = (this.pinyinName.compareTo(user.getPinyinName()) > 0) ? 1 :
//                ((this.pinyinName.compareTo(user.getPinyinName()) == 0) ? 0 : -1);
//        return result;
//    }
}
