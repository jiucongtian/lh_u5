package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/3/4.
 * Email zh@longhuapuxin.com
 */
public class ResponseRandomPeople extends ResponseDad {

    private List<People> People;

    public List<ResponseRandomPeople.People> getPeople() {
        return People;
    }

    public void setPeople(List<ResponseRandomPeople.People> people) {
        People = people;
    }

    public class People {
        private String Id;
        private String NickName;
        private String Portrait;
        private String LabelName;
        private String LabelCode;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String nickName) {
            NickName = nickName;
        }

        public String getPortrait() {
            return Portrait;
        }

        public void setPortrait(String portrait) {
            Portrait = portrait;
        }

        public String getLabelName() {
            return LabelName;
        }

        public void setLabelName(String labelName) {
            LabelName = labelName;
        }

        public String getLabelCode() {
            return LabelCode;
        }

        public void setLabelCode(String labelCode) {
            LabelCode = labelCode;
        }
    }
}
