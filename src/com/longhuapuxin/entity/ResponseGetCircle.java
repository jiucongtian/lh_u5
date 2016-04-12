package com.longhuapuxin.entity;

/**
 * Created by asus on 2016/1/25.
 */
public class ResponseGetCircle extends ResponseDad {
    public ResponseGetCircle.Circle getCircle() {
        return Circle;
    }

    public void setCircle(ResponseGetCircle.Circle circle) {
        Circle = circle;
    }

    private Circle Circle;

    public class Circle {
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

        private String Id;
        private String OwnerId;
        private String Name;
        private String Note;
    }
}
