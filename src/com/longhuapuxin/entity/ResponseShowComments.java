package com.longhuapuxin.entity;

import com.longhuapuxin.db.bean.Estimation;

import java.util.List;

/**
 * Created by ZH on 2016/3/11.
 * Email zh@longhuapuxin.com
 */
public class ResponseShowComments extends ResponseDad {

    private List<Estimation> FeedBacks;

    public List<Estimation> getFeedBacks() {
        return FeedBacks;
    }

    public void setFeedBacks(List<Estimation> feedBacks) {
        FeedBacks = feedBacks;
    }

}
