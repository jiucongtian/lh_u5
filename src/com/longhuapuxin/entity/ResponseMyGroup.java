package com.longhuapuxin.entity;

import java.util.List;

/**
 * Created by ZH on 2016/1/21.
 * Email zh@longhuapuxin.com
 */
public class ResponseMyGroup extends ResponseDad {
    private String Total;

    private List<Circle> Circles;

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Circle> getCircles() {
        return Circles;
    }

    public void setCircles(List<Circle> circles) {
        Circles = circles;
    }

    public class Circle {
        private String Id;
        private String OwnerId;
        private String Name;
        private String Note;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getOwnerId() {
            return OwnerId;
        }

        public void setOwnerId(String ownerId) {
            OwnerId = ownerId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getNote() {
            return Note;
        }

        public void setNote(String note) {
            Note = note;
        }
    }
}
