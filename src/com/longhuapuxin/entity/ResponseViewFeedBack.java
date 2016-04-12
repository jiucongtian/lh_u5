package com.longhuapuxin.entity;

import com.longhuapuxin.db.bean.Estimation;

import java.util.List;

/**
 * Created by lsy on 2016/3/10.
 */
public class ResponseViewFeedBack extends ResponseDad {
    private int Count;
    private Estimation FeedbackSummary;
    private List<Estimation> FeedBacks;

    public Estimation getFeedbackSummary() {
        return FeedbackSummary;
    }

    public void setFeedbackSummary(Estimation feedBackSummary) {
        FeedbackSummary = feedBackSummary;
    }

    public List<Estimation> getFeedBacks() {
        return FeedBacks;
    }

    public void setFeedBacks(List<Estimation> feedBacks) {
        FeedBacks = feedBacks;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
