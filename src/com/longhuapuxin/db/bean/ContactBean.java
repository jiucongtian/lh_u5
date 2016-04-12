package com.longhuapuxin.db.bean;

/**
 * Created by lsy on 2016/1/20.
 */
public class ContactBean  implements Comparable<ContactBean>{
    private int userId; //id
    private String nickName;//姓名
    private String phone; // 电话号码

    private int portrait; // 图片id
    private boolean cared;//是否关注
    private String spell;

    public  String  getSpell(){
        return  spell;
    }
    public  void setSpell(String val){
        spell=val;
    }

    public  int getUserId(){
        return  userId;
    }
    public void setUserId(int val) {
        userId = val;
    }
    public int getPortrait(){
        return portrait;
    }
    public  void setPortrait(int val){
        portrait=val;
    }
    public String getPhone(){
        return phone;
    }
    public void  setPhone(String val){
        phone=val;

    }
    public  boolean getCared(){
        return  cared;
    }
    public  void  setCared(boolean val){
        cared=val;
    }
    public String getNickName(){
        return  nickName;
    }
    public void setNickName(String val){
        nickName=val;
    }

    public int compareTo(ContactBean bean){
        return spell.compareTo(bean.getSpell());
    }
}
