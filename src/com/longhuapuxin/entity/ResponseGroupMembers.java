package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/1/22.
 * Email zh@longhuapuxin.com
 */
public class ResponseGroupMembers extends ResponseDad {
    private String Total;
    private List<Member> Members;

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Member> getMembers() {
        return Members;
    }

    public void setMembers(List<Member> members) {
        Members = members;
    }

    public class Member {
        private String UserId;
        private String NickName;
        private String Gender;
        private String Portrait;

        public String getUserId() {
            return UserId;
        }

        public void setUserId(String userId) {
            UserId = userId;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getGender() {
            return Gender;
        }

        public void setGender(String gender) {
            Gender = gender;
        }

        public String getPortrait() {
            return Portrait;
        }

        public void setPortrait(String portrait) {
            Portrait = portrait;
        }
    }
}
