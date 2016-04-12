package com.longhuapuxin.db.bean;

/**
 * Created by lsy on 2016/1/28.
 */
public class RedBag {
    private String CircleId;
    private String SessionId;

    public String getCircleId() {
        return CircleId;
    }

    public void setCircleId(String circleId) {
        CircleId = circleId;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getMoneyEnvelopeId() {
        return MoneyEnvelopeId;
    }

    public void setMoneyEnvelopeId(String moneyEnvelopeId) {
        MoneyEnvelopeId = moneyEnvelopeId;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    private String MoneyEnvelopeId;
    private String Note;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    private String Time;

    public String getPortrait() {
        return Portrait;
    }

    public void setPortrait(String portrait) {
        Portrait = portrait;
    }

    private String Portrait;
}
